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

package org.objectledge.parameters.directory;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
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
import org.objectledge.context.Context;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.DefaultDatabase;
import org.objectledge.database.IdGenerator;
import org.objectledge.database.JotmTransaction;
import org.objectledge.database.persistence.DefaultPersistence;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.naming.ContextFactory;
import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.UndefinedParameterException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DirectoryParametersTest extends TestCase
{
    private FileSystem fs = null;

    private ContextFactory contextFactory;
    
    /**
     * Constructor for DirectoryParametersTest.
     * @param arg0
     */
    public DirectoryParametersTest(String arg0)
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
        fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        try
        {
            InputSource source = new InputSource(fs.
                getInputStream("config/org.objectledge.logging.LoggingConfigurator.xml"));
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document logConfig = builder.parse(source);
            DOMConfigurator.configure(logConfig.getDocumentElement());

            Logger logger = new Log4JLogger(org.apache.log4j.Logger.
                getLogger(ContextFactory.class));
            DataSource ds = getDataSource();
            DefaultPicoContainer container = new DefaultPicoContainer();
            IdGenerator idGenerator = new IdGenerator(ds);
            JotmTransaction transaction = new JotmTransaction(0, new Context(), logger);
            Database database = new DefaultDatabase(ds, idGenerator, transaction);
            Persistence persistence = new DefaultPersistence(database, logger);
            container.registerComponentInstance(Persistence.class, persistence);            
            Configuration config = getConfig("naming/dbNaming.xml");
            contextFactory = new ContextFactory(container, config, logger);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }        
    }

    /*
     * Test for String get(String)
     */
    public void testGetString()
        throws Exception
    {
        DirContext ctx = contextFactory.getDirContext("people");
        assertNotNull(ctx);
        Attributes attrs = new BasicAttributes(true);
        ctx.createSubcontext("bar", attrs);
        DirContext dctx = (DirContext)ctx.lookup("bar");
        assertNotNull(dctx);
        Parameters parameters = new DirectoryParameters(dctx);
        assertEquals(parameters.getParameterNames().length,0);
        assertEquals(parameters.get("foo","foo"),"foo");
        try
        {
            parameters.get("foo");
            fail("should throw the exception");
        }
        catch(UndefinedParameterException e)
        {
            //ok!
        }
        parameters.add("foo","bar");
        assertEquals(parameters.get("foo","foo"),"bar");
        parameters.add("foo","bar2");
        try
        {
            parameters.get("foo");
            fail("should throw the exception");
        }
        catch(AmbiguousParameterException e)
        {
            //ok!
        }
        assertEquals(parameters.getStrings("foo").length,2);
        assertEquals(parameters.getStrings("bar").length,0);
        assertEquals(parameters.getParameterNames().length,1);
        assertEquals(parameters.isDefined("foo"),true);                
        assertEquals(parameters.isDefined("bar"),false);
        parameters.add("bar","foo");
        assertEquals(parameters.get("bar","bar"),"foo");
        parameters.remove("bar");
        assertEquals(parameters.get("bar","bar"),"bar");
        parameters.add("bar","foo");
        assertEquals(parameters.get("bar","bar"),"foo");
        parameters.remove("bar", "foo");
        assertEquals(parameters.get("bar","bar"),"bar");
        parameters.remove();
        assertEquals(parameters.getParameterNames().length,0);
        parameters.add("bar","foo");
        parameters.add("foo","bar");
        assertEquals(parameters.getParameterNames().length,2);
        HashSet set = new HashSet();
        set.add("foo");
        parameters.remove(set);
        assertEquals(parameters.get("bar","bar"),"foo");
        assertEquals(parameters.get("foo","foo"),"foo");
        parameters.add("bar","foo2");
        parameters.add("foo","bar");
        parameters.removeExcept(set);
        assertEquals(parameters.get("bar","bar"),"bar");
        assertEquals(parameters.get("foo","foo"),"bar");
        parameters.remove();
        parameters.add("foo", new String[] { "bar" });
        parameters.add("foo", new String[] { "foo", "buz" });
        parameters.add("bar", new String[] { "foo" });
        assertEquals(parameters.getParameterNames().length, 2);
        assertEquals(parameters.getStrings("foo").length, 3);
        
        parameters.remove();
        Parameters temp = new DefaultParameters();
        temp.add("foo",2);
        temp.add("bar",2);
        parameters.add("foo",1);
        parameters.add("bar",1);
        parameters.add(temp,false);
        assertEquals(parameters.getInts("foo").length,2);
        assertEquals(parameters.getInts("bar").length,2);
        parameters.remove();
        parameters.add("foo",1);
        parameters.add("bar",1);
        parameters.add(temp,true);
        assertEquals(parameters.getInts("foo").length,1);
        assertEquals(parameters.getInts("bar").length,1);
        assertEquals(parameters.getInt("foo"),2);
        assertEquals(parameters.getInt("bar"),2);
        
        
        parameters.remove();
        parameters.add("foo","bar");
        parameters.set("foo","foo");
        assertEquals(parameters.get("foo","bar"),"foo");
        parameters.set("foo", new String[] { "foo", "buz" });
        assertEquals(parameters.getStrings("foo").length,2);
        parameters.set("foo", new boolean[] {true});
        assertEquals(parameters.getBoolean("foo"),true);
        parameters.set("foo", new float[] {1.0F, 2.0F});
        assertEquals(parameters.getStrings("foo").length,2);
        parameters.set("foo", new int[] {1,2});
        assertEquals(parameters.getStrings("foo").length,2);
        parameters.set("foo", new long[] {1,2});
        assertEquals(parameters.getStrings("foo").length,2);
        parameters.toString();
        Parameters params = parameters.getChild("bar");
        assertEquals(params.getParameterNames().length,0);
    }


    /////////////// private 
    private DataSource getDataSource()
        throws Exception
    {
        jdbcDataSource ds = new jdbcDataSource();
        ds.setDatabase("jdbc:hsqldb:.");
        ds.setUser("sa");
        ds.setPassword("");
        DatabaseUtils.runScript(ds, getScript("naming_context_cleanup.sql"));
        DatabaseUtils.runScript(ds, getScript("dbcontext_id_generator.sql"));
        DatabaseUtils.runScript(ds, getScript("naming_context_hsqldb.sql"));
        DatabaseUtils.runScript(ds, getScript("naming_context_test.sql"));
        return ds;
    }
        
    private Reader getScript(String path)
        throws IOException
    {
        return fs.getReader("naming/"+path, "ISO-8859-2");
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

}
