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
package org.objectledge.threads;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.impl.Daemon;
import org.objectledge.threads.impl.WorkerPool;
import org.picocontainer.Startable;

/**
 * Thread pool component.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ThreadPool.java,v 1.10 2005-02-10 17:47:08 rafal Exp $
 */
public class ThreadPool
    implements Startable
{
    // constants ////////////////////////////////////////////////////////////////////////////////
    
    private static final String THREAD_GROUP_NAME = "Ledge ThreadPool";
    
    // instance variables ///////////////////////////////////////////////////////////////////////
    
    /** thread's processing context. */
    private Context context;    
    
    /** cleanup valve (possibly pipeline). */
    private Valve cleanup;
    
    /** thread pool's thread group. */
    private ThreadGroup threadGroup;
    
    private int daemonPriority = Thread.MIN_PRIORITY;
    
    private int workerPriority = Thread.MIN_PRIORITY;
    
    private int workerPoolCapacity = 10;
    
    private Set threads = new HashSet();

    private Logger log;
    
    private WorkerPool workerPool;
    
    /**
     * Component constructor.
     * 
     * @param cleanup the valve that should be invoked every time the thread finishes it's work.
     * @param context thread processing context.
     * @param config the pool configuration.
     * @param log the logger to use.
     */
    public ThreadPool(Valve cleanup, Context context, Configuration config, Logger log)
    {
        this.context = context;
        this.cleanup = cleanup;
        this.log = log;
        if(config != null)
        {
            daemonPriority = config.getChild("daemon-priority").getValueAsInteger(daemonPriority);
            workerPriority = config.getChild("worker-priority").getValueAsInteger(workerPriority);
            workerPoolCapacity = config.getChild("worker-pool-capacity").   
                getValueAsInteger(workerPoolCapacity);
        }
        
        this.threadGroup = new ThreadGroup(THREAD_GROUP_NAME);
        this.workerPool = new WorkerPool(workerPoolCapacity, workerPriority, threadGroup, 
            log, context, cleanup);
        runDaemon(workerPool.getSchedulingTask());
    }
    
    /**
     * Run the worker task.
     * 
     * @param task the task to run.
     */
    public void runWorker(Task task)
    {
        workerPool.dispatch(task);     
    }
    
    /**
     * Run the daemon task.
     * 
     * @param task the task to run.
     */
    public void runDaemon(Task task)
    {
        synchronized(threads)
        {
            threads.add(new Daemon(task, daemonPriority, threadGroup, log, context, cleanup));     
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
        // I wish Startable interface was split
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        synchronized(threads)
        {
            Iterator i = threads.iterator();
            while(i.hasNext())
            {
                Startable thread = (Startable)i.next();
                thread.stop();
                i.remove();
            }
        }
    }
}
