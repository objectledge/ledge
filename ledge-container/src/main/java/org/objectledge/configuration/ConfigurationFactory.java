/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 18, 2003
 */
package org.objectledge.configuration;

import javax.xml.parsers.SAXParserFactory;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pico.customization.CustomizedComponentAdapter;
import org.objectledge.pico.customization.CustomizedComponentProvider;
import org.objectledge.pico.customization.UnsupportedKeyTypeException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Returns a configuration for the specific component.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactory.java,v 1.5 2003-12-01 15:59:23 fil Exp $
 */
public class ConfigurationFactory
    implements CustomizedComponentProvider
{
    private FileSystem fileSystem;
    
    private String directory;
    
    /**
     * Creates a new instance of ConfigurationFactory.
     * 
     * @param container the container we are being registered to.
     * @param fileSystem the file system to read configurations from.
     * @param directory the name of the directory where configurations reside.
     */
    public ConfigurationFactory(MutablePicoContainer container, FileSystem fileSystem, 
        String directory)
    {
        this.fileSystem = fileSystem;
        this.directory = directory;
        if(container != null)
        {
            registerAdapter(container);
        }
    }

    /**
     * Returns the configuration of the specific compoenent in the system.
     * 
     * @param key the key of the component in the system.
     * @return the configuration.
     */
    public Configuration getConfig(Object key)
    {
        String name = getComponentName(key);
        String path = getComponentConfigurationPath(key);
        if(!fileSystem.exists(path))
        {
            throw new ComponentInitializationError("configuration file "+path+" for component "+
                name+" not found");
        }
        Configuration configuration;
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            SAXConfigurationHandler handler = new SAXConfigurationHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            InputSource source = new InputSource(fileSystem.getInputStream(path));
            reader.parse(source);
            configuration = handler.getConfiguration();
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("failed to parse configuration file "+
                path+" for compoenent "+name, e);
        }
        try
        {
            checkSchema(configuration, getComponentConfigurationSchemaPath(key));
        }
        catch(SAXException e)
        {
            throw new ComponentInitializationError("configuration file "+
                path+" for compoenent "+name+" does not fullfill schema constraints", e);
        }
        return configuration;
    }
    
    // CustomizedComponentProvider interface //////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public Object getCustomizedInsatnce(MutablePicoContainer dependenciesContainer, 
        Object componentKey, Class componentImplementation)
        throws PicoInitializationException, PicoIntrospectionException
    {
        return getConfig(componentKey);
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer container) throws NoSatisfiableConstructorsException
    {
        // no dependencies
    }
    
    // implemnetation /////////////////////////////////////////////////////////////////////////////

    /**
     * Returns human readable name of the component.
     * 
     * @param componentKey the component key.
     * @return human readable name of the component.
     */
    protected String getComponentName(Object componentKey)
        throws UnsupportedKeyTypeException
    {
        if(componentKey instanceof Class)
        {
            return ((Class)componentKey).getName();
        }
        else if(componentKey instanceof String)
        {
            return componentKey.toString();
        }
        else
        {
            throw new UnsupportedKeyTypeException("unsupported component key type "+
                componentKey.getClass().getName());
        }
    }

    /**
     * Returns the path of the configuration file for the specified key.
     * 
     * @param componentKey the key.
     * @return path the configuration file path.
     * @throws UnsupportedKeyTypeException if the componentKey has unsupported type.
     */
    protected String getComponentConfigurationPath(Object componentKey)
        throws UnsupportedKeyTypeException
    {
        if(componentKey instanceof Class)
        {
            return directory+((Class)componentKey).getName()+".xml";
        }
        else if(componentKey instanceof String)
        {
            return directory+((String)componentKey).replace(':','-')+".xml";
        }
        else
        {
            throw new UnsupportedKeyTypeException("unsupported component key type "+
                componentKey.getClass().getName());
        }
    }

    /**
     * Returns the path of the configuration schema file for the specified key.
     * 
     * @param componentKey the key.
     * @return path the configuration file path.
     * @throws UnsupportedKeyTypeException if the componentKey has unsupported type.
     */
    protected String getComponentConfigurationSchemaPath(Object componentKey)
        throws UnsupportedKeyTypeException
    {
        if(componentKey instanceof Class)
        {
            return ((Class)componentKey).getName().replace('.','/')+".schema";
        }
        else if(componentKey instanceof String)
        {
            String key = ((String)componentKey);
            if(key.indexOf(':') > 0)
            {
                key = key.substring(0, key.indexOf(':')); 
            }
            return key.replace('.','/')+".schema";
        }
        else
        {
            throw new UnsupportedKeyTypeException("unsupported component key type "+
                componentKey.getClass().getName());
        }
    }

    /**
     * Checks if an xml file fulfills it's associated schema.
     * 
     * @param configuration the configuration.
     * @param schemaPath the the schema file path.
     * @throws Exception if a schema violation is detected.
     */
    protected void checkSchema(Configuration configuration, String schemaPath)
        throws SAXException
    {
        // TODO implement schema checking
    }

    /**
     * Registers a CustomizedComponentAdapter for the {@link Configuration} type in the
     * specified container.
     * 
     * @param container the container.
     */
    protected void registerAdapter(MutablePicoContainer container)
    {
        MutablePicoContainer configurationContainer = new DefaultPicoContainer();
        ComponentAdapter configurationAdapter = new CustomizedComponentAdapter(Configuration.class, 
            configurationContainer, this);
        container.registerComponent(configurationAdapter);
    }
}
