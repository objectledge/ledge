/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 3, 2003
 */
package org.objectledge.logging;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LoggingConfiguratorTest.java,v 1.9 2004-06-01 15:34:49 fil Exp $
 */
public class LoggingConfiguratorTest extends TestCase
{

    /**
     * Constructor for LoggingConfiguratorTest.
     * @param arg0
     */
    public LoggingConfiguratorTest(String arg0)
    {
        super(arg0);
    }

    public void testConfigurator()
        throws Exception
    {
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
                "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        XMLValidator xv = new XMLValidator(new XMLGrammarCache());
        ConfigurationFactory cf = new ConfigurationFactory(fs, xv, "config");
        new LoggingConfigurator(cf);    
        Logger logger = Logger.getLogger(LoggingConfiguratorTest.class);
        logger.debug("Yipee!");
    }
}
