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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.Task;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: WorkerPoolTest.java,v 1.1 2004-02-02 14:24:44 fil Exp $
 */
public class WorkerPoolTest extends TestCase
{
    private static final int TASK_DURATION = 300;
    
    private static Map<String, String> whiteboard = new HashMap<String, String>();
    
    private WorkerPool pool;
    
    private Daemon scheduler;

    private static Logger log = 
        new Log4JLogger(org.apache.log4j.Logger.getLogger(WorkerTest.class));

    /**
     * Constructor for WorkerPoolTest.
     * @param arg0
     */
    public WorkerPoolTest(String arg0)
    {
        super(arg0);
    }

    public void setUp(int capacity)
    {
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Context context = new Context();
        Valve cleanup = new CleanupValve();
        pool = new WorkerPool(capacity, priority, threadGroup, log, context, cleanup);
        Task schedulerTask = pool.getSchedulingTask();
        scheduler = new Daemon(schedulerTask, priority, threadGroup, log, context, cleanup);
        whiteboard.clear();
    }
    
    public void tearDown()
    {
        scheduler.stop();
        scheduler = null;
        pool = null;        
    }
    
    public void testSequential()
        throws Exception
    {
        synchronized(whiteboard)
        {
            setUp(1);
            pool.dispatch(new Test1Task());
            pool.dispatch(new Test2Task());
            Thread.sleep(1000);
            assertNotNull(whiteboard.get("started test1"));
            assertNotNull(whiteboard.get("started test2"));
        }
    }

    public void testConcurrent()
        throws Exception
    {
        synchronized(whiteboard)
        {
            setUp(2);
            pool.dispatch(new Test1Task());
            pool.dispatch(new Test2Task());
            Thread.sleep(1000);
            assertNotNull(whiteboard.get("started test1"));
            assertNotNull(whiteboard.get("started test2"));
        }
    }

    private class Test1Task
        extends Task
    {
        public synchronized void process(Context context)
        {
           try
           {
               whiteboard.put("started test1", "yes");
               this.wait(TASK_DURATION);
           }
           catch(InterruptedException e)
           {
               whiteboard.put("interrupted test1", "yes");
               return;
           }
        }
    }

    private class Test2Task
        extends Task
    {
        public synchronized void process(Context context)
        {
           try
           {
               whiteboard.put("started test2", "yes");
               this.wait(TASK_DURATION);
           }
           catch(InterruptedException e)
           {
               whiteboard.put("interrupted test2", "yes");
               return;
           }
        }
    }

    private class CleanupValve
        implements Valve
    {
        public void process(Context context)
        {
            whiteboard.put("cleaned up", "yes");
        }
    }
}
