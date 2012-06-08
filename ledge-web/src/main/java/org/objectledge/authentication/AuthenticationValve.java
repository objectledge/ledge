// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;

/**
 * Pipeline processing valve that sets the context variable describing currently authenticated user.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AuthenticationValve.java,v 1.13 2005-07-22 17:25:47 pablo Exp $
 */
public class AuthenticationValve 
    implements Valve
{
	/** the authentication component */
	private UserManager userManager;
	
	/**
	 * Constructor.
	 * 
     * @param userManager the user manager component.
	 */
	public AuthenticationValve(UserManager userManager)
	{
		this.userManager = userManager;
	}
	
    /**
     * Run the pipeline valve - authenticate user.
     * 
     * @param context the thread's processing context.
     * @throws ProcessingException if authentication failed.
     */
    public void process(Context context)
        throws ProcessingException
    {
    	HttpContext httpContext = HttpContext.getHttpContext(context);
    	Principal principal = (Principal)httpContext.getRequest().
			getSession().getAttribute(WebConstants.PRINCIPAL_SESSION_KEY);
		Principal anonymous = null;
        try
        {
            anonymous = userManager.getAnonymousAccount();
        }
        catch(AuthenticationException e)
        {
            throw new ProcessingException("Failed to retrieve anonymous account");
        }
		boolean authenticated = false;
		if(principal == null)
		{
			principal = anonymous;
		}
		else
		{
			authenticated = !principal.equals(anonymous);
		}
        AuthenticationContext authenticationContext = new AuthenticationContext(principal,
            authenticated);
        context.setAttribute(AuthenticationContext.class, authenticationContext);
        
    	httpContext.getRequest().getSession().setAttribute(WebConstants.PRINCIPAL_SESSION_KEY, 
            principal);
    }
}
