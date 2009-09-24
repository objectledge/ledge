// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.modules.actions.authentication;

import java.security.Principal;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;

/**
 * Login action.
 *
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a> 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Impersonate.java,v 1.1 2006-02-09 13:32:40 pablo Exp $
 */
public class Impersonate 
    extends BaseAuthenticationAction
{
    private UserManager userManager;
    
    private Logger logger;
    
    /**
     * Action constructor.
     * 
     * @param logger the logger.
     * @param userManager the user manager.
     */
    public Impersonate(Logger logger, UserManager userManager)
    {
        super(logger, userManager);
    }

    /**
     * Runns the valve.
     *   
     * @param context the context.
     * @throws ProcessingException if action processing fails.
     */
    public void process(Context context) 
        throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        Parameters parameters = RequestParameters.getRequestParameters(context);

        String login = parameters.get("dn", null);
        if (login == null)
        {
            throw new ProcessingException("Required parameter (dn) not found");
        }
        Principal principal = null;
        try
        {
            principal = userManager.getUserByLogin(login);
        }
        catch (Exception e)
        {
            logger.debug("unknown username " + login);
            principal = null;
        }
        try
        {
            if(principal != null)
            {
                clearSession(httpContext.getRequest().getSession());
            	httpContext.getRequest().getSession().
                    setAttribute(WebConstants.PRINCIPAL_SESSION_KEY, principal);
            	AuthenticationContext authenticationContext = 
            	    AuthenticationContext.getAuthenticationContext(context);
                boolean authenticated = !principal.equals(userManager.getAnonymousAccount());
            	authenticationContext.setUserPrincipal(principal, authenticated);
            }
        }
        catch(AuthenticationException e)
        {
            throw new ProcessingException("failed to impersonate", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        Principal root = userManager.getSuperuserAccount();
        AuthenticationContext authenticationContext = 
            AuthenticationContext.getAuthenticationContext(context);
        if(root == null)
        {
            return false;
        }
        return root.equals(authenticationContext.getUserPrincipal());
    }
}
