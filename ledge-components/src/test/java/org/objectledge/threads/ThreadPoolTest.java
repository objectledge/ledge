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

import junit.framework.TestCase;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ThreadPoolTest.java,v 1.1 2004-01-30 14:40:05 fil Exp $
 */
public class ThreadPoolTest extends TestCase
{

    /**
     * Constructor for ThreadPoolTest.
     * @param arg0
     */
    public ThreadPoolTest(String arg0)
    {
        super(arg0);
    }

    public void testDaemon()
        throws InterruptedException
    {
        Context context = new Context();
        Valve cleanup = null;
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        ThreadPool pool = new ThreadPool(context, cleanup, log);
        pool.runDaemon(new TestTask());
        Thread.sleep(100);
        pool.stop();
    }

    public void testWorker()
        throws InterruptedException
    {
        Context context = new Context();
        Valve cleanup = null;
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        ThreadPool pool = new ThreadPool(context, cleanup, log);
        pool.runWorker(new TestTask());
        Thread.sleep(100);
        pool.stop();
    }
    
    private class TestTask
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
}
