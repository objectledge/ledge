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

package org.objectledge.web;

import java.util.Locale;

import org.jcontainer.dna.Configuration;
import org.objectledge.utils.StringUtils;

/**
 * Configuration component - it provide the access to common MVC configuration.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: WebConfigurator.java,v 1.3 2004-02-03 11:30:39 pablo Exp $
 */
public class WebConfigurator
{
	/** the default locale if not */
	public static final String DEFAULT_LOCALE = "en_US";
	
	/** the default encoding */
	public static final String DEFAULT_ENCODING = "ISO-8859-1";
	
	/** the default view token */
	public static final String DEFAULT_VIEW_TOKEN = "view";
	
	/** the default action token */
	public static final String DEFAULT_ACTION_TOKEN = "action";
	
	/** the default locale */
	private Locale defaultLocale;
	
	/** the default encoding */
	private String defaultEncoding;
	
	/** the view token */
	private String viewToken;
	
	/** the action token */
	private String actionToken;
	
	/**
	 * Constructor
	 * 
	 * @param config the configuration.
	 */
	public WebConfigurator(Configuration config)
	{
		String locale = config.getChild("default_locale").getValue(DEFAULT_LOCALE);
		defaultLocale = StringUtils.getLocale(locale);
		defaultEncoding = config.getChild("default_encoding").getValue(DEFAULT_ENCODING);
		viewToken = config.getChild("view_token").getValue(DEFAULT_VIEW_TOKEN);
		actionToken = config.getChild("action_token").getValue(DEFAULT_ACTION_TOKEN);
	}
	
    /**
     * Get the default locale.
     * 
     * @return the default locale.
     */
    public Locale getDefaultLocale()
    {
    	return defaultLocale;
    }
    
	/**
	 * Get the default encoding.
	 * 
	 * @return the default encoding.
	 */
	public String getDefaultEncoding()
	{
		return defaultEncoding;
	}

	/**
	 * Get the view token.
	 * 
	 * @return the view token.
	 */
	public String getViewToken()
	{
		return viewToken;
	}

	/**
	 * Get the action token.
	 * 
	 * @return the action token.
	 */
	public String getActionToken()
	{
		return actionToken;
	}
}
