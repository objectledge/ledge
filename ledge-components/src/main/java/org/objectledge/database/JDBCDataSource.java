package org.objectledge.database;

import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
    private static final String DATASOURCE_CLASS_NAME = "datasource.className";

    private static final String DATASOURCE_CLASSPATH = "datasource.classpath";

    public JDBCDataSource(Configuration config)
        throws ConfigurationException, SQLException
    {
        super(getDataSource(config));
    }

    public JDBCDataSource(Properties properties)
        throws SQLException
    {
        super(getDataSource(properties));
    }

    private static ClassLoader getClassLoader(String classpath)
        throws SQLException
    {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        if(currentClassLoader == null)
        {
            currentClassLoader = JDBCDataSource.class.getClassLoader();
        }
        if(classpath == null || classpath.length() == 0)
        {
            return currentClassLoader;
        }
        else
        {
            String pathSeparator = System.getProperty("path.separator");
            String[] elements = classpath.split(pathSeparator);
            try
            {
                URL[] urls = new URL[elements.length];
                for(int i = 0; i < elements.length; i++)
                {
                    urls[i] = new URL("file", "", elements[i].trim());
                }
                return new URLClassLoader(urls, currentClassLoader);
            }
            catch(MalformedURLException e)
            {
                throw new SQLException("failed to set up driver classpath", e);
            }
        }
    }

    private static DataSource getDataSource(Configuration config)
        throws ConfigurationException, SQLException
    {
        return getDataSource(getProperties(config));
    }

    private static DataSource getDataSource(Properties properties)
        throws SQLException
    {
        String className = properties.getProperty(DATASOURCE_CLASS_NAME);
        ClassLoader cassLoader = getClassLoader(properties.getProperty(DATASOURCE_CLASSPATH));
        DataSource ds;
        try
        {
            ds = (DataSource)Beans.instantiate(cassLoader, className);
        }
        catch(ClassNotFoundException | IOException e)
        {
            throw new SQLException("failed to instantiate XADataSource class " + className, e);
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
        for(Configuration property : config.getChildren("property"))
        {
            properties.setProperty(property.getAttribute("name"), property.getValue());
        }
        return properties;
    }
}
