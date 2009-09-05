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

import java.util.Iterator;
import java.util.List;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.database.persistence.PersistenceException;
import org.objectledge.database.persistence.Persistent;
import org.objectledge.database.persistence.PersistentFactory;
import org.objectledge.scheduler.AbstractJobDescriptor;
import org.objectledge.scheduler.AbstractScheduler;
import org.objectledge.scheduler.JobModificationException;
import org.objectledge.scheduler.Schedule;
import org.objectledge.scheduler.ScheduleFactory;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.MutablePicoContainer;

/**
 * Scheduler base on database.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class DBScheduler extends AbstractScheduler
    implements PersistentFactory
{
    //  instance variables ///////////////////////////////////////////////////

    /** The persistence service. */
    protected Persistence persistence;

    /**
     * Component contructor.
     * 
     * @param container the container to store loaded classes.
     * @param config the configuration.
     * @param logger the logger.
     * @param threadPool the thread pool component.
     * @param scheduleFactories the list of schedule factories.
     * @param persistence the persistence component.
     */
    public DBScheduler(
        MutablePicoContainer container,
        Configuration config,
        Logger logger,
        ThreadPool threadPool,
        ScheduleFactory[] scheduleFactories, 
        Persistence persistence)
    {
        super(container, config, logger, threadPool, scheduleFactories);
        this.persistence = persistence;
    }

    //  persitent factory implementation    //////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */

    public Persistent newInstance()
    {
        DBJobDescriptor job = new DBJobDescriptor(persistence, this);
        return (Persistent)job;
    }

    //  abstract scheduler impementation    //////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public synchronized AbstractJobDescriptor createJobDescriptor(String name,
                                             Schedule schedule, String jobClassName)
        throws JobModificationException
    {
        if (jobs.get(name) != null)
        {
            throw new IllegalArgumentException("job " + name + " already exists");
        }
        DBJobDescriptor job = null;
        try
        {
            job = (DBJobDescriptor)newInstance();
        }
        catch (Exception e)
        {
            throw new JobModificationException("failed to create instance", e);
        }
        job.init(name, schedule, jobClassName);
        jobs.put(name, job);
        try
        {
            persistence.save(job);
        }
        catch (PersistenceException e)
        {
            throw new JobModificationException("failed to save new job", e);
        }
        return job;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void deleteJobDescriptor(AbstractJobDescriptor job)
        throws JobModificationException
    {
        if (jobs.get(job.getName()) == null)
        {
            throw new IllegalArgumentException("unregistered job " + job.getName());
        }
        jobs.remove(job.getName());
        try
        {
            persistence.delete((Persistent)job);
        }
        catch (PersistenceException e)
        {
            throw new JobModificationException("failed to delete job", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean allowsModifications()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void loadJobs()
    {
        try
        {
            List<Persistent> jobList = persistence.load(null, this);
            Iterator<Persistent> i = jobList.iterator();
            while (i.hasNext())
            {
                AbstractJobDescriptor job = (AbstractJobDescriptor)i.next();
                jobs.put(job.getName(), job);
            }
        }
        catch (PersistenceException e)
        {
            ///CLOVER:OFF
            throw new ComponentInitializationError("Failed to load jobs ", e);
            ///CLOVER:ON
        }
    }
}
