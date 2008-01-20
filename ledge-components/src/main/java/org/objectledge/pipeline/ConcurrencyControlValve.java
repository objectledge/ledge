// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.pipeline;

import java.util.concurrent.Semaphore;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.statistics.AbstractMuninGraph;
import org.objectledge.statistics.MuninGraph;
import org.objectledge.statistics.ReflectiveStatisticsProvider;

/**
 * A valve that provides control over the number of threads executing another valve.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ConcurrencyControlValve.java,v 1.9 2008-01-20 15:17:52 rafal Exp $
 */
public class ConcurrencyControlValve
    extends ReflectiveStatisticsProvider
    implements Valve
{
    private final Valve nestedValve;

    private final Semaphore semaphore;

    private final int limit;

    private volatile int threadCount = 0;
    
    private final MuninGraph[] graphs;

    /**
     * Creates new ConcurrencyControlValve instance.
     * 
     * @param nestedValve the valve to control.
     * @param limit the maximum number of threads allowed to execute, or 0 for unlimited.
     */
    public ConcurrencyControlValve(final Valve nestedValve, final FileSystem fs, final int limit)
    {
        this.nestedValve = nestedValve;
        this.limit = limit;
        if(limit > 0)
        {
            semaphore = new Semaphore(limit, true);
        }
        else
        {
            semaphore = null;
        }
        graphs = new MuninGraph[] { new Threads(fs) };
    }

    /**
     * Creates a new ConcurrencyControlValve instance.
     *
     * @param nestedValve the valve to control.
     * @param config the confguration object.
     * @throws ConfigurationException if the configuration is incorrect.
     */
    public ConcurrencyControlValve(final Valve nestedValve, final FileSystem fs, final Configuration config)
        throws ConfigurationException
    {
        this(nestedValve, fs, config.getChild("limit").getValueAsInteger());
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        if(semaphore != null)
        {
            try
            {
                semaphore.acquireUninterruptibly();
                nestedValve.process(context);
            }
            finally
            {
                semaphore.release();
            }
        }
        else
        {
            try
            {
                threadCount++;
                nestedValve.process(context);
            }
            finally
            {
                threadCount--;
            }
        }
    }

    // statistics ///////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public MuninGraph[] getGraphs()
    {
        return graphs;
    }

    public class Threads
        extends AbstractMuninGraph
    {
        public Threads(FileSystem fs)
        {   
            super(fs);
        }
        
        public String getId()
        {
            return "threads";
        }
        
        /**
         * Returns the number of concurrently executing threads.
         * 
         * @return the number of concurrently executing threads.
         */
        public Number getRunning()
        {
            if(semaphore != null)
            {
                return new Integer(limit - semaphore.availablePermits());
            }
            else
            {
                return threadCount;
            }
        }
        
        /**
         * Returns the number of threads waiting for execution.
         * 
         * @return the number of threads waiting for execution.
         */
        public Number getWaiting()
        {
            if(semaphore != null)
            {
                return new Integer(semaphore.getQueueLength());
            }
            else
            {
                return 0;
            }
        }
    }
}
