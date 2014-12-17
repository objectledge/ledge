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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.database.impl.DelegatingConnection;
import org.objectledge.database.impl.DelegatingDataSource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.statistics.AbstractMuninGraph;
import org.objectledge.statistics.MuninGraph;
import org.objectledge.statistics.StatisticsProvider;
import org.objectledge.utils.StringUtils;

/**
 * A decorator for javax.sql.DataSource interface that makes sure a Thread uses only one physical
 * connection to the database.
 * <p>
 * Using more than one connection at different levels of the execution stack of a thread often leads
 * to java monitor -> data base lock -> java monitor deadlocks that are hard to reproduce and
 * diagnose. There are two techniques for avoiding that. One is passing the Connection object in
 * call arguments so the code that may needed gets the right instance. Another is using this
 * decorator that uses the thread's {@link Context} to cache the connection. All calls to
 * {@link #getConnection()} within the thread's execution context will return the same Connection
 * instance, and all calls {@link #getConnection(String,String)} will return the same Connection
 * instance per user argument value.
 * </p>
 * <p>
 * A special valve {@link GuardValve} is provided for checking if the thread has closed it's
 * connections properly.
 * </p>
 * <p>
 * If you are getting messages about threads owning open connectin in the log, you should set the
 * tracing parameters to non-zero value. Bigger values will put more stack frames into the trace.
 * </p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ThreadDataSource.java,v 1.15 2008-01-20 15:17:37 rafal Exp $
 */
