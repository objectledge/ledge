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

import java.sql.SQLException;

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.utils.StringUtils;

/**
 * Helps dealing with transactions in the application code.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Transaction.java,v 1.13 2005-10-03 07:27:03 rafal Exp $
 */
public abstract class Transaction
{
    /** {@link org.objectledge.context.Context} key under where tracing buffer is kept. */ 
    public static final String TRACE_BUFFER = 
        "org.objectledge.database.Transaction.traceBuffer";

    /** {@link org.objectledge.context.Context} key under where nesting counter is kept. */ 
    public static final String NESTING_COUNTER = 
        "org.objectledge.database.Transaction.nestingCounter";

    /** the logger. */
    protected Logger log;

    /** Context the context. */
    private Context context;
        
    /** The tracing depth. */
    private int tracing;   
    
    /** Requested transaction timeout. */
    private ThreadLocal<Integer> timeout = new ThreadLocal<Integer>();
    
    /** Default timeout value. */
    private int defaultTimeout;

    /** The logger for SQL statements, that will also receive transaction demarcation information. */
    private final Logger statementLog;

    /**
     * Constructs a Transaction component.
     * 
     * @param tracing tracing depth.
     * @param defaultTimeout default transaction timeout in seconds.
     * @param statementLogName The logger for SQL statements, that will also receive transaction
     *        demarcation information at {@code DEBUG} level. Pass {@code null} to disable.
     * @param context the threads processing context.
     * @param log the logger to use for error reporting.
     */
    protected Transaction(int tracing, int defaultTimeout, String statementLogName,
        Context context, Logger log, LoggingConfigurator loggingConfigurator)
    {
        this.tracing = tracing;
        this.defaultTimeout = defaultTimeout;
        this.context = context;
        this.log = log;
        this.statementLog = statementLogName != null ? loggingConfigurator
            .createLogger(statementLogName) : null;
    }
    
    /**
     * Constructs a Transaction component.
     * 
     * @param config component configuration.
     * @param context the threads processing context.
     * @param log the logger to use for error reporting.
     */
    protected Transaction(Config config, Context context, Logger log,
        LoggingConfigurator loggingConfigurator)
    {
        this.tracing = config.tracing;
        this.defaultTimeout = config.defaultTimeout;
        this.context = context;
        this.log = log;
        this.statementLog = config.statementLogName != null ? loggingConfigurator
            .createLogger(config.statementLogName) : null;
    }

    /**
     * Returns the JTA UserTransaction object.
     * 
     * @return the UserTransaction object.
     * @throws SQLException if the UserTransaction object is not accessible.
     */
    public abstract UserTransaction getUserTransaction()
        throws SQLException;
        
    /**
     * Returns the JTA TransactionManager object.
     * 
     * @return the TransactionManager object.
     * @throws SQLException if the TransactionManager object is not accessible.
     */
    public abstract TransactionManager getTransactionManager()
        throws SQLException;
    
    /**
     * Sets the timeout value for the next transaction to be started by the calling thread.
     * 
     * @param seconds the requested length of timeout period in seconds.
     * @throws SQLException if the timeout could not be set.
     */
    public void setTransactionTimeout(int seconds)
        throws SQLException
    {
        timeout.set(seconds);
    }
    
    /**
     * Begin the transaction, if there is none active.
     * 
     * @return <code>true</code> if the requester become the controller.
     * @throws SQLException if the operation fails.
     */
    public boolean begin()
        throws SQLException
    {
        boolean controler;
        try 
        {
            int status = getUserTransaction().getStatus();
            controler = (status == Status.STATUS_NO_TRANSACTION ||
                         status == Status.STATUS_COMMITTED ||
                         status == Status.STATUS_ROLLEDBACK);
        }
        catch(Exception e)
        {
            log.error("failed to check transaction status", e);
            throw (SQLException)new SQLException("failed to check transaction status").initCause(e);
        }
        trace("begin", controler);
        if(controler)
        {
            if(ThreadDataSource.hasOpenConnections(context))
            {
                log.error("Thread owns open database connection(s) " +                    "that would ignore global transaction.\n"+ThreadDataSource.getTrace(context));
                throw new SQLException("Thread owns open database connection(s) " +
                    "that would ignore global transaction (see log)");
            }
            try
            {
                getUserTransaction().setTransactionTimeout(
                    timeout.get() != null ? timeout.get() : defaultTimeout);
                getUserTransaction().begin();
            }
            catch(Exception e)
            {
                log.error("failed to begin transaction", e);
                throw (SQLException)new SQLException("failed to begin transaction").initCause(e);
            }
        }
        return controler;
    }
    
    /**
     * Commit the transaction, if the caller is the controller.
     * 
     * @param controller <code>true</code> if the caller is the controller.
     * @throws SQLException if the commit fails.
     */
    public void commit(boolean controller)
        throws SQLException
    {
        trace("commit", controller);
        if(controller)
        {
            timeout.remove(); // reset timeout to default value
            try
            {
                getUserTransaction().commit();
            }
            catch(Exception e)
            {
                log.error("commit failed", e);
                throw (SQLException)new SQLException("commit failed").initCause(e);
            }
        }
    }
    
