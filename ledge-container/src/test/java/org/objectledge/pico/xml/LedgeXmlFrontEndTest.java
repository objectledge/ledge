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

package org.objectledge.pico.xml;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.test.FooComponent;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.script.xml.XmlFrontEnd;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * <p>Created on Dec 8, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeXmlFrontEndTest.java,v 1.2 2003-12-15 15:34:10 fil Exp $
 */
public class LedgeXmlFrontEndTest extends TestCase
{

    /**
     * Constructor for LedgeXmlFrontEnd.
     * @param arg0
     */
    public LedgeXmlFrontEndTest(String arg0)
    {
        super(arg0);
    }

    public void testLedgeXmlFrontEnd() 
        throws Exception
    {
        InputSource source = new InputSource("composition/simple.xml");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document config = builder.parse(source);
        
        MutablePicoContainer container = new DefaultPicoContainer();
        container.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
        container.registerComponentInstance("org.objectledge.filesystem.FileSystem:root", ".");
        XmlFrontEnd xmlFrontEnd = new LedgeXmlFrontEnd();
        xmlFrontEnd.createPicoContainer(config.getDocumentElement(), container);
        
        container.getComponentInstance(FileSystem.class);
    }

    public void testAdapterFactory() 
        throws Exception
    {
        InputSource source = new InputSource("composition/adapter.xml");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document config = builder.parse(source);
        
        MutablePicoContainer rootContainer = new DefaultPicoContainer();
        rootContainer.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
        rootContainer.registerComponentInstance("org.objectledge.filesystem.FileSystem:root", ".");
        XmlFrontEnd xmlFrontEnd = new LedgeXmlFrontEnd();
        PicoContainer container = xmlFrontEnd.createPicoContainer(config.getDocumentElement(), 
            rootContainer);
        
        FooComponent foo = (FooComponent)container.getComponentInstance(FooComponent.class);     
        assertNotNull(foo);
        foo.log();
        
        Logger log = Logger.getLogger(this.getClass());
        dump(log, container, 0);
    }
    
    private void dump(Logger log, PicoContainer container, int depth)
    {
        StringBuffer buff = new StringBuffer();
        for(int i=0; i<depth; i++)
        {
            buff.append("  ");
        }
        String indent = buff.toString();
        List components = container.getComponentInstances();
        for(int i=0; i<components.size(); i++)
        {
            log.debug(indent+components.get(i));
            if(components.get(i) instanceof PicoContainer && components.get(i) != container)
            {
                dump(log, (PicoContainer)components.get(i), depth+1);
            }
        }
    }
}
