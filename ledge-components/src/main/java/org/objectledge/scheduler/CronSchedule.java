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

package org.objectledge.scheduler;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.objectledge.i18n.I18n;
import org.objectledge.scheduler.cron.CronParser;
import org.objectledge.scheduler.cron.ParseException;
import org.objectledge.scheduler.cron.Token;
import org.objectledge.scheduler.cron.ValueOutOfRangeException;

/**
 * A schedule that follows the configuration syntax of Unix cron utility. 
 *
 * <p>The configuration grammar:</p>
 * <pre>
 * schedule_config := ( ( minute hour day month weekday ) | special )
 * special := ( "@reboot" | "@yearly" | "@annually" | "@monthly" | "@weekly" |
 *   "@daily" | "@midnight" | "@hourly" )
 * minute := value_spec
 * hour := value_spec
 * day := value_spec
 * month := ( value_spec | month_name )
 * weekday := ( value_spec | weekday_name )
 * month_name := ( "jan" | "feb" | "mar" | "apr" | "may" | "jun" |
 *   "jul" | "aug" | "sep" | "oct" | "nov" | "dec" )
 * weekday_name := ( "mon" | "tue" | "wed" | "thu" | "fri" | "sat" | "sun" )
 * value_spec := ( "*" | list_of_values )
 * list_of_values := ( value_or_range ( "," value_or_range )* )
 * value_or_range := ( number | range )
 * range := number "-" number [ "/" number ]
 * number := LEXICAL( [0-9]+ )
 * </pre>
 *
 * <p>Configuration syntax is compatible with Paul Vixie's <a
 * href="ftp://ftp.vix.com/pub/vixie/cron-3.0">cron</a>, version 3.0.</p> 
 */
