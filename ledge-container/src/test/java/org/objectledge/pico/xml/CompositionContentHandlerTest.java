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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Test for the CompositionContentHandler.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CompositionContentHandlerTest.java,v 1.1 2005-02-04 02:29:26 rafal Exp $
 */
public class CompositionContentHandlerTest
    extends TestCase
{
    private XMLReader reader;
    private MutablePicoContainer parentContainer;
    private CompositionContentHandler compositionContentHandler;
    
    /**
     * {@inheritDoc}
     */
    protected void setUp()
        throws Exception
    {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        reader = parserFactory.newSAXParser().getXMLReader();
        parentContainer = new DefaultPicoContainer();
        compositionContentHandler = new CompositionContentHandler(parentContainer, getClass()
            .getClassLoader(), null);
        reader.setContentHandler(compositionContentHandler);
    }
    
    private void parse(String path)
        throws Exception
    {
        reader.parse(new InputSource(new InputStreamReader(new BufferedInputStream(
            new FileInputStream("src/test/resources/composition/"+path+".xml")))));
    }
    
    public void test1()
        throws Exception
    {
        parse("test1");
        assertNotNull(compositionContentHandler.getResult());
    }

    public void test2()
        throws Exception
    {
        parse("test2");
        PicoContainer container = compositionContentHandler.getResult();
        assertEquals(DefaultPicoContainer.class, container.getClass());
        PicoContainer sub1 = (PicoContainer)container.getComponentInstance("string-key");
        assertEquals(AlternateContainer.class, sub1.getClass());
        PicoContainer sub2 = (PicoContainer)container.getComponentInstance(PicoContainer.class);
        assertEquals(DefaultPicoContainer.class, sub2.getClass());
    }
    
    public void test3() 
        throws Exception
    {
        parse("test3");
    }

    public void test4() 
        throws Exception
    {
        parse("test4");
    }

    public void test5() 
        throws Exception
    {
        parse("test5");
    }

    public void test6() 
        throws Exception
    {
        parse("test6");
        PicoContainer result = compositionContentHandler.getResult();
        Test6c t6c = (Test6c)result.getComponentInstance(Test6c.class);
        assertEquals("<a, string s, int 1>", t6c.dump());
    }
    
    public void test7()
        throws Exception
    {
        parse("test7");
        PicoContainer result = compositionContentHandler.getResult(); 
        Test7 t71 = (Test7)result.getComponentInstance("t71");
        assertEquals("[a, b]", t71.dump());
        Test7 t72 = (Test7)result.getComponentInstance("t72");
        assertEquals("[[a, b], [a, b, c]]", t72.dump());
    }

    public void test8() 
        throws Exception
    {
        parse("test8");
        PicoContainer result = compositionContentHandler.getResult();
        Test6c t6c1 = (Test6c)result.getComponentInstance("t6c1");
        assertEquals("<a, string s, int 1>", t6c1.dump());
        Test6c t6c2 = (Test6c)result.getComponentInstance("t6c2");
        assertEquals("[string a, string b, int 9]", t6c2.dump());
        Test6d t6d = (Test6d)result.getComponentInstance("t6d");
        assertNotNull(t6d);
    }
    
    public void test9()
        throws Exception
    {
        parse("test9");
        PicoContainer result = compositionContentHandler.getResult();
        Context context = (Context)result.getComponentInstance(Context.class);
        Context a1 = (Context)result.getComponentInstance("a1");
        assertSame(context, a1);
        Context a2 = (Context)result.getComponentInstance("a2");
        assertSame(context, a2);
    }
    
    public void testSimple()
        throws Exception
    {
        parentContainer.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
        parentContainer.registerComponentInstance("org.objectledge.filesystem.FileSystem:root", 
            "src/test/resources");
        parse("simple");
        PicoContainer result = compositionContentHandler.getResult();
        assertNotNull(result.getComponentInstance(FileSystem.class));
    }

    public void testAdapter()
        throws Exception
    {
        parentContainer.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
        parentContainer.registerComponentInstance("org.objectledge.filesystem.FileSystem:root", 
            "src/test/resources");
        parse("adapter");
        PicoContainer result = compositionContentHandler.getResult();
        org.objectledge.test.FooComponent foo = 
            (org.objectledge.test.FooComponent)result.getComponentInstance(
                org.objectledge.test.FooComponent.class);
        assertNotNull(foo);
    }
}
