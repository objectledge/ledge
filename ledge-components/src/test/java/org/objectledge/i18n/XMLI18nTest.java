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

package org.objectledge.i18n;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.filesystem.impl.ClasspathFileSystemProvider;
import org.objectledge.i18n.impl.I18nTool;
import org.objectledge.i18n.impl.XMLI18n;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class XMLI18nTest extends TestCase
{
	/** i18n component */
	private XMLI18n i18n;
	
    /**
     * Constructor for XMLI18nTest.
     * @param arg0
     */
    public XMLI18nTest(String arg0)
    {
        super(arg0);
        String root = System.getProperty("ledge.root");
        if (root == null)
        {
            throw new RuntimeException("system property ledge.root undefined." + 
			" use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath",
        										 getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        try
        {
            InputSource source = new InputSource(
            	fs.getInputStream("config/org.objectledge.logging.LoggingConfigurator.xml"));
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document logConfig = builder.parse(source);
            DOMConfigurator.configure(logConfig.getDocumentElement());

            source = new InputSource(fs.getInputStream("config/org.objectledge.i18n.I18n.xml"));
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            SAXConfigurationHandler handler = new SAXConfigurationHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(source);
            Configuration config = handler.getConfiguration();
            Logger logger = Logger.getLogger(XMLI18n.class);
            i18n = new XMLI18n(config, new Log4JLogger(logger), fs , "locale/");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    public void testInit()
    {
    	i18n.reload();
    }

    /*
     * Test for String get(Locale, String)
     */
    public void testGetLocaleString()
    {
    	Locale plLocale = new Locale("pl","PL");
		Locale enLocale = new Locale("en","EN");
    	assertEquals("foo2",i18n.get(plLocale,"foo2"));
    	assertEquals("bar",i18n.get(plLocale,"foo.bar.foo"));
    	assertEquals("bar",i18n.get(enLocale,"foo.bar.foo"));
		assertEquals("bar",i18n.get(enLocale,"bar.foo.bar.foo"));
    }

    /*
     * Test for String get(Locale, String, String[])
     */
    public void testGetLocaleStringStringArray()
    {
		Locale plLocale = new Locale("pl","PL");
		String key = "foo_$1_bar_$2";
		String[] values = new String[]{"foo","bar"};
		assertEquals("foo_foo_bar_bar", i18n.get(plLocale, key, values));
    }

    public void testGetTool()
    {
		I18nTool tool = (I18nTool)i18n.getTool();
    	assertNotNull(tool);
		String key = "foo_$1_bar_$2";
		String[] values = new String[]{"foo","bar"};
		assertEquals("foo_foo_bar_bar", tool.get(key, values));
    	assertEquals("bar", tool.get("foo.bar.foo"));
    	tool = tool.usePrefix("foo");
    	assertEquals("bar", tool.get("bar.foo"));
		tool = tool.usePrefix("bar");
		assertEquals("bar", tool.get("foo"));
		tool = tool.useLocale("en_EN");
		assertEquals("bar", tool.get("foo"));
		String output = tool.get(key, values);
		assertEquals("foo.bar.foo_foo_bar_bar", output);
		tool = tool.usePrefix("");
    }

    public void testGetKey()
    {
    	assertEquals("i18n",i18n.getKey());
    }

}
