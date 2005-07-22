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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;

/**
 * Pipeline processing valve that sets the generated page output encoding.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EncodingLoaderValve.java,v 1.5 2005-07-22 17:25:54 pablo Exp $
 */
public class EncodingLoaderValve 
    extends AbstractI18nValve
{
	private Logger logger;
    private WebConfigurator webConfigurator;
    
    /**
     * Constructor.
     * 
     * @param logger the logger.
     * @param webConfigurator the web configurator component.
     */
    public EncodingLoaderValve(Logger logger, WebConfigurator webConfigurator)
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
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        String cookieKey = getCookieKeyBase(context);
        String encodingCookieKey = "encoding" + cookieKey + "." + 
            i18nContext.getLocale().toString();
        boolean setInCookie = false;
        boolean setInSession = false;

        // get encoding from session
        String encoding = (String) httpContext.
            getSessionAttribute(I18nWebConstants.ENCODING_SESSION_KEY);
        
        // get encoding from cookie
        if (encoding == null)
        {
            setInSession = true;
            Cookie encodingCookie = getCookie(httpContext, encodingCookieKey);
            if (encodingCookie == null)
            {
                setInCookie = true;
            }
            else
            {
                if(encodingCookie.getMaxAge() <= 60 * 24 * 3600) // less then 60 days left
                {
                    setInCookie = true;
                }
                
                String encodingString = encodingCookie.getValue();
                if(encodingString == null)
                {
                    setInCookie = true;
                }
                else
                {
                    try
                    {
                        new OutputStreamWriter(new ByteArrayOutputStream(), encodingString);
                        encoding = encodingString;
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        setInCookie = true;
                        logger.error("malformed " + encodingCookieKey + " cookie '" + 
                                     encodingString + "' received from client " +
                                     httpContext.getRequest().getRemoteAddr());
                    }
                }
            }
        }

        // get default encoding
        if (encoding == null)
        {
            setInSession = true;
            setInCookie = true;
            encoding = webConfigurator.getDefaultEncoding();
        }
        
        if (setInCookie)
        {
            setCookie(httpContext, encodingCookieKey, encoding);
        }

        if (setInSession)
        {
            httpContext.setSessionAttribute(I18nWebConstants.ENCODING_SESSION_KEY, encoding);
        }
        
        httpContext.setEncoding(encoding);
    }
}
