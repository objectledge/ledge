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
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.WebConstants;

/**
 * Pipeline processing valve that sets the locale.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * 
 * @version $Id: LocaleLoaderValve.java,v 1.7 2004-06-29 13:40:12 zwierzem Exp $
 */
public class LocaleLoaderValve 
    implements Valve, WebConstants
{
	/** the logger */
	private Logger logger;
    
    /** the web configurator */
    private WebConfigurator webConfigurator;

    /**
     * Constructor
     * 
     * @param logger the logger.
     * @param webConfigurator the web configurator component.
     */
    public LocaleLoaderValve(Logger logger, WebConfigurator webConfigurator)
    {
        this.logger = logger;
        this.webConfigurator = webConfigurator;
    }

    /**
     * Run the pipeline valve - authenticate user.
     * 
     * @param context the context.
     */
    public void process(Context context)
    {
        Locale defaultLocale = webConfigurator.getDefaultLocale();
		String defaultEncoding = webConfigurator.getDefaultEncoding();
		
        AuthenticationContext authenticationContext =
            AuthenticationContext.getAuthenticationContext(context);

        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = new I18nContext();
        context.setAttribute(I18nContext.class, i18nContext);

        // set up cookie keys - neccessary for browsers with multiple
        // users on a single user system - for instance Win95/98
        String cookieKey = ".anonymous";
        Principal principal = authenticationContext.getUserPrincipal();
        if (principal != null && principal.getName() != null)
        {
            cookieKey = "." + StringUtils.cookieNameSafeString(principal.getName());
        }
        String localeCookieKey = "locale" + cookieKey;
        String encodingCookieKey = "encoding" + cookieKey;

        String localeString = null;
        String encodingString = null;
        Cookie[] cookies = httpContext.getRequest().getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                if (cookies[i].getName().equals(localeCookieKey))
                {
                    localeString = cookies[i].getValue();
                }
            }
            if (localeString != null)
            {
                encodingCookieKey = encodingCookieKey + "." + localeString;
            }
            else
            {
                encodingCookieKey = encodingCookieKey + "." + defaultLocale.toString();
            }
            for (int i = 0; i < cookies.length; i++)
            {
                if (cookies[i].getName().equals(encodingCookieKey))
                {
                    encodingString = cookies[i].getValue();
                }
            }
        }
        Locale locale = (Locale)httpContext.getRequest().
        						getSession().getAttribute(LOCALE_SESSION_KEY);
        if (locale != null)
        {
            i18nContext.setLocale(locale);
        }
        else
        {
            if (localeString != null)
            {
                try
                {
                    locale = StringUtils.getLocale(localeString);
                    httpContext.getRequest().getSession().setAttribute(LOCALE_SESSION_KEY, locale);
                    i18nContext.setLocale(locale);
                }
                catch (IllegalArgumentException e)
                {
                    logger.error("malformed " + localeCookieKey + " cookie '" + 
                    			 localeString + "' received from client " +
                    			 httpContext.getRequest().getRemoteAddr());
                    Cookie cookie = new Cookie(localeCookieKey, "");
                    cookie.setMaxAge(0);
                    httpContext.getResponse().addCookie(cookie);
                }
            }
        }
        if (localeString == null)
        {
            if (locale == null)
            {
                locale = defaultLocale;
            }
            Cookie cookie = new Cookie(localeCookieKey, locale.toString());
            cookie.setMaxAge(3600 * 24 * 365);
            cookie.setPath(httpContext.getRequest().getContextPath() + 
                           httpContext.getRequest().getServletPath());
            httpContext.getResponse().addCookie(cookie);
        }

        String encoding = (String)httpContext.getRequest().
        				  getSession().getAttribute(ENCODING_SESSION_KEY);
        if (encoding != null)
        {
            httpContext.setEncoding(encoding);
        }
        else
        {
            if (encodingString != null)
            {
                try
                {
                    sun.io.CharToByteConverter.getConverter(encodingString);
                    httpContext.getRequest().getSession().
                    	setAttribute(ENCODING_SESSION_KEY, encodingString);
                    httpContext.setEncoding(encodingString);
                }
                catch (java.io.UnsupportedEncodingException e)
                {
                    logger.error("invalid " + encodingCookieKey + 
                                 " cookie '" + encodingString + "' received from client " +
                                 httpContext.getRequest().getRemoteAddr());
                    Cookie cookie = new Cookie(encodingCookieKey, "");
                    cookie.setMaxAge(0);
                    httpContext.getResponse().addCookie(cookie);
                }
            }
        }
        if (encodingString == null)
        {
            if (encoding == null)
            {
                encoding = defaultEncoding;
            }
            // rebuild the cookieKey just in case the locale was changed.
            encodingCookieKey = "encoding" + cookieKey + "." + locale.toString();

            Cookie cookie = new Cookie(encodingCookieKey, encoding);
            cookie.setMaxAge(3600 * 24 * 365);
            cookie.setPath(httpContext.getRequest().getContextPath() + 
                           httpContext.getRequest().getServletPath());
            httpContext.getResponse().addCookie(cookie);
        }

    }
}
