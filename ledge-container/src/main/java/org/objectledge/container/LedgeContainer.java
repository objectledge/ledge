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
 * @version $Id: LedgeContainer.java,v 1.3 2003-12-16 10:48:58 fil Exp $
 */
public class LedgeContainer
    extends NanoContainer
{
    // constants ////////////////////////////////////////////////////////////////////////////////
    
    /** Location of the container composition file. */
    public static final String COMPOSITION_PATH = "config/container.xml";

    /** Default xml front end implementation. */
    public static final String FRONT_END_CLASS = "org.objectledge.pico.xml.LedgeXmlFrontEnd";
    
    // instance variables ///////////////////////////////////////////////////////////////////////
    
    /** The filesystem to use for reading composition & it's schema. */
    protected FileSystem fs;
    
    /** The document builder to use. */
    protected DocumentBuilder documentBuilder;

    // initialization //////////////////////////////////////////////////////////////////////////

    /**
     * Creates a ledge container instance.
     * 
     * <p>A default JAXP parser, and a console monitor will be used.</p>
     * 
     * @param fs the file system to load container composition file from.
     * @throws ParserConfigurationException if the JAXP subsystem is not configured correctly.
     */
    public LedgeContainer(FileSystem fs)
        throws ParserConfigurationException
    {
        this(fs, new ConsoleNanoContainerMonitor());
    }

    /**
     * Creates a ledge container instance.
     * 
     * <p>A default JAXP parser will be used.</p>
     * 
     * @param fs the file system to load container composition file from.
     * @param monitor a nano container monitor to use.
     * @throws ParserConfigurationException if the JAXP subsystem is not configured correctly.
     */
    public LedgeContainer(FileSystem fs, NanoContainerMonitor monitor)
        throws ParserConfigurationException
    {
        this(fs, DocumentBuilderFactory.newInstance().newDocumentBuilder(), monitor);
    }
    
    /**
     * Creates a ledge container instance.
     * 
     * @param fs the file system to load container composition file from.
     * @param documentBuilder a document builder to use for parsing composition file.
     * @param monitor a nano container monitor to use.
     */
    public LedgeContainer(FileSystem fs, DocumentBuilder documentBuilder, 
        NanoContainerMonitor monitor)
    {
        super(monitor);
        this.fs = fs;
        this.documentBuilder = documentBuilder;
    }
    
    /**
     * {@inheritDoc}
     */
    protected PicoContainer createPicoContainer()
        throws PicoCompositionException
    {
        Element composition;
        try
        {
            composition = getComposition();
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
            MutablePicoContainer bootContainer = new DefaultPicoContainer();
            bootContainer.registerComponentInstance(FileSystem.class, fs);
            bootContainer.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
            return frontEnd.createPicoContainer(composition, bootContainer);
        }
        catch(Exception e)
        {
            throw new PicoCompositionException("failed to compose container", e);
        }
    }

    private Element getComposition() 
        throws Exception 
    {
        XMLValidator validator = new XMLValidator(fs);
        validator.validate(COMPOSITION_PATH, LedgeXmlFrontEnd.SCHEMA_PATH);
        InputSource inputSource = new InputSource(fs.getInputStream(COMPOSITION_PATH));
        Document document = documentBuilder.parse(inputSource);
        return document.getDocumentElement();
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
