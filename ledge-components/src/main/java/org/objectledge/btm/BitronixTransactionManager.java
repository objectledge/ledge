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
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.Transaction;
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

    public BitronixTransactionManager(org.jcontainer.dna.Configuration config)
        throws ConfigurationException
    {
        ConfigurationHandler.configure(dataSources, connectionFactories, transactionConfig,
            started, config);
        btm = TransactionManagerServices.getTransactionManager();
    }

    public BitronixTransactionManager(String dsName, String dsClass, Properties dsProperties)
    {
        ConfigurationHandler.configure(dataSources, dsName, dsClass, dsProperties);
        btm = TransactionManagerServices.getTransactionManager();
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
                catch(SQLException e)
                {
                    throw new RuntimeException(e);
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
