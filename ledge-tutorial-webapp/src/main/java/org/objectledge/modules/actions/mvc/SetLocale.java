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
package org.objectledge.modules.actions.mvc;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;
import org.objectledge.web.mvc.MVCContext;

/**
 * Set encoding action.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: SetLocale.java,v 1.6 2004-07-09 10:32:40 rafal Exp $
 */
public class SetLocale 
    implements Valve, WebConstants
{
    /**
     * Action constructor.
     */
    public SetLocale(Context context)
    {
    }

    /**
     * Run the valve.
     * 
     * @param context the context.
     */
    public void process(Context context) throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        AuthenticationContext authContext = AuthenticationContext.getAuthenticationContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String localeString = parameters.get("locale", null);
        Locale locale = null;
        try
        {
            locale = StringUtils.getLocale(localeString);
        }
        catch(IllegalArgumentException e)
        {
            throw new ProcessingException(e);
        }

        String cookieKey = "";
        Principal principal = authContext.getUserPrincipal();
        if (principal != null && principal.getName() != null)
        {
            cookieKey = cookieKey + "." + StringUtils.cookieNameSafeString(principal.getName());
        }
        else
        {
            cookieKey = cookieKey + ".anonymous";
        }
        String localeCookieKey = "locale" + cookieKey;
        String encodingCookieKey = "encoding" + cookieKey + "." + locale.toString();
        Cookie cookie = new Cookie(localeCookieKey, localeString);
        cookie.setMaxAge(3600 * 24 * 365);
        cookie.setPath(httpContext.getRequest().getContextPath() + 
                       httpContext.getRequest().getServletPath());
        httpContext.getResponse().addCookie(cookie);
        httpContext.getRequest().getSession().setAttribute(LOCALE_SESSION_KEY, locale);
        i18nContext.setLocale(locale);
        Cookie[] cookies = httpContext.getRequest().getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                if (cookies[i].getName().equals(encodingCookieKey))
                {
                    httpContext.getRequest().getSession().setAttribute(ENCODING_SESSION_KEY, cookies[i].getValue());
                    httpContext.setEncoding(cookies[i].getValue());
                }
            }
        }
    }
}

