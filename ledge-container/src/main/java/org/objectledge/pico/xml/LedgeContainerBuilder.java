// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.pico.xml;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.PicoContainer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * <code>LedgeContainerBuilder</code> has the same functionality as the Nano-provided
 * <code>XMLFrontEnd</code> but with a few changes and additions.
 *  
 * <ul>
 * <li>{@link org.picocontainer.defaults.ComponentAdapterFactory}
 *  may be declared for the top level container</li>
 * <li>{@link org.picocontainer.defaults.ComponentAdapterFactory}
 *  may be declared using tags, with nesting (DecoratingComponentAdapterFactoryPatern)</li>
 * <li>for both components and component-parameters, both string and class keys may be used</li>
 * <li>{@link org.objectledge.pico.SequenceParameter}s are supported</li>
 * <li>The composition definition (file) is checked against a
 *  <a href="http://relaxng.org/">RelaxNG schema</a>.</li>
 * </ul>
 * 
 * <h3>Related</h3>
 * <ul>
 * <li>{@link ./container.rng}</li>
 * <li><a href="http://objectledge.org/viewcvs.cgi/ledge-container/src/test/resources/container1/config/container.xml?rev=HEAD&content-type=text/vnd.viewcvs-markup">Example configuration file</a></li>
 * </ul>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski </a>
 * @version $Id: LedgeContainerBuilder.java,v 1.2 2005-07-07 08:30:01 zwierzem Exp $
 */
public class LedgeContainerBuilder
    extends ScriptedContainerBuilder
{
    /** Location of the container composition schema. */
    public static final String SCHEMA_PATH = "org/objectledge/pico/xml/container.rng";
    
    private final URL scriptURL;
    private final ClassLoader classLoader;
    private final SAXParserFactory parserFactory;

    /**
     * Creates new LedgeContainerBuilder instance.
     * 
     * @param configURL the URL of XML based container configuration file
     * @param classLoader the class loader.
     */
    public LedgeContainerBuilder(final URL configURL, final ClassLoader classLoader)
    {
        super(configURL, classLoader);
        this.scriptURL = configURL;
        this.classLoader = classLoader;
        try
        {
            parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
        }
        catch(Exception e)
        {
            throw new NanoContainerMarkupException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected PicoContainer createContainerFromScript(PicoContainer parentContainer,
        Object assemblyScope)
    {
        validate(scriptURL, parentContainer);
        CompositionContentHandler handler = new CompositionContentHandler(
            parentContainer, classLoader, assemblyScope);
        try
        {
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            reader.setEntityResolver(new EntityResolver()
                {
                    public InputSource resolveEntity(String publicId, String systemId)
                        throws IOException
                    {
                        URL url = new URL(scriptURL, systemId);
                        return new InputSource(url.openStream());
                    }
                });
            reader.setContentHandler(handler);
            reader.parse(new InputSource(scriptURL.toString()));
            return handler.getResult();
        }
        catch(SAXParseException e)
        {
            throw new PicoCompositionException("parse error " + e.getMessage() + " in " +
                e.getSystemId() + " at line " + e.getLineNumber(), e);
        }
        catch(Exception e)
        {
            throw new PicoCompositionException("error processing " + scriptURL, e);
        }
    }
    
    private void validate(URL scriptURL, PicoContainer container)
    {
        XMLValidator validator = (XMLValidator)container.getComponentInstance(XMLValidator.class);
        FileSystem fileSystem = (FileSystem)container.getComponentInstance(FileSystem.class);
        
        try
        {
            validator.validate(scriptURL, fileSystem.getResource(SCHEMA_PATH));
        }
        catch(SAXParseException e)
        {
            throw new PicoCompositionException("parse error " + e.getMessage() + " in " +
                e.getSystemId() + " at line " + e.getLineNumber(), e);
        }
        catch(Exception e)
        {
            throw new PicoCompositionException("composition file " + scriptURL +
                " is missing or invalid", e);
        }
    }
}
