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

package org.objectledge.i18n;

import java.security.Principal;

import javax.servlet.http.Cookie;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;

/**
 * Base i18n processing valve with utility methods.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractI18nValve.java,v 1.3 2005-07-25 12:51:43 rafal Exp $
 */
public abstract class AbstractI18nValve 
    implements Valve
{
    /**
     * Creates a base name of the cookie.
     * @param context the request context.
     * @return the basic part of cookie key.
     */
    protected String getCookieKeyBase(Context context)
    {
        AuthenticationContext authenticationContext =
            AuthenticationContext.getAuthenticationContext(context);

        // set up cookie keys - neccessary for browsers with multiple
        // users on a single user system - for instance Win95/98
        String cookieKey = ".anonymous";
        if(authenticationContext != null)
        {
            Principal principal = authenticationContext.getUserPrincipal();
            if (principal != null && principal.getName() != null)
            {
                cookieKey = "." + StringUtils.cookieNameSafeString(principal.getName());
            }
        }
        return cookieKey;
    }

    /**
     * Gets the cookie from the HTTP request.
     * 
     * @param httpContext the HTTPContext.
     * @param cookieName the name of the cookie.
     * @return the cookie object.
     */
    protected Cookie getCookie(HttpContext httpContext, String cookieName)
    {
        String value = null;
        Cookie[] cookies = httpContext.getRequest().getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                if (cookies[i].getName().equals(cookieName))
                {
                    return cookies[i];
                }
            }
        }
        return null;
    }

    /**
     * Sets the cookie vaid for one year.
     * 
     * @param httpContext the HttpContext.
     * @param name name of the cookie.
     * @param value value of the cookie.
     */
    protected void setCookie(HttpContext httpContext, String name, String value)
    {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(3600 * 24 * 365);
        cookie.setPath(httpContext.getRequest().getContextPath() + 
                       httpContext.getRequest().getServletPath());
        httpContext.getResponse().addCookie(cookie);
    }
}
