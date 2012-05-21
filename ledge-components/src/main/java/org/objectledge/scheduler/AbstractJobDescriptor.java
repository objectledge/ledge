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

import java.util.Date;

/**
 * Describes a <code>Job</code> that should be run periodicaly by the system.
 *
 * <p>Properties of a shcheduled job:</p>
 * <ul>
 *   <li>name: string 1) 2)</li>
 *   <li>scheduleType: string 1) 2)</li>
 *   <li>scheduleConfig: string 1) 2)</li>
 *   <li>jobClass: string 1) 2)</li>
 *   <li>argument: string 1)</li>
 *   <li>runCount: int</li>
 *   <li>runCountLimit: int 1)</li>
 *   <li>lastRunTime:d ate</li>
 *   <li>runTimeLimitStart: date 1)</li>
 *   <li>runTimeLimitEnd: date 1)</li>
 *   <li>reentrant: boolean 1)</li>
 *   <li>enabled: boolean</li>
 * </ul>
 * <p> 1) can be specified in the config file driven implementation </p>
 * <p> 2) required property </p>
 */
public abstract class AbstractJobDescriptor
{
    // instance variables ////////////////////////////////////////////////////

    /** The scheduled job's name. */
    protected String name;

    /** The job's associated Schedule. */
    protected Schedule schedule;

    /** The execution argument. */
    protected String argument;
    
    /** The scheduled job's Job class name. */
    protected String jobClassName;
    
    /** The job's execution count to date. */
    protected int runCount;

    /** The job's execution count limit. */
    protected int runCountLimit;
    
    /** The job's last run time. */
    protected Date lastRunTime;
    
    /** The lower limit of job's execution period. */
    protected Date runTimeLimitStart;
    
    /** The upper limit of job's execution period. */
    protected Date runTimeLimitEnd;

    /** The job's reentrantness flag. */
    protected boolean reentrant;
    
    /** The job's currently running flag. */
    protected boolean running;

    /** The job's auto-clean flag. */
    protected boolean autoClean;
    
    /** The job's enabled flag. */
    protected boolean enabled;

    /**
     * Returns the name of the job.
     *
     * @return the name of the job.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Returns the associated schedule.
     *
     * @return the associated schedule.
     */    
    public Schedule getSchedule()
    {
        return schedule;    
    }    

    /**
     * Changes the shedule schedule.
     *
     * @param schedule the new schedule.
     * @throws JobModificationException if the job state could not be saved.
     */
    public void setSchedule(Schedule schedule)
        throws JobModificationException
    {
        this.schedule = schedule;
        saveChanges();
    }

    /**
     * Returns the job class name.
     *
     * 
     * @return the job class name.
     */
    public String getJobClassName()
    {
        return jobClassName;
    }

    /**
     * Changes the job specification.
     *
     * @param jobClassName the job class name.
     * @throws JobModificationException if the job state could not be saved. 
     */
    public void setJobClassName(String jobClassName)
        throws JobModificationException
    {
        this.jobClassName = jobClassName;
        saveChanges();
    }
    
    /**
     * Returns the job execution argument.
     *
     * <p>You can use the execution argument to specify the unit of work that
     * the scheduled job should perform. You can have multiple parallel
     * instanes of a Job class perfroming different units of work.</p>
     *
     * @return the execution argument, or <code>null</code> if undefined.
     */
    public String getArgument()
    {
        return argument;
    }

    /**
     * Sets the job execution argument.
     *
     * <p>You can use the execution argument to specify the unit of work that
     * the scheduled job should perform. You can have multiple parallel
     * instanes of a Job class perfroming different units of work.</p>
     *
     * @param argument the execution argument, or <code>null</code> if undefined.
     * @throws JobModificationException if the job state could not be saved. 
     */
    public void setArgument(String argument)
        throws JobModificationException
    {
        this.argument = argument;
        saveChanges();
    }

    /**
     * Returns the count of job executions to date.
     *
     * @return the coutn of job executions to date.
     */
    public int getRunCount()
    {
        return runCount;
    }

    /**
     * Returns the maximum number of job executions.
     *
     * <p>When no limit is set, -1 will be returned. Newly created <code>
     * ScheduledJobs</code> have no limit set.
     *
     * @return the maximum number of job executions, or -1 if disabled.
     */
    public int getRunCountLimit()
    {
        return runCountLimit;
    }
    
    /**
     * Sets the maximum number of job executions.
     *
     * <p>Use value of -1 to disable the limit.
     *
     * @param limit the maximum number of job executions, or -1 if disabled.
     * @throws JobModificationException if the job state could not be saved. 
     */
    public void setRunCountLimit(int limit)
        throws JobModificationException
    {
        this.runCountLimit = limit;
        saveChanges();
    }

    /**
     * Return the job's last execution time.
     *
     * @return the job's last execution time, or <code>null</code> if unknown.
     */
    public Date getLastRunTime()
    {
        return lastRunTime;
    }

