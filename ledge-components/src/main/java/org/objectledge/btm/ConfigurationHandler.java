package org.objectledge.btm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.database.Transaction;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.resource.jms.PoolingConnectionFactory;

class ConfigurationHandler
{
    private static final Set<String> RESOURCE_BEAN_PROPERTIES = new HashSet<>();

    static
    {
        RESOURCE_BEAN_PROPERTIES.addAll(Arrays.asList("className", "automaticEnlistingEnabled",
            "useTmJoin", "minPoolSize", "maxPoolSize", "maxIdleTime", "acquireIncrement",
            "acquisitionTimeout", "acquisitionInterval", "deferConnectionRelease",
            "twoPcOrderingPosition", "applyTransactionTimeout", "allowLocalTransactions",
            "shareTransactionConnections", "ignoreRecoveryFailures"));
    }

    static void configure(Map<String, DataSource> dataSources,
        Map<String, ConnectionFactory> connectionFactories, Transaction.Config tracing,
        Configuration config)
        throws ConfigurationException
    {
        bitronix.tm.Configuration btm = TransactionManagerServices.getConfiguration();
        setDefaults(btm);
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "tm":
                configureTm(btm, child);
                break;
            case "jdbc":
                configureJdbc(dataSources, child);
                break;
            case "jms":
                configureJms(connectionFactories, child);
                break;
            case "tracing":
                configureTracing(tracing, child);
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
        tracing.setDefaultTimeout(btm.getDefaultTransactionTimeout());
    }

    private static void setDefaults(bitronix.tm.Configuration btm)
    {
        btm.setServerId(null);
        btm.setLogPart1Filename("btm1.tlog");
        btm.setLogPart2Filename("btm2.tlog");
        btm.setForcedWriteEnabled(true);
        btm.setForceBatchingEnabled(true);
        btm.setMaxLogSizeInMb(2);
        btm.setFilterLogStatus(false);
        btm.setSkipCorruptedLogs(false);
        btm.setAsynchronous2Pc(false);
        btm.setWarnAboutZeroResourceTransaction(true);
        btm.setDebugZeroResourceTransaction(false);
        btm.setDefaultTransactionTimeout(60);
        btm.setGracefulShutdownInterval(60);
        btm.setBackgroundRecoveryIntervalSeconds(60);
        btm.setDisableJmx(false);
        btm.setJndiUserTransactionName("java:comp/UserTransaction");
        btm.setJndiTransactionSynchronizationRegistryName("java:comp/TransactionSynchronizationRegistry");
        btm.setJournal("disk");
        btm.setExceptionAnalyzer(null);
        btm.setCurrentNodeOnlyRecovery(true);
        btm.setAllowMultipleLrc(false);
        btm.setResourceConfigurationFilename(null);
    }

