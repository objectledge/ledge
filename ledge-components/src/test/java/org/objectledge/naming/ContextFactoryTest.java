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

package org.objectledge.naming;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;
import org.hsqldb.jdbcDataSource;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class ContextFactoryTest extends TestCase
{
    private FileSystem fs;
    
    private Logger log;
    
    private ContextFactory contextFactory;

    /**
     * Constructor for ContextFactoryTest.
     * @param arg0
     */
    public ContextFactoryTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
        throws Exception
    {
        String root = System.getProperty("ledge.root");
        if (root == null)
        {
            throw new RuntimeException("system property ledge.root undefined." + 
                " use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        try
        {
            InputSource source = new InputSource(fs.
                getInputStream("config/org.objectledge.logging.LoggingConfigurator.xml"));
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document logConfig = builder.parse(source);
            DOMConfigurator.configure(logConfig.getDocumentElement());

            log = new Log4JLogger(org.apache.log4j.Logger.
                getLogger(ContextFactory.class));
            PicoContainer container = new DefaultPicoContainer();
            Configuration config = getConfig("naming/mock.xml"); 
            contextFactory = new ContextFactory(container, config, log);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private Configuration getConfig(String name)
        throws Exception
    {
        InputSource source = new InputSource(fs.
            getInputStream(name));
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SAXConfigurationHandler handler = new SAXConfigurationHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(source);
        return handler.getConfiguration();
    }

    public void testGetContext()
    {
        try
        {
            Context context = contextFactory.getContext("foo");
            assertNotNull(context);
            contextFactory.reconnect("foo");
            context = contextFactory.getContext("foo");
            assertNotNull(context);
            context = contextFactory.getContext("bar");
            assertNotNull(context);
            contextFactory.reconnect("bar");
            context = contextFactory.getContext("bar");
            assertNotNull(context);
        }
        catch (NamingException e)
        {
            fail("Exception occured: " + e);
        }
        try
        {
            contextFactory.getContext("unknown");
            fail("shoud throw the exception");
        }
        catch (NamingException e)
        {
            //ok!
        }
    }

    public void testGetDirContext()
    {
        try
        {
            DirContext context = contextFactory.getDirContext("foo");
            assertNotNull(context);
            contextFactory.reconnect("foo");
            context = contextFactory.getDirContext("foo");
            assertNotNull(context);
            context = contextFactory.getDirContext("bar");
            assertNotNull(context);
            contextFactory.reconnect("bar");
            context = contextFactory.getDirContext("bar");
            assertNotNull(context);
        }
        catch (NamingException e)
        {
            fail("Exception occured: " + e);
        }
        try
        {
            contextFactory.getDirContext("unknown");
            fail("shoud throw the exception");
        }
        catch (NamingException e)
        {
            //ok!
        }
    }

    public void testDbNaming()
        throws Exception
    {
        DataSource ds = getDataSource();
        DefaultPicoContainer container = new DefaultPicoContainer();
        container.registerComponentInstance("TestDS", ds);
        container.registerComponentInstance(DataSource.class, ds);
        Configuration config = getConfig("naming/dbNaming.xml");
        contextFactory = new ContextFactory(container, config, log);
        
        contextFactory.getContext("byKey");
        contextFactory.getContext("byClass");
    }
    
    private DataSource getDataSource()
        throws Exception
    {
        jdbcDataSource ds = new jdbcDataSource();
        ds.setDatabase("jdbc:hsqldb:.");
        ds.setUser("sa");
        ds.setPassword("");
        DatabaseUtils.runScript(ds, getScript("dbcontext_cleanup.sql"));
        DatabaseUtils.runScript(ds, getScript("dbcontext_id_generator.sql"));
        DatabaseUtils.runScript(ds, getScript("dbcontext_hsqldb.sql"));
        DatabaseUtils.runScript(ds, getScript("dbcontext_test.sql"));
        return ds;
    }
    
    private LineNumberReader getScript(String path)
        throws IOException
    {
        return new LineNumberReader(new InputStreamReader(
            new FileInputStream("src/test/resources/naming/"+path), "ISO-8859-2"));
    }
}