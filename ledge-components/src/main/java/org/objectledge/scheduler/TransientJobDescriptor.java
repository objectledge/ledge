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
import java.util.Date;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 * A concrete ScheduledJob implemenation suitable for transient, configuration
 * file driven scheduler implementation.
 */
public class TransientJobDescriptor
    extends AbstractJobDescriptor
{
    /**
     * {@inheritDoc}
     */
    public synchronized void setSchedule(Schedule schedule)
        throws JobModificationException
    {
        unsupported();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setJobSpec(String jobSpec)
        throws JobModificationException
    {
        unsupported();
    }

    /**
     * {@inheritDoc}
     */
    public void setArgument(String argument)
        throws JobModificationException
    {
        unsupported();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setRunCountLimit(int limit)
        throws JobModificationException
    {
        unsupported();
    }

    /**
     * {@inheritDoc}
     */
    public void setTimeLimit(Date start, Date end)
        throws JobModificationException
    {
        unsupported();
    }

    /**
     * {@inheritDoc}
     */
    public void setAutoClean(boolean autoClean)
        throws JobModificationException
    {
        unsupported();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setReentrant(boolean reentrant)
        throws JobModificationException
    {
        unsupported();
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * Initializes the job object.
     *
     * @param scheduler the scheduler.
     * @param name the scheduled job name.
     * @param config the configuration.
     * @throws JobModificationException if the job state could not be saved.
     * @throws InvalidScheduleException if schedule type factore was not registered.
     */
    void init(AbstractScheduler scheduler, String name, Configuration config)
        throws JobModificationException, InvalidScheduleException, 
                ConfigurationException, ParseException
    {
        String scheduleType = config.getAttribute("scheduleType");
        String scheduleConfig = config.getAttribute("scheduleConfig");
        String jobClassName = config.getAttribute("jobClassName");
        Schedule schedule = scheduler.createSchedule(scheduleType, scheduleConfig);
        super.init(name, schedule, jobClassName);

        if(config.getAttribute("argument","").length() > 0)
        {
            super.setArgument(config.getAttribute("argument"));
        }
        if(config.getAttribute("runCountLimit").length() > 0)
        {
            super.setRunCountLimit(config.getAttributeAsInteger("runCountLimit"));
        }
        if(config.getAttribute("runTimeLimit").length() > 0)
        {
            String range = config.getAttribute("runTimeLimit");
            int pos = range.indexOf("::");
            if(pos < 0 || pos == range.length() - 2)
            {
                throw new IllegalArgumentException("malformed runTimeLimit, "+
                                                   "'[start]::[end]' expected");
            }
            Date startDate = null;
            String start = range.substring(0,pos).trim();
            if(!start.equals(""))
            {
                startDate = scheduler.getDateFormat().parse(start);
            }
            Date endDate = null;
            String end = range.substring(pos+2).trim();
            if(!end.equals(""))
            {
                endDate = scheduler.getDateFormat().parse(end);
            }
            super.setTimeLimit(startDate, endDate);
        }
        if(config.getAttribute("rentrant").length() > 0)
        {
            super.setReentrant(config.getAttributeAsBoolean("reentrant"));
        }
        setEnabled(true);
    }

    /**
     * Updates the persistent state.
     * 
     * @throws JobModificationException if the job state could not be saved.
     */
    protected void saveChanges()
        throws JobModificationException
    {
        // do nothing
    }

    /**
     * Throws an exception to indicate that the operation is not supported.
     */
    private void unsupported()
        throws JobModificationException
    {
        throw new JobModificationException("transient scheduled jobs cannot be modified");
    }
}
