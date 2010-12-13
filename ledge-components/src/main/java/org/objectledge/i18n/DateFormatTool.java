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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The DateFormat contex tool.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DateFormatTool.java,v 1.8 2005-03-22 11:11:35 pablo Exp $
 */
public class DateFormatTool
{
    /** The date formater. */
    protected DateFormatter dateFormater;
    
    /** The current locale. */
    protected Locale locale;

    /** The DateFormat in use. */
    protected DateFormat format;

    /**
     * Default constructor.
     * 
     * @param dateFormater the date formatter object.
     * @param locale the locale.
     * @param format the date format object.
     */
    public DateFormatTool(DateFormatter dateFormater, Locale locale, DateFormat format)
    {
        this.dateFormater = dateFormater;
        this.locale = locale;
        this.format = format;
    }
    
    // public API ////////////////////////////////////////////////////////////
    
    /**
     * Sets the formatting style.
     *
     * @param patternAlias the pattern name.
     * @return new DateFormatTool instance.
     * @throws Exception if the specified pattern alias is not configured for this tool.
     */
    public DateFormatTool style(String patternAlias)
        throws Exception
    {
        DateFormatTool target = createInstance(this);
        target.format = dateFormater.getDateFormat(patternAlias, locale);
        if(target.format == null)
        {
            throw new Exception("Pattern alias '"+patternAlias+
                "' not defined in date formatter");
        }
        return target;
    }

    /**
     * Sets the formatting pattern.
     *
     * <p>The syntax of patterns is descirbed in
     * <code>java.text.SimpleDateFormat</code> documentation.</p>
     *
     * @param pattern the formatting pattern.
     * @return new DateFormatTool instance.
     */
    public DateFormatTool pattern(String pattern)
    {
        DateFormatTool target = createInstance(this);
        target.format = new SimpleDateFormat(pattern, locale);
        return target;
    }
    
    /**
     * Returns the current date and time.
     *
     * @return the current date and time.
     */
    public Date now()
    {
        return new Date();
    }

    /**
     * Returns a Date object for a specified UNIX time.
     *
     * @param time the UNIX time.
     * @return a Date object for the specified time.
     */
    public Date getDate(long time)
    {
        return new Date(time);
    }

    /**
     * Formats the Date object.
     *
     * @param date the Date.
     * @return formatted date.
     */
    public String format(Date date)
    {
        if(date == null)
        {
            return null;
        }
        return format.format(date);
    }

    /**
     * Formats the date given as Unix time.
     *
     * @param date the date given as Unix time.
     * @return formatted date.
     */
    public String format(long date)
    {
        return format(new Date(date));
    }

    /**
     * Returns fields of the specified date using local time zone.
     *
     * <p>The field order is as follows:
     *  <ul>
     *   <li>year</li>
     *   <li>month (zero based)</li>
     *   <li>day</li>
     *   <li>hour of day (24h)</li>
     *   <li>minute</li>
     *   <li>second</li>
     *   <li>millisecond</li>
     *  </ul>
     * </p>
     * @param date the date to split.
     * @return list of date fields, as described above.
     */
    public List<Integer> getFields(Date date)
    {
        return getFields(date, TimeZone.getDefault());
    }

    /**
     * Returns fields of the specified date using specified time zone.
     *
     * <p>The field order is as follows:
     *  <ul>
     *   <li>year</li>
     *   <li>month (zero based)</li>
     *   <li>day</li>
     *   <li>hour of day (24h)</li>
     *   <li>minute</li>
     *   <li>second</li>
     *   <li>millisecond</li>
     *  </ul>
     * </p>
     * @param date the date to split.
     * @param zone the timezone to use.
     * @return list of date fields, as described above.
     */
    public List<Integer> getFields(Date date, TimeZone zone)
    {
        ArrayList<Integer> list = new ArrayList<Integer>(7);
        if(date == null)
        {
            return list; 
        }
        Calendar cal = Calendar.getInstance(zone);
        cal.setTime(date);
        list.add(new Integer(cal.get(Calendar.YEAR)));
        list.add(new Integer(cal.get(Calendar.MONTH)));
        list.add(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
        list.add(new Integer(cal.get(Calendar.HOUR_OF_DAY)));
        list.add(new Integer(cal.get(Calendar.MINUTE)));
        list.add(new Integer(cal.get(Calendar.SECOND)));
        list.add(new Integer(cal.get(Calendar.MILLISECOND)));
        return list;
    }

    /**
     * Returns the time zone object with the specified id.
     * 
     * @param id time zone identifier string.
     * @return a TimeZone object.
     */
    public TimeZone getTimeZone(String id)
    {
        return TimeZone.getTimeZone(id);
    }

    /**
     * Returns the available time zone ids.
     * 
     * @return list of recognized time zone ids.
     */
    public List<String> getTimeZoneIds()
    {
        return Arrays.asList(TimeZone.getAvailableIDs());
    }
    
    /**
     * Parse a date from string according to the active format.
     * <p>
     * This is useful for embedding date constants in templates: <code>#set($date = $format_date.pattern('yyyy-MM-dd').parse('2010-06-28'))</code>
     * </p>
     * 
     * @param stringDate a date in string form. String must conform to the active pattern. 
     * @return Date object or null if the string is malformed.
     */
    public Date parse(String stringDate)
    {
        try
        {
            return format.parse(stringDate);
        }
        catch(ParseException e)
        {
            return null;
        }
    }

    // implementation ------------------------------------------------------------------------------

    /**
     * Creates the DateFormatTool instance for copying. This method is intended to be overriden by
     * extending classes in order to provide DateFormatTool instances of proper class.
     * 
     * @param source copied object
     * @return created instance of the linktool.
     */
    protected DateFormatTool createInstance(DateFormatTool source)
    {
        return new DateFormatTool(source.dateFormater, source.locale, source.format);
    }
}