    /**
     * Rollback the transaction, if the caller is the controller.
     * 
     * @param controller <code>true</code> if the caller is the controller.
     * @throws SQLException if the rollback fails.
     */
    public void rollback(boolean controller)
        throws SQLException
    {
        trace("rollback", controller);
        if(controller)
        {
            timeout.remove(); // reset timeout to default value
            try
            {
                getUserTransaction().rollback();
            }
            catch(Exception e)
            {
                log.error("rollback failed", e);
                throw (SQLException)new SQLException("rollback failed"+e).initCause(e);
            }
        }
        else
        {
            try
            {
                if(getUserTransaction().getStatus() == Status.STATUS_ACTIVE)
                {
                    getUserTransaction().setRollbackOnly();
                }
            }
            catch(Exception e)
            {
                log.error("setRollbackOnly failed", e);
                throw (SQLException)new SQLException("setRollbackOnly failed").initCause(e);
            }
        }
    }

    /**
     * A valve that checks if the thread finished any transactions it might have started.
     */    
    public static class GuardValve
        implements Valve
    {
        /** The transaction wrapper object. */
        private Transaction transaction;
        
        /** The logger. */
        private Logger log;
        
        /**
         * Creates an instance of the GuardValve.
         * 
         * @param transaction the Transaction.
         * @param log the logger.
         */
        public GuardValve(Transaction transaction, Logger log)
        {
            this.transaction = transaction;
            this.log = log;
        }
        
        /** 
         * {@inheritDoc}
         */
        public void process(Context context) throws ProcessingException
        {
            transaction.cleanupState(context, log);
        }
    }
    
    // implementation ///////////////////////////////////////////////////////////////////////////
    
    void cleanupState(Context context, Logger log)
    {
        try
        {
            int status = getUserTransaction().getStatus();
            if(!(status == Status.STATUS_NO_TRANSACTION ||
                status == Status.STATUS_COMMITTED ||
                status == Status.STATUS_ROLLEDBACK))
            {
                log.error("Thread owns an active transaction.\n"+
                    getTrace(context)+
                    "Rolling the transaction back.");
                try
                {
                    getUserTransaction().rollback();
                }
                catch(Exception e)
                {
                    log.error("rollaback failed", e);
                }
            }
        }
        catch(Exception e)
        {
            log.error("failed to check transaction state", e);
        }
    }
    
    private static String getTrace(Context context)
    {
        StringBuilder trace = (StringBuilder)context.getAttribute(TRACE_BUFFER);
        if(trace != null)
        {
            return trace.toString();
        }
        else
        {
            return "Set tracing parameter to 1 or more to see the places "+
                "where transaction events occured.\n";
        }
    }
    
    private void trace(String op, boolean controller)
    {
        if(statementLog != null)
        {
            statementLog.debug((controller ? "actual " : "elided ") + op);
        }
        if(tracing > 0)
        {
            Counter nestingCounter = (Counter)context.getAttribute(NESTING_COUNTER);
            if(nestingCounter == null)
            {
                nestingCounter = new Counter();
                context.setAttribute(NESTING_COUNTER, nestingCounter);
            }
            if(op.equals("begin"))
            {
                nestingCounter.inc();
            }
            else
            {
                nestingCounter.dec();
            }
            StringBuilder trace = (StringBuilder)context.getAttribute(TRACE_BUFFER);
            if(trace == null)
            {
                trace = new StringBuilder();
                context.setAttribute(TRACE_BUFFER, trace);
            }
            StringUtils.indent(trace, (nestingCounter.get()-1)*2).append(op).append(" at\n");
            StackTraceElement[] frames = new Exception().getStackTrace();
            int start = 0;
            for(int i=0; i<frames.length; i++)
            {
                if(frames[i].getMethodName().equals(op))
                {
                    start = i;
                    break;
                }
            }
            for(int i=start+1; i<frames.length && i<=start+tracing; i++)
            {
                StringUtils.indent(trace, (nestingCounter.get()-1)*2).
                    append(frames[i].toString()).append('\n');
            }
        }
    }
    
    /**
     * Tiny helper class.
     */
    private static class Counter
    {
        private int count;
        
        public Counter()
        {
            count = 0;
        }
        
        public void inc()
        {
            count++;
        }
        
        public void dec()
        {
            count--;
        }
        
        public int get()
        {
            return count;
        }
    }

    public static class Config
    {
        private int tracing;

        private int defaultTimeout;

        private String statementLogName;

        public Config(int tracing, int defaultTimeout, String statementLogName)
        {
            this.tracing = tracing;
            this.defaultTimeout = defaultTimeout;
            this.statementLogName = statementLogName;
        }

        public Config(Configuration config)
        {
            tracing = config.getChild("tracing").getValueAsInteger(0);
            defaultTimeout = config.getChild("transactionTimeout").getValueAsInteger(0);
            statementLogName = config.getChild("statementLog").getValue(null);
        }

        public int getTracing()
        {
            return tracing;
        }

        public int getDefaultTimeout()
        {
            return defaultTimeout;
        }

        public String getStatementLogName()
        {
            return statementLogName;
        }
    }
}
