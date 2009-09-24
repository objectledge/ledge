// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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

package org.objectledge.scheduler;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A schedule that allows running jobs periodicaly, at specified intervals.
 * 
 * The interval should be specified as an integer followed by one of: s,m,h or d, specifying
 * seconds, minutes, hours or days respectively.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: IntervalSchedule.java,v 1.1 2006-04-21 12:08:09 rafal Exp $
 */
public class IntervalSchedule
    implements Schedule
{
    public static final String TYPE = "interval";
    private static final Pattern PATTERN = Pattern.compile("(\\d+) ([smhd])");
    
    private String config;
    private long interval; 
    
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
        this.config = config;
        Matcher matcher = PATTERN.matcher(config);
        if(matcher.matches())
        {
            interval = Long.parseLong(matcher.group(1));
            switch(matcher.group(2).charAt(0))
            {
            case 'd':
                interval *= 24;
                // $FALL-THROUGH$
            case 'h':
                interval *= 60;
                // $FALL-THROUGH$
            case 'm':                
                interval *= 60;
                // $FALL-THROUGH$
            case 's':
                interval *= 1000;
            }
        }
        else
        {
            throw new InvalidScheduleException(config+" does not match regexp " + PATTERN);
        }
    }
    
    long getInterval()
    {
        return interval;
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
        if(lastRunTime != null)
        {
            return new Date(lastRunTime.getTime() + interval);
        }
        else
        {
            return new Date(currentTime.getTime() + interval);            
        }
    }
}
