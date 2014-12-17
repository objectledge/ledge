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

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.log4j.NDC;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pipeline.ConcurrencyControlValve;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.statistics.AbstractMuninGraph;
import org.objectledge.statistics.MuninGraph;
import org.objectledge.statistics.ReflectiveStatisticsProvider;
import org.objectledge.statistics.RoundRobinAverage;
import org.objectledge.utils.StringUtils;

/**
 * A valve that counts processed HTTP requests and sessions.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: RequestTrackingValve.java,v 1.7 2008-10-02 15:32:32 rafal Exp $
 */
public class RequestTrackingValve
    extends ReflectiveStatisticsProvider
    implements Valve
{
    private static final int AVG_DURATION_WINDOW = 2500;
    
    private static final int AVG_DURATION_WARMUP = 100;

    private final Valve nested;
    
    private final Logger log;
    
    private final PrintWriter performanceLog;
    
    private final PrintWriter slowRequestLog;
    
    private final long slowRequestThreshold;

    private final DateFormat timeFormat;
    
    private static final String DEFAULT_TIME_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss";
    
    private final long timeResolution;
    
    private static final long DEFAULT_TIME_RESOLUTION = 1000L;
    
    private final MemoryMXBean memoryMXBean;
    
    private final List<GarbageCollectorMXBean> garbageCollectorMXBeans;

    private int totalRequests = 0;
    
    private int processedRequests = 0;

    private int droppedRequests = 0;

    private int totalSessions = 0;
    
    private int concurrentSessions = 0;
    
    private int concurrentRequests = 0;
    
    private RoundRobinAverage averageDuration = new RoundRobinAverage(AVG_DURATION_WINDOW);
    
    private String currentTime;
    
    private long lastCurrentTime;
    
    private final MuninGraph[] graphs;
    
    /** Context attribute key where request marker string can be found */
    public static final String REQUEST_MARKER_KEY = RequestTrackingValve.class.getName() + ".RequestMarker";
    
    /**
     * Creates new RequestTrackingValve instance.
     * 
     * @param nested the nested valve.
     * @param log the logger to use.
     * @param fileSystem the FileSystem component.
     * @param performanceLogPath the FileSystem path of performance log file.
     * @param keepPerformanceLog should performace log be appended to after restart
     * @param slowRequestLogPath the FileSystem path of slow request log file.
     * @param slowRequestThreshold minimum processing duration in milliseconds for requests that
     *        should be logged in slow request log.
     * @param keepSlowRequestLog should slow request log be appended to after restart
     * @param timeFormatPattern the SimpleDateFormat pattern to use for date formatting.
     * @param timeResolution the resolution of time measurement in the custom logs.
     */
    public RequestTrackingValve(final Valve nested, final Logger log, final FileSystem fileSystem,
        final String performanceLogPath, boolean keepPerformanceLog,
        final String slowRequestLogPath, final long slowRequestThreshold,
        boolean keepSlowRequestLog, final String timeFormatPattern, final long timeResolution)
    {
        this.nested = nested;
        this.log = log;
        try
        {
            if(performanceLogPath != null)
            {
                this.performanceLog = new PrintWriter(new OutputStreamWriter(
                    fileSystem.getOutputStream(performanceLogPath, keepPerformanceLog), "UTF-8"));
            }
            else
            {
                this.performanceLog = null;
            }
            if(slowRequestLogPath != null)
            {
                this.slowRequestLog = new PrintWriter(new OutputStreamWriter(
                    fileSystem.getOutputStream(slowRequestLogPath, keepSlowRequestLog), "UTF-8"));
            }
            else
            {
                this.slowRequestLog = null;
            }
        }
        catch(UnsupportedEncodingException e)
        {
            throw new ComponentInitializationError("internal error", e);
        }
        this.slowRequestThreshold = slowRequestThreshold;
        this.timeFormat = new SimpleDateFormat(timeFormatPattern);
        this.timeResolution = timeResolution;
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.graphs = new MuninGraph[] { new RequestsCount(fileSystem),
                        new RequestsDuration(fileSystem), new ServedSessions(fileSystem),
                        new ConcurrentSessions(fileSystem) };
    }

    /**
     * Creates new RequestTrackingValve instance.
     *
     * @param nested the nested valve.
     * @param log the logger to use.
     * @param fileSystem the FileSystem component.
     */
    public RequestTrackingValve(final Valve nested, final Logger log, final FileSystem fileSystem)
    {
        this(nested, log, fileSystem, null, false, null, Long.MAX_VALUE,
            false, DEFAULT_TIME_FORMAT_PATTERN, DEFAULT_TIME_RESOLUTION);
    }

    /**
     * Creates new RequestTrackingValve instance.
     *
     * @param nested the nested valve.
     * @param log the logger to use.
     * @param fileSystem the FileSystem component.
     * @param config the component configuration.
     */    
    public RequestTrackingValve(final Valve nested, final Logger log, final FileSystem fileSystem,
        final Configuration config)
    {
        this(nested, log, fileSystem, config.getChild("performanceLog").getChild("path")
            .getValue(null), config.getChild("performanceLog").getChild("keep", false) != null,
                        config.getChild("slowRequestLog").getChild("path").getValue(null), config
                            .getChild("slowRequestLog").getChild("threshold")
                            .getValueAsLong(Long.MAX_VALUE), config.getChild("slowRequestLog")
                            .getChild("keep", false) != null, config.getChild("time")
                            .getChild("format").getValue(DEFAULT_TIME_FORMAT_PATTERN), config
                            .getChild("time").getChild("resolution")
                            .getValueAsLong(DEFAULT_TIME_RESOLUTION));
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        long startTime = System.currentTimeMillis();
        int r = 0;
        int s = -1;
        String requestUrl = null;
        try
        {
            HttpContext httpContext = HttpContext.getHttpContext(context);
            r = totalRequests++;
            concurrentRequests++;
            String requestMarker;
            SessionMarker sm = trackSession(httpContext);
            if(sm != null)
            {
                s = sm.getId();
                requestMarker = "R" + r + " S" + s;
            }
            else
            {
                requestMarker = "R" + r + " NS";
            }
            context.setAttribute(REQUEST_MARKER_KEY, requestMarker);
            NDC.push(requestMarker);
            requestUrl = getRequestUrl(httpContext);
            if(log.isInfoEnabled())
            {
                log.info("starting "+requestUrl);
            }
            startTime = System.currentTimeMillis();
            if(performanceLog != null || slowRequestLog != null)
            {
                updateCurrentTime(startTime);
            }
            nested.process(context);
            if(context.getAttribute(ConcurrencyControlValve.DROPPED_REQUEST_MARKER) != null)
            {
                droppedRequests++;
            }
            else
            {
                processedRequests++;
            }
        }
        finally
        {
            long duration = System.currentTimeMillis() - startTime;
            if(totalRequests > AVG_DURATION_WARMUP) {
                averageDuration.addSample(duration);
            }
            if(log.isInfoEnabled())
            {
                log.info("done in "
                    + StringUtils.formatMilliIntervalAsSeconds(duration));
            }
            if(performanceLog != null)
            {
                performanceLog(r, s, startTime, duration);
            }
            if(slowRequestLog != null && duration > slowRequestThreshold)
            {
                slowRequestLog(r, s, duration, requestUrl);
            }
            NDC.pop();
            concurrentRequests--;
        }
    }
    
    private SessionMarker trackSession(HttpContext httpContext)
    {
        HttpSession session = httpContext.getRequest().getSession(false);
        if(session != null)
        {
            SessionMarker marker = (SessionMarker)session.getAttribute(SessionMarker.KEY);
            if(marker == null)
            {
                concurrentSessions++;
                marker = new SessionMarker(this, totalSessions++);
                session.setAttribute(SessionMarker.KEY, marker);
            }
            return marker;
        }
        else
        {
            return null;
        }
    }
    
    private void updateCurrentTime(long time)
    {
        synchronized(timeFormat)
        {
            if(currentTime == null || time - lastCurrentTime > timeResolution)
            {
                currentTime = timeFormat.format(new Date(time));
                lastCurrentTime = time;
            }
        }
    }

    private String getRequestUrl(HttpContext httpContext)
    {
        StringBuffer buff = httpContext.getRequest().getRequestURL();
        return buff.toString();
    }
    
    void sessionExpired(SessionMarker marker, String sessionId)
    {
        concurrentSessions--;
        log.info("session S" + marker.getId() + " " + sessionId + " expired");
    }
    
    /**
     * An object used for detecting session expiry.
     */
    static class SessionMarker implements HttpSessionBindingListener, Serializable
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

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
                    valve.sessionExpired(this, event.getSession().getId());
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

    // performance related logs /////////////////////////////////////////////////////////////////
    
    private void performanceLog(int r, int s, long startTime, long duration)
    {
        performanceLog.format("%s, %d, %d, %d, %d, %d, %d, %d, %d, %d\n", currentTime, r, s,
            getTimeOfDay(startTime), duration, concurrentRequests, concurrentSessions,
            getUsedMemory(), getGCCount(), getGCTime());
        performanceLog.flush();
    }
    
    private int getTimeOfDay(long time)
    {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60
            + cal.get(Calendar.SECOND);
    }
    
    private long getUsedMemory()
    {
        return memoryMXBean.getHeapMemoryUsage().getUsed()
            + memoryMXBean.getNonHeapMemoryUsage().getUsed();
    }
    
    private long getGCCount()
    {
        long count = 0L;
        for(GarbageCollectorMXBean gc : garbageCollectorMXBeans)
        {
            count += gc.getCollectionCount();
        }            
        return count;
    }
    
    private long getGCTime()
    {
        long time = 0L;
        for(GarbageCollectorMXBean gc : garbageCollectorMXBeans)
        {
            time += gc.getCollectionTime();
        }            
        return time;
    }    
    
    private void slowRequestLog(int r, int s, long duration, String url)
    {
        if(s >= 0)
        {
            slowRequestLog.format("%s R%d S%d %dms %s\n", currentTime, r, s, duration, url);
        }
        else
        {
            slowRequestLog.format("%s R%d NS %dms %s\n", currentTime, r, s, duration, url);
        }
        slowRequestLog.flush();
    }
    
    // statistics ///////////////////////////////////////////////////////////////////////////////
       
    /**
     * {@inheritDoc}
     */
    public MuninGraph[] getGraphs()
    {
        return graphs;
    }

    public class RequestsCount
        extends AbstractMuninGraph
    {
        public RequestsCount(FileSystem fs)
        {
            super(fs);
        }
        
        public String getId()
        {
            return "requestsCount";
        }
        
        /**
         * Returns the total number of received requests.
         * 
         * @return the total number of received requests.
         */
        public Number getCount()
        {
            return Integer.valueOf(totalRequests);
        }

        /**
         * Returns the total number of served requests.
         * 
         * @return the total number of served requests.
         */
        public Number getProcessedCount()
        {
            return Integer.valueOf(processedRequests);
        }

        /**
         * Returns the total number of dropped requests.
         * 
         * @return the total number of dropped requests.
         */
        public Number getDroppedCount()
        {
            return Integer.valueOf(droppedRequests);
        }
    }
   
    public class RequestsDuration
        extends AbstractMuninGraph
    {
        public RequestsDuration(FileSystem fs)
        {
            super(fs);
        }
        
        public String getId()
        {
            return "requestsDuration";
        }
        
        /**
         * Returns the total duration of request processing.
         * 
         * @return the total duration of request processing.
         */
        public Number getDuration()
        {
            return averageDuration.getAverage();
        }
    }    
    
    public class ServedSessions
        extends AbstractMuninGraph
    {
        public ServedSessions(FileSystem fs)
        {
            super(fs);
        }
        
        public String getId()
        {
            return "servedSessions";
        }
        
        /**
         * Returns the total number of served sessions.
         * 
         * @return the total number of served sessions.
         */
        public Number getCount()
        {
            return Integer.valueOf(totalSessions);
        }
    }
    
    public class ConcurrentSessions
        extends AbstractMuninGraph
    {
        public ConcurrentSessions(FileSystem fs)
        {
            super(fs);
        }
        
        public String getId()
        {
            return "concurrentSessions";
        }
        
        /**
         * Returns the current number of concurrently active sessions.
         * 
         * @return the current number of concurrently active sessions.
         */
        public Number getCount()
        {
            return Integer.valueOf(concurrentSessions);
        }
    }
}
