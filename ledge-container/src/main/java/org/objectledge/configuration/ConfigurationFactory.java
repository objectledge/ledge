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

package org.objectledge.configuration;

import java.io.IOException;

import javax.xml.parsers.SAXParserFactory;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.jcontainer.dna.impl.SAXConfigurationSerializer;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pico.customization.CustomizedComponentAdapter;
import org.objectledge.pico.customization.CustomizedComponentProvider;
import org.objectledge.pico.customization.UnsupportedKeyTypeException;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Validator;

/**
 * Returns a configuration for the specific component.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactory.java,v 1.12 2003-12-05 08:42:25 fil Exp $
 */
public class ConfigurationFactory
    implements CustomizedComponentProvider
{
    private FileSystem fileSystem;
    
    private String directory;
    
    private XMLValidator xmlValidator;
    
    /**
     * Creates a new instance of ConfigurationFactory.
     * 
     * @param container the container we are being registered to.
     * @param fileSystem the file system to read configurations from.
     * @param xmlValidator the validator for configuration files.
     * @param directory the name of the directory where configurations reside.
     */
    public ConfigurationFactory(MutablePicoContainer container, FileSystem fileSystem, 
        XMLValidator xmlValidator, String directory)
    {
        this.fileSystem = fileSystem;
        this.xmlValidator = xmlValidator;
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
        String name = getComponentName(key);
        String path = getComponentConfigurationPath(key);
        String schema = getComponentConfigurationSchemaPath(key);
        if(!fileSystem.exists(path))
        {
            throw new ComponentInitializationError("configuration file "+path+" for component "+
                name+" not found");
        }
        if(!fileSystem.exists(schema))
        {
            throw new ComponentInitializationError("schema file "+schema+" for component "+
                name+" not found");
        }
        Configuration configuration;
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            SAXConfigurationHandler handler = new SAXConfigurationHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            InputSource source = new InputSource(fileSystem.getInputStream(path));
            reader.parse(source);
            configuration = handler.getConfiguration();
            checkSchema(path, schema);
        }
        catch(SAXParseException e)
        {
            throw new ComponentInitializationError("parse error in configuration of component "+
                name+": "+e.getMessage()+" in "+e.getSystemId()+" at line "+e.getLineNumber(), e);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("configuration file "+
                path+" for component "+name+" is malformed", e);
        }
        return configuration;
    }
    
    /**
     * Returns an input source for reading in the configuration file.
     * 
     * @param key the component key.
     * @return the input source.
     */
    public InputSource getConfigurationSource(Object key)
    {
        String name = getComponentName(key);
        String path = getComponentConfigurationPath(key);
        String schema = getComponentConfigurationSchemaPath(key);
        if(!fileSystem.exists(path))
        {
            throw new ComponentInitializationError("configuration file "+path+" for component "+
                name+" not found");
        }
        if(!fileSystem.exists(schema))
        {
            throw new ComponentInitializationError("schema file "+schema+" for component "+
                name+" not found");
        }
        try
        {
            checkSchema(path, schema);
        }
        catch(SAXParseException e)
        {
            throw new ComponentInitializationError("parse error in configuration of component "+
                name+": "+e.getMessage()+" in "+e.getSystemId()+" at line "+e.getLineNumber(), e);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("configuration file "+
                path+" for component "+name+" is malformed", e);
        }
        return new InputSource(fileSystem.getInputStream(path));
    }
    
    // CustomizedComponentProvider interface //////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public ComponentAdapter getCustomizedAdapter(MutablePicoContainer dependenciesContainer, 
        Object componentKey, Class componentImplementation)
        throws PicoInitializationException, PicoIntrospectionException
    {
        return new InstanceComponentAdapter(getComponentName(componentKey), 
            getConfig(componentKey));
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer container) throws NoSatisfiableConstructorsException
    {
        // no dependencies
    }
    
    // implemnetation /////////////////////////////////////////////////////////////////////////////

    /**
     * Returns human readable name of the component.
     * 
     * @param componentKey the component key.
     * @return human readable name of the component.
     * @throws UnsupportedKeyTypeException if the component key is of unsupported type.
     */
    protected String getComponentName(Object componentKey)
        throws UnsupportedKeyTypeException
    {
        if(componentKey instanceof Class)
        {
            return ((Class)componentKey).getName();
        }
        else if(componentKey instanceof String)
        {
            return componentKey.toString();
        }
        else
        {
            throw new UnsupportedKeyTypeException("unsupported component key type "+
                componentKey.getClass().getName());
        }
    }

    /**
     * Returns the path of the configuration file for the specified key.
     * 
     * @param componentKey the key.
     * @return path the configuration file path.
     * @throws UnsupportedKeyTypeException if the componentKey has unsupported type.
     */
    protected String getComponentConfigurationPath(Object componentKey)
        throws UnsupportedKeyTypeException
    {
        if(componentKey instanceof Class)
        {
            return directory+"/"+((Class)componentKey).getName()+".xml";
        }
        else if(componentKey instanceof String)
        {
            return directory+"/"+((String)componentKey).replace(':','-')+".xml";
        }
        else
        {
            throw new UnsupportedKeyTypeException("unsupported component key type "+
                componentKey.getClass().getName());
        }
    }

    /**
     * Returns the path of the configuration schema file for the specified key.
     * 
     * @param componentKey the key.
     * @return path the configuration file path.
     * @throws UnsupportedKeyTypeException if the componentKey has unsupported type.
     */
    protected String getComponentConfigurationSchemaPath(Object componentKey)
        throws UnsupportedKeyTypeException
    {
        if(componentKey instanceof Class)
        {
            return ((Class)componentKey).getName().replace('.','/')+".rng";
        }
        else if(componentKey instanceof String)
        {
            String key = ((String)componentKey);
            if(key.indexOf(':') > 0)
            {
                key = key.substring(0, key.indexOf(':')); 
            }
            return key.replace('.','/')+".rng";
        }
        else
        {
            throw new UnsupportedKeyTypeException("unsupported component key type "+
                componentKey.getClass().getName());
        }
    }

    /**
     * Checks if an xml file fulfills it's associated schema.
     * 
     * @param configuration the configuration.
     * @param schemaPath the the schema file path.
     * @throws IOException if the configuration, or schema cannot be read.
     * @throws SAXException if the configuration, or schema cannot be parsed.
     * @throws IncorrectSchemaException if the schema is malformed.
     */
    protected void checkSchema(Configuration configuration, String schemaPath)
        throws SAXException, IOException, IncorrectSchemaException
    {
        xmlValidator.validate(schemaPath, XMLValidator.RELAXNG_SCHEMA);
        Validator validator = xmlValidator.getValidator(schemaPath);
        SAXConfigurationSerializer serializer = new SAXConfigurationSerializer();
        serializer.serialize(configuration, validator.getContentHandler());
    }

    /**
     * Checks if an xml file fulfills it's associated schema.
     * 
     * @param configuration the configuration file path.
     * @param schemaPath the the schema file path.
     * @throws IOException if the configuration, or schema cannot be read.
     * @throws SAXException if the configuration, or schema cannot be parsed.
     * @throws IncorrectSchemaException if the schema is malformed.
     */
    protected void checkSchema(String configuration, String schemaPath)
        throws SAXException, IOException, IncorrectSchemaException
    {
        xmlValidator.validate(schemaPath, XMLValidator.RELAXNG_SCHEMA);
        xmlValidator.validate(configuration, schemaPath);
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
