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

package org.objectledge.i18n;

import java.util.Locale;

import org.objectledge.utils.StringUtils;

/**
 * The I18n contex tool.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: I18nTool.java,v 1.6 2004-08-19 15:19:10 zwierzem Exp $
 */
public class I18nTool
{
    private I18n i18n;

    /** buffer used keep the tools prefix and to build absolute keys. */
    protected StringBuffer prefixBuf = new StringBuffer();
	
	/** current locale */
	private Locale locale;
	
	/**
	 * Default constructor.
	 * @param i18n the i18n component.
	 * @param locale the locale.
	 * @param prefix the prefix.	 
	 */
	public I18nTool(I18n i18n, Locale locale, String prefix)
	{
		this.i18n = i18n;
		this.locale = locale;

        prefixBuf.setLength(0);
        if(prefix != null)
        {
            prefixBuf.append(prefix);
        }
	}
	
	/**
	 * Returns a new I18nTool using a prefix.
	 * If prefix is an empty string the prefix is set to null,
	 *
	 * @param prefix the prefix to use.
	 * @return the i18n tool.
	 */
	public I18nTool usePrefix(String prefix)
	{
        int prefixLength = prefixBuf.length();        
		if(prefix.length() > 0)
		{
			if(prefixLength > 0)
			{
				prefixBuf.append('.');
			}
			prefixBuf.append(prefix);
		}
        I18nTool target = createInstance(this); // the prefix buffer is extended
        prefixBuf.setLength(prefixLength); // get back to previous prefix
        return target;
	}
	
	/**
	 * Override request locale.
	 * 
	 * @param locale the locale string representation.
	 * @return the i18n tool.
	 */
	public I18nTool useLocale(String locale)
	{
        I18nTool target = createInstance(this);
        target.locale = StringUtils.getLocale(locale);
		return target; 
	}
	
	/** 
	 * Get the string value.
	 * 
	 * @param key the key.
	 * @return the string value.
	 */
	public String get(String key)
	{
		return i18n.get(locale, getKey(key));
	}

    /** 
     * Get the string value with given default value if the string is missing in
     * both current and default locale.
     * 
     * @param key the key.
     * @param defaultValue the default value in case key mapping is missing.
     * @return the string value.
     */
    public String get(String key, String defaultValue)
    {
        return i18n.get(locale, getKey(key), defaultValue);
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
		return i18n.get(locale, getKey(key), values);
	}
    
    // implementation ------------------------------------------------------------------------------

    /**
     * Creates the I18nTool instance for copying. This method is intended to be overriden by
     * extending classes in order to provide LinkTool instances of proper class.
     * 
     * @param source copied object
     * @return created instance of the linktool.
     */
    protected I18nTool createInstance(I18nTool source)
    {
        return new I18nTool(source.i18n, source.locale, source.prefixBuf.toString());
    }

    /**
     * Creates an absolute key suitable for direct use with I18n component.
     * 
     * @param key key relative to this tool's set prefix.
     */
    protected String getKey(String key)
    {
        int bLength = prefixBuf.length();        
        if(bLength == 0)
        {
            return key;
        }
        String newKey = prefixBuf.append('.').append(key).toString();
        prefixBuf.setLength(bLength);
        return newKey;
    }
}
