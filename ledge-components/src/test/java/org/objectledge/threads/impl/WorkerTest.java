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
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.Task;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: WorkerTest.java,v 1.1 2004-02-02 13:07:07 fil Exp $
 */
public class WorkerTest extends TestCase
{
    private static final int DELAY = 200;
    
    private static Map whiteboard = new HashMap();
    
    private static Logger log = 
        new Log4JLogger(org.apache.log4j.Logger.getLogger(WorkerTest.class));

    /**
     * Constructor for WorkerTest.
     * @param arg0
     */
    public WorkerTest(String arg0)
    {
        super(arg0);
    }
    
    public void testShortRunning()
        throws Exception
    {
        synchronized(whiteboard)
        {
            Task task = new ShortRunningTask();
            int priority = Thread.NORM_PRIORITY;
            ThreadGroup threadGroup = new ThreadGroup("Test group");
            Context context = new Context();
            whiteboard.clear();
            Valve cleanup = new CleanupValve();
            Worker worker = new Worker("testShortRunning", priority, 
                null, threadGroup, log, context, cleanup);
            worker.dispatch(task);            
            Thread.sleep(DELAY);
            worker.stop();
            Thread.sleep(DELAY);
            assertNotNull(whiteboard.get("started"));
            assertNull(whiteboard.get("interrupted"));
            assertNotNull(whiteboard.get("cleaned up"));
        }
    }

    public void testLongRunning()
        throws Exception
    {
        synchronized(whiteboard)
        {
            Task task = new LongRunningTask();
            int priority = Thread.NORM_PRIORITY;
            ThreadGroup threadGroup = new ThreadGroup("Test group");
            Context context = new Context();
            whiteboard.clear();
            Valve cleanup = new CleanupValve();
            Worker worker = new Worker("testLongRunning", priority, 
                null, threadGroup, log, context, cleanup);
            worker.dispatch(task);  
            worker.getCurrentTask();          
            Thread.sleep(DELAY);
            worker.stop();
            Thread.sleep(DELAY);
            assertNotNull(whiteboard.get("started"));
            assertNotNull(whiteboard.get("interrupted"));
            assertNotNull(whiteboard.get("cleaned up"));
        }
    }

    public void testFailing()
        throws Exception
    {
        synchronized(whiteboard)
        {
            Task task = new FailingTask();
            int priority = Thread.NORM_PRIORITY;
            ThreadGroup threadGroup = new ThreadGroup("Test group");
            Context context = new Context();
            whiteboard.clear();
            Valve cleanup = new CleanupValve();
            Worker worker = new Worker("testFailing", priority, 
                null, threadGroup, log, context, cleanup);
            worker.dispatch(task);            
            Thread.sleep(DELAY);
            worker.stop();
            Thread.sleep(DELAY);
            assertNotNull(whiteboard.get("started"));
            assertNull(whiteboard.get("interrupted"));
            assertNotNull(whiteboard.get("cleaned up"));
        }
    }

    public void testIdleStop()
        throws Exception
    {
        synchronized(whiteboard)
        {
            int priority = Thread.NORM_PRIORITY;
            ThreadGroup threadGroup = new ThreadGroup("Test group");
            Context context = new Context();
            whiteboard.clear();
            Valve cleanup = new CleanupValve();
            Worker worker = new Worker("testLongRunning", priority, 
                null, threadGroup, log, context, cleanup);
            Thread.sleep(DELAY);
            worker.stop();
        }
    }
    
    public void testExternalStop()
        throws Exception
    {
        synchronized(whiteboard)
        {
            Task task = new LongRunningTask();
            int priority = Thread.NORM_PRIORITY;
            ThreadGroup threadGroup = new ThreadGroup("Test group");
            Context context = new Context();
            whiteboard.clear();
            Valve cleanup = new CleanupValve();
            Worker worker = new Worker("testExternalStop", priority, 
                null, threadGroup, log, context, cleanup);
            worker.dispatch(task);
            Thread.sleep(DELAY);
            Thread[] threads = new Thread[1];
            assertEquals(1, threadGroup.activeCount());
            threadGroup.enumerate(threads);
            threads[0].stop();
            worker.stop();
            Thread.sleep(DELAY);
            assertNotNull(whiteboard.get("started"));
            assertNull(whiteboard.get("interrupted"));
            assertNull(whiteboard.get("cleaned up"));
        }
    }
    
    public void testFailedCleanup()
        throws Exception
    {
        synchronized(whiteboard)
        {
            Task task = new LongRunningTask();
            int priority = Thread.NORM_PRIORITY;
            ThreadGroup threadGroup = new ThreadGroup("Test group");
            Context context = new Context();
            whiteboard.clear();
            Valve cleanup = new FailingCleanupValve();
            Worker worker = new Worker("testFailedCleanup", priority, 
                null, threadGroup, log, context, cleanup);
            worker.dispatch(task);
            Thread.sleep(DELAY);
            worker.stop();
            Thread.sleep(DELAY);
            assertNotNull(whiteboard.get("started"));
            assertNotNull(whiteboard.get("interrupted"));
            assertNull(whiteboard.get("cleaned up"));
        }
    }
    
    // tasks & cleanup valves ///////////////////////////////////////////////////////////////////

    private class LongRunningTask
        extends Task
    {
        public synchronized void process(Context context)
        {
           try
           {
               whiteboard.put("started", "yes");
               this.wait();
           }
           catch(InterruptedException e)
           {
               whiteboard.put("interrupted", "yes");
               return;
           }
        }
    }

    private class ShortRunningTask
        extends Task
    {
        public void process(Context context)
        {
            whiteboard.put("started", "yes");
        }
    }

    private class FailingTask
        extends Task
    {
        public void process(Context context)
            throws ProcessingException
        {
            whiteboard.put("started", "yes");
            throw new ProcessingException("processing failed");
        }
    }
    
    private class ForcedStopTask
        extends Task
    {
        public synchronized void process(Context context)
        {
           try
           {
               whiteboard.put("started", "yes");
               this.wait();
           }
           catch(InterruptedException e)
           {
               whiteboard.put("interrupted", "yes");
               return;
           }
        }
        
        public void terminate(Thread thread)
        {
            thread.stop();
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

    private class FailingCleanupValve
        implements Valve
    {
        public void process(Context context)
            throws ProcessingException
        {
            throw new ProcessingException("failed cleanup");
        }
    }
}