    /**
     * Returns lower limit of job execution period.
     *
     * <p>The job will not be run before the time returned. Newly created jobs
     * have no limit set.</p>
     *
     * @return lower limit of job execution period, or <code>null</code> if 
     *         disabled.
     */  
    public Date getTimeLimitStart()
    {
        return runTimeLimitStart;
    }
        
    /**
     * Returns upper limit of job execution period.
     *
     * <p>The job will not be run after the time returned. Newly created jobs
     * have no limit set.</p>
     *
     * @return upper limit of job execution period, or <code>null</code> if 
     *         disabled.
     */  
    public Date getTimeLimitEnd()
    {
        return runTimeLimitEnd;
    }
    
    /**
     * Sets the job execution period.
     * 
     * <p>The job will only be run in the time period specified.</p>
     *
     * @param start lower limit of job execution period, or <code>null</code> 
     *        if disabled.
     * @param end upper limit of job execution period, or <code>null</code> 
     *        if disabled.
     * @throws JobModificationException if the job state could not be saved.
     */     
    public void setTimeLimit(Date start, Date end)
        throws JobModificationException
    {
        this.runTimeLimitStart = start;
        this.runTimeLimitEnd = end;
        saveChanges();
    }

    /**
     * Checks if the job is being executed at the moment.
     *
     * @return <code>true</code> if the job is being executed at the moment.
     */
    public boolean isRunning()
    {
        return running;
    }

    /**
     * Returns the job's auto-clean flag.
     *
     * <p>Jobs that have the auto-clean flag enabled are deleted once they
     * exceed their run count limit, run time period, or their schedule's
     * <code>getNextRunTime</code> method returns <code>null</code>.
     *
     * @return the job's auto-clean flag.
     */
    public boolean getAutoClean()
    {
        return autoClean;
    }

    /**
     * Returns the job's auto-clean flag.
     *
     * <p>Jobs that have the auto-clean flag enabled are deleted once they
     * exceed their run count limit, run time period, or their schedule's
     * <code>getNextRunTime</code> method returns <code>null</code>.
     *
     * @param autoClean the job's auto-clean flag.
     * @throws JobModificationException if the job state could not be saved. 
     */
    public void setAutoClean(boolean autoClean)
        throws JobModificationException
    {
        this.autoClean = autoClean;
        saveChanges();
    }

    /**
     * Returns the job's reentrantness flag.
     *
     * <p>If the flag is set, execution of the job may be started even if the 
     * pervious execution has not terminated yet. Newly created jobs are not
     * reentrant by default.</p>
     *
     * @return the job's reentrantess flag state.
     */
    public boolean isReentrant()
    {
        return reentrant;
    }

    /**
     * Sets the job's reentrantness flag.
     *
     * @param reentrant the job's reentrantess flag state.
     * @throws JobModificationException if the job state could not be saved.
     */
    public void setReentrant(boolean reentrant)
        throws JobModificationException
    {
        this.reentrant = reentrant;
        saveChanges();
    }

    /**
     * Checks if the job is enabled.
     *
     * <p>Newly created jobs are disabled by default. Only enabled jobs will
     * be executed by the scheduler</p>
     *
     * @return <code>true</code> if the job is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    // package private methods ///////////////////////////////////////////////

    /**
     * Initializes the job descriptor.
     *
     * @param name the scheduled job name.
     * @param schedule the job's schedule.
     * @param jobClassName the job class name. 
     */
    public void init(String name, Schedule schedule, String jobClassName)
    {
        this.name = name;
        this.schedule = schedule;
        this.jobClassName = jobClassName;
        this.runCount = 0;
        this.runCountLimit = -1;
        this.lastRunTime = null;
        this.runTimeLimitStart = null;
        this.runTimeLimitEnd = null;
        this.running = false;
        this.reentrant = false;
        this.enabled = false;
    }

    /**
     * Sets the job's last execution time.
     *
     * @param lastRunTime the job's last execution time.
     */
    void setLastRunTime(Date lastRunTime)
        throws JobModificationException
    {
        this.lastRunTime = lastRunTime;
        saveChanges();
    }
    
    /**
     * Sets the count of job executions to date.
     *
     * @param runCount the count of job executions to date.
     */
    void setRunCount(int runCount)
        throws JobModificationException
    {
        this.runCount = runCount;
        saveChanges();
    }

    /**
     * Sets the 'enabled' flag for the job.
     *
     * @param enabled <code>true</code> if the job is to be enabled.
     */
    void setEnabled(boolean enabled)
        throws JobModificationException
    {
        this.enabled = enabled;
        saveChanges();
    }

    /**
     * Sets the 'currently running' flag for the job.
     *
     * @param running <code>true</code> if the job is being executed at the
     *        moment. 
     */
    void setRunning(boolean running)
    {
        this.running = running;
    }

    // defined by implementation /////////////////////////////////////////////    
    
    /**
     * Updates the persistent state.
     * 
     * @throws JobModificationException if the job state could not be saved.
     */
    protected abstract void saveChanges()
        throws JobModificationException;
}
