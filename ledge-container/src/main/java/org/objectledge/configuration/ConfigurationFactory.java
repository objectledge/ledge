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
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Returns a configuration for the specific component.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactory.java,v 1.1 2003-11-28 11:01:50 fil Exp $
 */
public class ConfigurationFactory
{
    private FileSystem fileSystem;
    
    private String directory;
    
    /**
     * Creates a new instance of ConfigurationFactory.
     * 
     * @param fileSystem the file system to read configurations from.
     * @param directory the name of the directory where configurations reside.
     */
    public ConfigurationFactory(FileSystem fileSystem, String directory)
    {
        this.fileSystem = fileSystem;
        this.directory = directory;
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
}
