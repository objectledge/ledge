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

import java.util.HashSet;
import java.util.Properties;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.btm.BitronixDataSource;
import org.objectledge.btm.BitronixTransaction;
import org.objectledge.btm.BitronixTransactionManager;
import org.objectledge.context.Context;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.DefaultDatabase;
import org.objectledge.database.IdGenerator;
import org.objectledge.database.SequenceIdGenerator;
import org.objectledge.database.Transaction;
import org.objectledge.database.persistence.DefaultPersistence;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LedgeDOMConfigurator;
import org.objectledge.naming.ContextFactory;
import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.UndefinedParameterException;
import org.objectledge.test.LedgeTestCase;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DirectoryParametersTest extends LedgeTestCase
{
    private ContextFactory contextFactory;
    
    private BitronixTransactionManager btm;

    public void setUp()
    	throws Exception
	{
        FileSystem fs = getFileSystem();
        InputSource source = new InputSource(fs.getInputStream(
            "config/org.objectledge.logging.LoggingConfigurator.xml"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document logConfig = builder.parse(source);
        LedgeDOMConfigurator configurator = new LedgeDOMConfigurator(fs);
        configurator.doConfigure(logConfig.getDocumentElement(), LogManager.getLoggerRepository());

        Logger logger = new Log4JLogger(org.apache.log4j.Logger.
            getLogger(ContextFactory.class));
        DefaultPicoContainer container = new DefaultPicoContainer();
        btm = new BitronixTransactionManager("hsql", "org.hsqldb.jdbc.pool.JDBCXADataSource",
            getDsProperties(), getFileSystem(), logger);
        DataSource dataSource = new BitronixDataSource("hsql", btm);
        prepareDataSource(dataSource);
        Transaction transaction = new BitronixTransaction(btm, new Context(), logger, null);
        IdGenerator idGenerator = new SequenceIdGenerator(dataSource);
        Database database = new DefaultDatabase(dataSource, idGenerator, transaction);
        Persistence persistence = new DefaultPersistence(database, logger);
        container.registerComponentInstance(Persistence.class, persistence);            
        Configuration config = getConfig("naming/dbNaming.xml");
            contextFactory = new ContextFactory(container, config, logger);    
	}
    
    public void tearDown()
    {
        btm.stop();
    }

    private Properties getDsProperties()
    {
        Properties properties = new Properties();
        properties.put("url", "jdbc:hsqldb:.");
        properties.put("user", "sa");
        return properties;
    }

    private void prepareDataSource(DataSource ds)
        throws Exception
    {
        FileSystem fs = getFileSystem();
        if(!DatabaseUtils.hasTable(ds, "ledge_naming_context"))
        {
            DatabaseUtils.runScript(ds, fs.getReader("sql/naming/DBNamingTables.sql", "UTF-8"));
        }
        DatabaseUtils.runScript(ds, fs.getReader("sql/naming/DBNamingTest.sql", "UTF-8"));
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
        HashSet<String> set = new HashSet<String>();
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


    private Configuration getConfig(String name)
        throws Exception
    {
        return getConfig(getFileSystem(), name);
    }

}
