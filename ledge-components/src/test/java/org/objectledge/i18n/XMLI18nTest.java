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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.i18n.xml.XMLI18n;
import org.objectledge.logging.LedgeDOMConfigurator;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class XMLI18nTest extends LedgeTestCase
{
	/** i18n component */
	private XMLI18n i18n;
	
    public void setUp()
    throws Exception
    {
        try
        {
            FileSystem fs = getFileSystem();
            InputSource source = new InputSource(
            	fs.getInputStream("config/org.objectledge.logging.LoggingConfigurator.xml"));
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document logConfig = builder.parse(source);
            LedgeDOMConfigurator configurator = new LedgeDOMConfigurator(fs);
            configurator.doConfigure(logConfig.getDocumentElement(), 
                LogManager.getLoggerRepository());

            Configuration config = getConfig(fs, "config/org.objectledge.i18n.I18n.xml");
            Logger logger = Logger.getLogger(XMLI18n.class);
            XMLValidator validator = new XMLValidator(new XMLGrammarCache());
            i18n = new XMLI18n(config, new Log4JLogger(logger), fs, validator);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void tearDown() throws Exception
    {
        super.tearDown();
        i18n = null;
    }

    public void testInit()
    {
    	i18n.reload();
    }

	/*
	 * Test for Locale getDefaultLocale()
	 */
	public void testGetDefaultLocale()
	{
		Locale plLocale = new Locale("pl","PL");
		assertEquals(plLocale, i18n.getDefaultLocale());
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
        assertEquals("foo&bar",i18n.get(plLocale,"bar.foo.foobar"));
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
		I18nTool tool = new I18nTool(i18n, i18n.getDefaultLocale(), null);
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

    public void testUndefined()
    {
        Locale plLocale = new Locale("pl","PL");
        Locale noLocale = new Locale("no","NO");
        assertEquals("foo2", i18n.get(noLocale, "foo2"));
        assertEquals("undefined", i18n.get(plLocale, "undefined"));
    }
}