public class CronSchedule
    implements Schedule
{
    // constants /////////////////////////////////////////////////////////////

    /** index of minute field. */
    private static final int MINUTE = 0;
    
    /** index of hour field. */
    private static final int HOUR_OF_DAY = 1;
    
    /** index of day field. */
    private static final int DAY_OF_MONTH = 2;
    
    /** index of month field. */
    private static final int MONTH = 3;
    
    /** index of weekday field. */
    private static final int DAY_OF_WEEK = 4;

    /** The schedule type. */
    public static final String TYPE = "cron";

    // instance variables ////////////////////////////////////////////////////
    
    /** The textual configuration form. */
    private String config;

    /** The parserd schedule data. */
    private int[][] schedule;
    
    /** I18n component */
    private I18n i18n;
    
    /**
     * Constructor.
     * 
     * @param i18n the i18n component.
     */
    public CronSchedule(I18n i18n)
    {
        this.i18n = i18n;
    }
    
    /**
     * {@inheritDoc}
     */
    public void init(AbstractScheduler scheduler, String config)
        throws InvalidScheduleException
    {
        setConfig(config);
    }

    /**
     * {@inheritDoc}
     */
    public String getType()
    {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public String getConfig()
    {
        return config;
    }

    /**
     * {@inheritDoc}
     */
    public void setConfig(String config)
        throws InvalidScheduleException
    {
        //TODO Add Polling here...
        StringReader sr = new StringReader("");
        CronParser parser = new CronParser(sr);
        try
        {
            this.schedule = parser.parse(config);
            this.config = config;
        }
        catch(ParseException e)
        {
            throw new InvalidScheduleException(localizeParseException(e), e);
        }
        catch(ValueOutOfRangeException e)
        {
            throw new InvalidScheduleException(localizeValueOutOrRangeException(e), e);
        }
        //TODO recycle parser
    }

    /**
     * {@inheritDoc}
     */
    public boolean atStartup()
    {
        return (schedule.length == 0);
    }

    /**
     * {@inheritDoc}
     */
    public Date getNextRunTime(Date currentTime, Date lastRunTime)
    {
        if(schedule.length == 0)
        {
            return null;
        }
        else
        {
            GregorianCalendar nextRun = new GregorianCalendar();
            nextRun.setTime(currentTime);
            // skip to next full minute
            nextRun.set(Calendar.MILLISECOND, 0);
            nextRun.set(Calendar.SECOND, 0);
            nextRun.add(Calendar.MINUTE, 1);

            computeDay(nextRun);
            computeHour(nextRun);
            computeMinute(nextRun);
                        
            return nextRun.getTime();
        }
    }

    // implementation ////////////////////////////////////////////////////////

    private void computeMonth(Calendar nextRunDOM)
    {
        int i;
        if(schedule[MONTH].length != 0)
        {
            loop: for(i=0; i<schedule[MONTH].length; i++)
            {
                if(schedule[MONTH][i] >= nextRunDOM.get(Calendar.MONTH))
                {
                    break loop;
                }
            }
            if(i == schedule[MONTH].length)
            {
                nextRunDOM.add(Calendar.YEAR, 1);
                nextRunDOM.set(Calendar.MONTH, schedule[MONTH][0]);
                nextRunDOM.set(Calendar.DAY_OF_MONTH, 0);
                nextRunDOM.set(Calendar.HOUR_OF_DAY, 0);
                nextRunDOM.set(Calendar.MINUTE, 0);
            }
            else
            {
                if(schedule[MONTH][i] > nextRunDOM.get(Calendar.MONTH))
                {
                    nextRunDOM.set(Calendar.MONTH, schedule[MONTH][i]);
                    nextRunDOM.set(Calendar.DAY_OF_MONTH, 0);
                    nextRunDOM.set(Calendar.HOUR_OF_DAY, 0);
                    nextRunDOM.set(Calendar.MINUTE, 0);
                }
            }
        }        
    }

    private void computeDay(Calendar nextRun)
    {
        int i;
        GregorianCalendar nextRunDOM = new GregorianCalendar();
        nextRunDOM.setTime(nextRun.getTime());

        computeMonth(nextRunDOM);
        
        // day of month
        if(schedule[DAY_OF_MONTH].length != 0)
        {
            loop: for(i=0; i<schedule[DAY_OF_MONTH].length; i++)
            {
                if(schedule[DAY_OF_MONTH][i] >= nextRunDOM.get(Calendar.DAY_OF_MONTH))
                {
                    break loop;
                }
            }
            if(i == schedule[DAY_OF_MONTH].length)
            {
                nextRunDOM.add(Calendar.MONTH, 1);
                computeMonth(nextRunDOM);
                // FIXME day > month length
                nextRunDOM.set(Calendar.DAY_OF_MONTH, schedule[DAY_OF_MONTH][0]);
                nextRunDOM.set(Calendar.HOUR_OF_DAY, 0);
                nextRunDOM.set(Calendar.MINUTE, 0);
            }
            else
            {
                if(schedule[DAY_OF_MONTH][i] > nextRunDOM.get(Calendar.DAY_OF_MONTH))
                {
                    nextRunDOM.set(Calendar.DAY_OF_MONTH, schedule[DAY_OF_MONTH][i]);
                    // FIXME day > month length
                    nextRunDOM.set(Calendar.HOUR_OF_DAY, 0);
                    nextRunDOM.set(Calendar.MINUTE, 0);
                }
            }
        }

        GregorianCalendar nextRunDOW = new GregorianCalendar();
        nextRunDOW.setTime(nextRun.getTime());
        // day of week
        if(schedule[DAY_OF_WEEK].length != 0)
        {
            loop: for(i=0; i<schedule[DAY_OF_WEEK].length; i++)
            {
                if(schedule[DAY_OF_WEEK][i] >= nextRunDOW.get(Calendar.DAY_OF_WEEK))
                {
                    break loop;
                }
            }
            if(i == schedule[DAY_OF_WEEK].length)
            {
                nextRunDOW.add(Calendar.WEEK_OF_YEAR, 1);
                nextRunDOW.set(Calendar.DAY_OF_WEEK, schedule[DAY_OF_WEEK][0]);
                nextRunDOW.set(Calendar.HOUR_OF_DAY, 0);
                nextRunDOW.set(Calendar.MINUTE, 0);
            }
            else
            {
                if(schedule[DAY_OF_WEEK][i] > nextRunDOW.get(Calendar.DAY_OF_WEEK))
                {
                    if(nextRunDOW.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY &&
                       nextRunDOW.getFirstDayOfWeek() != Calendar.SUNDAY)
                    {
                        nextRunDOW.add(Calendar.WEEK_OF_YEAR, 1);
                    }   
                    nextRunDOW.set(Calendar.DAY_OF_WEEK, schedule[DAY_OF_WEEK][i]);
                    nextRunDOW.set(Calendar.HOUR_OF_DAY, 0);
                    nextRunDOW.set(Calendar.MINUTE, 0);
                }
            }
        }
        
        if(schedule[DAY_OF_WEEK].length == 0)
        {
            nextRun.setTime(nextRunDOM.getTime());
        }
        else
        {
            if(schedule[DAY_OF_MONTH].length == 0 && schedule[MONTH].length == 0)
            {
                nextRun.setTime(nextRunDOW.getTime());
            }
            else
            {
                if(nextRunDOM.getTime().compareTo(nextRunDOW.getTime()) < 0)
                {
                    nextRun.setTime(nextRunDOM.getTime());
                }
                else
                {
                    nextRun.setTime(nextRunDOW.getTime());
                }
            }    
        }
    }

    private void computeHour(Calendar nextRun)
    {
        int i;
        if(schedule[HOUR_OF_DAY].length != 0)
        {
            loop: for(i=0; i<schedule[HOUR_OF_DAY].length; i++)
            {
                if(schedule[HOUR_OF_DAY][i] >= nextRun.get(Calendar.HOUR_OF_DAY))
                {
                    break loop;
                }
            }
            if(i == schedule[HOUR_OF_DAY].length)
            {
                nextRun.add(Calendar.DAY_OF_MONTH, 1);
                computeDay(nextRun);
                nextRun.set(Calendar.HOUR_OF_DAY, schedule[HOUR_OF_DAY][0]);
                nextRun.set(Calendar.MINUTE, 0);
            }
            else
            {
                if(schedule[HOUR_OF_DAY][i] > nextRun.get(Calendar.HOUR_OF_DAY))
                {
                    nextRun.set(Calendar.MINUTE, 0);
                    nextRun.set(Calendar.HOUR_OF_DAY, schedule[HOUR_OF_DAY][i]);
                }
            }
        }
    }

    private void computeMinute(Calendar nextRun)
    {
        int i;
        if(schedule[MINUTE].length != 0)
        {
            loop: for(i=0; i<schedule[MINUTE].length; i++)
            {
                if(schedule[MINUTE][i] >= nextRun.get(Calendar.MINUTE))
                {
                    break loop;
                }
            }
            if(i == schedule[MINUTE].length)
            {
                nextRun.add(Calendar.HOUR_OF_DAY, 1);
                computeHour(nextRun);
                nextRun.set(Calendar.MINUTE, schedule[MINUTE][0]);
            }
            else
            {
                if(schedule[MINUTE][i] > nextRun.get(Calendar.MINUTE))
                {
                    nextRun.set(Calendar.MINUTE, schedule[MINUTE][i]);
                }
            }
        }
    }

    /**
     * The end of line string for this machine.
     */
    protected String eol = System.getProperty("line.separator", "\n");

    /**
     * Create a localized message out of ParseException object.
     *
     * @param ex the exception.
     */
    private String localizeParseException(ParseException ex)
    {
        String expected = "";
        int maxSize = 0;
        for (int i = 0; i < ex.expectedTokenSequences.length; i++) 
        {
            if (maxSize < ex.expectedTokenSequences[i].length) 
            {
                maxSize = ex.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < ex.expectedTokenSequences[i].length; j++) 
            {
                expected += ex.tokenImage[ex.expectedTokenSequences[i][j]] + " ";
            }
            if (ex.expectedTokenSequences[i][ex.expectedTokenSequences[i].length - 1] != 0) 
            {
                expected += "...";
            }
            expected += eol + "    ";
        }

        String encountered = "";
        Token tok = ex.currentToken.next;
        for (int i = 0; i < maxSize; i++) 
        {
            if (i != 0) encountered += " ";
            if (tok.kind == 0) 
            {
                encountered += ex.tokenImage[0];
                break;
            }
            encountered += add_escapes(tok.image);
            tok = tok.next; 
        }

        String[] strings = new String[] { 
            encountered, 
            Integer.toString(ex.currentToken.next.beginColumn), 
            expected 
        };
                
        String pattern;
        if(ex.expectedTokenSequences.length == 1)
        {
            pattern = "ledge.scheduler.cron.parseOne";
        }
        else
        {
            pattern = "ledge.scheduler.cron.parseMany";
        }
        return i18n.get(i18n.getDefaultLocale(), pattern, strings);
    }

    /**
     * Create a localized message out of ParseException object.
     *
     * @param ex the exception.
     */
    private String localizeValueOutOrRangeException(ValueOutOfRangeException ex)
    {
        String[] strings = new String[] { 
            Integer.toString(ex.token.beginColumn), 
            Integer.toString(ex.value),
            Integer.toString(ex.min),
            Integer.toString(ex.max)
        };
        String pattern = "ledge.scheduler.cron.outOfRange";
        return i18n.get(i18n.getDefaultLocale(), pattern, strings);
    }

    /**
     * Used to convert raw characters to their escaped version
     * when these raw version cannot be used as part of an ASCII
     * string literal.
     *
     * <p>This method was copied from JavaCC generated code.</p>
     *
     * @author JavaCC team
     */
    protected String add_escapes(String str) 
    {
        StringBuffer retval = new StringBuffer();
        char ch;
        for (int i = 0; i < str.length(); i++) 
        {
            switch (str.charAt(i))
            {
            case 0 :
                continue;
            case '\b':
                retval.append("\\b");
                continue;
            case '\t':
                retval.append("\\t");
                continue;
            case '\n':
                retval.append("\\n");
                continue;
            case '\f':
                retval.append("\\f");
                continue;
            case '\r':
                retval.append("\\r");
                continue;
            case '\"':
                retval.append("\\\"");
                continue;
            case '\'':
                retval.append("\\\'");
                continue;
            case '\\':
                retval.append("\\\\");
                continue;
            default:
                if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) 
                {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                } 
                else 
                {
                    retval.append(ch);
                }
                continue;
            }
        }
        return retval.toString();
    }
}
