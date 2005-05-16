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
package org.objectledge.web;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.log4j.NDC;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.statistics.DataSource;
import org.objectledge.statistics.Graph;
import org.objectledge.statistics.ReflectiveStatisticsProvider;
import org.objectledge.utils.StringUtils;

/**
 * A valve that counts processed HTTP requests and sessions.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: RequestTrackingValve.java,v 1.1 2005-05-16 09:33:03 rafal Exp $
 */
public class RequestTrackingValve
    extends ReflectiveStatisticsProvider
    implements Valve
{
    private final Valve nested;

    private int totalRequests = 0;
    
    private int totalSessions = 0;
    
    private int concurrentSessions = 0;
    
    private long totalDuration = 0;

    private final Logger log;
    
    /**
     * Creates new RequestTrackingValve instance.
     *
     * @param nested the nested valve.
     * @param log the logger to use.
     */
    public RequestTrackingValve(final Valve nested, final Logger log)
    {
        this.nested = nested;
        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        long timer = 0;
        try
        {
            HttpContext httpContext = HttpContext.getHttpContext(context);
            int r = totalRequests++;
            int s = trackSession(httpContext).getId(); 
            NDC.push("R"+r+" S"+s);
            if(log.isInfoEnabled())
            {
                log.info("starting "+getRequestUrl(httpContext));
            }
            timer = System.currentTimeMillis();
            nested.process(context);
        }
        finally
        {
            long duration = System.currentTimeMillis() - timer;
            totalDuration += duration;
            if(log.isInfoEnabled())
            {
                log.info("done in "
                    + StringUtils.formatMilliIntervalAsSeconds(duration));
            }
            NDC.pop();
        }
    }
    
    private SessionMarker trackSession(HttpContext httpContext)
    {
        HttpSession session = httpContext.getRequest().getSession();
        SessionMarker marker = (SessionMarker)session.getAttribute(SessionMarker.KEY);
        if(marker == null)
        {
            concurrentSessions++;
            marker = new SessionMarker(this, totalSessions++);
            session.setAttribute(SessionMarker.KEY, marker);
        }
        return marker;
    }

    private String getRequestUrl(HttpContext httpContext)
    {
        StringBuffer buff = httpContext.getRequest().getRequestURL();
        return buff.toString();
    }
    
    void sessionExpired(SessionMarker marker)
    {
        concurrentSessions--;
        log.info("session S"+marker.getId()+" expired");
    }
    
    /**
     * An object used for detecting session expiry.
     */
    static class SessionMarker implements HttpSessionBindingListener, Serializable
    {
        public static final String KEY = SessionMarker.class.getName();
        
        private transient WeakReference<RequestTrackingValve> valveRef;
        
        private final int id;

        /**
         * Creates new SessionMarker instance.
         * 
         * @param valve the owner valve.
         * @param id session identifier.
         */
        public SessionMarker(RequestTrackingValve valve, int id)
        {
            this.valveRef = new WeakReference<RequestTrackingValve>(valve);
            this.id = id;
        }
        
        /**
         * {@inheritDoc}
         */
        public void valueBound(HttpSessionBindingEvent event)
        {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         */
        public void valueUnbound(HttpSessionBindingEvent event)
        {
            if(valveRef != null)
            {
                RequestTrackingValve valve = valveRef.get();
                if(valve != null)
                {
                    valve.sessionExpired(this);
                }
            }
        }
        
        /**
         * Returns the session identifier.
         *  
         * @return the session identifier.
         */
        public int getId()
        {
            return id;
        }
    }

    // statistics ///////////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "Request tracking";
    }

    private static final DataSource REQUESTS_DS = new DataSource("requests_count",
        "Served requests", null, DataSource.Type.COUNTER, DataSource.Graph.LINE1);
    
    private static final Graph REQUESTS_GRAPH = new Graph("requests", "Served requests", 
        null, new DataSource[] { REQUESTS_DS }, "number of requests");

    private static final DataSource REQUESTS_DURATION_DS = new DataSource("requests_duration_value",
        "Requests processing time", null, DataSource.Type.COUNTER, DataSource.Graph.LINE1);
    
    private static final Graph REQUESTS_DURATION_GRAPH = new Graph("requests_duration",
        "Requests processing time", null, new DataSource[] { REQUESTS_DURATION_DS },
        "milliseconds");

    private static final DataSource SESSIONS_DS = new DataSource("sessions_count",
        "Served sessions", null, DataSource.Type.COUNTER, DataSource.Graph.LINE1);
    
    private static final Graph SESSIONS_GRAPH = new Graph("sessions", "Served sessions", 
        null, new DataSource[] { SESSIONS_DS }, "number of sessions");

    private static final DataSource CONCURRENT_SESSIONS_DS = new DataSource(
        "concurrent_sessions_count", "Concurrent sessions", null, DataSource.Type.GAUGE,
        DataSource.Graph.LINE1);

    private static final Graph CONCURRENT_SESSIONS_GRAPH = new Graph("concurrent_sessions",
        "Concurrent sessions", null, new DataSource[] { CONCURRENT_SESSIONS_DS },
        "number of sessions");

    private static final Graph[] GRAPHS = {
        REQUESTS_GRAPH,
        REQUESTS_DURATION_GRAPH,
        SESSIONS_GRAPH,
        CONCURRENT_SESSIONS_GRAPH
    };
    
    private static final DataSource[] DATA_SOURCES = {
        REQUESTS_DS,
        REQUESTS_DURATION_DS,
        SESSIONS_DS,
        CONCURRENT_SESSIONS_DS        
    };
    
    /**
     * {@inheritDoc}
     */
    public Graph[] getGraphs()
    {
        return GRAPHS;
    }

    /**
     * {@inheritDoc}
     */
    public DataSource[] getDataSources()
    {
        return DATA_SOURCES;
    }

    /**
     * Returns the total number of served requests.
     * 
     * @return the total number of served requests.
     */
    public Number getRequestsCountValue()
    {
        return new Integer(totalRequests);
    }
    
    /**
     * Returns the total duration of request processing.
     * 
     * @return the total duration of request processing.
     */
    public Number getRequestsDurationValueValue()
    {
        return new Long(totalDuration);
    }
    
    /**
     * Returns the total number of served sessions.
     * 
     * @return the total number of served sessions.
     */
    public Number getSessionsCountValue()
    {
        return new Integer(totalSessions);
    }
    
    /**
     * Returns the current number of concurrently active sessions.
     * 
     * @return the current number of concurrently active sessions.
     */
    public Number getConcurrentSessionsCountValue()
    {
        return new Integer(concurrentSessions);
    }
}
