// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.pico.customization;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.configuration.CustomizedConfigurationProvider;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.test.FooComponent;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
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
 * @version $Id: CustomizationTest.java,v 1.19 2005-02-04 02:29:34 rafal Exp $
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
        FileSystemProvider lfs = new LocalFileSystemProvider("local", "src/test/resources");
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);

        ComponentAdapterFactory factory = new CustomizingConstructorComponentAdapterFactory();
        factory = new CachingComponentAdapterFactory(factory);
        MutablePicoContainer container = new DefaultPicoContainer(factory);

        container.registerComponentInstance(FileSystem.class, fs);
        container.registerComponentImplementation(XMLGrammarCache.class, XMLGrammarCache.class);
        container.registerComponentImplementation(XMLValidator.class, XMLValidator.class);
        container.registerComponentImplementation(
            ConfigurationFactory.class, 
            ConfigurationFactory.class,
            new Parameter[] {
                new ComponentParameter(FileSystem.class),
                new ComponentParameter(XMLValidator.class),
                new ConstantParameter("config")
            }).getComponentInstance(container);
        container.registerComponentImplementation(CustomizedConfigurationProvider.class,
            CustomizedConfigurationProvider.class);    
        ComponentAdapter adapter = new CustomizedComponentAdapter(
            Configuration.class, 
            (CustomizedComponentProvider)container.
                getComponentInstance(CustomizedConfigurationProvider.class));
        adapter.verify(container);
        container.registerComponent(adapter);
        container.registerComponentImplementation(LoggingConfigurator.class, 
            LoggingConfigurator.class).getComponentInstance(container);
            
        container.registerComponentImplementation(LoggerFactory.class, LoggerFactory.class).
            getComponentInstance(container);
        container.registerComponent(new CustomizedComponentAdapter(
            Logger.class, 
            (CustomizedComponentProvider)container.
                getComponentInstance(LoggerFactory.class)));

        container.registerComponentImplementation(FooComponent.class);
        FooComponent foo = (FooComponent)container.getComponentInstance(FooComponent.class);
        assertEquals(foo.getConfiguration().getValue("a"), "a");
        foo.log();
    }
}
