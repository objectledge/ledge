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
package org.objectledge.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.database.impl.DelegatingConnection;
import org.objectledge.database.impl.DelegatingDataSource;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.utils.StringUtils;

/**
 * A decorator for javax.sql.DataSource interface that makes sure a Thread uses only one physical
 * connection to the database.
 * 
 * <p>Using more than one connection at different levels of the execution stack of a thread often
 * leads to java monitor -> data base lock -> java monitor deadlocks that are hard to reproduce
 * and diagnose. There are two techniques for avoiding that. One is passing the Connection object
 * in call arguments so the code that may needed gets the right instance. Another is using this
 * decorator that uses the thread's {@link Context} to cache the connection. All calls to 
 * {@link #getConnection()} within the thread's execution context will return the same Connection
 * instance, and all calls {@link #getConnection(String,String)} will return the same Connection
 * instance per user argument value.</p> 
 * 
 * <p>A special valve {@link GuardValve} is provided for checking if the thread has closed it's
 * connections properly.</p>
 * 
 * <p>If you are getting messages about threads owning open connectin in the log, you should
 * set the tracing parameters to non-zero value. Bigger values will put more stack frames into
 * the trace.</p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ThreadDataSource.java,v 1.7 2005-10-07 15:35:41 rafal Exp $
 */
