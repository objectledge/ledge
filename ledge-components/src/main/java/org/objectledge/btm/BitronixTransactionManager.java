package org.objectledge.btm;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.Transaction;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.Startable;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.resource.jms.PoolingConnectionFactory;

public class BitronixTransactionManager
    implements Startable, AutoCloseable
{
    private bitronix.tm.BitronixTransactionManager btm;

    private final Transaction.Config transactionConfig = new Transaction.Config();

    private final Map<String, PoolingDataSource> dataSources = new HashMap<>();

    private final Map<String, PoolingConnectionFactory> connectionFactories = new HashMap<>();

    private final Set<ResourceBean> started = new HashSet<>();

    private final Logger log;

    public BitronixTransactionManager(org.jcontainer.dna.Configuration config,
        FileSystem fileSystem, Logger log)
        throws ConfigurationException
    {
        ConfigurationHandler.configure(dataSources, connectionFactories, transactionConfig,
            started, config, fileSystem);
        btm = TransactionManagerServices.getTransactionManager();
        this.log = log;
    }

    public BitronixTransactionManager(String dsName, String dsClass, Properties dsProperties,
        FileSystem fileSystem, Logger logger)
    {
        ConfigurationHandler.configure(dataSources, dsName, dsClass, dsProperties, fileSystem);
        btm = TransactionManagerServices.getTransactionManager();
        log = logger;
    }

    Transaction.Config getTansactionConfig()
    {
        return transactionConfig;
    }

    UserTransaction getUserTransaction()
    {
        return btm;
    }

    TransactionManager getTransactionManager()
    {
        return btm;
    }

    DataSource getDataSource(String uniqueName)
    {
        final PoolingDataSource dataSource = dataSources.get(uniqueName);
        started.add(dataSource);
        return dataSource;
    }

    ConnectionFactory getConnectionFactory(String uniqueName)
    {
        final PoolingConnectionFactory connectionFactory = connectionFactories.get(uniqueName);
        started.add(connectionFactory);
        return connectionFactory;
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
        for(PoolingDataSource dataSource : dataSources.values())
        {
            if(started.contains(dataSource))
            {
                try
                {
                    DatabaseUtils.shutdown(dataSource);
                }
                catch(bitronix.tm.resource.ResourceConfigurationException | SQLException e)
                {
                    log.error("excetion while closing data source", e);
                }
                dataSource.close();
            }
        }
        for(PoolingConnectionFactory connectionFactory : connectionFactories.values())
        {
            if(started.contains(connectionFactory))
            {
                connectionFactory.close();
            }
        }
        btm.shutdown();
    }

    @Override
    public void close()
    {
        stop();
    }
}
