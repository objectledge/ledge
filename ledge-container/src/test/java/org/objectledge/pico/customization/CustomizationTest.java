/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.pico.customization;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.impl.LocalFileSystemProvider;
import org.objectledge.test.FooComponent;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizationTest.java,v 1.1 2003-11-28 15:52:44 fil Exp $
 */
public class CustomizationTest extends TestCase
{

    /**
     * Constructor for CustomizationTest.
     * @param arg0
     */
    public CustomizationTest(String arg0)
    {
        super(arg0);
    }

    public void testCustomization() throws Exception
    {
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs }, 4096, 4096);

        ComponentAdapterFactory factory = new CustomizingConstructorComponentAdapterFactory();
        factory = new CachingComponentAdapterFactory(factory);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        container.registerComponentInstance(MutablePicoContainer.class, container);
        container.registerComponentInstance(FileSystem.class, fs);
        container.registerComponentImplementation(
            ConfigurationFactory.class, 
            ConfigurationFactory.class,
            new Parameter[] {
                new ComponentParameter(MutablePicoContainer.class),
                new ComponentParameter(FileSystem.class),
                new ConstantParameter("config")
            }
        ).getComponentInstance(container);

        container.registerComponentImplementation(FooComponent.class);
        FooComponent foo = (FooComponent)container.getComponentInstance(FooComponent.class);
        assertEquals(foo.getConfiguration().getValue("a"), "a");
    }
}
