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

package org.objectledge.templating.velocity;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.filesystem.impl.ClasspathFileSystemProvider;
import org.objectledge.pipeline.Pipeline;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.templating.TemplatingContextLoader;
import org.objectledge.templating.tools.ContextToolFactory;
import org.objectledge.templating.tools.ContextToolPopulator;
import org.objectledge.templating.tools.ContextToolRecycler;
import org.objectledge.templating.tools.ContextTools;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Velocity Templating test.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class VelocityTemplatingTest extends TestCase
{
    private Templating templating;

    /**
     * Constructor for VelocityTemplatingTest.
     * @param arg0 the arg.
     */
    public VelocityTemplatingTest(String arg0)
    {
        super(arg0);
		String root = System.getProperty("ledge.root");
		if(root == null)
		{
			throw new RuntimeException("system property ledge.root undefined." +				" use -Dledge.root=.../ledge-container/src/test/resources");
		}
		FileSystemProvider lfs = new LocalFileSystemProvider("local", root);
		FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
			getClass().getClassLoader());
		FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
		try
		{
            InputSource source = new InputSource(fs.getInputStream(
                "config/org.objectledge.logging.LoggingConfigurator.xml"));
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document logConfig = builder.parse(source);
            DOMConfigurator.configure(logConfig.getDocumentElement());

            source = new InputSource(fs.getInputStream(
                "config/org.objectledge.templating.velocity.VelocityTemplating.xml"));
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            SAXConfigurationHandler handler = new SAXConfigurationHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(source);
            Configuration config = handler.getConfiguration();
                
			Logger logger = Logger.getLogger(VelocityTemplating.class);
			templating = new VelocityTemplating(config, new Log4JLogger(logger), fs);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
		super.setUp();
    }

    /**
    	 * CreateContext method test. 
     */
    public void testCreateContext()
    {
        assertNotNull(templating);
        TemplatingContext context = templating.createContext();
        assertNotNull(context);
        context.put("foo","bar");
        assertEquals("bar",context.get("foo"));
        assertTrue(context.containsKey("foo"));
        assertEquals(1, context.getKeys().length);
        context.remove("foo");
        assertNull(context.get("foo"));
		assertEquals(0, context.getKeys().length);
    }

    /**
     * CreateContext method test. 
     */
    public void testTemplateExists()
    {
    	assertEquals(templating.templateExists("foo"), false);
    	assertEquals(templating.templateExists("bar"), true);
    }

    /**
     * CreateContext method test. 
     */
    public void testGetTemplate()
    {
    	try
    	{
    		assertNotNull(templating.getTemplate("bar"));
    		// cache test
			assertNotNull(templating.getTemplate("bar"));
    	}
    	catch(TemplateNotFoundException e)
    	{
    		fail(e.getMessage());
    	}
		try
		{
			templating.getTemplate("foo");
			fail("Should throw TemplateNotFoundException");
		}
		catch(TemplateNotFoundException e)
		{
			//do nothing
		}
    }

    /**
     * CreateContext method test. 
     */
    public void testEvaluate()
    {
		try
		{
			TemplatingContext context = templating.createContext();
			context.put("foo","bar");
			StringWriter target = new StringWriter();
			StringReader source = new StringReader("foo $foo");
			templating.evaluate(context,source,target,"test"); 
			assertEquals("foo bar",target.toString());
		}
		catch(MergingException e)
		{
			fail(e.getMessage());
		}
    }

    /**
     * CreateContext method test. 
     */
    public void testMerge()
    {
		try
		{
			Template template = templating.getTemplate("bar");
			TemplatingContext context = templating.createContext();
			context.put("foo","bar");
			assertEquals("foo bar",template.merge(context));
		}
		catch(TemplateNotFoundException e)
		{
			fail(e.getMessage());
		}
		catch(MergingException e)
		{
			fail(e.getMessage());
		}
    }

    /**
     * CreateContext method test. 
     */
    public void testGetTemplateEncoding()
    {
    	assertEquals("ISO-8859-2", templating.getTemplateEncoding());
    }
    
	public void testPipelineComponents()
	{
		try
		{
		    Context context = new Context();
	    	Runnable[] runnable = new Runnable[0];
	    	Runnable[] tryValves = new Runnable[3];
	    	ContextTools contextTools = new ContextTools(new ContextToolFactory[0]);
	    	tryValves[0] = new TemplatingContextLoader(context, templating);
			tryValves[1] = new ContextToolPopulator(context, contextTools);
			tryValves[2] = new ContextToolRecycler(context, contextTools);
		
			Logger logger = Logger.getLogger(Pipeline.class);
			Pipeline pipe = new Pipeline(context, new Log4JLogger(logger), 
										tryValves, runnable, runnable);
			pipe.run();
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
    }
}