public class ThreadDataSource
    extends DelegatingDataSource
{
    /**
     * {@link org.objectledge.context.Context} key under which DataSource -> ThreadConnection map is
     * kept.
     */
    public static final String THREAD_MAP = "org.objectledge.database.ThreadDataSource.threadMap";

    /**
     * {@link org.objectledge.context.Context}key under where open/close tracing buffer is kept.
     */
    public static final String TRACE_BUFFER = "org.objectledge.database.ThreadDataSource.traceBuffer";

    /** thread's processing context. */
    private final Context context;

    /** Component configuration. */
    private final Config config;

    /** the logger. */
    private final Logger log;

    /** the logger for SQL statements. */
    private final Logger statementLog;

    private final Statistics statistics;

    private static final Map<String, String> connToThread = new ConcurrentHashMap<>();

    private boolean suppressNonPostgresWarning = false;

    /**
     * Creates a ThreadDataSource instance.
     * 
     * @param dataSource the delegate DataSource.
     * @param config component's configuration.
     * @param context thread's processing context.
     * @param loggingConfigurator the logging configurator (may be null).
     * @param statistics Statistics component (may be null).
     * @param log the logger.
     */
    public ThreadDataSource(DataSource dataSource, Config config, Context context,
        LoggingConfigurator loggingConfigurator, Statistics statistics, Logger log)
    {
        super(dataSource);
        this.context = context;
        this.config = config;
        this.log = log;
        if(config.getStatementLogName() != null && loggingConfigurator != null)
        {
            this.statementLog = loggingConfigurator.createLogger(config.getStatementLogName());
        }
        else
        {
            this.statementLog = log;
        }
        this.statistics = statistics;
    }

    /**
     * Creates a new ThreadDataSource instance.
     * 
     * @param dataSource the delegate DataSource.
     * @param config component's configuration.
     * @param context thread's processing context.
     * @param loggingConfigurator the logging configurator (may be null).
     * @param statistics Statistics component (may be null).
     * @param log the logger.
     */
    public ThreadDataSource(DataSource dataSource, Configuration config,
        LoggingConfigurator loggingConfigurator, Context context, Statistics statistics, Logger log)
    {
        this(dataSource, new Config(config), context, loggingConfigurator, statistics, log);
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
            conn = acquireConnection(null, null, config.getRetryCount());
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
            conn = acquireConnection(user, password, config.getRetryCount());
        }
        else
        {
            ((ThreadConnection)conn).enter();
        }
        return conn;
    }

    private String getConnId(Connection conn)
    {
        StringBuilder buff = new StringBuilder();
        if(conn.getClass().getName().startsWith("org.enhydra"))
        {
            try
            {
                Field c = conn.getClass().getField("con");
                Object nested = c.get(conn);
                buff.append(nested.getClass().getName()).append("@")
                    .append(Integer.toString(System.identityHashCode(nested), 16));
                if(nested.getClass().getName().contains("postgres"))
                {
                    buff.append(" pid: ").append(postgresPid(nested));
                }
            }
            catch(NoSuchFieldException | SecurityException | IllegalArgumentException
                            | IllegalAccessException e)
            {
                log.error("introspection problem ", e);
            }
        }
        else if(conn.getClass().getName().contains("$Proxy"))
        {
            buff.append(conn.toString());
            try
            {
                final Class<?> pgConnClass = Class.forName("org.postgresql.PGConnection");
                final Method getPid = pgConnClass.getMethod("getBackendPID");
                if(conn.isWrapperFor(pgConnClass))
                {
                    try
                    {
                        int pid = (Integer)getPid.invoke(conn.unwrap(Connection.class),
                            new Object[0]);
                        buff.append(" pid: ").append(pid);
                    }
                    catch(Exception e)
                    {
                        log.error("introspection problem ", e);
                    }
                }
            }
            catch(Exception e)
            {
                if(!suppressNonPostgresWarning)
                {
                    log.warn(
                        "introspection problem, probably not a Postgress connection, or patched driver not avaialable",
                        e);
                    suppressNonPostgresWarning = true;
                }
            }
        }
        else
        {
            buff.append(conn.getClass().getName()).append("@")
                .append(Integer.toString(System.identityHashCode(conn), 16));
        }
        return buff.toString();
    }

    private String getWrapperId(Connection conn, String connId)
    {
        StringBuilder buff = new StringBuilder();
        buff.append(conn.getClass().getName()).append("@")
            .append(Integer.toString(System.identityHashCode(conn), 16));
        if(!buff.toString().equals(connId))
        {
            buff.append(" wrapping ").append(connId);
        }
        return buff.toString();
    }

    private int postgresPid(Object obj)
    {
        try
        {
            Class<?> pgAbstractJdbc2Connection = Class
                .forName("org.postgresql.jdbc2.AbstractJdbc2Connection");
            Field protoConnectionField = pgAbstractJdbc2Connection
                .getDeclaredField("protoConnection");
            protoConnectionField.setAccessible(true);
            Object protoConnection = protoConnectionField.get(obj);
            Class<?> pgProtocolConnectionImpl = protoConnection.getClass();
            Field pidField = pgProtocolConnectionImpl.getDeclaredField("cancelPid");
            pidField.setAccessible(true);
            return (Integer)pidField.get(protoConnection);
        }
        catch(ClassNotFoundException | NoSuchFieldException | SecurityException
                        | IllegalArgumentException | IllegalAccessException e)
        {
            log.error("introspection problem", e);
        }
        return 0;
    }

    private void registerConnection(Connection conn)
    {
        String connId = getConnId(conn);
        String wrapperId = getWrapperId(conn, connId);
        String threadName = Thread.currentThread().getName();
        if(connToThread.containsKey(connId))
        {
            log.error("associating connection " + wrapperId + " with thread " + threadName
                + " while " + connId + " is already associated with thread "
                + connToThread.get(connId));
        }
        else
        {
            connToThread.put(connId, threadName);
            log.info("associating connection " + wrapperId + " with thread " + threadName);
        }
    }

    private void unregisterConnection(Connection conn)
    {
        String connId = getConnId(conn);
        String wrapperId = getWrapperId(conn, connId);
        String threadName = Thread.currentThread().getName();
        connToThread.remove(connId);
        log.info("disassociating connection " + wrapperId + " from thread " + threadName);
    }

    private Connection acquireConnection(String user, String password, int retries)
        throws SQLException
    {
        if(retries > 0)
        {
            try
            {
                Connection conn;
                if(user != null)
                {
                    conn = super.getConnection(user, password);
                }
                else
                {
                    conn = super.getConnection();
                }
                setApplicationName(conn, Thread.currentThread().getName());
                if(validateConnection(conn))
                {
                    registerConnection(conn);
                    conn = new ThreadConnection(conn, user);
                    setCachedConnection(conn, user);
                    return conn;
                }
                else
                {
                    log.error("invalid connection acquired, attempting to reconnect");
                    try
                    {
                        conn.close();
                    }
                    catch(SQLException e)
                    {
                        log.error("exception when closing invalid connection", e);
                    }
                    return acquireConnection(user, password, retries - 1);
                }
            }
            catch(SQLException e)
            {
                log.error("failed to acquire connection, attempting to reconnect", e);
                return acquireConnection(user, password, retries - 1);
            }
        }
        else
        {
            throw new SQLException("unable to acquire a valid connection");
        }
    }

    private boolean validateConnection(Connection conn)
    {
        if(config.validationQuery != null)
        {
            try(Statement stmt = conn.createStatement())
            {
                stmt.executeQuery(config.validationQuery);
                return true;
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                log.debug("connection validation failed", e);
                return false;
            }
        }
        return true;
    }

    // GuardValve ///////////////////////////////////////////////////////////////////////////////

    /**
     * A valve that makes sure the ivoking thread has released all database connections.
     * <p>
     * This valve should be used as ThreadPool cleanup valve, and in the "finally" section of HTTP
     * processing pipelines to prevent database connection pool depletion in case of incorrectly
     * written code.
     * </p>
     * <p>
     * When the valve is invoked, the thread is verified against all the data sources, to check that
     * has released the connection properly. If not, error messge is written to the log, and the
     * connection is forcibly closed.
     * </p>
     * 
     * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
     * @version $Id: ThreadDataSource.java,v 1.15 2008-01-20 15:17:37 rafal Exp $
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
        public void process(Context context)
            throws ProcessingException
        {
            cleanupState(context, log);
        }
    }

    // implementation ///////////////////////////////////////////////////////////////////////////

    void trace(boolean enter, String user, int refCount)
    {
        if(config.getTracing() > 0)
        {
            StringBuilder trace = (StringBuilder)context.getAttribute(TRACE_BUFFER);
            if(trace == null)
            {
                trace = new StringBuilder();
                context.setAttribute(TRACE_BUFFER, trace);
            }
            StringUtils.indent(trace, (refCount - 1) * 2).append("connection ");
            if(user != null)
            {
                trace.append("for user ").append(user).append(' ');
            }
            trace.append(enter ? "opened" : "closed").append(" at\n");
            StackTraceElement[] frames = new Exception().getStackTrace();
            int start = 0;
            for(int i = 0; i < frames.length; i++)
            {
                if(frames[i].getMethodName().equals(enter ? "getConnection" : "close"))
                {
                    start = i;
                    break;
                }
            }
            for(int i = start + 1; i < frames.length && i <= start + config.getTracing(); i++)
            {
                StringUtils.indent(trace, (refCount - 1) * 2).append(frames[i].toString())
                    .append('\n');
            }
        }
    }

    private static Map<ThreadDataSource, Map<String, Connection>> getThreadMap(Context context)
    {
        return (Map<ThreadDataSource, Map<String, Connection>>)context.getAttribute(THREAD_MAP);
    }

    /**
     * @param conn a new connection for the thread.
     */
    void setCachedConnection(Connection conn, String user)
    {
        Map<ThreadDataSource, Map<String, Connection>> threadMap = getThreadMap(context);
        if(threadMap == null)
        {
            threadMap = new HashMap<ThreadDataSource, Map<String, Connection>>();
            context.setAttribute(THREAD_MAP, threadMap);
        }
        Map<String, Connection> userMap = threadMap.get(this);
        if(userMap == null)
        {
            userMap = new HashMap<String, Connection>();
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
        Map<ThreadDataSource, Map<String, Connection>> threadMap = getThreadMap(context);
        if(threadMap == null)
        {
            return null;
        }
        Map<String, Connection> userMap = threadMap.get(this);
        if(userMap == null)
        {
            return null;
        }
        return userMap.get(user);
    }

    private void setApplicationName(Connection conn, String name)
    {
        try
        {
            DatabaseMetaData md = conn.getMetaData();
            String dbProd = md.getDatabaseProductName();
            int dbMaj = md.getDatabaseMajorVersion();
            if(dbProd.equals("PostgreSQL") && dbMaj >= 9)
            {
                conn.setClientInfo("ApplicationName", name);
            }
        }
        catch(SQLException e)
        {
            log.error("failed to set ApplicationName", e);
        }
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
            return "Set tracing parameter to 1 or more to see the places "
                + "where connections were opened and closed.\n";
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
        Map<ThreadDataSource, Map<String, Connection>> threadMap = getThreadMap(context);
        if(threadMap != null)
        {
            Iterator<Map<String, Connection>> i = threadMap.values().iterator();
            while(i.hasNext())
            {
                Map<String, Connection> userMap = i.next();
                Iterator<Connection> j = userMap.values().iterator();
                while(j.hasNext())
                {
                    ThreadConnection conn = (ThreadConnection)j.next();
                    if(conn.isInUse())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void unusedConnection(ThreadConnection conn)
        throws SQLException
    {
        setApplicationName(conn, null);
        if(!config.isCacheConnection())
        {
            conn.closeConnection();
        }
    }

    /**
     * Close the connection if the thread has one, and report the error condition.
     * 
     * @param context thread's processing context.
     */
    static void cleanupState(Context context, Logger log)
    {
        Map<ThreadDataSource, Map<String, Connection>> threadMap = getThreadMap(context);
        if(threadMap != null)
        {
            Iterator<Map<String, Connection>> i = threadMap.values().iterator();
            while(i.hasNext())
            {
                Map<String, Connection> userMap = i.next();
                if(userMap != null && !userMap.isEmpty())
                {
                    Iterator<Connection> j = userMap.values().iterator();
                    while(j.hasNext())
                    {
                        ThreadConnection conn = (ThreadConnection)j.next();
                        if(conn.isInUse())
                        {
                            log.error("Thread owns an open connection.\n" + getTrace(context)
                                + "Attempting cleanup now.");
                        }
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

    void updateStatistics(int reads, int writes, long timeMillis)
    {
        if(reads + writes > 0)
        {
            if(statistics != null)
            {
                statistics.update(reads, writes, timeMillis);
            }
            log.info(reads + " reads, " + writes + " writes " + " spent " + timeMillis + "ms");
            if(!log.equals(statementLog))
            {
                statementLog.info(reads + " reads, " + writes + " writes " + " spent " + timeMillis
                    + "ms");
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

        private int refCount = 1;

        private int reads = 0;

        private int writes = 0;

        private long startTime;

        private long totalTimeMillis = 0L;

        ThreadConnection(Connection conn, String user)
        {
            super(conn);
            this.user = user;
            trace(true, user, refCount);
        }

        // usage tracing ////////////////////////////////////////////////////////////////////////

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
                unusedConnection(this);
            }
            if(refCount < 0)
            {
                throw new SQLException("too many close() calls");
            }
        }

        boolean isInUse()
        {
            return refCount > 0;
        }

        // statement tracing ////////////////////////////////////////////////////////////////////

        void startStatement(String sql)
        {
            statementLog.debug(sql);
            startTime = System.currentTimeMillis();
        }

        void finishStatement(String sql)
        {
            totalTimeMillis += System.currentTimeMillis() - startTime;
            if(sql.trim().toUpperCase().startsWith("SELECT"))
            {
                reads++;
            }
            else
            {
                writes++;
            }
        }

        void closeConnection()
            throws SQLException
        {
            updateStatistics(reads, writes, totalTimeMillis);
            unregisterConnection(getDelegate());
            setCachedConnection(null, user);
            getDelegate().close();
        }

        // dependant objects handling ///////////////////////////////////////////////////////////

        /**
         * {@inheritDoc}
         */
        @Override
        protected Statement wrapStatement(Statement orig)
        {
            return new MonitoringStatement(orig, this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PreparedStatement wrapPreparedStatement(PreparedStatement orig, String sql)
        {
            return new MonitoringPreparedStatement(orig, sql, this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CallableStatement wrapCallableStatement(CallableStatement orig, String sql)
        {
            return new MonitoringCallableStatement(orig, sql, this);
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

    /**
     * Publishes database usage statistics.
     */
    public static class Statistics
        implements StatisticsProvider
    {
        private final MuninGraph[] graphs;

        public Statistics(FileSystem fs)
        {
            graphs = new MuninGraph[] { new StatementCount(fs), new AccessTime(fs) };
        }

        private int totalReads = 0;

        private int totalWrites = 0;

        private long totalAccessTime = 0;

        /**
         * {@inheritDoc}
         */
        public String getName()
        {
            return "Database";
        }

        /**
         * {@inheritDoc}
         */
        public MuninGraph[] getGraphs()
        {
            return graphs;
        }

        public class StatementCount
            extends AbstractMuninGraph
        {
            public StatementCount(FileSystem fs)
            {
                super(fs);
            }

            public String getId()
            {
                return "dbStatementCount";
            }

            /**
             * Returns number of preformed DB reads.
             * 
             * @return number of preformed DB reads.
             */
            public int getReads()
            {
                return totalReads;
            }

            /**
             * Returns number of preformed DB writes.
             * 
             * @return number of preformed DB writes.
             */
            public int getWrites()
            {
                return totalWrites;
            }
        }

        public class AccessTime
            extends AbstractMuninGraph
        {
            public AccessTime(FileSystem fs)
            {
                super(fs);
            }

            public String getId()
            {
                return "dbAccessTime";
            }

            /**
             * Returns total DB access time in milliseconds.
             * 
             * @return total DB access time in milliseconds.
             */
            public long getTime()
            {
                return totalAccessTime;
            }
        }

        /**
         * Update db access statistics.
         * 
         * @param reads number of performed reads.
         * @param writes number of performed writes.
         * @param accessTime combined duration of these reads and writes.
         */
        void update(int reads, int writes, long accessTime)
        {
            totalReads += reads;
            totalWrites += writes;
            totalAccessTime += accessTime;
        }
    }

    /**
     * ThreadDataSource component configuration.
     */
    public static class Config
    {
        /** tracing depth (0 if disabled). */
        private int tracing = 0;

        /** should the thread's connection be cached while unused. */
        private boolean cacheConnection = false;

        /** name of the Logger that should be used to log SQL statements, {@code null} if disabled. */
        private String statementLogName = null;

        /** number of times connection acquisition should be retried on error. */
        private int retryCount = 1;

        /** query to be used for connection validation, {@code null} if disabled. */
        private String validationQuery = "SELECT 1";

        /**
         * Create default configuration instance.
         */
        public Config()
        {
        }

        /**
         * Create configuration instance based on DNA Configuration.
         * 
         * @param config
         */
        public Config(Configuration config)
        {
            tracing = config.getChild("tracing").getValueAsInteger(tracing);
            cacheConnection = config.getChild("cacheConnection").getValueAsBoolean(cacheConnection);
            statementLogName = config.getChild("statementLog").getValue(statementLogName);
            retryCount = config.getChild("retryCount").getValueAsInteger(retryCount);
            validationQuery = config.getChild("validationQuery").getValue(validationQuery);
        }

        public int getTracing()
        {
            return tracing;
        }

        public boolean isCacheConnection()
        {
            return cacheConnection;
        }

        public String getStatementLogName()
        {
            return statementLogName;
        }

        public int getRetryCount()
        {
            return retryCount;
        }

        public String getValidationQuery()
        {
            return validationQuery;
        }

        public Config withTracing(int tracing)
        {
            this.tracing = tracing;
            return this;
        }

        public Config withCacheConnection(boolean cacheConnection)
        {
            this.cacheConnection = cacheConnection;
            return this;
        }

        public Config withStatementLogName(String statementLogName)
        {
            this.statementLogName = statementLogName;
            return this;
        }

        public Config withRetryCount(int retryCount)
        {
            this.retryCount = retryCount;
            return this;
        }

        public Config withValidationQuery(String validationQuery)
        {
            this.validationQuery = validationQuery;
            return this;
        }
    }
}
