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
import org.jcontainer.dna.Logger;
import org.objectledge.i18n.I18n;
import org.objectledge.utils.StringUtils;

/**
 * Base implementation of I18n component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractI18n.java,v 1.6 2004-08-19 15:40:34 zwierzem Exp $
 */
public abstract class AbstractI18n implements I18n
{
	/** undefinied value */
	public static final String DEFAULT_UNDEFINED_VALUE = "[?]";
	
	/** default locale */
	public static final String DEFAULT_LOCALE = "en_US";
	
	/** default setting for locale defaulting */
	public static final boolean DEFAULT_USE_DEFAULT_LOCALE = true;
	
	/** default setting for using key as undefinied value */
	public static final boolean DEFAULT_USE_KEY_IF_UNDEFINED = true;
	
	/** logger */
	protected Logger logger;
	
	/** string returned when a key has no defined value */
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
	 * @param logger the logger. 
	 */
	public AbstractI18n(Configuration config, Logger logger)
	{
		this.logger = logger;
		undefinedValue = config.getChild("undefined_value").getValue(DEFAULT_UNDEFINED_VALUE);
		defaultLocale = StringUtils.
			getLocale(config.getChild("default_locale").getValue(DEFAULT_LOCALE));
		useDefaultLocale = config.getChild("use_default_locale").
			getValueAsBoolean(DEFAULT_USE_DEFAULT_LOCALE);
		useKeyIfUndefined = config.getChild("use_key_if_undefined").
			getValueAsBoolean(DEFAULT_USE_KEY_IF_UNDEFINED);
		localeMap = new HashMap();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Locale getDefaultLocale()
	{
		return defaultLocale;
	}

    /**
     * {@inheritDoc}
     */
    public String get(Locale locale, String key)
    {
        String value = getInternal(locale, key);
        if(value == null)
        {
            value = useKeyIfUndefined ? key : undefinedValue;
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public String get(Locale locale, String key, String defaultValue)
    {
        String value = getInternal(locale, key);
        if(value == null)
        {
            value = defaultValue;
        }
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    protected String getInternal(Locale locale, String key)
    {
        String value = null;
        // get selected bundle
        Map bundle = (Map)localeMap.get(locale.toString());
        if(bundle != null)
        {
            value = (String)bundle.get(key);
            if(value != null)
            {
                return value;
            }
            if(locale.equals(defaultLocale) || !useDefaultLocale)
            {
                return null;
            }
        }
        // get default bundle
        bundle = (Map)localeMap.get(defaultLocale.toString());
        if(bundle != null)
        {
            value = (String)bundle.get(key);
        }
        return value;
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
	public abstract void reload();
}
