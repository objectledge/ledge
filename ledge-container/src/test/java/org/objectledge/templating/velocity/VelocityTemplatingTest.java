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

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jcontainer.dna.Configuration;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.impl.ClasspathFileSystemProvider;
import org.objectledge.filesystem.impl.LocalFileSystemProvider;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.xml.XMLValidator;

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
			XMLValidator xv = new XMLValidator(fs);
			ConfigurationFactory cf = new ConfigurationFactory(null, fs, xv, "config");
			Configuration config = cf.getConfig(VelocityTemplating.class);
			new LoggingConfigurator(cf);    
			Logger logger = Logger.getLogger(VelocityTemplating.class);
			templating = new VelocityTemplating(config, logger, fs);
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
    	}
    	catch(TemplateNotFoundException e)
    	{
    		fail(e.getMessage());
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
}
