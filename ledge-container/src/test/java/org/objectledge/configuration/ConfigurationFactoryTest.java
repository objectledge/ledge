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
import org.objectledge.filesystem.impl.ClasspathFileSystemProvider;
import org.objectledge.filesystem.impl.LocalFileSystemProvider;
import org.objectledge.xml.XMLValidator;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactoryTest.java,v 1.3 2003-12-02 13:12:44 fil Exp $
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
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        XMLValidator xv = new XMLValidator(fs);
        cf = new ConfigurationFactory(null, fs, xv, "config");
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
