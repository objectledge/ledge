//
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

/**
 * A valve that 
 * 
 * @version $Id: UserTrackingValve.java,v 1.1 2005-07-26 09:25:47 rafal Exp $
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class UserTrackingValve
    implements Valve
{
    private final Map<Principal,PunchCard> cardRack = new HashMap<Principal,PunchCard>();
    
    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        AuthenticationContext authContext = AuthenticationContext.getAuthenticationContext(context);
        if(authContext != null && authContext.isUserAuthenticated())
        {
            synchronized(cardRack)
            {
                Principal principal = authContext.getUserPrincipal();
                PunchCard card = cardRack.get(principal);
                if(card == null)
                {
                    card =  new PunchCard(principal);
                    cardRack.put(principal, card);
                    HttpContext httpContext = HttpContext.getHttpContext(context);
                    HttpSession session = httpContext.getRequest().getSession();
                    session.setAttribute(PunchCard.class.getName(), card);
                }
                else
                {
                    card.touch();
                }
            }
        }
    }

    /**
     * Returns the principals of all users that have an active session.
     * 
     * @return a set of principal objects.
     */
    public Set<Principal> getLoggedInUsers()
    {
        synchronized(cardRack)
        {
            return new HashSet<Principal>(cardRack.keySet());
        }
    }
    
    /**
     * Returns the timestamp of user's most recent click.
     * 
     * @param principal the users's principal.
     * @return the timestamp of user's most recent click, or null if user is not logged in.
     */
    public Date getLastClickTime(Principal principal)
    {
        synchronized(cardRack)
        {
            PunchCard card = cardRack.get(principal);
            if(card != null)
            {
                return card.getLastClickTime();
            }
            else
            {
                return null;
            }
        }
    }
    
    void checkOut(PunchCard card)
    {
        synchronized(cardRack)
        {
            cardRack.remove(card.getPrincipal());
        }        
    }
    
    /**
     * A user's puch card.
     */
    private class PunchCard 
        implements HttpSessionBindingListener
    {
        private final Principal principal;
        
        private Date lastClickTime;

        public PunchCard(Principal principal) {
            this.principal = principal;
            this.lastClickTime = new Date();
        }
        
        public Principal getPrincipal() {
            return principal;
        }
        
        public void touch() {
            lastClickTime = new Date();
        }
        
        public Date getLastClickTime() {
            return lastClickTime;
        }                

        /**
         * {@inheritDoc}
         */
        public void valueBound(HttpSessionBindingEvent arg0)
        {
            // ingnore
        }

        /**
         * {@inheritDoc}
         */
        public void valueUnbound(HttpSessionBindingEvent event)
        {
            if(event.getValue() instanceof PunchCard)
            {
                checkOut((PunchCard)event.getValue());
            }
        }
    }
}
