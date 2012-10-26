package org.objectledge.database;

import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.database.impl.DelegatingDataSource;

public class JDBCDataSource
    extends DelegatingDataSource
{
    public JDBCDataSource(Configuration config)
        throws ConfigurationException, SQLException
    {
        super(getDataSource(config));
    }

    public JDBCDataSource(String driverClasspanh, String dataSourceClass, Properties properties)
        throws SQLException
    {
        super(getDataSource(driverClasspanh, dataSourceClass, properties));
    }

    private static DataSource getDataSource(Configuration config)
        throws ConfigurationException, SQLException
    {
        return getDataSource(config.getChild("classpath").getValue(""),
            config.getChild("dataSource").getValue(), getProperties(config));
    }

    private static DataSource getDataSource(String driverClaspath, String dataSourceClass,
        Properties properties)
        throws SQLException
    {
        DataSource ds;
        try
        {
            ClassLoader cassLoader = DatabaseUtils.getDriverClassLoader(driverClaspath);
            ds = (DataSource)Beans.instantiate(cassLoader, dataSourceClass);
        }
        catch(ClassNotFoundException | IOException e)
        {
            throw new SQLException("failed to instantiate DataSource class " + dataSourceClass, e);
        }
        setJavaBeanProperties(ds, properties);
        return ds;
    }

    private static void setJavaBeanProperties(DataSource ds, Properties properties)
        throws SQLException
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(ds.getClass());
            Map<PropertyDescriptor, String> propertyValues = new HashMap<>();
            for(PropertyDescriptor descr : beanInfo.getPropertyDescriptors())
            {
                if(properties.containsKey(descr.getName()))
                {
                    propertyValues.put(descr, properties.getProperty(descr.getName()));
                }
            }
            for(Map.Entry<PropertyDescriptor, String> pv : propertyValues.entrySet())
            {
                Method wm = pv.getKey().getWriteMethod();
                try
                {
                    wm.invoke(ds, new Object[] { pv.getValue() });
                }
                catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    throw new SQLException("failed to set property " + pv.getKey().getName()
                        + " on " + ds.getClass().getName(), e);
                }
            }
        }
        catch(IntrospectionException e)
        {
            throw new SQLException("failed to introspect " + ds.getClass().getName()
                + " properties", e);
        }
    }

    private static Properties getProperties(Configuration config)
        throws ConfigurationException
    {
        Properties properties = new Properties();
        for(Configuration property : config.getChild("properties").getChildren("property"))
        {
            properties.setProperty(property.getAttribute("name"), property.getValue());
        }
        return properties;
    }
}
