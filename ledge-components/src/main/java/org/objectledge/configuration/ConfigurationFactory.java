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
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.jcontainer.dna.impl.SAXConfigurationSerializer;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.sun.msv.verifier.Verifier;

/**
 * Returns a configuration for the specific component.
 * 
 * <p>
 * Configuration factory plugs into the container using 
 * {@link org.objectledge.pico.customization component customization} mechanism,
 * and provides components that declare a dependency on a DNA Configuration (using a
 * <code>org.jcontainer.dna.Configuration</code> component parameter) with an initialized
 * configuration object.
 * </p>
 *  
 * <p>
 * The contents of the configuration object are read from an XML file,
 * and verified against a RelaxNG schema.
 * </p>
 *
 * <h3>Functionality anticipated in the future</h3>
 * <ul>
 * <li>Detecting of components that support runtime reconfiguration (possibly by extending
 *  <code>org.jcontainer.dna.Configurable</code>interface.</li>
 * <li>Triggering of re-reading configuration from files on reconfigurable components</li>
 * <li>Programmatic setting of configuration of reconfigurable components.</li>
 * </ul>
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactory.java,v 1.12 2005-07-29 12:06:21 pablo Exp $
 */
public class ConfigurationFactory
{
    private FileSystem fileSystem;
    
    private String directory;
    
    private XMLValidator xmlValidator;
    
    private URL relaxngUrl;
    
    /**
     * Creates a new instance of ConfigurationFactory.
     * 
     * @param fileSystem the file system to read configurations from.
     * @param xmlValidator the validator for configuration files.
     * @param directory the name of the directory where configurations reside.
     * @throws IOException if the RelaxNG schema cannot be found in classpath.
     */
    public ConfigurationFactory(FileSystem fileSystem, XMLValidator xmlValidator, String directory)
        throws IOException
    {
        this.fileSystem = fileSystem;
        this.xmlValidator = xmlValidator;
        this.directory = directory;
        this.relaxngUrl = fileSystem.getResource(XMLValidator.RELAXNG_SCHEMA);
    }

