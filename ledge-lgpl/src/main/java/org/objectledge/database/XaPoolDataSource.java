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

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.enhydra.jdbc.core.CoreDataSource;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.database.impl.DelegatingDataSource;
import org.objectledge.logging.LoggingConfigurator;
import org.picocontainer.Startable;

/**
 * An implementation of DataSource interface using HSQLDB.
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: XaPoolDataSource.java,v 1.3 2005-02-10 17:46:08 rafal Exp $
 */
public class XaPoolDataSource extends DelegatingDataSource
    implements XADataSource, Startable
{
    
    /**
     * Constructs a DataSource instance.
     * 
     * @param transaction transaction manager wrapper.
     * @param config the data source configuration.
     * @param loggingConfigurator enforces instantiation order on Pico, may be null.
     * @throws ConfigurationException if the configuration is invalid.
     * @throws SQLException if the pool could not be initialized.
     */
    public XaPoolDataSource(Transaction transaction, Configuration config, LoggingConfigurator 
        loggingConfigurator)
        throws ConfigurationException, SQLException
    {
        super(getDataSource(transaction, config));
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
        // I wish Startable interface was split
    }

    /**
     * Forcibly terminates all connections in the pool.
     */    
    public void stop()
    {
        ((CoreDataSource)getDelegate()).shutdown(true);
    }

    /**
     * Initializes the pool.
     * 
     * @param transaction transaction manager wrapper.
     * @param config the data source configuration.
     * @return configured deleate data source.
     * @throws ConfigurationException if the configuration is invalid.
     * @throws SQLException if the pool could not be initialized.
     */
    private static DataSource getDataSource(Transaction transaction, Configuration config)
        throws ConfigurationException, SQLException
    {
        StandardXADataSource xaDataSource = new StandardXADataSource();
        xaDataSource.setTransactionManager(transaction.getTransactionManager());

        Configuration connectionConfig = config.getChild("connection");     
        String driverName = connectionConfig.getChild("driver").getValue();
        String url = connectionConfig.getChild("url").getValue();
        String user = connectionConfig.getChild("user").getValue(null);
        String password = connectionConfig.getChild("password").getValue(null);

        int loginTimeout = connectionConfig.getChild("login-timeout").getValueAsInteger(-1);
        int transactionIsolation = connectionConfig.
            getChild("transaction-isolation").getValueAsInteger(-1);
        int preparedStmtCacheSize = connectionConfig.
            getChild("prepared-statement-cache-size").getValueAsInteger(-1);

        Configuration deadlockConfig = config.getChild("deadlock"); 
        long deadlockMaxWait = deadlockConfig.getChild("max-wait").getValueAsInteger(-1);
        long deadlockRetryWait = deadlockConfig.getChild("retry-wait").getValueAsInteger(-1);
        
        // CoreDataSource
        if(user != null)
        {
            xaDataSource.setUser(user);
        }
        if(password != null)
        {
            xaDataSource.setPassword(password);
        }
        if(loginTimeout != -1)
        {
            xaDataSource.setLoginTimeout(loginTimeout);
        }
        // StandardDataSource
        xaDataSource.setDriverName(driverName);
        xaDataSource.setUrl(url);
        if(transactionIsolation != -1)
        {
            xaDataSource.setTransactionIsolation(transactionIsolation);
        }
        // StandardConnectionPoolDataSource  
        if(preparedStmtCacheSize != -1)
        {
            xaDataSource.setPreparedStmtCacheSize(preparedStmtCacheSize);
        }
        // StandardXADataSource
        if(deadlockMaxWait != -1)
        {
            xaDataSource.setDeadLockMaxWait(deadlockMaxWait);        
        }
        if(deadlockRetryWait != -1)
        {
            xaDataSource.setDeadLockRetryWait(deadlockRetryWait);
        }
        xaDataSource.setTransactionManager(transaction.getTransactionManager());
        
        // pooling //////////////////////////////////////////////////////////////////////////////

        Configuration poolConfig = config.getChild("pool");

        Configuration minSizeConfig = poolConfig.getChild("capacity").getChild("min");
        int minSize = minSizeConfig.getValueAsInteger(-1);
        Configuration maxSizeConfig = poolConfig.getChild("capacity")
            .getChild("max");
        int maxSize = maxSizeConfig.getValueAsInteger(-1);
        boolean gc = poolConfig.getChild("cleanup").
            getChild("gc").getValueAsBoolean(false);
        int lifeTime = poolConfig.getChild("cleanup").
            getChild("unused-life-time").getValueAsInteger(-1);
        int sleepTime = poolConfig.getChild("cleanup").
            getChild("interval").getValueAsInteger(-1);
        int checkLevel = poolConfig.getChild("checking").
            getChild("level").getValueAsInteger(0);
        String jdbcTestStatement = poolConfig.getChild("checking").
            getChild("statement").getValue(null);
        
        StandardXAPoolDataSource xaPoolDataSource = new StandardXAPoolDataSource(xaDataSource);
        xaPoolDataSource.setTransactionManager(transaction.getTransactionManager());
        xaPoolDataSource.setDataSource(xaDataSource);

        // CoreDataSource
        if(user != null)
        {
            xaPoolDataSource.setUser(user);
        }
        if(password != null)
        {
            xaPoolDataSource.setPassword(password);
        }
        // StandardPoolDataSource
        if(minSize != -1)
        {
            try
            {
                xaPoolDataSource.setMinSize(minSize);
            }
            catch(Exception e)
            {
                throw new ConfigurationException("illegal minimum pool size", 
                    minSizeConfig.getPath(), minSizeConfig.getLocation(), e);
            }
        }
        if(maxSize != -1)
        {
            try
            {
                xaPoolDataSource.setMaxSize(maxSize);
            }
            catch(Exception e)
            {
                throw new ConfigurationException("illegal maximum pool size", 
                    maxSizeConfig.getPath(), maxSizeConfig.getLocation(), e);
            }
        }
        xaPoolDataSource.setGC(gc);
        if(lifeTime != -1)
        {
            xaPoolDataSource.setLifeTime(lifeTime);
        }
        if(sleepTime != -1)
        {
            xaPoolDataSource.setLifeTime(sleepTime);
        }
        xaPoolDataSource.setCheckLevelObject(checkLevel);
        if(jdbcTestStatement != null)
        {
            xaPoolDataSource.setJdbcTestStmt(jdbcTestStatement);
        }
        return xaPoolDataSource;
    }
    
    /**
     * {@inheritDoc}
     */
    public XAConnection getXAConnection()
        throws SQLException
    {
        return ((XADataSource)getDelegate()).getXAConnection();
    }

    /**
     * {@inheritDoc}
     */
    public XAConnection getXAConnection(String user, String password)
        throws SQLException
    {
        return ((XADataSource)getDelegate()).getXAConnection(user, password);
    }
}
