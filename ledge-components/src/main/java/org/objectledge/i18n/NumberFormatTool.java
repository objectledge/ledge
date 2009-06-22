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
import java.util.Locale;

/**
 * The NumberFormat contex tool.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: NumberFormatTool.java,v 1.1 2005-08-29 20:28:13 rafal Exp $
 */
public class NumberFormatTool
{
    /** The number formater. */
    protected NumberFormatter numberFormater;
    
	/** The current locale. */
	protected Locale locale;
    
    protected NumberFormat format;

	/**
	 * Default constructor.
     * 
	 * @param numberFormater the number formatter object.
	 * @param locale the locale.
     * @param format the number format object.
	 */
	public NumberFormatTool(NumberFormatter numberFormater, Locale locale, NumberFormat format)
	{
		this.numberFormater = numberFormater;
		this.locale = locale;
        this.format = format;
	}
	
    // public API ////////////////////////////////////////////////////////////
	
    /**
     * Sets the formatting style.
     *
     * @param patternAlias the pattern name.
     * @return new NumberFormatTool instance.
     * @throws Exception if the specified pattern alias is not configured for this tool.
     */
    public NumberFormatTool style(String patternAlias)
        throws Exception
    {
        NumberFormatTool target = createInstance(this);
        target.format = numberFormater.getNumberFormat(patternAlias, locale);
        if(target.format == null)
        {
            throw new Exception("Pattern alias '"+patternAlias+
                "' not defined in number formatter");
        }
        return target;
    }

    /**
     * Sets the formatting pattern.
     *
     * <p>The syntax of patterns is descirbed in
     * <code>java.text.DecimalFormat</code> documentation.</p>
     *
     * @param pattern the formatting pattern.
     * @return new NumberFormatTool instance.
     */
    public NumberFormatTool pattern(String pattern)
    {
        NumberFormatTool target = createInstance(this);
        target.format = new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
        return target;
    }
	

    /**
     * Formats the Number object.
     *
     * @param date the Date.
     * @return formatted date.
     */
    public String format(Object f)
    {
        if(f == null)
        {
            return null;
        }
        return format.format(f);
    }


	
    // implementation ------------------------------------------------------------------------------

    /**
     * Creates the NumberFormatTool instance for copying. This method is intended to be overriden by
     * extending classes in order to provide FormatFormatTool instances of proper class.
     * 
     * @param source copied object
     * @return created instance of the linktool.
     */
    protected NumberFormatTool createInstance(NumberFormatTool source)
    {
        return new NumberFormatTool(source.numberFormater, source.locale, source.format);
    }
}
