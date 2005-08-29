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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.utils.StringUtils;

/**
 * The number formater component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * 
 * @version $Id: NumberFormatter.java,v 1.1 2005-08-29 20:28:13 rafal Exp $
 */
public class NumberFormatter
{
	/** The logger. */
	protected Logger logger;

	/** The locale map. */
	protected Map localeMap;
	
	/** The default locale patterns. */
	protected Map defaultPatterns;
	
	/**
	 * Component constructor.
	 *
	 * @param config the configuration.
	 * @param logger the logger. 
     * @throws ConfigurationException if the component configuration is malformed.
	 */
	public NumberFormatter(Configuration config, Logger logger)
		throws ConfigurationException
	{
		this.logger = logger;
		localeMap = new HashMap();
		defaultPatterns = new HashMap();
        Configuration[] locales = config.getChildren("locale");
        for (int i = 0; i < locales.length; i++)
        {
            String name = locales[i].getAttribute("name");
            String defaultPattern = locales[i].getAttribute("defaultPattern");
            Configuration[] patterns = locales[i].getChildren("pattern");
            Locale locale = StringUtils.getLocale(name);
            Map map = new HashMap();
            localeMap.put(locale, map);
            defaultPatterns.put(locale, defaultPattern);
            for (int j = 0; j < patterns.length; j++)
            {
                String patternName = patterns[j].getAttribute("name");
                String patternValue = patterns[j].getAttribute("value", null);
                if (patternValue == null)
                {
                    patternValue = patterns[j].getValue();
                }
                map.put(patternName, patternValue);
            }
        }
	}
	
	/**
	 * Get the number format based on defined pattern.
	 * 
	 * @param pattern the pattern.
	 * @param locale the locale.
	 * @return the number format object.
	 */
	public NumberFormat getNumberFormat(String pattern, Locale locale)
	{
	    Map patterns = (Map)localeMap.get(locale);
	    if(patterns == null)
	    {
	        return null;
	    }
        String patternValue =  (String)patterns.get(pattern);
        if(patternValue == null)
        {
            return null;
        }
        return new DecimalFormat(patternValue, new DecimalFormatSymbols(locale));
	}
	
 
    
	/**
	 * Get the default number format for locale.
	 * 
	 * @param locale the locale.
	 * @return the number format object.
	 */
	public NumberFormat getNumberFormat(Locale locale)
	{
        return getNumberFormat(getDefaultPattern(locale), locale);
	}	
	
	/**
	 * Get the default pattern for locale. 
	 * 
	 * @param locale the locale.
	 * @return the default pattern name.
	 */
	public String getDefaultPattern(Locale locale)
	{
	    return (String)defaultPatterns.get(locale);
	}
}
