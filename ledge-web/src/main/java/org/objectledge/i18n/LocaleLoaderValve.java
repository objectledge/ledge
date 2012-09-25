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

import java.util.Locale;

import javax.servlet.http.Cookie;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;

/**
 * Pipeline processing valve that sets the locale.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LocaleLoaderValve.java,v 1.13 2005-07-29 12:45:46 pablo Exp $
 */
public class LocaleLoaderValve 
    extends AbstractI18nValve
{
    private Logger logger;
    private I18n i18n;
    
    /**
     * Constructor.
     * 
     * @param logger the logger.
     * @param i18n the i18n component.
     */
    public LocaleLoaderValve(Logger logger, I18n i18n)
    {
        this.logger = logger;
        this.i18n = i18n;
    }

    /**
     * Run the pipeline valve.
     * 
     * @param context the context.
     */
    public void process(Context context)
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        String cookieKey = getCookieKeyBase(context);
        String localeCookieKey = "locale" + cookieKey;
        boolean setInCookie = false;
        boolean setInSession = false;

        // get locale from session
        Locale locale = (Locale)httpContext.
            getSessionAttribute(I18nWebConstants.LOCALE_SESSION_KEY);
        
        // get locale from cookie
        if (locale == null)
        {
            Cookie localeCookie = getCookie(httpContext, localeCookieKey);
            if (localeCookie != null)
            {
                if(localeCookie.getMaxAge() <= 60 * 24 * 3600) // less then 60 days left
                {
                    setInCookie = true;
                }
                
                String localeString = localeCookie.getValue();
                if(localeString != null)
                {
                    try
                    {
                        locale = StringUtils.getLocale(localeString);
                        setInSession = true;
                    }
                    catch (IllegalArgumentException e)
                    {
                        logger.error("malformed " + localeCookieKey + " cookie '" + 
                        			 localeString + "' received from client " +
                        			 httpContext.getRequest().getRemoteAddr());
                    }
                }
            }
        }

        // get default locale
        if (locale == null)
        {
            locale = i18n.getPreferedLocale();
        }
        
        if (setInCookie)
        {
            setCookie(httpContext, localeCookieKey, locale.toString());
        }

        if (setInSession)
        {
            httpContext.setSessionAttribute(I18nWebConstants.LOCALE_SESSION_KEY, locale);
        }
        
        I18nContext i18nContext = new I18nContext(locale);
        context.setAttribute(I18nContext.class, i18nContext);
    }
}
