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
import org.objectledge.pico.customization.CustomizedComponentProvider;
import org.objectledge.pico.customization.CustomizedComponentAdapter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Returns a configuration for the specific component.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactory.java,v 1.4 2003-12-01 10:14:48 fil Exp $
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
        String name;
        if(key instanceof Class)
        {
            name = ((Class)key).getName();
        }
        else
        {
            name = key.toString();
        }
        String path = directory+"/"+name+".xml";
        if(!fileSystem.exists(path))
        {
            throw new ComponentInitializationError("configuration file "+path+" for compoenent "+
                name+" not found");
        }
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            SAXConfigurationHandler handler = new SAXConfigurationHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            InputSource source = new InputSource(fileSystem.getInputStream(path));
            reader.parse(source);
            return handler.getConfiguration();
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("failed to parse configuration file "+
                path+" for compoenent "+name, e);
        }
    }
    
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
