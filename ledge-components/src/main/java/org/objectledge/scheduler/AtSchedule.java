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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * A schedule that allows you to execute jobs at specific moments of time.
 *
 * <p>The configuration of the schedule is expected to be semicolon separated
 * list of dates. Each date must adhere to the pattern used by the
 * Scheduler (see {@link AbstractScheduler#DATE_FROMAT_DEFAULT}).</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AtSchedule.java,v 1.1 2004-01-30 14:53:20 pablo Exp $
 */
public class AtSchedule
    implements Schedule
{
    // constants /////////////////////////////////////////////////////////////

    /** The schedule type. */
    public static final String TYPE = "at";

    // instance variables ////////////////////////////////////////////////////

    /** Scheduler */
    private AbstractScheduler scheduler;

    /** Stored configuration. */
    private String config;
    
    /** Parsed configuration. */
    private Date[] runTime;

    /**
     * package private constructor.
     */
    AtSchedule()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void init(AbstractScheduler scheduler, String config)
        throws InvalidScheduleException
    {
        this.scheduler = scheduler;
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
        this.config = config;
        
        StringTokenizer st = new StringTokenizer(config, ";");
        ArrayList temp = new ArrayList();
        try
        {
            while(st.hasMoreTokens())
            {
                temp.add(scheduler.getDateFormat().parse(st.nextToken()));
            }
        }
        catch(ParseException e)
        {
            throw new InvalidScheduleException("invalid date format", e);
        }
        Collections.sort(temp);
        runTime = new Date[temp.size()];
        temp.toArray(runTime);
    }

    /**
     * {@inheritDoc}
     */
    public boolean atStartup()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Date getNextRunTime(Date currentTime, Date lastRunTime)
    {
        Date next = null;
        for(int i=0; i<runTime.length; i++)
        {
            if(currentTime.compareTo(runTime[i]) < 0)
            {
                next = runTime[i];
                break;
            }
        }
        return next;
    }
}
