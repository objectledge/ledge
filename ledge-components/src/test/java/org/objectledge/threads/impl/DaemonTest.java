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
 * @version $Id: DaemonTest.java,v 1.1 2004-01-30 14:40:05 fil Exp $
 */
public class DaemonTest extends TestCase
{

    /**
     * Constructor for DaemonTest.
     * @param arg0
     */
    public DaemonTest(String arg0)
    {
        super(arg0);
    }

    public void testNormal()
        throws Exception
    {
        Task task = new NormalTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = new CleanupValve();
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNotNull(context.getAttribute("interrupted"));
        assertNotNull(context.getAttribute("cleaned up"));
    }
    
    public void testEarlyExit()
        throws Exception
    {
        Task task = new EarlyExitTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = new CleanupValve();
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNull(context.getAttribute("interrupted"));
        assertNotNull(context.getAttribute("cleaned up"));
    }
    
    public void testFailing()
        throws Exception
    {
        Task task = new FailingTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = new CleanupValve();
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNull(context.getAttribute("interrupted"));
        assertNotNull(context.getAttribute("cleaned up"));
    }

    public void testForcedStop()
        throws Exception
    {
        Task task = new ForcedStopTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = new CleanupValve();
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNull(context.getAttribute("interrupted"));
        assertNull(context.getAttribute("cleaned up"));
    }
    
    public void testExternalStop()
        throws Exception
    {
        Task task = new NormalTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = new CleanupValve();
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        Thread[] threads = new Thread[1];
        assertEquals(1, threadGroup.activeCount());
        threadGroup.enumerate(threads);
        threads[0].stop();
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNull(context.getAttribute("interrupted"));
        assertNull(context.getAttribute("cleaned up"));
    }
    
    public void testDoubleStop()
        throws Exception
    {
        Task task = new NormalTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = new CleanupValve();
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        daemon.stop();
        Thread.sleep(100);
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNotNull(context.getAttribute("interrupted"));
        assertNotNull(context.getAttribute("cleaned up"));
        
    }

    public void testNoCleanup()
        throws Exception
    {
        Task task = new NormalTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = null;
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNotNull(context.getAttribute("interrupted"));
        assertNull(context.getAttribute("cleaned up"));
    }

    public void testFailedCleanup()
        throws Exception
    {
        Task task = new NormalTask();
        int priority = Thread.NORM_PRIORITY;
        ThreadGroup threadGroup = new ThreadGroup("Test group");
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Context context = new Context();
        context.clearAttributes();
        Valve cleanup = new FailedCleanupValve();
        Daemon daemon = new Daemon(task, priority, threadGroup, log, context, cleanup);
        Thread.sleep(100);
        daemon.stop();
        assertNotNull(context.getAttribute("started"));
        assertNotNull(context.getAttribute("interrupted"));
        assertNull(context.getAttribute("cleaned up"));
    }

    private class NormalTask
        extends Task
    {
        public synchronized void process(Context context)
        {
           try
           {
               context.setAttribute("started", "yes");
               this.wait();
           }
           catch(InterruptedException e)
           {
               context.setAttribute("interrupted", "yes");
               return;
           }
        }
    }

    private class EarlyExitTask
        extends Task
    {
        public void process(Context context)
        {
            context.setAttribute("started", "yes");
        }
    }

    private class FailingTask
        extends Task
    {
        public void process(Context context)
            throws ProcessingException
        {
            context.setAttribute("started", "yes");
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
               context.setAttribute("started", "yes");
               this.wait();
           }
           catch(InterruptedException e)
           {
               context.setAttribute("interrupted", "yes");
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
            context.setAttribute("cleaned up", "yes");
        }
    }

    private class FailedCleanupValve
        implements Valve
    {
        public void process(Context context)
            throws ProcessingException
        {
            throw new ProcessingException("failed cleanup");
        }
    }
}
