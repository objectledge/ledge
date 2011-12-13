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

import java.io.Serializable;
import java.security.Principal;

import org.objectledge.context.Context;

/**
 * The authentication context contains all information about web application user.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AuthenticationContext.java,v 1.1 2004-06-29 13:40:13 zwierzem Exp $
 */
public class AuthenticationContext
    implements Serializable
{
    /**
     * SerialVersionUID as required by Java serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
	 *  Useful method to retrieve authentication context from context.
	 *
	 * @param context the context.
	 * @return the authentication context.
	 */
	public static AuthenticationContext getAuthenticationContext(Context context)
	{
		return context.getAttribute(AuthenticationContext.class);
	}

	/** the user. */
	private Principal user;

    /** is the user authenticated */
	private boolean authenticated;
	
	/** has authentication context of the session changed during this request? */
	private boolean changed;

	/**
	 * Construct new authentication context.
     */
	public AuthenticationContext(Principal user, boolean authenticated)
	{
        this.user = user;
        this.authenticated = authenticated;
        this.changed = false;
	}
	
    /**
     * Returns the user performing the request.
     *
     * @return the user.
     */
    public Principal getUserPrincipal()
    {
    	return user;
    }

    /**
     * Checks whether user is authenticated by system. 
     * 
     * @return <code>true</code> if the current user is not an anounymous.
     */
    public boolean isUserAuthenticated()
    {
    	return authenticated;
    }

    /**
     * Sets the user principal.
     * 
     * @param user the current authenticated user.
     * @param authenticated <code>true</code> if named user is authenticated.
     */
    public void setUserPrincipal(Principal user, boolean authenticated)
    {
        this.user = user;
        this.authenticated = authenticated;
        this.changed = true;
    }
    
    /**
     * Returns {@code true} if the authentication context of the session has changed during
     * processing of the current request.
     * <p>
     * Authentication context can be changed by the authentication.Login, authentication.Logout,
     * authentication.Impersonate actions and the SingleSingOnValve. When the context changes,
     * PostAuthenticationValve will execute it's nested valves.
     * </p>
     * 
     * @return {@code true} if the authentication context of the session has changed during
     * processing of the current request.
     */
    public boolean isChanged()
    {
        return changed;
    }
}