public class ThreadDataSource
    extends DelegatingDataSource
{
    /**
     * {@link org.objectledge.context.Context} key under which DataSource -> ThreadConnection map 
     * is kept.
     */ 
    public static final String THREAD_MAP = "org.objectledge.database.ThreadDataSource.threadMap";
    
    /** {@link org.objectledge.context.Context}key under where open/close tracing buffer is kept. 
     */ 
    public static final String TRACE_BUFFER = 
        "org.objectledge.database.ThreadDataSource.traceBuffer";
    
    /** thread's processing context. */
    private Context context;
    
    /** tracing depth (0 if disabled). */
    private int tracing;
    
    /** the logger. */
    private Logger log;

    /**
     * Creates a ThreadDataSource instance.
     * 
     * @param dataSource delegate DataSource.
     * @param tracing tracing depth.
     * @param context thread's processing context.
     * @param log the logger to report error to.
     */    
    public ThreadDataSource(DataSource dataSource, int tracing, Context context, Logger log)
    {
        super(dataSource);
        this.context = context;
        this.tracing = tracing;
        this.log = log;
    }
    
    // DataSource interface /////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */    
    public Connection getConnection()
        throws SQLException
    {
        Connection conn = getCachedConnection(null);
        if(conn == null)
        {
            conn = super.getConnection();
            conn = new ThreadConnection(conn, null, log);
            setCachedConnection(conn, null);
        }
        else
        {
            ((ThreadConnection)conn).enter();
        }
        return conn;
    }

    /**
     * {@inheritDoc}
     */    
    public Connection getConnection(String user, String password)
        throws SQLException
    {
        Connection conn = getCachedConnection(user);
        if(conn == null)
        {
            conn = super.getConnection(user, password);
            conn = new ThreadConnection(conn, user, log);
            setCachedConnection(conn, user);
        }
        else
        {
            ((ThreadConnection)conn).enter();
        }
        return conn;
    }
    
    // GuardValve ///////////////////////////////////////////////////////////////////////////////
    
    /**
     * A valve that makes sure the ivoking thread has released all database connections.
     * 
     * <p> This valve should be used as ThreadPool cleanup valve, and in the "finally" section
     * of HTTP processing pipelines to prevent database connection pool depletion in case of
     * incorrectly written code.</p>
     * 
     * <p>When the valve is invoked, the thread is verified against all the data sources, to check
     * that has released the connection properly. If not, error messge is written to the log, 
     * and the connection is forcibly closed.</p>
     * 
     * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
     * @version $Id: ThreadDataSource.java,v 1.7 2005-10-07 15:35:41 rafal Exp $
     */
    public static class GuardValve
        implements Valve
    {
        private Logger log;
                
        /**
         * Constructs a GuardValve instance.
         * 
         * @param log the logger.
         */
        public GuardValve(Logger log)
        {
            this.log = log;
        }
        
        /** 
         * {@inheritDoc}
         */
        public void process(Context context) throws ProcessingException
        {
            cleanupState(context, log);
        }
    }

    // implementation ///////////////////////////////////////////////////////////////////////////

    void trace(boolean enter, String user, int refCount)
    {
        if(tracing > 0)
        {
            StringBuilder trace = (StringBuilder)context.getAttribute(TRACE_BUFFER);
            if(trace == null)
            {
                trace = new StringBuilder();
                context.setAttribute(TRACE_BUFFER, trace);
            }
            StringUtils.indent(trace, (refCount-1)*2).append("connection ");
            if(user != null)
            {
                trace.append("for user ").append(user).append(' ');
            }
            trace.append(enter ? "opened" : "closed").append(" at\n");
            StackTraceElement[] frames = new Exception().getStackTrace();
            int start = 0;
            for(int i=0; i<frames.length; i++)
            {
                if(frames[i].getMethodName().equals(enter ? "getConnection" : "close"))
                {
                    start = i;
                    break;
                }
            }
            for(int i=start+1; i<frames.length && i<=start+tracing; i++)
            {
                StringUtils.indent(trace, (refCount-1)*2).append(frames[i].toString()).append('\n');
            }
        }
    }
    
    /**
     * @param conn a new connection for the thread.
     */
    void setCachedConnection(Connection conn, String user)
    {
        Map threadMap = (Map)context.getAttribute(THREAD_MAP);
        if(threadMap == null)
        {
            threadMap = new HashMap();
            context.setAttribute(THREAD_MAP, threadMap);
        }
        Map userMap = (Map)threadMap.get(this);
        if(userMap == null)
        {
            userMap = new HashMap();
            threadMap.put(this, userMap);
        }
        if(conn != null)
        {
            userMap.put(user, conn);
        }
        else
        {
            userMap.remove(user);
        }
    }

    /**
     * @return the cached connection.
     */
    private Connection getCachedConnection(String user)
    {
        Map threadMap = (Map)context.getAttribute(THREAD_MAP);
        if(threadMap == null)
        {
            return null;
        }
        Map userMap = (Map)threadMap.get(this);
        if(userMap == null)
        {
            return null;
        }
        return (Connection)userMap.get(user);
    }

    /**
     * Returns the connection openning/closing trace.
     *  
     * @param context thread's processing context.
     * @return connection trace, or <code>null</code> if tracing is disabled.
     */
    public static String getTrace(Context context)
    {
        StringBuilder trace = (StringBuilder)context.getAttribute(TRACE_BUFFER);
        if(trace != null)
        {
            return trace.toString();
        }
        else
        {
            return "Set tracing parameter to 1 or more to see the places "+
                "where connections were opened and closed.\n";
        }
    }
    
    /**
     * Checks if the calling thread has opened any database connections.
     *
     * @param context thread's processing context.
     * @return <code>true</code> if the calling thread has opened any database connections.
     */
    public static boolean hasOpenConnections(Context context)
    {
        Map threadMap = (Map)context.getAttribute(THREAD_MAP);
        if(threadMap != null)
        {
            Iterator i = threadMap.values().iterator();
            while(i.hasNext())
            {
                Map userMap = (Map)i.next();
                if(!userMap.isEmpty())
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Close the connection if the thread has one, and report the error condition.
     *
     * @param context thread's processing context.
     */
    static void cleanupState(Context context, Logger log)
    {
        Map threadMap = (Map)context.getAttribute(THREAD_MAP);
        if(threadMap != null)
        {
            Iterator i = threadMap.values().iterator();
            while(i.hasNext())
            {
                Map userMap = (Map)i.next();
                if(userMap != null && !userMap.isEmpty())
                {
                    log.error("Thread owns an open connection.\n"+
                        getTrace(context)+
                        "Attempting cleanup now.");
                    Iterator j = userMap.values().iterator();
                    while(j.hasNext())
                    {
                        ThreadConnection conn = (ThreadConnection)j.next();
                        conn.logUsage();
                        try
                        {
                            conn.closeConnection();
                        }
                        catch(SQLException e)
                        {
                            log.error("failed to close connection", e);
                        }
                    }
                }
                
            }
        }
    }

    /**
     * A thread's cached connection.
     */    
    public class ThreadConnection
        extends DelegatingConnection
    {
        private final String user;
        
        private final Logger log;
        
        private int refCount = 1;        
        
        private int reads = 0;
        
        private int writes = 0;
        
        private long startTime;
        
        private long totalTimeMillis = 0L;
        
        ThreadConnection(Connection conn, String user, Logger log)
        {
            super(conn);
            this.user = user;
            this.log = log;
            trace(true, user, refCount);
        }
        
        void enter()
        {
            refCount++;
            trace(true, user, refCount);
        }
        
        private void leave()
            throws SQLException
        {
            trace(false, user, refCount);
            refCount--;
            if(refCount == 0)
            {
                closeConnection();
            }
            if(refCount < 0)
            {
                throw new SQLException("too many close() calls");
            }
        }
        
        private void registerRead(long timeMillis)
        {
            reads++;
            totalTimeMillis += timeMillis;
        }
        
        private void registerWrite(long timeMillis)
        {
            writes++;
            totalTimeMillis += timeMillis;
        }
        
        void startStatement(String sql)
        {
            log.debug(sql);
            startTime = System.currentTimeMillis();
        }
        
        void finishStatement(String sql)
        {
            long timeMillis = System.currentTimeMillis() - startTime;
            if(sql.trim().toUpperCase().startsWith("SELECT"))
            {
                registerRead(timeMillis);
            }
            else
            {
                registerWrite(timeMillis);
            }
        }
        
        void logUsage()
        {
            log.info(reads + " reads, " + writes + " writes " + " spent " + totalTimeMillis + "ms");
        }        
        
        void closeConnection()
            throws SQLException
        {
            getDelegate().close();
            setCachedConnection(null, user);
        }
        
        

        // Connection interface /////////////////////////////////////////////////////////////////
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected Statement wrapStatement(Statement orig)
        {
            return new MonitoringStatement(orig, this, log);
        }

        /**
         * {@inheritDoc}
         */
        public void close()
            throws SQLException
        {
            leave();
        }
    }
}