    /**
     * Returns the configuration of the specific compoenent in the system.
     * 
     * @param componentName the name of the component
     * @param componentClass the implementation class of the component.
     * @return the configuration.
     */
    public Configuration getConfig(String componentName, Class<?> componentClass)
    {
        String path = getComponentConfigurationPath(componentName);
        InputSource source = getConfigurationSource(componentName, componentClass);
        Configuration configuration;
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            SAXConfigurationHandler handler = new SAXConfigurationHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(source);
            configuration = handler.getConfiguration();
        }
        catch(SAXParseException e)
        {
            throw new ComponentInitializationError("parse error in configuration of component "+
                componentName+": "+e.getMessage()+" in "+e.getSystemId()+" at line "+
                e.getLineNumber(), e);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("configuration file "+
                path+" for component "+componentName+" is malformed", e);
        }
        return configuration;
    }
    
    /**
     * Returns the configuration of the specific compoenent in the system.
     * 
     * @param componentRole the role of the component.
     * @param componentImplementation the implementation class of the component.
     * @return the configuration.
     */
    public Configuration getConfig(Class<?> componentRole, Class<?> componentImplementation)
    {
        return getConfig(componentRole.getName(), componentImplementation);
    }

    /**
     * Returns an input source for reading in the configuration file.
     * 
     * @param componentName the name of the component
     * @param componentClass the implementation class of the component.
     * @return the input source.
     */
    public InputSource getConfigurationSource(String componentName, Class<?> componentClass)
    {
        String path = getComponentConfigurationPath(componentName);
        InputSource source = getRawConfigurationSource(componentName);
        String schema = getComponentConfigurationSchemaPath(componentClass);
        if(!fileSystem.exists(schema))
        {
            throw new ComponentInitializationError("schema file "+schema+" for component "+
                componentName+" not found");
        }
        try
        {
            checkSchema(path, schema);
        }
        catch(SAXParseException e)
        {
            throw new ComponentInitializationError("parse error in configuration of component "+
                componentName+": "+e.getMessage()+" in "+e.getSystemId()+" at line "+
                e.getLineNumber(), e);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("configuration file "+
                path+" for component "+componentName+" is malformed: '" + e.getMessage() + "'", e);
        }
        return source;
    }

    /**
     * Return a raw (unchecked) configuration source for a component.
     * 
     * @param componentName the name of the component.
     * @return an InputSource for reading the configuration.
     */
    public InputSource getRawConfigurationSource(String componentName)
    {
        String path = getComponentConfigurationPath(componentName);
        if(!fileSystem.exists(path))
        {
            throw new ComponentInitializationError("configuration file "+path+" for component "+
                componentName+" not found");
        }
        return new InputSource(fileSystem.getInputStream(path));
    }

    /**
     * Checks if a given component has a configration file available.
     * 
     * @param componentName the name of the component.
     * @return <code>true</code> if the component has a configration file available.
     */
    public boolean hasConfig(String componentName)
    {
        String path = getComponentConfigurationPath(componentName);
        return fileSystem.exists(path);
    }
    
    /**
     * Returns an input source for reading in the configuration file.
     * 
     * @param componentRole the role of the component
     * @param componentClass the implementation class of the component.
     * @return the input source.
     */
    public InputSource getConfigurationSource(Class<?> componentRole, Class<?> componentClass)
    {
        return getConfigurationSource(componentRole.getName(), componentClass);
    }
    
    /**
     * Returns an InputSource for reading the container composition file.
     * 
     * @return an InputSource for reading the container composition file. 
     */
    public InputSource getCompositionSource()
    {
        String path = directory + "/container.xml";
        if(!fileSystem.exists(path))
        {
            throw new ComponentInitializationError("composition file " + path + " not found");
        }
        return new InputSource(fileSystem.getInputStream(path));
    }
    
    // implemnetation /////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the path of the configuration file for the specified key.
     * 
     * @param componentName the name of the component.
     * @return path the configuration file path.
     */
    protected String getComponentConfigurationPath(String componentName)
    {
        return directory+"/"+componentName+".xml";
    }

    /**
     * Returns the path of the configuration schema file for the specified key.
     * 
     * @param componentImplementation the implementation class of the component.
     * @return path the configuration file path.
     */
    protected String getComponentConfigurationSchemaPath(Class<?> componentImplementation)
    {
        return ((Class<?>)componentImplementation).getName().replace('.','/')+".rng";
    }

    /**
     * Checks if an xml file fulfills it's associated schema.
     * 
     * @param configuration the configuration.
     * @param schemaPath the the schema file path.
     * @throws IOException if the configuration, or schema cannot be read.
     * @throws SAXException if the configuration, or schema cannot be parsed.
     * @throws ParserConfigurationException if the parser is badly configured.
     */
    protected void checkSchema(Configuration configuration, String schemaPath)
        throws SAXException, IOException, ParserConfigurationException
    {
        URL schemaUrl = fileSystem.getResource(schemaPath);
        xmlValidator.validate(schemaUrl, relaxngUrl);
        Verifier verifier= xmlValidator.getVerifier(schemaUrl);
        SAXConfigurationSerializer serializer = new SAXConfigurationSerializer();
        serializer.serialize(configuration, verifier);
    }

    /**
     * Checks if an xml file fulfills it's associated schema.
     * 
     * @param configuration the configuration file path.
     * @param schemaPath the the schema file path.
     * @throws IOException if the configuration, or schema cannot be read.
     * @throws SAXException if the configuration, or schema cannot be parsed.
     * @throws ParserConfigurationException if the parser is badly configured.
     */
    protected void checkSchema(String configuration, String schemaPath)
        throws SAXException, IOException, ParserConfigurationException
    {
        URL schemaUrl = fileSystem.getResource(schemaPath);
        xmlValidator.validate(schemaUrl, relaxngUrl);
        xmlValidator.validate(fileSystem.getResource(configuration), schemaUrl);
    }
}
