/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 18, 2003
 */
package org.objectledge.configuration;

import org.jcontainer.dna.Configuration;
import org.objectledge.filesystem.FileSystem;

/**
 * Returns a configuration for the specific component.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactory.java,v 1.1 2003-11-24 15:55:28 fil Exp $
 */
public class ConfigurationFactory
{
    /**
     * Creates a new instance of ConfigurationFactory.
     * 
     * @param fileSystem the file system to read configurations from.
     * @param directory the name of the directory where configurations reside.
     */
    public ConfigurationFactory(FileSystem fileSystem, String directory)
    {
    }

    /**
     * Returns the configuration of the specific compoenent in the system.
     * 
     * @param role the component role.
     * @return the configuration.
     */
    public Configuration getConfig(Class role)
    {
        return null;
    }
}
