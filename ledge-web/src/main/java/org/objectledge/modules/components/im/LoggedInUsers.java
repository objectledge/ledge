// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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

package org.objectledge.modules.components.im;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.UserManager;
import org.objectledge.authentication.UserTrackingValve;
import org.objectledge.context.Context;
import org.objectledge.im.InstantMessaging;
import org.objectledge.im.InstantMessagingContact;
import org.objectledge.im.InstantMessagingProtocol;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.directory.DirectoryParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.components.AbstractComponent;
import org.objectledge.web.mvc.security.SecurityChecking;


/**
 * A component that displays logged in users along with relevan IM information.
 * 
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: LoggedInUsers.java,v 1.3 2006-02-15 14:21:50 pablo Exp $
 */
public class LoggedInUsers
    extends AbstractComponent
    implements SecurityChecking
{
    /**
     * Compares principal by name.
     */
    private static class PrincipalComparator
        implements Comparator<Principal>
    {
        public static final PrincipalComparator INSTANCE = new PrincipalComparator();

        private PrincipalComparator()
        {
        }

        public int compare(Principal o1, Principal o2)
        {
            return o1.getName().compareTo(o2.getName());
        }
    }

    /**
     * Compares IM contacts by protocol id, then by screen name.
     */
    private static class ContactComparator
        implements Comparator<InstantMessagingContact>
    {
        public static final ContactComparator INSTANCE = new ContactComparator();

        private ContactComparator()
        {
        }

        public int compare(InstantMessagingContact o1, InstantMessagingContact o2)
        {
            int result = o1.getProtocol().getId().compareTo(o2.getProtocol().getId());
            if(result == 0)
            {
                result = o1.getScreenName().compareTo(o2.getScreenName());
            }
            return result;
        }
    }

    private final UserTrackingValve userTracker;

    private final UserManager userManager;

    private final InstantMessaging instantMessaging;

    /**
     * Creates a LoggedInUsers visual component instance.
     * 
     * @param context the request context.
     * @param userTracker the UserTrackinValve component.
     * @param userManager the UserManagerComponent.
     * @param instantMessaging the InstantMessaging component.
     */
    public LoggedInUsers(Context context, UserTrackingValve userTracker, UserManager userManager,
        InstantMessaging instantMessaging)
    {
        super(context);
        this.userTracker = userTracker;
        this.userManager = userManager;
        this.instantMessaging = instantMessaging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        try
        {
            Set<Principal> principals = userTracker.getLoggedInUsers();
            Map<Principal, String> login = new HashMap<Principal, String>(principals.size());
            Map<Principal, Parameters> personalData;
            personalData = new HashMap<Principal, Parameters>(principals.size());
            Map<Principal, Integer> idleTime;
            idleTime = new HashMap<Principal, Integer>(principals.size());
            Map<Principal, List<InstantMessagingContact>> contacts;
            contacts = new HashMap<Principal, List<InstantMessagingContact>>();
            Date now = new Date();
            for(Principal p : principals)
            {
                login.put(p, userManager.getLogin(p));
                Parameters userPersonalData = new DirectoryParameters(userManager
                    .getPersonalData(p));
                personalData.put(p, userPersonalData);
                int idle = 0;
                if(userTracker.getLastClickTime(p)!=null)
                {
                    idle = (int)((now.getTime() - userTracker.getLastClickTime(p).getTime()) / 1000);
                }
                idleTime.put(p, idle);
                List<InstantMessagingContact> userContacts;
                userContacts = new ArrayList<InstantMessagingContact>(instantMessaging
                    .getContacts(userPersonalData));
                Collections.sort(userContacts, ContactComparator.INSTANCE);
                contacts.put(p, userContacts);
            }
            List<Principal> users = new ArrayList<Principal>(principals);
            Collections.sort(users, PrincipalComparator.INSTANCE);
            templatingContext.put("users", users);
            templatingContext.put("login", login);
            templatingContext.put("personalData", personalData);
            templatingContext.put("idleTime", idleTime);
            templatingContext.put("contacts", contacts);
            AuthenticationContext authContext = AuthenticationContext
                .getAuthenticationContext(context);
            templatingContext.put("currentUser", authContext.getUserPrincipal());
            templatingContext.put("currentUserLogin", 
                userManager.getLogin(authContext.getUserPrincipal()));

            Collection<InstantMessagingProtocol> protocols = instantMessaging.getProtocols();
            List<List<String>> protocolOptions = new ArrayList<List<String>>(protocols.size());
            for(InstantMessagingProtocol protocol : protocols)
            {
                List<String> item = new ArrayList<String>(2);
                item.add(protocol.getName());
                item.add(protocol.getId());
                protocolOptions.add(item);
            }
            templatingContext.put("protocolOptions", protocolOptions);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve user information", e);
        }
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean requiresSecureChannel(Context context)
        throws ProcessingException
    {
        return false;
    }

    /**
     * @{inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws ProcessingException
    {
        return true;
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
    
}
