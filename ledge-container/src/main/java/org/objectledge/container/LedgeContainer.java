// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
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

package org.objectledge.container;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.reflection.StringToObjectConverter;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.configuration.CustomizedConfigurationProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.pico.ConstructorInjectionComponentAdapter;
import org.objectledge.pico.DefaultComponentAdapterFactory;
import org.objectledge.pico.LedgeStringToObjectConverter;
import org.objectledge.pico.customization.CustomizedComponentAdapter;
import org.objectledge.pico.customization.CustomizingConstructorComponentAdapterFactory;
import org.objectledge.pico.xml.LedgeContainerBuilder;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * <code>LedgeContainer</code> is a NanoContainer flawor, that uses the
 * {@link org.objectledge.filesystem.FileSystem} component for loading the composition file.
 * The composition file is parsed using Ledge XmlFrontEnd.
 * <code>LedgeContainer</code> also pre-feeds the internal PicoContainer with objects
 * required by various container subsystems, including a FileSystem, ClassLoader and 
 * confiuration directory path.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeContainer.java,v 1.14 2005-07-07 08:30:02 zwierzem Exp $
 */
public class LedgeContainer
{
    // constants ////////////////////////////////////////////////////////////////////////////////
    
    /** Location of the container composition file. */
    public static final String COMPOSITION_FILE = "/container.xml";

    /** Default xml front end implementation. */
    public static final String FRONT_END_CLASS = 
        "org.objectledge.pico.xml.LedgeXMLContainerBuilder";
    
    /** Config base path key. */
    public static final String CONFIG_BASE_KEY = "org.objectledge.ConfigBase";
    
    /** The embedded container. */
    protected ObjectReference containerRef = new SimpleReference();

    /** The embedded container builder. */
    protected ContainerBuilder containerBuilder;

    // initialization //////////////////////////////////////////////////////////////////////////

    /**
     * Creates an instance of the LedgeContainer.
     * 
     * @param fs the FileSystem
     * @param configBase configuration directory.
     * @param classLoader the class loader to load classes with.
     * @param componentInstanaces additional component instances to be available in the boot
     *        container. Boot container is populated before composition file is parsed.
     * @throws IOException if the composition file could not be read.
     * @throws ClassNotFoundException if the container builder class could not be instantiated.
     * @throws PicoCompositionException if the composition file is invalid, or could not be verified
     *         due to system failure.
     */ 
    public LedgeContainer(FileSystem fs, String configBase, ClassLoader classLoader,
        Map<Object, Object> componentInstanaces)
        throws IOException, ClassNotFoundException, PicoCompositionException
    {
        containerBuilder = new LedgeContainerBuilder(getCompositionFile(fs, configBase), 
            classLoader);
        ObjectReference parentRef = new SimpleReference();
        parentRef.set(getBootContainer(fs, configBase, classLoader, componentInstanaces));
        containerBuilder.buildContainer(containerRef, parentRef, null, true);
    }
    
    /**
     * Creates an instance of the LedgeContainer.
     * 
     * @param fs the FileSystem
     * @param configBase configuration directory.
     * @param classLoader the class loader to load classes with.
     * @throws IOException if the composition file could not be read.
     * @throws ClassNotFoundException if the container builder class could not be instantiated.
     * @throws PicoCompositionException if the composition file is invalid, or could not be 
     *         verified due to system failure.
     */    
    public LedgeContainer(FileSystem fs, String configBase, ClassLoader classLoader)
        throws IOException, ClassNotFoundException, PicoCompositionException
    {
        this(fs, configBase, classLoader, null);
    }

    /**
     * Returns the embedded container.
     * 
     * @return the embedded container.
     */
    public MutablePicoContainer getContainer()
    {
        return (MutablePicoContainer)containerRef.get();
    }
    
    /**
     * Kills the embedded container. 
     */
    public void killContainer()
    {
        containerBuilder.killContainer(containerRef);
    }
    
    /**
     * Verifies and returns the composition file.
     * 
     * @param fs the FileSystem to load compositoin file from.
     * @param configBase configuration directory.
     * @return the composition file.
     * @throws IOException if the file could not be read.
     * @throws PicoCompositionException if the composition file is invalid, or could not be 
     *         verified due to system failure.
     */
    protected static URL getCompositionFile(FileSystem fs, String configBase)
        throws IOException, PicoCompositionException
    {
        return fs.getResource(configBase + COMPOSITION_FILE);        
    }

    /**
     * Returns the boot component container.
     * 
     * @param fs the FileSystem
     * @param configBase configuration directory.
     * @param classLoader the classload to load classes with.
     * @param componentInstanaces TODO
     * @return the boot component container.
     */    
    protected static PicoContainer getBootContainer(FileSystem fs, String configBase, 
        ClassLoader classLoader, Map<Object, Object> componentInstanaces)
    {
        MutablePicoContainer bootContainer = new DefaultPicoContainer(
            new DefaultComponentAdapterFactory());
        bootContainer.registerComponentInstance(FileSystem.class, fs);
        bootContainer.registerComponentInstance(ClassLoader.class, classLoader);
        bootContainer.registerComponentImplementation(XMLGrammarCache.class);
        bootContainer.registerComponentImplementation(XMLValidator.class);
        bootContainer.registerComponentImplementation(StringToObjectConverter.class,
            LedgeStringToObjectConverter.class);
        // config
        bootContainer.registerComponentImplementation(ConfigurationFactory.class,
            ConfigurationFactory.class, params(COMPONENT, COMPONENT, constant(configBase)));
        bootContainer.registerComponentImplementation(CustomizedConfigurationProvider.class);
        ComponentAdapter adapter = new ConstructorInjectionComponentAdapter("anonymous", 
            CustomizedComponentAdapter.class, params(constant(Configuration.class), 
                component(CustomizedConfigurationProvider.class)));
        adapter = (ComponentAdapter)adapter.getComponentInstance(bootContainer);
        bootContainer.registerComponent(adapter);
        // logging
        bootContainer.registerComponentImplementation(LoggingConfigurator.class);
        bootContainer.registerComponentImplementation(LoggerFactory.class);
        adapter = new ConstructorInjectionComponentAdapter("anonymous", 
            CustomizedComponentAdapter.class, params(constant(Logger.class), 
                component(LoggerFactory.class)));
        adapter = (ComponentAdapter)adapter.getComponentInstance(bootContainer);
        bootContainer.registerComponent(adapter);
        // factory
        bootContainer.
            registerComponentImplementation(CustomizingConstructorComponentAdapterFactory.class);
        bootContainer.registerComponentImplementation(ComponentAdapterFactory.class,
            CachingComponentAdapterFactory.class, 
            params(component(CustomizingConstructorComponentAdapterFactory.class)));
        if(componentInstanaces != null)
        {
            for(Map.Entry<Object, Object> componentInstance : componentInstanaces.entrySet())
            {
                bootContainer.registerComponentInstance(componentInstance.getKey(), componentInstance.getValue());
            }
        }
        return bootContainer;
    }

    private static final Parameter COMPONENT = new ComponentParameter();
    
    
    private static Parameter[] params(Parameter... parameters)
    {
        return parameters;
    }
    
    private static Parameter constant(Object value)
    {
        return new ConstantParameter(value);
    }
    
    private static Parameter component(Object key)
    {
        return new ComponentParameter(key);
    }
}
