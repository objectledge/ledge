/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.pico.customization;

import junit.framework.TestCase;

import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.impl.ClasspathFileSystemProvider;
import org.objectledge.filesystem.impl.LocalFileSystemProvider;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.test.FooComponent;
import org.objectledge.xml.XMLValidator;
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
 * @version $Id: CustomizationTest.java,v 1.3 2003-12-02 15:24:14 fil Exp $
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
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);

        ComponentAdapterFactory factory = new CustomizingConstructorComponentAdapterFactory();
        factory = new CachingComponentAdapterFactory(factory);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        container.registerComponentInstance(MutablePicoContainer.class, container);
        container.registerComponentInstance(FileSystem.class, fs);
        container.registerComponentImplementation(XMLValidator.class, XMLValidator.class);
        container.registerComponentImplementation(
            ConfigurationFactory.class, 
            ConfigurationFactory.class,
            new Parameter[] {
                new ComponentParameter(MutablePicoContainer.class),
                new ComponentParameter(FileSystem.class),
                new ComponentParameter(XMLValidator.class),
                new ConstantParameter("config")
            }).getComponentInstance(container);
        container.registerComponentImplementation(LoggerFactory.class, LoggerFactory.class).
            getComponentInstance(container);

        container.registerComponentImplementation(FooComponent.class);
        FooComponent foo = (FooComponent)container.getComponentInstance(FooComponent.class);
        assertEquals(foo.getConfiguration().getValue("a"), "a");
        foo.log();
    }
}
