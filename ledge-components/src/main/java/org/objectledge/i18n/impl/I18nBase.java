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

package org.objectledge.i18n.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.objectledge.i18n.I18n;
import org.objectledge.templating.ContextToolFactory;
import org.objectledge.utils.StringUtils;

/**
 * Base implementation of I18n component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class I18nBase implements I18n, ContextToolFactory
{
	/** default context tool key */
	public static final String DEFAULT_CONTEXT_TOOL_KEY = "i18n";
	
	/** undefinied value */
	public static final String DEFAULT_UNDEFINED_VALUE = "[?]";
	
	/** default locale */
	public static final String DEFAULT_LOCALE = "en_US";
	
	/** default setting for locale defaulting */
	public static final boolean DEFAULT_USE_DEFAULT_LOCALE = true;
	
	/** default setting for using key as undefinied value */
	public static final boolean DEFAULT_USE_KEY_IF_UNDEFINED = true;
	
	/** context tool key */
	private String contextToolKey;

	/** context tool key */
	private String undefinedValue;

	/** default locale */
	private Locale defaultLocale;
	
	/** use default locale */
	private boolean useDefaultLocale;
	
	/** use key if undefined switch */
	private boolean useKeyIfUndefined;
	
	/** the localization bundles map */
	protected Map localeMap;
	
	/**
	 * Component constructor.
	 *
	 * @param config the configuration. 
	 */
	public I18nBase(Configuration config)
	{
		contextToolKey = config.getChild("context_tool_key").getValue(DEFAULT_CONTEXT_TOOL_KEY);
		undefinedValue = config.getChild("undefined_value").getValue(DEFAULT_UNDEFINED_VALUE);
		defaultLocale = new Locale(config.getChild("default_locale").getValue(DEFAULT_LOCALE));
		useDefaultLocale = config.getChild("use_default_locale").
			getValueAsBoolean(DEFAULT_USE_DEFAULT_LOCALE);
		useKeyIfUndefined = config.getChild("use_key_if_undefined").
			getValueAsBoolean(DEFAULT_USE_KEY_IF_UNDEFINED);
		localeMap = new HashMap();
	}
	
    /**
     * {@inheritDoc}
     */
    public String get(Locale locale, String key)
    {
		Map bundle = (Map)localeMap.get(locale.toString());
		String value = null;
		if(bundle != null)
		{
			value = (String)bundle.get(key);
			if(value != null)
			{
				return value;
			}
			if(locale.equals(defaultLocale) || !useDefaultLocale)
			{
				return useKeyIfUndefined ? key : undefinedValue;
			}
		}
		bundle = (Map)localeMap.get(defaultLocale.toString());
		if(bundle != null)
		{
			value = (String)bundle.get(key);
		}
		if(value != null)
		{
			return value;
		}
		return useKeyIfUndefined ? key : undefinedValue;
    }
    
	/**
	 * {@inheritDoc}
	 */
    public String get(Locale locale, String key, String[] values)
	{
		String template = get(locale, key);
		return StringUtils.substitute(template, values);
	}
    
    
	/**
	 * {@inheritDoc}
	 */
	protected void init()
	{
		//empty loader should be overriden
	}
		
    
    // context tool factory methods
	/**
	 * {@inheritDoc}
	 */
	public Object getTool()
	{
		return new I18nTool(this, this, defaultLocale, null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void recycleTool(Object tool)
	{
		//TODO recycle object when pooling available
	}

	/**
	 * {@inheritDoc}
	 */
	public String getKey()
	{
		return contextToolKey;
	}
}
