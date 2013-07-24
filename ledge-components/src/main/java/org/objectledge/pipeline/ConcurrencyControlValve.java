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
import java.util.concurrent.TimeUnit;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.statistics.AbstractMuninGraph;
import org.objectledge.statistics.MuninGraph;
import org.objectledge.statistics.ReflectiveStatisticsProvider;
import org.objectledge.utils.StringUtils;

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

    private final Config config;

    private volatile int threadCount = 0;
    
    private final MuninGraph[] graphs;

    private final Logger log;

    /**
     * When Context attribute with this key is present, session is treated as privileged.
     * {@link org.objectledge.web.dispatcher.PipelineHttpDispatcher} sets this attribute to the value
     * of {@code HttpSession} attribute with the same name.
     */
    public static final String PRIVILEGED_SESSION_MARKER = "org.objectledge.pipeline.concurrency.PrivilegedSession";

    /**
     * When request is dropped because timeout occurs while waiting on semaphore, Context attribute
     * with this name is. {@link org.objectledge.web.RequestTrackingValve} uses it to update request
     * counts correctly.
     */
    public static final String DROPPED_REQUEST_MARKER = "org.objectledge.pipeline.concurrency.DroppedRequest";

    /**
     * Creates new ConcurrencyControlValve instance.
     * 
     * @param nestedValve the valve to control.
     * @param limit the maximum number of threads allowed to execute, or 0 for unlimited.
     * @param log logger
     */
    public ConcurrencyControlValve(final Valve nestedValve, final FileSystem fs,
        final Config config,
        final Logger log)
    {
        this.nestedValve = nestedValve;
        this.config = config;
        this.log = log;
        if(config.getLimit() > 0)
        {
            semaphore = new Semaphore(config.getLimit(), true);
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
     * @param log logger
     * @throws ConfigurationException if the configuration is incorrect.
     */
    public ConcurrencyControlValve(final Valve nestedValve, final FileSystem fs,
        final Configuration config, final Logger log)
        throws ConfigurationException
    {
        this(nestedValve, fs, new Config(config), log);
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        if(semaphore != null)
        {
            long startTime = 0;
            if(log.isInfoEnabled())
            {
                startTime = System.currentTimeMillis();
            }
            try
            {
                int timeout = context.getAttribute(PRIVILEGED_SESSION_MARKER) != null ? config
                    .getPrivilegedTimeout() : config.getTimeout();
                if(semaphore.tryAcquire(timeout, TimeUnit.SECONDS))
                {
                    if(log.isInfoEnabled())
                    {
                        long endTime = System.currentTimeMillis();
                        if(endTime > startTime)
                        {
                            log.info("queued for "
                                + StringUtils.formatMilliIntervalAsSeconds(endTime - startTime));
                        }
                        startTime = endTime;
                    }
                    try
                    {
                        nestedValve.process(context);
                    }
                    finally
                    {
                        if(log.isInfoEnabled())
                        {
                            long endTime = System.currentTimeMillis();
                            log.info("processed in "
                                + StringUtils.formatMilliIntervalAsSeconds(endTime - startTime));
                        }
                        semaphore.release();
                    }
                }
                else
                {
                    context.setAttribute(DROPPED_REQUEST_MARKER, Boolean.TRUE);
                    long endTime = System.currentTimeMillis();
                    if(endTime > startTime)
                    {
                        log.info("dropped after "
                            + StringUtils.formatMilliIntervalAsSeconds(endTime - startTime));
                    }
                }
            }
            catch(InterruptedException e)
            {
                long endTime = System.currentTimeMillis();
                if(endTime > startTime)
                {
                    log.error("interrupted while waiting on semaphore after "
                        + StringUtils.formatMilliIntervalAsSeconds(endTime - startTime));
                }
                else
                {
                    log.error("interrupted while waiting on semaphore", e);
                }
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
                return Integer.valueOf(config.getLimit() - semaphore.availablePermits());
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
                return Integer.valueOf(semaphore.getQueueLength());
            }
            else
            {
                return 0;
            }
        }
    }

    public static class Config
    {
        private final int limit;

        private final int timeout;

        private final int privilegedTimeout;

        public Config(final int limit, final int timeout, final int privilegedTimeout)
        {
            this.limit = limit;
            this.timeout = timeout;
            this.privilegedTimeout = privilegedTimeout;
        }

        public Config(Configuration config)
            throws ConfigurationException
        {
            this.limit = config.getChild("limit").getValueAsInteger();
            this.timeout = config.getChild("timeout").getValueAsInteger();
            this.privilegedTimeout = config.getChild("privilegedTimeout").getValueAsInteger();
        }

        public int getLimit()
        {
            return limit;
        }

        public int getTimeout()
        {
            return timeout;
        }

        public int getPrivilegedTimeout()
        {
            return privilegedTimeout;
        }
    }
}
