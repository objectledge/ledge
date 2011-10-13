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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;

/**
 * Base class for scheduler components.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski </a>
 */
public abstract class AbstractScheduler
    implements Startable
{
    /** The default DateFormat pattern used by the UI (yyy-MM-dd HH:mm). */
    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm";

    // instance variables ////////////////////////////////////////////////////

    /** the container */
    private MutablePicoContainer container;

    /** the configuration. */
    protected Configuration config;

    /** The logging facility. */
    protected Logger logger;

    /** The pool service. */
    protected ThreadPool threadPool;

    /** The registered schedule types factories. */
    private Map<String, ScheduleFactory> scheduleFactory = new HashMap<String, ScheduleFactory>();

    /** The registered jobs. */
    protected Map<String, AbstractJobDescriptor> jobs = new HashMap<String, AbstractJobDescriptor>();

    /** The job queue. */
    private SortedMap<Long, Set<AbstractJobDescriptor>> queue = new TreeMap<Long, Set<AbstractJobDescriptor>>();

    /** Runners of a specific job. */
    private Map<AbstractJobDescriptor, Set<RunnerTask>> runners = new HashMap<AbstractJobDescriptor, Set<RunnerTask>>();

    /** The date format to be used for the UI. */
    protected DateFormat format;

    /**
     * Component contructor.
     * 
     * @param container the container to store loaded classes.
     * @param config the configuration.
     * @param logger the logger.
     * @param threadPool the thread pool component.
     * @param scheduleFactories the list of schedule factories.
     */
    public AbstractScheduler(MutablePicoContainer container, Configuration config, Logger logger,
        ThreadPool threadPool, ScheduleFactory[] scheduleFactories)
    {
        this.container = container;
        this.config = config;
        this.logger = logger;
        this.threadPool = threadPool;
        for(int i = 0; i < scheduleFactories.length; i++)
        {
            scheduleFactory.put(scheduleFactories[i].getName(), scheduleFactories[i]);
        }
        String formatString = config.getChild("date_format").getValue(DATE_FORMAT_DEFAULT);
        format = new SimpleDateFormat(formatString);
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
        loadJobs();
        Iterator<AbstractJobDescriptor> i = jobs.values().iterator();
        while(i.hasNext())
        {
            AbstractJobDescriptor job = i.next();
            if(job.getSchedule().atStartup())
            {
                run(job);
            }
            schedule(job);
        }
        threadPool.runDaemon(new SchedulerTask());
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // I wish startable interface was split back to Startable/Stoppable
    }

    // Scheduler interface ////////////////////////////////////////////

    /**
     * Creates a new scheduled job.
     * <p>
     * Note that newly created jobs are initialy disabled. You need to call
     * {@link #enable(AbstractJobDescriptor)}to allow the job's execution.
     * </p>
     * <p>
     * Non-perisistent implementation may decide to disallow creation of new jobs at run time. This
     * mehtod would throw <code>UnsupportedOperationException</code> in that case.
     * </p>
     * 
     * @param name the name of the job.
     * @param schedule the job's schedule.
     * @param jobSpec job class specification, see
     *        {@link AbstractJobDescriptor#setJobClassName(String)}
     * @return the scheduled job.
     * @throws JobModificationException if the job could not be instantiated.
     */
    public abstract AbstractJobDescriptor createJobDescriptor(String name, Schedule schedule,
        String jobSpec)
        throws JobModificationException;

    /**
     * Deletes a scheduled job.
     * <p>
     * Non-persisten implementaion may decide to dissalow this operation.
     * </p>
     * 
     * @param job the job.
     * @throws JobModificationException if the job could not be deleted.
     */
    public abstract void deleteJobDescriptor(AbstractJobDescriptor job)
        throws JobModificationException;

    /**
     * Enables a scheduled job.
     * 
     * @param job the job.
     * @throws JobModificationException if the job state could not be saved.
     */
    public void enable(AbstractJobDescriptor job)
        throws JobModificationException
    {
        JobModificationException e = null;
        synchronized(job)
        {
            try
            {
                job.setEnabled(true);
            }
            catch(JobModificationException ee)
            {
                e = ee;
            }
        }
        schedule(job);
        if(e != null)
        {
            throw e;
        }
    }

    /**
     * Disables a scheduled job.
     * <p>
     * An attempt will be made to terminate all instances of this job running at the moment. Would
     * this attempt be effective depends on the implementation of <code>execute()</code> and
     * <code>terminate(Thread)</code> methods in the Job class.
     * </p>
     * 
     * @param job the job.
     * @throws JobModificationException if the job state could not be saved.
     */
    public void disable(AbstractJobDescriptor job)
        throws JobModificationException
    {
        JobModificationException e = null;
        synchronized(job)
        {
            try
            {
                job.setEnabled(false);
            }
            catch(JobModificationException ee)
            {
                e = ee;
            }
        }
        terminate(job);
        if(e != null)
        {
            throw e;
        }
    }

    /**
     * Returns all currently configured jobs.
     * 
     * @return all currently configured jobs.
     */
    public synchronized AbstractJobDescriptor[] getJobDescriptors()
    {
        AbstractJobDescriptor[] result = new AbstractJobDescriptor[jobs.size()];
        jobs.values().toArray(result);
        return result;
    }

    /**
     * Returns a configured job with the specified name.
     * 
     * @param name the job's name.
     * @return the scheduled job.
     */
    public AbstractJobDescriptor getJobDescriptor(String name)
    {
        return jobs.get(name);
    }

    /**
     * Returns available schedule types.
     * 
     * @return available schedule types.
     */
    public String[] getScheduleTypes()
    {
        String[] result = new String[scheduleFactory.size()];
        scheduleFactory.keySet().toArray(result);
        return result;
    }

    /**
     * Creates a Schedule object.
     * 
     * @param type the schedule type.
     * @param config the schedule configuration.
     * @return the schedule.
     * @throws InvalidScheduleException if schedule factory is not registerd.
     */
    public Schedule createSchedule(String type, String config)
        throws InvalidScheduleException
    {
        ScheduleFactory factory = scheduleFactory.get(type);
        if(factory == null)
        {
            throw new InvalidScheduleException("Schedule factory for type '" + type
                + "' not registered");
        }
        Schedule schedule = factory.getInstance();
        schedule.init(this, config);
        return schedule;
    }

    /**
     * Returns <code>true</code> if the implemenation allows job manipulation at run time.
     * <p>
     * If this method returns <code>false</code>, the following methods are not supported, and
     * should not be called:
     * </p>
     * <ul>
     * <li><code>SchedulerService.createJob()</code></li>
     * <li><code>SchedulerService.deleteJob()</code></li>
     * <li><code>ScheduledJob.setSchedule()</code></li>
     * <li><code>ScheduledJob.setJobSpec()</code></li>
     * <li><code>ScheduledJob.setRunCountLimit()</code></li>
     * <li><code>ScheduledJob.setTimeLimit()</code></li>
     * <li><code>ScheduledJob.setReentrant()</code></li>
     * </ul>
     * 
     * @return <code>true</code> if the implemenation allows job manipulation at run time.
     */
    public abstract boolean allowsModifications();

    /**
     * Returns the DateFormat that should be used by the UI.
     * 
     * @return the date format.
     */
    public DateFormat getDateFormat()
    {
        return format;
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * Loads the jobs from storage / configuration.
     */
    protected abstract void loadJobs();

    /**
     * Schedules the next execution of a job.
     * 
     * @param job the job.
     */
    void schedule(AbstractJobDescriptor job)
    {
        if(job.isEnabled())
        {
            Date nextRun = job.getSchedule().getNextRunTime(new Date(), job.getLastRunTime());
            logger.debug("scheduling " + job.getName() + " at " + nextRun + " (last run " + job.getLastRunTime() + ")");
            if(nextRun != null)
            {
                Date start = job.getTimeLimitStart();
                Date end = job.getTimeLimitEnd();
                if((start == null || nextRun.compareTo(start) > 0)
                    && (end == null || nextRun.compareTo(end) < 0))
                {
                    int countLimit = job.getRunCountLimit();
                    if(countLimit < 0 || job.getRunCount() < countLimit)
                    {
                        Long target = new Long(nextRun.getTime());
                        Set<AbstractJobDescriptor> set = queue.get(target);
                        if(set == null)
                        {
                            set = new HashSet<AbstractJobDescriptor>();
                            queue.put(target, set);
                        }
                        set.add(job);
                        synchronized(queue)
                        {
                            queue.notify();
                        }
                        return;
                    }
                }
            }
            if(job.getAutoClean())
            {
                try
                {
                    deleteJobDescriptor(job);
                }
                catch(JobModificationException e)
                {
                    logger.error("failed to auto-clean expired job " + job.getName(), e);
                }
            }
        }
    }

    /**
     * Terminates execution of all instances of the job.
     */
    private void terminate(AbstractJobDescriptor job)
    {
        synchronized(runners)
        {
            Set<RunnerTask> set = runners.get(job);
            if(set != null)
            {
                Iterator<RunnerTask> i = set.iterator();
                while(i.hasNext())
                {
                    (i.next()).terminate();
                }
            }
        }
    }

    /**
     * Runs a specified job within a pool service's worker thread.
     * 
     * @param job the job.
     */
    public void run(AbstractJobDescriptor job)
    {
        synchronized(job)
        {
            if(!job.isRunning() || job.isReentrant())
            {
                logger.debug("starting " + job.getName() + " at " + new Date());
                job.setRunning(true);
                RunnerTask rt = new RunnerTask(job);
                threadPool.runWorker(rt);
            }
        }
    }

    /**
     * Get job component.
     * 
     * @param job the job description.
     * @return the job object.
     * @throws JobNotFoundException if job class not found.
     */
    Job getJobObject(AbstractJobDescriptor job)
        throws JobNotFoundException
    {
        String className = job.getJobClassName();
        try
        {
            Class<?> clazz = Class.forName(className);
            if(container.getComponentInstance(clazz) == null)
            {
                container.registerComponentImplementation(clazz);
            }
            return (Job)container.getComponentInstance(clazz);
        }
        catch(ClassNotFoundException e)
        {
            throw new JobNotFoundException("Couldn't find job class '" + className + "' ", e);
        }
    }

    /**
     * The worker task that runs a sheduled job.
     */
    private class RunnerTask
        extends Task
    {
        private AbstractJobDescriptor job;

        private Thread thread;

        public RunnerTask(AbstractJobDescriptor job)
        {
            this.job = job;
            this.thread = Thread.currentThread();
        }

        public String getName()
        {
            return "scheduler: " + job.getName();
        }

        public void process(Context context)
        {
            synchronized(runners)
            {
                Set<RunnerTask> set = runners.get(job);
                if(set == null)
                {
                    set = new HashSet<RunnerTask>();
                    runners.put(job, set);
                }
                set.add(this);
            }

            synchronized(job)
            {
                try
                {
                    job.setLastRunTime(new Date());
                    job.setRunCount(job.getRunCount() + 1);
                }
                catch(JobModificationException e)
                {
                    logger.warn("failed to save job accountig information", e);
                }
            }

            try
            {
                try
                {
                    getJobObject(job).run(new String[] { job.getArgument() });
                }
                catch(JobNotFoundException e)
                {
                    logger.error("invalid job specification " + job.getJobClassName(), e);
                }
                finally
                {
                    synchronized(runners)
                    {
                        Set<RunnerTask> set = runners.get(job);
                        if(set != null)
                        {
                            set.remove(this);
                            if(set.isEmpty())
                            {
                                synchronized(job)
                                {
                                    job.setRunning(false);
                                }
                            }
                        }
                    }

                    if(!job.isReentrant())
                    {
                        AbstractScheduler.this.schedule(job);
                    }
                    thread = null;
                    
                }
            }
            catch(VirtualMachineError e)
            {
                throw e;
            }
            catch(ThreadDeath e)
            {
                throw e;
            }
            catch(Throwable t)
            {
                logger.error("uncaught exception in scheduled job " + job.getName(), t);
            }
        }

        public void terminate(Thread t)
        {
            try
            {
                getJobObject(job).terminate(t);
            }
            catch(JobNotFoundException e)
            {
                logger.error("invalid job specification " + job.getJobClassName(), e);
            }
        }

        public void terminate()
        {
            if(thread != null)
            {
                terminate(thread);
            }
        }
    }

    /**
     * The daemon task that performs the timekeeping
     */
    private class SchedulerTask
        extends Task
    {
        public String getName()
        {
            return "Job Scheduler";
        }

        public void process(Context context)
        {
            synchronized(queue)
            {
                long now;
                loop: while(!Thread.interrupted())
                {
                    // queue is empty - wait indefinetely
                    if(queue.size() == 0)
                    {
                        try
                        {
                            queue.wait();
                        }
                        catch(InterruptedException e)
                        {
                            break loop;
                        }
                    }
                    // there is something in the queue
                    now = System.currentTimeMillis();
                    Long first = queue.firstKey();
                    while(!queue.isEmpty() && first.longValue() <= now)
                    {
                        // the first element of the que has reached or passed
                        // it's target time
                        Set<AbstractJobDescriptor> set = queue.remove(first);
                        Iterator<AbstractJobDescriptor> i = set.iterator();
                        while(i.hasNext())
                        {
                            AbstractJobDescriptor job = i.next();
                            AbstractScheduler.this.run(job);
                            if(job.isReentrant())
                            {
                                AbstractScheduler.this.schedule(job);
                            }
                        }
                        if(!queue.isEmpty())
                        {
                            first = queue.firstKey();
                        }
                    }
                    // wait for the first element's target time
                    if(!queue.isEmpty())
                    {
                        try
                        {
                            queue.wait(first.longValue() - now);
                        }
                        catch(InterruptedException e)
                        {
                            break loop;
                        }
                    }
                }
            }
        }

        public void terminate(Thread t)
        {
            t.interrupt();
        }
    }
}
