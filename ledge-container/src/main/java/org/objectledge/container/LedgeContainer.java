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

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nanocontainer.ConsoleNanoContainerMonitor;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.NanoContainerMonitor;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pico.xml.LedgeXmlFrontEnd;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.script.PicoCompositionException;
import org.picoextras.script.xml.XmlFrontEnd;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * A customized NanoContainer that uses {@link FileSystem} to load the composition file.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeContainer.java,v 1.6 2004-01-13 13:27:38 fil Exp $
 */
public class LedgeContainer
    extends NanoContainer
{
    // constants ////////////////////////////////////////////////////////////////////////////////
    
    /** Location of the container composition file. */
    public static final String COMPOSITION_FILE = "/container.xml";

    /** Default xml front end implementation. */
    public static final String FRONT_END_CLASS = "org.objectledge.pico.xml.LedgeXmlFrontEnd";
    
    /** Config base path key. */
    public static final String CONFIG_BASE_KEY = "org.objectledge.ConfigBase";
    
    // instance variables ///////////////////////////////////////////////////////////////////////
    
    /** The filesystem to use for reading composition & it's schema. */
    protected FileSystem fs;
    
    /** The path name of the configuration base directory. */
    protected String configBase;
    
    /** The document builder to use. */
    protected DocumentBuilder documentBuilder;

    /** The boot container (hard rerence to make sure it not goes away). */
    protected MutablePicoContainer bootContainer;

    // initialization //////////////////////////////////////////////////////////////////////////

    /**
     * Creates a ledge container instance.
     * 
     * <p>A default JAXP parser, and a console monitor will be used.</p>
     * 
     * @param fs the file system to load container composition file from.
     * @param configBase the configuration directory path.
     * @throws ParserConfigurationException if the JAXP subsystem is not configured correctly.
     */
    public LedgeContainer(FileSystem fs, String configBase)
        throws ParserConfigurationException
    {
        this(fs, configBase, new ConsoleNanoContainerMonitor());
    }

    /**
     * Creates a ledge container instance.
     * 
     * <p>A default JAXP parser will be used.</p>
     * 
     * @param fs the file system to load container composition file from.
     * @param configBase the configuration directory path.
     * @param monitor a nano container monitor to use.
     * @throws ParserConfigurationException if the JAXP subsystem is not configured correctly.
     */
    public LedgeContainer(FileSystem fs, String configBase, NanoContainerMonitor monitor)
        throws ParserConfigurationException
    {
        this(fs, configBase, DocumentBuilderFactory.newInstance().newDocumentBuilder(), monitor);
    }
    
    /**
     * Creates a ledge container instance.
     * 
     * @param fs the file system to load container composition file from.
     * @param configBase the configuration directory path.
     * @param documentBuilder a document builder to use for parsing composition file.
     * @param monitor a nano container monitor to use.
     */
    public LedgeContainer(FileSystem fs, String configBase, DocumentBuilder documentBuilder, 
        NanoContainerMonitor monitor)
    {
        super(monitor);
        this.fs = fs;
        this.configBase = configBase;
        this.documentBuilder = documentBuilder;
        init();
    }
    
    /**
     * {@inheritDoc}
     */
    protected PicoContainer createPicoContainer()
        throws PicoCompositionException
    {
        URL compositionUrl = null;
        Element composition;
        try
        {
            compositionUrl = getCompositionUrl();
            composition = getComposition(compositionUrl);
        }
        catch(Exception e)
        {
            throw new PicoCompositionException("composition file is missing or invalid", e);
        }

        XmlFrontEnd frontEnd;
        try
        {
            frontEnd = getFrontEnd(composition);
        }
        catch(Exception e)
        {
            throw new PicoCompositionException("failed to intialize front end", e);
        }
        
        try
        {
            bootContainer = new DefaultPicoContainer();
            bootContainer.registerComponentInstance(FileSystem.class, fs);
            bootContainer.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
            bootContainer.registerComponentInstance(CONFIG_BASE_KEY, configBase);
            return frontEnd.createPicoContainer(composition, bootContainer);
        }
        catch(Exception e)
        {
            throw new PicoCompositionException("failed to compose container from "+compositionUrl,
                e);
        }
    }

    private Element getComposition(URL compositionUrl) 
        throws Exception 
    {
        XMLValidator validator = new XMLValidator();
        validator.validate(compositionUrl, fs.getResource(LedgeXmlFrontEnd.SCHEMA_PATH));
        InputSource inputSource = new InputSource(compositionUrl.toString());
        Document document = documentBuilder.parse(inputSource);
        return document.getDocumentElement();
    }
    
    private URL getCompositionUrl()
        throws MalformedURLException
    {
        return fs.getResource(configBase + COMPOSITION_FILE);
    }
    
    private XmlFrontEnd getFrontEnd(Element element)
        throws Exception
    {
        String cn = element.getAttribute("front-end");
        if(cn == null || cn.equals(""))
        {
            cn = FRONT_END_CLASS;    
        }
        return (XmlFrontEnd) Class.forName(cn).newInstance();
    }
}
