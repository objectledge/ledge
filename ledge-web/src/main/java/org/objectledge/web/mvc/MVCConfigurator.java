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

package org.objectledge.web.mvc;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.utils.StringUtils;

/**
 * Configuration component - it provide the access to common MVC configuration.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MVCConfigurator.java,v 1.1 2004-01-12 12:19:46 pablo Exp $
 */
public class MVCConfigurator
{
	/** the default locale if not */
	public static final String DEFAULT_LOCALE = "en_US";
	
	/** the default encoding */
	public static final String DEFAULT_ENCODING = "ISO-8859-1";
	
	/** the default view token */
	public static final String DEFAULT_VIEW_TOKEN = "view";
	
	/** the default action token */
	public static final String DEFAULT_ACTION_TOKEN = "action";
	
	/** the default query separator */
	public static final String DEFAULT_QUERY_SEPARATOR = "&";
	
	/** the default locale */
	private Locale defaultLocale;
	
	/** the default encoding */
	private String defaultEncoding;
	
	/** the view token */
	private String viewToken;
	
	/** the action token */
	private String actionToken;
	
	/** the sticky parameters keys */
	private Set stickyKeys;
	
	/** the pathinfo parameters keys */
	private Set pathinfoKeys;
	
	/** the queryt separator */
	private String querySeparator;
	
	/**
	 * Constructor
	 * 
	 * @param config the configuration.
	 */
	public MVCConfigurator(Configuration config)
	{
		String locale = config.getChild("default_locale").getValue(DEFAULT_LOCALE);
		defaultLocale = StringUtils.getLocale(locale);
		defaultEncoding = config.getChild("default_encoding").getValue(DEFAULT_ENCODING);
		viewToken = config.getChild("view_token").getValue(DEFAULT_VIEW_TOKEN);
		actionToken = config.getChild("action_token").getValue(DEFAULT_ACTION_TOKEN);
		stickyKeys = new HashSet();
		pathinfoKeys = new HashSet();
		try
		{
			Configuration[] keys = config.getChild("sticky").getChildren("key");
			for (int i = 0; i < keys.length; i++)
			{
				stickyKeys.add(keys[i].getValue());
			}
			keys = config.getChild("pathinfo").getChildren("key");
			for (int i = 0; i < keys.length; i++)
			{
				pathinfoKeys.add(keys[i].getValue());
			}
		}
		catch (ConfigurationException e)
		{
			throw new ComponentInitializationError("failed to configure the component", e);
		}
		querySeparator = config.getChild("query_separator").getValue(DEFAULT_QUERY_SEPARATOR);
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

	/**
	 * Get the sticky parameters keys set.
	 * 
	 * @return the sticky keys.
	 */
	public Set getStickyKeys()
	{
		return stickyKeys;
	}

	/**
	 * Get the path info parameters keys.
	 * 
	 * @return the default encoding.
	 */
	public Set getPathInfoKeys()
	{
		return pathinfoKeys;
	}
	
    /**
     *  Get the query string separator. 
     *
     * @return the query separator. 
     */
    public String getQuerySeparator()
    {
    	return querySeparator;
    }
     
}
