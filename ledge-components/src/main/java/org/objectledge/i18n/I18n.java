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

import java.util.List;
import java.util.Locale;

/**
 * Common interface for i18n various i18n implementations.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: I18n.java,v 1.10 2004-12-20 16:08:05 pablo Exp $
 */
public interface I18n
{
	/** 
	 * Returns configured default locale.
	 * 
	 * @return the default locale object. 
	 */
	public Locale getDefaultLocale();
	
	/** 
	 * Returns configured locales.
	 * 
	 * @return an array of defined locale. 
	 */
	public Locale[] getSupportedLocales();
	
	/**
	 * Returns the human readable name of the given locale.
	 * 
	 * @param locale the locale.
	 * @return the human readable name of the given locale.
	 */
	public String getLocaleName(Locale locale);
	
    /** 
     * Checks if a string value is defined for a default locale.
     * 
     * @param key the key.
     * @return <code>true</code> if the string is defined
     */
    public boolean defined(String key);

    /** 
     * Checks if a string value is defined for a given locale.
     * 
     * @param locale the locale.
     * @param key the key.
     * @return <code>true</code> if the string is defined
     */
    public boolean defined(Locale locale, String key);

    /** 
	 * Get the string value.
	 * 
	 * @param locale the locale.
	 * @param key the key.
	 * @return the string value.
	 */
	public String get(Locale locale, String key);
	
    /** 
     * Get the string value with given default value if the string is missing in
     * both given and default locale.
     * 
     * @param locale the locale.
     * @param key the key.
     * @param defaultValue the default value in case key mapping is missing.
     * @return the string value.
     */
    public String get(Locale locale, String key, String defaultValue);

    /**
	 * Get the string and replace $[1..n] variables with given values.
	 *
	 * @param locale the locale.
	 * @param key the key.
	 * @param values the values use for substitution.
	 * @return the the output string.
	 */
	public String get(Locale locale, String key, String[] values);

    /**
	 * Get the string and replace $[1..n] variables with given values.
	 *
	 * @param locale the locale.
	 * @param key the key.
	 * @param values the values use for substitution.
	 * @return the the output string.
	 */
	public String get(Locale locale, String key, List values);	
	
	/**
	 * Reload the localization.
	 * 
	 */
	public void reload();
}
