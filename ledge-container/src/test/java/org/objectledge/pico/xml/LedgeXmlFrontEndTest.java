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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.test.FooComponent;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.xml.sax.SAXParseException;

/**
 *
 * <p>Created on Dec 8, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeXmlFrontEndTest.java,v 1.10 2004-06-01 15:34:50 fil Exp $
 */
public class LedgeXmlFrontEndTest extends TestCase
{
    private FileSystem fs;
    
    private XMLValidator validator;

    /**
     * Constructor for LedgeXmlFrontEnd.
     * @param arg0
     */
    public LedgeXmlFrontEndTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
        throws Exception
    {
        super.setUp();
        
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
                "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        fs = FileSystem.getStandardFileSystem(root);
        validator = new XMLValidator(new XMLGrammarCache());
    }

    private Reader getReader(String path)
        throws IOException
    {
        return new InputStreamReader(fs.getInputStream(path));
    }

    public void testLedgeXmlFrontEnd() 
        throws Exception
    {
        try
        {
            validator.validate(fs.getResource("composition/simple.xml"), 
                fs.getResource(LedgeXMLContainerBuilder.SCHEMA_PATH));
        }
        catch(SAXParseException e)
        {
            throw new Exception("parse error "+e.getMessage()+" in "+e.getSystemId()+
                " at line "+e.getLineNumber(), e);
        }
        
        MutablePicoContainer container = new DefaultPicoContainer();
        container.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
        container.registerComponentInstance("org.objectledge.filesystem.FileSystem:root", 
            "src/test/resources");
        
        ContainerBuilder builder = 
            new LedgeXMLContainerBuilder(getReader("composition/simple.xml"), 
            getClass().getClassLoader());
        ObjectReference parentRef = new SimpleReference();
        parentRef.set(container);
        ObjectReference containerRef = new SimpleReference();
        builder.buildContainer(containerRef, parentRef, null);
        container = (MutablePicoContainer)containerRef.get();

        container.getComponentInstance(FileSystem.class);
    }

    public void testAdapterFactory() 
        throws Exception
    {
        try
        {
            validator.validate(fs.getResource("composition/adapter.xml"), 
                fs.getResource(LedgeXMLContainerBuilder.SCHEMA_PATH));
        }
        catch(SAXParseException e)
        {
            throw new Exception("parse error "+e.getMessage()+" in "+e.getSystemId()+
                " at line "+e.getLineNumber(), e);
        }
        
        MutablePicoContainer container = new DefaultPicoContainer();
        container.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
        container.registerComponentInstance("org.objectledge.filesystem.FileSystem:root", 
            "src/test/resources");
        
        ContainerBuilder builder = 
            new LedgeXMLContainerBuilder(getReader("composition/adapter.xml"), 
            getClass().getClassLoader());
        ObjectReference parentRef = new SimpleReference();
        parentRef.set(container);
        ObjectReference containerRef = new SimpleReference();
        builder.buildContainer(containerRef, parentRef, null);
        container = (MutablePicoContainer)containerRef.get();
        
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
