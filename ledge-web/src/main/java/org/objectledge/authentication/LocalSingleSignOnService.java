//
// Copyright (c) 2003-2011, Caltha - Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.authentication;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;

public class LocalSingleSignOnService
    implements SingleSignOnService
{
    private static final String DEFAULT_RANDOM_ALGORITHM = "NativePRNG";

    private static final String DEFAULT_RANDOM_PROVIDER = "SUN";

    private static final int DEFAULT_BYTES_PER_TICKET = 16;

    private static final int DEFAULT_TICKET_VALIDITY_TIME = 60;

    private final Logger log;

    private final List<Realm> realms;

    private final Map<String, Ticket> tickets = Collections
        .synchronizedMap(new HashMap<String, Ticket>());

    private final Random random;

    private int bytesPerTicket;

    private int ticketValidityTime;

    private final Map<PrincipalRealm, LogInStatus> userStatus = Collections
        .synchronizedMap(new HashMap<PrincipalRealm, LogInStatus>());

    public LocalSingleSignOnService(ThreadPool threadPool, Configuration config, Logger log)
        throws ConfigurationException
    {
        this.log = log;
        Configuration[] realmConfigs = config.getChild("realms").getChildren("realm");
        List<Realm> realms = new ArrayList<Realm>();
        Set<String> domains = new HashSet<String>();
        Realm globalRealm = null;
        realmLoop: for(Configuration realmConfig : realmConfigs)
        {
            String realmName = realmConfig.getAttribute("name");
            if(realmName.length() == 0)
            {
                throw new ConfigurationException("realm name empty", realmConfig.getPath(),
                    realmConfig.getLocation());
            }
            for(Realm prevRealm : realms)
            {
                if(prevRealm.getName().equals(realmName))
                {
                    throw new ConfigurationException("realm name not unique",
                        realmConfig.getPath(), realmConfig.getLocation());
                }
            }
            String realmMaster = realmConfig.getAttribute("master");
            Configuration[] domainConfigs = realmConfig.getChildren();
            if(domainConfigs.length == 1 && domainConfigs[0].getName().equals("allDomains"))
            {
                globalRealm = new Realm(realmName, realmMaster, null);
                break realmLoop;
            }
            else
            {
                domains.clear();
                for(Configuration domainConfig : domainConfigs)
                {
                    String domain = domainConfig.getValue();
                    if(domain.equals(realmMaster))
                    {
                        throw new ConfigurationException(
                            "realm may not contain it's own master as subordinate",
                            domainConfig.getPath(), domainConfig.getLocation());
                    }
                    domains.add(domain);
                }
                realms.add(new Realm(realmName, realmMaster, domains));
            }
        }
        if(globalRealm != null)
        {
            this.realms = Collections.singletonList(globalRealm);
        }
        else
        {
            this.realms = Collections.unmodifiableList(realms);
        }

        Configuration providerConfig = config.getChild("random", true).getChild("provider", true);
        String randomProvider = providerConfig.getValue(DEFAULT_RANDOM_PROVIDER);
        Configuration algorithmConfig = config.getChild("random", true).getChild("algorithm", true);
        String randomAlgorithm = algorithmConfig.getValue(DEFAULT_RANDOM_ALGORITHM);

        try
        {
            random = SecureRandom.getInstance(randomAlgorithm, randomProvider);
        }
        catch(NoSuchProviderException e)
        {
            throw new ConfigurationException("invalid provider", providerConfig.getPath(),
                providerConfig.getLocation());
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new ConfigurationException("invalid algorithm", algorithmConfig.getPath(),
                algorithmConfig.getLocation());
        }
        this.bytesPerTicket = config.getChild("tickets", true).getChild("size", true)
            .getValueAsInteger(DEFAULT_BYTES_PER_TICKET);
        this.ticketValidityTime = config.getChild("tickets", true).getChild("validityTime", true)
            .getValueAsInteger(DEFAULT_TICKET_VALIDITY_TIME);

        threadPool.runDaemon(new TicketExpiryTask());
    }

    // ..........................................................................................

    public String generateTicket(Principal principal, String domain, String client)
    {
        Realm realm = findRealmByMaster(domain);
        if(realm != null)
        {
            Ticket ticket = generateTicket(principal, realm, client);
            log.debug("ACCEPTED " + client + ", " + principal.getName() + " generated ticket "
                + ticket);
            return ticket.getId();
        }
        else
        {
            log.warn("DECLINED " + client + ", " + principal.getName() + " " + domain
                + " is not a realm master");
            return null;
        }
    }

    public Principal validateTicket(String ticketId, String domain, String client)
    {
        Ticket ticket = tickets.remove(ticketId);
        if(ticket != null)
        {
            if(ticket.getClient().equals(client))
            {
                if(ticket.getRealm().containsDomain(domain))
                {
                    log.debug("ACCEPTED ticket " + ticket.toString());
                    return ticket.getPrincipal();
                }
                else
                {
                    log.warn("DECLINED ticket " + ticket.toString() + " provided from domain "
                        + domain + " outside of realm ");
                }
            }
            else
            {
                log.warn("DECLINED ticket " + ticket.toString() + " provided by client "
                    + client);
            }
        }
        else
        {
            log.warn("DECLINED ticket " + ticketId + " - expired on invalid");
        }
        return null;
    }

    @Override
    public void logIn(Principal principal, String domain)
    {
        List<Realm> domainRealms = findRealmsByMember(domain);
        synchronized(userStatus)
        {
            for(Realm realm : domainRealms)
            {
                log.debug("LOGGED IN user " + principal.getName() + " to realm " + realm.getName());
                userStatus.put(new PrincipalRealm(principal, realm),
                    LogInStatus.LOGGED_IN);
            }
        }
    }

    @Override
    public void logOut(Principal principal, String domain)
    {
        // mark user as logged out
        List<Realm> domainRealms = findRealmsByMember(domain);
        synchronized(userStatus)
        {
            for(Realm realm : domainRealms)
            {
                log.debug("LOGGED OUT user " + principal.getName() + " from realm " + realm.getName());
                userStatus.put(new PrincipalRealm(principal, realm),
                    LogInStatus.LOGGED_OUT);
            }
        }
        // invalidate outstanding tickets
        synchronized(tickets)
        {
            Iterator<Ticket> i = tickets.values().iterator();
            while(i.hasNext())
            {
                Ticket ticket = i.next();
                if(ticket.getPrincipal().equals(principal))                
                {
                    log.debug("INVALIDATED ticket " + ticket.toString() + " because of user logout");
                    i.remove();
                }
            }
        }
    }

    @Override
    public LogInStatus checkStatus(Principal principal, String domain)
    {
        List<Realm> domainRealms = findRealmsByMember(domain);
        if(domainRealms.size() > 0)
        {
            LogInStatus status = userStatus.get(new PrincipalRealm(principal, domainRealms.get(0)));
            if(status != null)
            {
                return status;
            }
        }
        return LogInStatus.UNKNOWN;
    }

    // ..........................................................................................

    private Ticket generateTicket(Principal principal, Realm realm, String client)
    {
        byte idBytes[] = new byte[bytesPerTicket];
        random.nextBytes(idBytes);
        byte encodedId[] = Base64.encodeBase64(idBytes, false);
        String id;
        try
        {
            id = new String(encodedId, "ISO-8859-1");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new Error("ISO-8859-1 encoding not supported");
        }
        Ticket ticket = new Ticket(principal, realm, client, id);
        tickets.put(id, ticket);
        return ticket;
    }

    private Realm findRealmByMaster(String domain)
    {
        for(Realm realm : realms)
        {
            if(realm.getMaster().equals(domain))
            {
                return realm;
            }
        }
        return null;
    }

    private List<Realm> findRealmsByMember(String domain)
    {
        List<Realm> realms = new ArrayList<Realm>(1);
        for(Realm realm : realms)
        {
            if(realm.containsDomain(domain))
            {
                realms.add(realm);
            }
        }
        return realms;
    }

    // ..........................................................................................

    private static class Realm
    {
        private final String name;

        private final String master;

        private final Set<String> domains;

        public Realm(String name, String master, Set<String> domains)
        {
            this.name = name;
            this.master = master;
            this.domains = domains;
        }

        public String getName()
        {
            return name;
        }

        public String getMaster()
        {
            return master;
        }

        public boolean containsDomain(String domain)
        {
            if(domains == null)
            {
                // global realm contains any domain except the master                
                return !master.equals(domain);
            }
            else
            {
                return domains.contains(domain);
            }
        }
    }

    private static class Ticket
    {
        private final Principal principal;

        private final Realm realm;

        private final String client;

        private final String id;

        private final long timestamp;

        public Ticket(Principal principal, Realm realm, String client, String id)
        {
            this.principal = principal;
            this.realm = realm;
            this.client = client;
            this.id = id;
            this.timestamp = System.currentTimeMillis();
        }

        public Principal getPrincipal()
        {
            return principal;
        }

        public Realm getRealm()
        {
            return realm;
        }

        public String getClient()
        {
            return client;
        }

        public String getId()
        {
            return id;
        }

        public long getAge()
        {
            return (System.currentTimeMillis() - timestamp) / 1000;
        }

        @Override
        public int hashCode()
        {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if(obj == null || !(obj instanceof Ticket))
            {
                return false;
            }
            return id.equals(((Ticket)obj).id);
        }

        @Override
        public String toString()
        {
            return id + " for realm " + realm.getName() + ", client" + client + ", "
                + principal.getName();
        }
    }

    private class TicketExpiryTask
        extends Task
    {
        @Override
        public String getName()
        {
            return "SSO Ticket expiry";
        }

        @Override
        public void process(Context context)
            throws ProcessingException
        {
            synchronized(tickets)
            {
                Iterator<Ticket> i = tickets.values().iterator();
                while(i.hasNext())
                {
                    Ticket ticket = i.next();
                    if(ticket.getAge() > ticketValidityTime)
                    {
                        log.debug("EXPIRED ticket " + ticket.toString());
                        i.remove();
                    }
                }
            }
        }
    }

    private static class PrincipalRealm
    {
        private final Principal principal;

        private final Realm realm;

        public PrincipalRealm(Principal principal, Realm realm)
        {
            this.principal = principal;
            this.realm = realm;
        }

        @Override
        public int hashCode()
        {
            return principal.hashCode() ^ realm.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if(obj == null || !(obj instanceof PrincipalRealm))
            {
                return false;
            }
            PrincipalRealm other = (PrincipalRealm)obj;
            return principal.equals(other.principal) && realm.equals(other.realm);
        }

        @Override
        public String toString()
        {
            return "user " + principal.getName() + " in realm " + realm.getName();            		
        }
    }
}
