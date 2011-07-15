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
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;

import com.sun.mail.util.ASCIIUtility;

public class LocalSingleSignOnService
{
    private static final String DEFAULT_RANDOM_ALGORITHM = "NativePRNG";

    private static final String DEFAULT_RANDOM_PROVIDER = "SUN";

    private static final int DEFAULT_BYTES_PER_TICKET = 16;

    private final Logger logger;

    private final List<Realm> realms;

    private final Random random;

    private int bytesPerTicket;
    
    private final Base64 base64;
    
    public LocalSingleSignOnService(Configuration config, Logger logger)
        throws ConfigurationException
    {
        this.logger = logger;
        Configuration[] realmConfigs = config.getChild("realms").getChildren("realm");
        List<Realm> realms = new ArrayList<Realm>();
        Set<String> sites = new HashSet<String>();
        Realm globalRealm = null;
        realmLoop: for(Configuration realmConfig : realmConfigs)
        {
            String realmName = realmConfig.getAttribute("name");
            String realmMaster = realmConfig.getAttribute("master");
            Configuration[] siteConfigs = realmConfig.getChildren();
            if(siteConfigs.length == 1 && siteConfigs[0].getName().equals("allSites"))
            {
                globalRealm = new Realm(realmName, realmMaster, null);
                break realmLoop;
            }
            else
            {
                sites.clear();
                for(Configuration siteConfig : siteConfigs)
                {
                    sites.add(siteConfig.getValue());
                }
                realms.add(new Realm(realmName, realmMaster, sites));
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
        this.bytesPerTicket = config.getChild("random", true).getChild("bytesPerTicket", true)
            .getValueAsInteger(DEFAULT_BYTES_PER_TICKET);
        this.base64 = new Base64();
    }
    
    private Ticket generateTicket(Principal principal)
    {
        byte idBytes[] = new byte[bytesPerTicket];
        random.nextBytes(idBytes);
        byte encodedId[] = base64.encodeBase64(idBytes, false);
        String id;
        try
        {
            id = new String(encodedId, "ISO-8859-1");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new Error("ISO-8859-1 encoding not supported");
        }
        return new Ticket(principal, id);
    }

    private static class Realm
    {
        private final String name;

        private final Set<String> sites;

        public Realm(String name, String master, Set<String> sites)
        {
            this.name = name;
            this.sites = sites;
        }

        public String getName()
        {
            return name;
        }

        public boolean containsSite(String site)
        {
            return sites.contains(site);
        }
    }

    private static class Ticket
    {
        private final Principal principal;

        private final String id;

        private final long timestamp;

        public Ticket(Principal principal, String id)
        {
            this.principal = principal;
            this.id = id;
            this.timestamp = System.currentTimeMillis();
        }

        public Principal getPrincipal()
        {
            return principal;
        }

        public String getId()
        {
            return id;
        }

        public long getAge()
        {
            return (System.currentTimeMillis() - timestamp) / 1000;
        }
    }
}
