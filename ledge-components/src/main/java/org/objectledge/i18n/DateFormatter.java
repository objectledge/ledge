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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 * The date formater component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DateFormatter.java,v 1.6 2006-03-07 17:35:06 zwierzem Exp $
 */
public class DateFormatter extends AbstractFormatter
{
	public DateFormatter(Configuration config, I18n i18n)
        throws ConfigurationException
    {
        super(config, i18n);
    }

    /**
	 * Get the date format based on defined pattern.
	 * 
	 * @param pattern the pattern.
	 * @param locale the locale.
	 * @return the date format object.
	 */
	public DateFormat getDateFormat(String pattern, Locale locale)
	{
        String patternValue =  getPatternValue(pattern, locale);
        if(patternValue == null)
        {
            return null;
        }
	    return new SimpleDateFormat(patternValue, locale);
	}
	
	/**
	 * Get the default date format for locale.
	 * 
	 * @param locale the locale.
	 * @return the date format object.
	 */
	public DateFormat getDateFormat(Locale locale)
	{
        return getDateFormat(getDefaultPattern(locale), locale);
	}
}
