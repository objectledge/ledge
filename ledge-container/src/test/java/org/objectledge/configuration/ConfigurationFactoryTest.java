/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.configuration;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.impl.LocalFileSystemProvider;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactoryTest.java,v 1.1 2003-11-28 11:15:39 fil Exp $
 */
public class ConfigurationFactoryTest 
    extends TestCase
{
    private ConfigurationFactory cf;
    
    /**
     * Constructor for ConfigurationFactoryTest.
     * @param arg0
     */
    public ConfigurationFactoryTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() 
        throws Exception
    {
        super.setUp();
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs }, 4096, 4096);
        cf = new ConfigurationFactory(fs, "config");
    }

    public void testGetConfig()
        throws ConfigurationException
    {
        Configuration config = cf.getConfig(org.objectledge.test.FooComponent.class);
        Configuration a = config.getChild("a");
        assertEquals(a.getValue(), "a");
        Configuration b = config.getChild("b");
        assertEquals(b.getAttribute("attr"), "b");
        Configuration d[] = config.getChild("c").getChildren("d");
        assertEquals(d[0].getValue(), "d1");
        assertEquals(d[1].getValue(), "d2");
    }
}
