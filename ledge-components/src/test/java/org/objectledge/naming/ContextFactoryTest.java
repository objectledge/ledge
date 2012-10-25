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

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
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
import org.objectledge.test.LedgeTestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class ContextFactoryTest
    extends LedgeTestCase
{
    private Logger log;

    private ContextFactory contextFactory;

    private BitronixTransactionManager btm;

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

            log = new Log4JLogger(org.apache.log4j.Logger.getLogger(ContextFactory.class));
            PicoContainer container = new DefaultPicoContainer();
            Configuration config = getConfig("naming/mock.xml");
            contextFactory = new ContextFactory(container, config, log);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void tearDown()
    {
        if(btm != null)
        {
            btm.stop();
        }
    }

    private Properties getDsProperties()
    {
        Properties properties = new Properties();
        properties.put("url", "jdbc:hsqldb:.");
        properties.put("user", "sa");
        return properties;
    }

    private Configuration getConfig(String name)
        throws Exception
    {
        return getConfig(getFileSystem(), name);
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
        catch(NamingException e)
        {
            fail("Exception occured: " + e);
        }
        try
        {
            contextFactory.getContext("unknown");
            fail("shoud throw the exception");
        }
        catch(NamingException e)
        {
            // ok!
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
        catch(NamingException e)
        {
            fail("Exception occured: " + e);
        }
        try
        {
            contextFactory.getDirContext("unknown");
            fail("shoud throw the exception");
        }
        catch(NamingException e)
        {
            // ok!
        }
    }

    public void testDbNaming()
        throws Exception
    {
        DefaultPicoContainer container = new DefaultPicoContainer();
        btm = new BitronixTransactionManager("hsql", "org.hsqldb.jdbc.pool.JDBCXADataSource",
            getDsProperties());
        DataSource ds = new BitronixDataSource("hsql", btm);
        prepareDataSource(ds);
        Transaction transaction = new BitronixTransaction(btm,
            new org.objectledge.context.Context(), log, null);
        IdGenerator idGenerator = new SequenceIdGenerator(ds);
        Database database = new DefaultDatabase(ds, idGenerator, transaction);
        Persistence persistence = new DefaultPersistence(database, log);
        container.registerComponentInstance(Persistence.class, persistence);

        container.registerComponentInstance("TestDS", ds);
        container.registerComponentInstance(DataSource.class, ds);
        Configuration config = getConfig("naming/dbNaming.xml");
        contextFactory = new ContextFactory(container, config, log);

        contextFactory.getContext("byKey");
        contextFactory.getContext("byClass");
    }

    private void prepareDataSource(DataSource ds)
        throws Exception
    {
        if(!DatabaseUtils.hasTable(ds, "ledge_naming_context"))
        {
            DatabaseUtils.runScript(ds, getScript("sql/naming/DBNamingTables.sql"));
        }
        DatabaseUtils.runScript(ds, getScript("sql/naming/DBNamingTest.sql"));
    }

    private Reader getScript(String path)
        throws IOException
    {
        return getFileSystem().getReader(path, "ISO-8859-2");
    }
}
