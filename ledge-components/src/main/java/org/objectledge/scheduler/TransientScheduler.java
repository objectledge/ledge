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

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.MutablePicoContainer;

/**
 * A transient, configuration driven scheduler.
 */
public class TransientScheduler extends AbstractScheduler
{
    /**
     * Component contructor.
     * 
     * @param container the container to store loaded classes.
     * @param config the configuration.
     * @param logger the logger.
     * @param threadPool the thread pool component.
     * @param scheduleFactories the list of schedule factories.
     */
    public TransientScheduler(MutablePicoContainer container, Configuration config, 
                              Logger logger, ThreadPool threadPool, 
                              ScheduleFactory[] scheduleFactories)
    {
        super(container, config, logger, threadPool, scheduleFactories);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractJobDescriptor createJobDescriptor(String name, Schedule schedule, 
                                                      String jobClassName)
        throws JobModificationException
    {
        throw new UnsupportedOperationException("transient scheduler does not support" + 
                                                 " job creation at runtime");
    }

    /**
     * {@inheritDoc}
     */
    public void deleteJobDescriptor(AbstractJobDescriptor job) throws JobModificationException
    {
        throw new UnsupportedOperationException("transient scheduler does not support" +
                                                 " job deletion at runtime");
    }

    /**
     * {@inheritDoc}
     */
    public boolean allowsModifications()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected void loadJobs()
    {
        try
        {
            Configuration[] jobConfigs = config.getChildren("job");
            for (int i = 0; i < jobConfigs.length; i++)
            {
                TransientJobDescriptor job = new TransientJobDescriptor();
                String name = jobConfigs[i].getAttribute("name");
                job.init(this, name, jobConfigs[i]);
                jobs.put(name, job);
            }
        }
        catch (Exception e)
        {
            throw new ComponentInitializationError("failed to load jobs", e);
        }
    }
}
