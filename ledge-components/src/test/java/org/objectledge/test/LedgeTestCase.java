// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.jmock.MockObjectTestCase;
import org.jmock.cglib.CGLIBCoreMock;
import org.jmock.core.Constraint;
import org.jmock.core.CoreMock;
import org.jmock.core.DynamicMock;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Ledge testcases base class.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public abstract class LedgeTestCase extends MockObjectTestCase
{
    /**
     * 
     */
    private static final String LOG4J_PATTERN = "%-5p [%t] %x %30.30c - %m%n";
    private FileSystem fileSystem;

    // filesystem ///////////////////////////////////////////////////////////////////////////////

    /**
     * Returns FileSystem component instance rooted at "src/test/resources" directory.
     * 
     * @return FileSystem component instance.
     */
    protected FileSystem getFileSystem() 
    {
        if (fileSystem == null)
        {
            fileSystem = FileSystem.getStandardFileSystem("src/test/resources");
        }
        return fileSystem;
    }

    /**
     * Tear down test fixture
     */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        fileSystem = null;
    }

    /**
     * Returns standard FileSystem rooted at the specified path.
     * 
     * @param root the root directory of the standard file system.
     * @return FileSystem component instance.
     */
    protected FileSystem getFileSystem(String root)
    {
        return FileSystem.getStandardFileSystem(root);
    }

    // configuraitons & schemata /////////////////////////////////////////////////////////////////

    /**
     * Get the configuration.
     * 
     * @param fs the file system.
     * @param name the config file name.
     * @return the configuration
     * @throws Exception if happens.
     */
    protected Configuration getConfig(FileSystem fs, String name) throws Exception
    {
        if(!fs.exists(name))
        {
            throw new IOException("configuration file " + name + " not found");
        }
        InputSource source = new InputSource(fs.getInputStream(name));
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SAXConfigurationHandler handler = new SAXConfigurationHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(source);
        return handler.getConfiguration();
    }

    /**
     * Check component's configuration against a specified schema.
     * 
     * @param configuration the configuration path.
     * @param schema schema path.
     * @throws Exception if the configration validation fails.
     */
    protected void checkSchema(String configuration, String schema) throws Exception
    {

        getFileSystem();
        XMLValidator validator = new XMLValidator(new XMLGrammarCache());
        URL rngUrl = fileSystem.getResource(XMLValidator.RELAXNG_SCHEMA);
        URL schemaUrl = fileSystem.getResource(schema);
        URL configUrl = fileSystem.getResource(configuration);
        try
        {
            validator.validate(schemaUrl, rngUrl);
        }
        catch (SAXParseException e)
        {
            throw new Exception("parse error " + e.getMessage() + " in "
                                 + e.getSystemId() + " at line " + e.getLineNumber(), e);
        }
        catch (Exception e)
        {
            throw new Exception("schema file " + schemaUrl + "is missing or invalid", e);
        }
        try
        {
            validator.validate(configUrl, schemaUrl);
        }
        catch (SAXParseException e)
        {
            throw new Exception("parse error " + e.getMessage() +
                                 " in " + e.getSystemId() + " at line " + e.getLineNumber(), e);
        }
        catch (Exception e)
        {
            throw new Exception("configuration file " + configUrl + "is missing or invalid", e);
        }
    }
    
    protected void initLog4J(String level)
    {
        Properties props = new Properties();
        props.setProperty("log4j.rootLogger", level + ", console");
        props.setProperty("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
        props.setProperty("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");        
        props.setProperty("log4j.appender.console.layout.ConversionPattern", LOG4J_PATTERN);        
        org.apache.log4j.PropertyConfigurator.configure(props);
    }
    
    protected Logger getLogger()
    {
        return new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
    }

    // jMock goodies ////////////////////////////////////////////////////////////////////////////

    /**
     * Create a CoreMock object.
     * 
     * <p>CGLIB mocks will be created for concrete classes, JMock CoreMocks for interfaces.</p>
     * 
     * @param mockedType to type to mock.
     * @param roleName mocked object role name
     * @return a CoreMock object.
     */
    @SuppressWarnings("unchecked")
    protected DynamicMock newCoreMock(Class mockedType, String roleName)
    {
        if((mockedType.getModifiers() & Modifier.INTERFACE) == Modifier.INTERFACE)
        {
            return new CoreMock(mockedType, roleName);
        }
        else
        {
            return new CGLIBCoreMock(mockedType, roleName);
        }        
    }
        
    /**
     * Create a Constraint on map elements.
     * 
     * @param key of the element to check.
     * @param c the constraint to check on the element.
     * @return Constraint instance.
     */
    public <T> Constraint mapElement(T key, Constraint c)
    {
        return new MapElement<T>(key, c);
    }
}