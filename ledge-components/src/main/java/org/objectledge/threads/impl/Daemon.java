// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.threads.impl;

import org.apache.log4j.NDC;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.Task;
import org.picocontainer.Startable;

/**
 * A daemon thread helper object.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Daemon.java,v 1.5 2007-12-21 00:05:22 rafal Exp $
 */
public class Daemon
    implements Runnable, Startable
{
    private Thread thread;
    
    private Logger log;
    
    private Task task; 
    
    private Context context;
    
    private Valve cleanup;
    
    private boolean shutdown = false;
    
    private boolean running = false;
    
    /**
     * Creates a daemon thread.
     * 
     * @param task the task to run.
     * @param priority the task's priority (see {@link java.lang.Thread} description)
     * @param threadGroup the thread group where the thread should belong.
     * @param log the logger to use.
     * @param context thread's processing context.
     * @param cleanup cleanup valve to invoke, should the task terminate.
     */
    public Daemon(Task task, int priority, 
        ThreadGroup threadGroup, Logger log, Context context, Valve cleanup)
    {
        this.log = log;
        this.context = context;
        this.task = task;
        this.cleanup = cleanup;
        thread = new Thread(threadGroup, this, task.getName());
        thread.setPriority(priority);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * {@inheritDoc}
     */    
    public void run()
    {
        try
        {
            NDC.push("D " + task.getName());
            try
            {
                running = true;
                log.info("starting "+task.getName());
                task.process(context);
                if(shutdown)
                {
                    log.info("finished "+task.getName());
                }
                else
                {
                    log.error(task.getName()+" has quit unexpectedly");
                }
            }
            ///CLOVER:OFF
            catch(VirtualMachineError e)
            {
                throw e;
            }
            ///CLOVER:ON
            catch(ThreadDeath e)
            {
                log.warn(task.getName()+" was forcibly stopped", e);
                throw e;
            }
            catch(Throwable e)
            {
                log.error("unhandled exception in "+task.getName(), e);
            }
            finally
            {
                running = false;
            }        
            
            // cleanup
            if(cleanup != null)
            {
                try
                {
                    cleanup.process(context);
                }
                ///CLOVER:OFF
                catch(VirtualMachineError e)
                {
                    throw e;
                }
                catch(ThreadDeath e)
                {
                    throw e;
                }
                ///CLOVER:ON
                catch(Throwable e)
                {
                    log.error("uncaught exception in cleanup after "+task.getName(), e);
                }
                finally
                {
                    context.clearAttributes();
                }
            }
        }
        finally
        {
            NDC.pop();            
        }
    }

    /**
     * {@inheritDoc}
     */    
    public void start()
    {
        // Startable interface should really be split
    }
    
    /**
     * {@inheritDoc}
     */    
    public void stop()
    {
        shutdown = true;
        if(running)
        {
            log.info("asking "+task.getName()+" to terminate");
            task.terminate(thread);
        }
        else
        {
            log.warn(task.getName()+" is not running");
        }
    }
}
