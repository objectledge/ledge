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
import org.objectledge.context.Context;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;
import org.objectledge.web.mvc.MVCContext;

/**
 * Pipeline processing valve that sets the locale.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * 
 * @version $Id: LocaleLoaderValve.java,v 1.3 2004-01-13 15:48:39 pablo Exp $
 */
public class LocaleLoaderValve implements Runnable, WebConstants
{
    /** the context */
    private Context context;

	/** the logger */
	private Logger logger;

    /**
     * Constructor
     * 
     * @param context the context.
     * @param logger the logger.
     */
    public LocaleLoaderValve(Context context, Logger logger)
    {
        this.context = context;
        this.logger = logger;
    }

    /**
     * Run the pipeline valve - authenticate user.
     */
    public void run()
    {
        //TODO Take those values from configuration, somehow... 
        Locale defaultLocale = StringUtils.getLocale("en_US");
		String defaultEncoding = "ISO-8858-1";
		
        HttpContext httpContext = HttpContext.retrieve(context);
        MVCContext mvcContext = MVCContext.retrieve(context);

        // set up cookie keys - neccessary for browsers with multiple
        // users on a single user system - for instance Win95/98
        String cookieKey = ".anonymous";
        Principal principal = mvcContext.getUserPrincipal();
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
            mvcContext.setLocale(locale);
        }
        else
        {
            if (localeString != null)
            {
                try
                {
                    locale = StringUtils.getLocale(localeString);
                    httpContext.getRequest().getSession().setAttribute(LOCALE_SESSION_KEY, locale);
                    mvcContext.setLocale(locale);
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
                                 " cookie '" + encoding + "' received from client " +
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
