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
 * @version $Id: ThreadDataSource.java,v 1.1 2004-02-06 14:29:19 fil Exp $
 */
public class ThreadDataSource
    extends DelegatingDataSource
{
    /** {@link Conext} key under which DataSource -> ThreadConnection map is kept. */ 
    public static final String THREAD_MAP = "org.objectledge.database.ThreadDataSource.threadMap";
    
    /** {@link Conext} key under where open/close tracing buffer is kept. */ 
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
            conn = new ThreadConnection(conn, null);
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
            conn = new ThreadConnection(conn, user);
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
     * <p>Only one instance of this valve is necessary, it takes an array of all configured 
     * ThreadDataSource instances into it's constructors.</p>
     * <p>When the valve is invoked, the thread is verified against all the data sources, to check
     * that has released the connection properly. If not, error messge is written to the log, 
     * and the connection is forcibly closed.</p>
     * 
     * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
     * @version $Id: ThreadDataSource.java,v 1.1 2004-02-06 14:29:19 fil Exp $
     */
    public static class GuardValve
        implements Valve
    {
        /** the thread data sources to guard. */
        private ThreadDataSource[] threadDataSources;
        
        /**
         * Constructs a GuardValve instance.
         * 
         * @param threadDataSources the threadDataSources to guard.
         */
        public GuardValve(ThreadDataSource[] threadDataSources)
        {
            this.threadDataSources = threadDataSources;
        }
        
        /** 
         * {@inheritDoc}
         */
        public void process(Context context) throws ProcessingException
        {
            for(int i=0; i<threadDataSources.length; i++)
            {
                threadDataSources[i].cleanupState();
            }
        }
    }

    // implementation ///////////////////////////////////////////////////////////////////////////

    private void trace(boolean enter, String user, int refCount)
    {
        if(tracing > 0)
        {
            StringBuffer trace = (StringBuffer)context.getAttribute(TRACE_BUFFER);
            if(trace == null)
            {
                trace = new StringBuffer();
                context.setAttribute(TRACE_BUFFER, trace);
            }
            indent(trace, (refCount-1)*2).append("connection ");
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
                indent(trace, (refCount-1)*2).append(frames[i].toString()).append('\n');
            }
        }
    }
    
    private StringBuffer indent(StringBuffer buffer, int d)
    {
        for(int i=0; i<d; i++)
        {
            buffer.append(' ');
        }
        return buffer;
    }

    /**
     * @param conn a new connection for the thread.
     */
    private void setCachedConnection(Connection conn, String user)
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
     * @return
     */
    private Connection getCachedConnection(String user)
    {
        Map threadMap = (Map)context.getAttribute(THREAD_MAP);
        if(threadMap == null)
        {
            return null;
        }
        Map userMap = (Map)threadMap.get(this);
        return (Connection)userMap.get(user);
    }

    /**
     * Returns the connection openning/closing trace.
     *  
     * @return connection trace, or <code>null</code> if tracing is disabled.
     */
    private String getTrace()
    {
        StringBuffer trace = (StringBuffer)context.getAttribute(TRACE_BUFFER);
        if(trace != null)
        {
            return trace.toString();
        }
        else
        {
            return null;
        }
    }

    /**
     * Close the connection if the thread has one, and report the error condition.
     */
    private void cleanupState()
    {
        Map threadMap = (Map)context.getAttribute(THREAD_MAP);
        if(threadMap != null)
        {
            Map userMap = (Map)threadMap.get(this);
            if(userMap != null && !userMap.isEmpty())
            {
                String trace = getTrace();
                if(trace == null)
                {
                    trace = "Set tracing parameter to 1 or more"+
                    " to see the places where connections were opened and closed.\n";
                }
                log.error("Thread owns an open connection.\n"+trace+
                    "Attempting cleanup now.");
                Iterator i = userMap.values().iterator();
                while(i.hasNext())
                {
                    ThreadConnection conn = (ThreadConnection)i.next();
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

    /**
     * A thread's cached connection.
     */    
    public class ThreadConnection
        extends DelegatingConnection
    {
        private int refCount = 1;
        
        private String user;
        
        private ThreadConnection(Connection conn, String user)
        {
            super(conn);
            this.user = user;
            trace(true, user, refCount);
        }
        
        private void enter()
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
        
        private void closeConnection()
            throws SQLException
        {
            getDelegate().close();
            setCachedConnection(null, user);
        }

        // Connection interface /////////////////////////////////////////////////////////////////
        
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
