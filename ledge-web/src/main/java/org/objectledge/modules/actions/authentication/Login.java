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
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;
import org.objectledge.web.mvc.security.LoginRequiredException;

/**
 * Login action.
 *
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a> 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Login.java,v 1.2 2004-12-22 08:58:30 rafal Exp $
 */
public class Login 
    extends BaseAuthenticationAction
    implements Valve
{
    /**
     * Action constructor.
     * 
     * @param logger the logger.
     * @param userManager the user manager.
     */
    public Login(Logger logger, UserManager userManager)
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

        String login = parameters.get(LOGIN_PARAM, null);
        String password = parameters.get(PASSWORD_PARAM, null);
        parameters.remove(LOGIN_PARAM);
        parameters.remove(PASSWORD_PARAM);        

        Principal anonymous = null;
        try
        {
            anonymous = userManager.getAnonymousAccount();
        }
        catch(AuthenticationException e)
        {
            throw new ProcessingException("UserManager exception", e);
        }
        if (login == null || password == null)
        {
            throw new ProcessingException("Required parameters (" + LOGIN_PARAM +
                                                   ", " + PASSWORD_PARAM + ") not found");
        }
        Principal principal = null;
        try
        {
            principal = userManager.getUserByLogin(login);
            if (userManager.checkUserPassword(principal, password))
            {
                clearSession(httpContext.getRequest().getSession());
            }
            else
            {
                logger.debug("Invalid password for user " + login);
                principal = null;
            }
        }
        catch (Exception e)
        {
            logger.debug("unknown username " + login);
            principal = null;
        }
        boolean authenticated;
        if (principal == null)
        {
            principal = anonymous;
            authenticated = false;
        }
        else
        {
            authenticated = true;
        	httpContext.getRequest().getSession().
                setAttribute(WebConstants.PRINCIPAL_SESSION_KEY, principal);
        }

        AuthenticationContext authenticationContext = 
            AuthenticationContext.getAuthenticationContext(context);
        authenticationContext.setUserPrincipal(principal, authenticated);

        if (!authenticated)
        {
            throw new LoginRequiredException("Login failed");
        }
    }
}