    private static void configureTracing(Transaction.Config transactionConfig, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "disabled":
                transactionConfig.setTracing(0);
                break;
            case "depth":
                transactionConfig.setTracing(child.getValueAsInteger());
                break;
            case "statementLog":
                transactionConfig.setStatementLogName(child.getValue());
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    private static void configureTm(bitronix.tm.Configuration btm, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "serverId":
                btm.setServerId(child.getValue());
                break;
            case "disableJmx":
                btm.setDisableJmx(child.getValueAsBoolean());
                break;
            case "allowMultipleLrc":
                btm.setAllowMultipleLrc(child.getValueAsBoolean());
                break;
            case "currentNodeOnlyRecovery":
                btm.setCurrentNodeOnlyRecovery(child.getValueAsBoolean());
                break;
            case "exceptionAnalyzer":
                btm.setExceptionAnalyzer(child.getValue());
                break;
            case "twopc":
                configure2pc(btm, child);
                break;
            case "jndi":
                configureJndi(btm, child);
                break;
            case "journal":
                configureJournal(btm, child);
                break;
            case "timer":
                configureTimer(btm, child);
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    private static void configure2pc(bitronix.tm.Configuration btm, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "asynchronous2Pc":
                btm.setAsynchronous2Pc(child.getValueAsBoolean());
                break;
            case "warnAboutZeroResourceTransaction":
                btm.setWarnAboutZeroResourceTransaction(child.getValueAsBoolean());
                break;
            case "debugZeroResourceTransaction":
                btm.setDebugZeroResourceTransaction(child.getValueAsBoolean());
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    private static void configureJndi(bitronix.tm.Configuration btm, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "jndiUserTransactionName":
                btm.setJndiUserTransactionName(child.getValue());
                break;
            case "jndiTransactionSynchronizationRegistryName":
                btm.setJndiTransactionSynchronizationRegistryName(child.getValue());
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    private static void configureJournal(bitronix.tm.Configuration btm, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "null":
                break;
            case "class":
                btm.setJournal(child.getValue());
                break;
            case "disk":
                configureDiskJournal(btm, child);
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    private static void configureDiskJournal(bitronix.tm.Configuration btm, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "logPart1Filename":
                btm.setLogPart1Filename(child.getValue());
                break;
            case "logPart2Filename":
                btm.setLogPart2Filename(child.getValue());
                break;
            case "forcedWriteEnabled":
                btm.setForcedWriteEnabled(child.getValueAsBoolean());
                break;
            case "forceBatchingEnabled":
                btm.setForceBatchingEnabled(child.getValueAsBoolean());
                break;
            case "maxLogSizeInMb":
                btm.setMaxLogSizeInMb(child.getValueAsInteger());
                break;
            case "filterLogStatus":
                btm.setFilterLogStatus(child.getValueAsBoolean());
                break;
            case "skipCorruptedLogs":
                btm.setSkipCorruptedLogs(child.getValueAsBoolean());
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    private static void configureTimer(bitronix.tm.Configuration btm, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "defaultTransactionTimeout":
                btm.setDefaultTransactionTimeout(child.getValueAsInteger());
                break;
            case "gracefulShutdownInterval":
                btm.setGracefulShutdownInterval(child.getValueAsInteger());
                break;
            case "backgroundRecoveryIntervalSeconds":
                btm.setBackgroundRecoveryIntervalSeconds(child.getValueAsInteger());
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    private static void configureJdbc(Map<String, DataSource> dataSources, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren("connectionPool"))
        {
            configureJdbcPool(dataSources, child);
        }
    }

    private static void configureJdbcPool(Map<String, DataSource> dataSources, Configuration config)
        throws ConfigurationException
    {
        PoolingDataSource ds = new PoolingDataSource();
        final String uniqueName = confgureResourceBean(ds, config);

        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "testQuery":
                ds.setTestQuery(child.getValue());
                break;
            case "enableJdbc4ConnectionTest":
                ds.setEnableJdbc4ConnectionTest(child.getValueAsBoolean());
                break;
            case "preparedStatementCacheSize":
                ds.setPreparedStatementCacheSize(child.getValueAsInteger());
                break;
            case "isolationLevel":
                ds.setIsolationLevel(child.getValue());
                break;
            case "cursorHoldability":
                ds.setCursorHoldability(child.getValue());
                break;
            case "localAutoCommit":
                ds.setLocalAutoCommit(child.getValue());
                break;
            case "driverProperties":
                configureDriverProperties(ds.getDriverProperties(), child);
                break;
            default:
                if(!RESOURCE_BEAN_PROPERTIES.contains(child.getName()))
                {
                    throw new ConfigurationException("unsupported element " + child.getName(),
                        child.getPath(), child.getLocation());
                }
            }
        }

        if(config.getAttributeAsBoolean("eager", false))
        {
            ds.init();
        }
        dataSources.put(uniqueName, ds);
    }

    private static void configureJms(Map<String, ConnectionFactory> connectionFactories,
        Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren("connectionPool"))
        {
            configureJmsPool(connectionFactories, child);
        }
    }

    private static void configureJmsPool(Map<String, ConnectionFactory> connectionFactories,
        Configuration config)
        throws ConfigurationException
    {
        PoolingConnectionFactory cf = new PoolingConnectionFactory();
        final String uniqueName = confgureResourceBean(cf, config);

        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "cacheProducersConsumers":
                cf.setCacheProducersConsumers(child.getValueAsBoolean());
                break;
            case "testConnections":
                cf.setTestConnections(child.getValueAsBoolean());
                break;
            case "user":
                cf.setUser(child.getValue());
                break;
            case "password":
                cf.setPassword(child.getValue());
                break;
            case "driverProperties":
                configureDriverProperties(cf.getDriverProperties(), child);
                break;
            default:
                if(!RESOURCE_BEAN_PROPERTIES.contains(child.getName()))
                {
                    throw new ConfigurationException("unsupported element " + child.getName(),
                        child.getPath(), child.getLocation());
                }
            }
        }

        if(config.getAttributeAsBoolean("eager", false))
        {
            cf.init();
        }
        connectionFactories.put(uniqueName, cf);
    }

    private static String confgureResourceBean(ResourceBean bean, Configuration config)
        throws ConfigurationException
    {
        final String uniqueName = config.getAttribute("uniqueName");
        bean.setUniqueName(uniqueName);
        if(config.getAttributeAsBoolean("disabled", false))
        {
            bean.setDisabled(true);
        }

        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "className":
                bean.setClassName(child.getValue());
                break;
            case "automaticEnlistingEnabled":
                bean.setAutomaticEnlistingEnabled(child.getValueAsBoolean());
                break;
            case "useTmJoin":
                bean.setUseTmJoin(child.getValueAsBoolean());
                break;
            case "minPoolSize":
                bean.setMinPoolSize(child.getValueAsInteger());
                break;
            case "maxPoolSize":
                bean.setMaxPoolSize(child.getValueAsInteger());
                break;
            case "maxIdleTime":
                bean.setMaxIdleTime(child.getValueAsInteger());
                break;
            case "acquireIncrement":
                bean.setAcquireIncrement(child.getValueAsInteger());
                break;
            case "acquisitionTimeout":
                bean.setAcquisitionTimeout(child.getValueAsInteger());
                break;
            case "acquisitionInterval":
                bean.setAcquisitionInterval(child.getValueAsInteger());
                break;
            case "deferConnectionRelease":
                bean.setDeferConnectionRelease(child.getValueAsBoolean());
                break;
            case "twoPcOrderingPosition":
                bean.setTwoPcOrderingPosition(child.getValueAsInteger());
                break;
            case "applyTransactionTimeout":
                bean.setApplyTransactionTimeout(child.getValueAsBoolean());
                break;
            case "allowLocalTransactions":
                bean.setAllowLocalTransactions(child.getValueAsBoolean());
                break;
            case "shareTransactionConnections":
                bean.setShareTransactionConnections(child.getValueAsBoolean());
                break;
            case "ignoreRecoveryFailures":
                bean.setIgnoreRecoveryFailures(child.getValueAsBoolean());
                break;
            }
        }
        return uniqueName;
    }

    private static void configureDriverProperties(Properties driverProperties, Configuration config)
        throws ConfigurationException
    {
        for(Configuration child : config.getChildren())
        {
            switch(child.getName())
            {
            case "property":
                driverProperties.setProperty(child.getAttribute("name"), child.getValue());
                break;
            default:
                throw new ConfigurationException("unsupported element " + child.getName(),
                    child.getPath(), child.getLocation());
            }
        }
    }

    public static void configure(Map<String, DataSource> dataSources, String dsName, String dsClass, Properties dsProperties)
    {
        bitronix.tm.Configuration btm = TransactionManagerServices.getConfiguration();
        setDefaults(btm);
        btm.setServerId("default");
        btm.setLogPart1Filename("target/btm1.tlog");
        btm.setLogPart2Filename("target/btm2.tlog");
        btm.setDisableJmx(true);
        PoolingDataSource ds = new PoolingDataSource();
        ds.setUniqueName(dsName);
        ds.setClassName(dsClass);
        ds.setAllowLocalTransactions(true);
        ds.setShareTransactionConnections(true);
        ds.setLocalAutoCommit("false");
        ds.setMaxPoolSize(10);
        ds.getDriverProperties().putAll(dsProperties);
        dataSources.put(dsName, ds);
    }
}
