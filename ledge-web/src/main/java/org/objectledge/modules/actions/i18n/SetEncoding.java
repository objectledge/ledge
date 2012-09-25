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
package org.objectledge.modules.actions.i18n;

import java.nio.charset.Charset;
import java.security.Principal;

import javax.servlet.http.Cookie;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.i18n.I18nContext;
import org.objectledge.i18n.I18nWebConstants;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;

/**
 * Set encoding action.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: SetEncoding.java,v 1.5 2006-03-03 15:06:52 zwierzem Exp $
 */
public class SetEncoding 
    implements Valve
{

	private WebConfigurator webConfigurator;

	/**
	 * Constructor.
	 * 
	 * @param webConfigurator the web configurator component.
	 */
	public SetEncoding(WebConfigurator webConfigurator) {
		this.webConfigurator = webConfigurator;
	}
	
    /**
     * Run the valve.
     * 
     * @param context the context.
     * @throws ProcessingException if action processing fails.
     */
    public void process(Context context) throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String encoding = parameters.get("encoding",null);
        if(encoding == null)
        {
            throw new ProcessingException("Parameter 'encoding' not found");
        }
        try
        {
            Charset.forName(encoding);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Unsupported encoding "+encoding);
        }
        
		HttpContext httpContext = HttpContext.getHttpContext(context);
		if (!webConfigurator.getDefaultEncoding().equals(encoding))
		{        
	        String cookieKey = "encoding";
	        AuthenticationContext authenticationContext =
	                AuthenticationContext.getAuthenticationContext(context);
	        Principal principal = (authenticationContext != null) ?
	                authenticationContext.getUserPrincipal() : null;
	        if(principal != null && principal.getName() != null)
	        {
	            cookieKey = cookieKey + "." + StringUtils.
                    cookieNameSafeString(principal.getName());
	        }
	        else
	        {
	            cookieKey = cookieKey + ".anonymous";
	        }
	        I18nContext i18nContext = I18nContext.getI18nContext(context);
	        cookieKey = cookieKey + "." + i18nContext.getLocale().toString();
	        Cookie cookie = new Cookie(cookieKey, encoding);
	        cookie.setMaxAge(3600*24*365);
	        cookie.setPath(httpContext.getRequest().getContextPath()+
                       httpContext.getRequest().getServletPath());
	        httpContext.getResponse().addCookie(cookie);
	        httpContext.setSessionAttribute(I18nWebConstants.ENCODING_SESSION_KEY, encoding);
		}
        httpContext.setEncoding(encoding);
    }
}
