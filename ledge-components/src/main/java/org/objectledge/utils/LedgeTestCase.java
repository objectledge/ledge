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

package org.objectledge.utils;

import java.lang.reflect.Modifier;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.jmock.core.DynamicMock;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public abstract class LedgeTestCase extends MockObjectTestCase
{
    private FileSystem fileSystem;

    // filesystem ///////////////////////////////////////////////////////////////////////////////    

    protected FileSystem getFileSystem() throws Exception
    {
        if (fileSystem == null)
        {
            String root = System.getProperty("ledge.root");
            if (root != null && root.length() > 0)
            {
                fileSystem = FileSystem.getStandardFileSystem(root);
            }
            else
            {
                fileSystem = FileSystem.getStandardFileSystem(".");
            }
        }
        return fileSystem;
    }

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
        InputSource source = new InputSource(fs.getInputStream(name));
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SAXConfigurationHandler handler = new SAXConfigurationHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(source);
        return handler.getConfiguration();
    }

    protected void checkSchema(String configuration, String schema) throws Exception
    {

        getFileSystem();
        XMLValidator validator = new XMLValidator();
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
            throw new Exception("composition file " + schemaUrl + "is missing or invalid", e);
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
            throw new Exception("composition file " + configUrl + "is missing or invalid", e);
        }
    }

    // jMock goodies ////////////////////////////////////////////////////////////////////////////
 
    /**
     * Creates a mock object implementation.
     * 
     * @param mockedType the mocked type.
     * @return a mock object implementation.
     */   
    public Mock mock(Class mockedType)
    {
        if((mockedType.getModifiers() & Modifier.INTERFACE) == Modifier.INTERFACE)
        {
            return new Mock(mockedType);
        }
        else
        {
            return new org.jmock.cglib.Mock(mockedType);
        }
    }

    /**
     * Creates a mock object implementation.
     * 
     * @param mockedType the mocked type.
     * @param name the mock object name.
     * @return a mock object implementation.
     */   
    public Mock mock(Class mockedType, String name)
    {
        if((mockedType.getModifiers() & Modifier.INTERFACE) == Modifier.INTERFACE)
        {
            return new Mock(mockedType, name);
        }
        else
        {
            return new org.jmock.cglib.Mock(mockedType, name);
        }
    }
    
    /**
     * Creates a mock object implementation.
     * 
     * @parm coreMock a coreMock to wrap.
     * @return a mock object implementation.
     */   
    public Mock mock(DynamicMock coreMock)
    {
        return new Mock(coreMock);
    }    
    
    /**
     * Create a Constraint on map elements
     * 
     * @param key of the element to check.
     * @param c the constraint to check on the element.
     * @return Constraint instance.
     */
    public Constraint mapElement(Object key, Constraint c)
    {
        return new MapElement(key, c);
    }

}