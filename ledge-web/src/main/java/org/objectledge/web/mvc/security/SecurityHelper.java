// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.web.mvc.security;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

/**
 * Util class used to check components security.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SecurityHelper.java,v 1.8 2004-12-22 08:58:35 rafal Exp $
 */
public class SecurityHelper
{
    /**
     * The constructor.
     */
    public SecurityHelper()
    {
        
    }

    /**
     * Check the security on object.
     * 
     * @param obj the object.
     * @param context the context.
     * @throws InsecureChannelException if secure channel was required.
     * @throws LoginRequiredException if authenticated user was required.
     * @throws AccessDeniedException if user has no rights to access the object.
     * @throws ProcessingException if an application error occurs.
     */
    public void checkSecurity(Object obj, Context context)
        throws InsecureChannelException, LoginRequiredException, 
        	   AccessDeniedException, ProcessingException
    {
        boolean condition = false;
        if(obj instanceof SecurityChecking)
        {
            SecurityChecking scObj = (SecurityChecking)obj;
            try
            {
                condition = scObj.requiresSecureChannel(context);
            }
            catch(Exception e)
            {
                throw new ProcessingException("exception occured during security checking", e);
            }
            if(condition)
            {
                HttpContext httpContext = HttpContext.getHttpContext(context);
                if(httpContext == null)
                {
                    throw new IllegalStateException("failed to retrieve http context " +
                                                     " for security checking purpose");
                }
                if(!httpContext.getRequest().isSecure())
                {
                    throw new InsecureChannelException("Secure channel required for "+
                                                        obj.getClass().getName());
                }
            }
            try
            {
                condition = scObj.requiresAuthenticatedUser(context);
            }
            catch(Exception e)
            {
                throw new ProcessingException("exception occured during security checking", e);
            }                
            if(condition)
            {
                AuthenticationContext authenticationContext = 
                    AuthenticationContext.getAuthenticationContext(context);
                if(authenticationContext == null)
                {
                    throw new IllegalStateException("failed to retrieve mvc context " +
                                                     " for security checking purpose");
                }
                if(!authenticationContext.isUserAuthenticated())
                {
                    throw new LoginRequiredException("Only authenticated user can access "+
                                                       obj.getClass().getName());
                }
            }
            try
            {
                condition = !scObj.checkAccessRights(context);
            }
            catch(Exception e)
            {
                throw new ProcessingException("exception occured during security checking", e);
            }                
            if(condition)
            {
                throw new AccessDeniedException("The authenticated user has no right to access "+
                                                  obj.getClass().getName());
            }            
        }
    }
}
