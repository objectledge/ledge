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

import java.util.Locale;

import org.objectledge.i18n.I18n;
import org.objectledge.templating.ContextToolFactory;
import org.objectledge.utils.StringUtils;

/**
 * The I18n contex tool.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class I18nTool
{
	/** i18n component */
	private I18n i18n;
	
	/** i18n tool factory - for recycling */
	private ContextToolFactory factory;
	
	/** prefix */
	private String prefix;
	
	/** locale */
	private Locale locale;
	
	/**
	 * Default constructor.
	 * 
	 * @param i18n the i18n component.
	 * @param prefix the prefix;
	 */
	I18nTool(I18n i18n, ContextToolFactory factory, Locale locale, String prefix)
	{
		this.i18n = i18n;
		this.factory = factory;
		this.locale = locale;
		this.prefix = prefix;
	}
	
	/**
	 * Returns a new I18nTool using a prefix.
	 *
	 * @param prefix the prefix to use.
	 * @return the i18n tool.
	 */
	public I18nTool usePrefix(String prefix)
	{
		String newPrefix = null;
		if(prefix.length() > 0)
		{
			newPrefix = this.prefix;
			if(newPrefix != null)
			{
				newPrefix = newPrefix + "."+ prefix;
			}
			else
			{
				newPrefix = prefix;
			}
		}
		I18nTool child = new I18nTool(i18n, factory, locale, newPrefix);
	 	return child;
	}
	
	/**
	 * Override request locale.
	 * 
	 * @param locale the locale string representation.
	 * @return the i18n tool.
	 */
	public I18nTool useLocale(String locale)
	{
		return new I18nTool(i18n, factory, StringUtils.getLocale(locale), prefix); 
	}
	
	/** 
	 * Get the string value.
	 * 
	 * @param key the key.
	 * @return the string value.
	 */
	public String get(String key)
	{
		if(prefix == null || prefix.length()==0)
		{
			return i18n.get(locale, key);
		}
		return i18n.get(locale, prefix + "." + key);
	}

	/**
	 * Get the string and replace $[1..n] variables with given values.
	 *
	 * @param key the key.
	 * @param values the values use for substitution.
	 * @return the output string.
	 */
	public String get(String key, String[] values)
	{
		if(prefix == null || prefix.length()==0)
		{
			return i18n.get(locale, key, values);
		}
		return i18n.get(locale, prefix + "." + key, values);
	}
}
