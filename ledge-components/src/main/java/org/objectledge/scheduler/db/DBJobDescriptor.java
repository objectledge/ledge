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

package org.objectledge.scheduler.db;

import java.sql.SQLException;

import org.objectledge.database.persistence.InputRecord;
import org.objectledge.database.persistence.OutputRecord;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.database.persistence.Persistent;
import org.objectledge.scheduler.AbstractJobDescriptor;
import org.objectledge.scheduler.AbstractScheduler;
import org.objectledge.scheduler.InvalidScheduleException;
import org.objectledge.scheduler.JobModificationException;
import org.objectledge.scheduler.Schedule;

/**
 * Persistent scheduled job descriptor based on database.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class DBJobDescriptor extends AbstractJobDescriptor
    implements Persistent
{
    // constants /////////////////////////////////////////////////////////////

    /** The table name. */
    public static final String TABLE_NAME = "ledge_scheduler";

    /** The key columns. */
    public static final String[] KEY_COLUMNS = new String[] { "job_id" };

    // instance variables ////////////////////////////////////////////////////

    /** The persistence. */
    private Persistence persistence;
    
    /** The scheduler */
    private AbstractScheduler scheduler;

    /** The job id. */
    private long jobId = -1L;

    /**
     * Constructor.
     * 
     * @param persistence the persitence component.
     * @param scheduler the scheduler component.
     */
    DBJobDescriptor(Persistence persistence, AbstractScheduler scheduler)
    {
        this.persistence = persistence;
        this.scheduler = scheduler;
    }

    // Persistent interface //////////////////////////////////////////////////

    /**
     * Returns the name of the table this type is mapped to.
     *
     * @return the name of the table.
     */
    public String getTable()
    {
        return TABLE_NAME;
    }

    /**
     * Returns the names of the key columns.
     *
     * @return the names of the key columns.
     */
    public String[] getKeyColumns()
    {
        return KEY_COLUMNS;
    }

    /**
     * {@inheritDoc}
     */
    public void getData(OutputRecord record)
        throws SQLException
    {
        record.setLong("job_id", jobId);
        record.setString("job_name", getName());
        record.setString("schedule_type", getSchedule().getType());
        record.setString("schedule_config", getSchedule().getConfig());
        record.setString("job_class_name", getJobClassName());
        if (getArgument() != null)
        {
            record.setString("argument", getArgument());
        }
        else
        {
            record.setNull("argument");
        }
        record.setInteger("run_count", getRunCount());
        record.setInteger("run_count_limit", getRunCountLimit());
        if (getLastRunTime() != null)
        {
            record.setTimestamp("last_run_time", getLastRunTime());
        }
        else
        {
            record.setNull("last_run_time");
        }
        if (getTimeLimitStart() != null)
        {
            record.setTimestamp("run_time_limit_start", getTimeLimitStart());
        }
        else
        {
            record.setNull("run_time_limit_start");
        }
        if (getTimeLimitEnd() != null)
        {
            record.setTimestamp("run_time_limit_end", getTimeLimitEnd());
        }
        else
        {
            record.setNull("run_time_limit_end");
        }
        record.setBoolean("auto_clean", getAutoClean());
        record.setBoolean("reentrant", isReentrant());
        record.setBoolean("enabled", isEnabled());
    }

    /**
     * {@inheritDoc}
     */
    public void setData(InputRecord record)
        throws SQLException
    {
        jobId = record.getLong("job_id");
        String name = record.getString("job_name");
        String scheduleType = record.getString("schedule_type");
        String scheduleConfig = record.getString("schedule_config");
        String jobClassName = record.getString("job_class_name");
        Schedule schedule = null;
        try
        {
            schedule = scheduler.createSchedule(scheduleType, scheduleConfig);
        }
        catch (InvalidScheduleException e)
        {
            throw new SQLException("failed to create schedule", e);
        }
        try
        {
            super.init(name, schedule, jobClassName);
            if (!record.isNull("argument"))
            {
                argument = record.getString("argument");
            }
            if (!record.isNull("run_count"))
            {
                runCount = record.getInteger("run_count");
            }
            if (!record.isNull("run_count_limit"))
            {
                runCountLimit = record.getInteger("run_count_limit");
            }
            if (!record.isNull("last_run_time"))
            {
                lastRunTime = record.getDate("last_run_time");
            }
            if (!record.isNull("run_time_limit_start"))
            {
                runTimeLimitStart = record.getDate("run_time_limit_start");
            }
            if (!record.isNull("run_time_limit_end"))
            {
                runTimeLimitEnd = record.getDate("run_time_limit_end");
            }
            if (!record.isNull("auto_clean"))
            {
                autoClean = record.getBoolean("auto_clean");
            }
            if (!record.isNull("reentrant"))
            {
                reentrant = record.getBoolean("reentrant");
            }
            if (!record.isNull("enabled"))
            {
                enabled = record.getBoolean("enabled");
            }
        }
        catch (Exception e)
        {
            throw new SQLException("Failed to initialize scheduled job", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean getSaved()
    {
        return jobId != -1L;
    }

    /**
     * {@inheritDoc}
     */
    public void setSaved(long id)
    {
        this.jobId = id;
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    protected void saveChanges() throws JobModificationException
    {
        try
        {
            persistence.save(this);
        }
        catch(SQLException e)
        {
            throw new JobModificationException("failed to save job state", e);
        }
    }

}
