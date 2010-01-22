// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Date formatter component.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DateFormatter.java,v 1.3 2008-01-08 19:36:12 rafal Exp $
 */
public class DateFormatter
{
    final private String pattern;
    
    final private Locale locale;    
    
    final private TimeZone timeZone;

    private ObjectPool dateFormatPool = new GenericObjectPool(new DateFormatFactory());    
    
    /**
     * Constructs an instance of the date formatter.
     * 
     * @param pattern the formatting pattern.
     * @param locale the locale name.
     */    
    public DateFormatter(String pattern, String locale)
    {
        this.pattern = pattern;
        this.locale = StringUtils.getLocale(locale);
        this.timeZone = TimeZone.getDefault();
    }

    /**
     * Constructs an instance of the date formatter.
     * 
     * @param pattern the formatting pattern.
     * @param locale the locale name.
     */    
    public DateFormatter(String pattern, String locale, String timeZone)
    {
        this.pattern = pattern;
        this.locale = StringUtils.getLocale(locale);
        this.timeZone = TimeZone.getTimeZone(timeZone);
    }

    /**
     * Format date to string according to defined pattern.  
     * 
     * @param date the date.
     * @return the string representation of date.
     */
    public String format(Date date)
    {
        DateFormat df = getDateFormat();
        try
        { 
            return df.format(date);
        }
        finally
        {
            releaseDateForamt(df);
        }
    }
    
    /**
     * Parse date from string.  
     * 
     * @param source the string representation of date.
     * @return the date.
     * @throws ParseException if string format is invalid.
     */
    public Date parse(String source)
        throws ParseException
    {
        DateFormat df = getDateFormat();
        try
        { 
            return df.parse(source);
        }
        finally
        {
            releaseDateForamt(df);
        }
    }    

    /**
     * Acquired DateFormat object from the pool.
     * 
     * @return DateFormat object.
     */    
    private DateFormat getDateFormat()
    {
        try
        {
            return (DateFormat)dateFormatPool.borrowObject();
        }
        ///CLOVER:OFF
        catch(Exception e)
        {
            throw (RuntimeException)new IllegalStateException("unexpected object pool failure").
                initCause(e);
        }
        ///CLOVER:ON
    }

    /**
     * Releases DateFormat object into the pool.
     * 
     * @param format DateFormat object.
     */    
    private void releaseDateForamt(DateFormat format)
    {
        try
        {
            dateFormatPool.returnObject(format);        
        }
        ///CLOVER:OFF
        catch(Exception e)
        {   
            throw (RuntimeException)new IllegalStateException("unexpected object pool failure").
                initCause(e);
        }
        ///CLOVER:ON
    }
    
    /**
     * A factory of DateFormat objects.
     */
    private class DateFormatFactory
        extends BasePoolableObjectFactory
    {
        /**
         * {@inheritDoc}
         */
        public Object makeObject() throws Exception
        {
            DateFormat df = new SimpleDateFormat(pattern, locale);
            df.setTimeZone(timeZone);
            return df;
        }
    }
}

