// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.database;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Status;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.Startable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: XaPoolDataSourceTest.java,v 1.7 2004-06-28 10:08:46 fil Exp $
 */
public class XaPoolDataSourceTest extends TestCase
{
    private Transaction transaction;
    
    private DataSource dataSource;

    public void setUp()
        throws Exception
    {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(JotmTransactionTest.class));
        transaction = new JotmTransaction(0, new Context(), log, null);
        
        DefaultConfiguration conf = new DefaultConfiguration("config","","");
        DefaultConfiguration conn = new DefaultConfiguration("connection","","config");
        conf.addChild(conn);
        DefaultConfiguration driver = new DefaultConfiguration("driver","","config/connection");
        driver.setValue("org.hsqldb.jdbcDriver"); 
        conn.addChild(driver);    
        DefaultConfiguration url = new DefaultConfiguration("url","","config/connection");
        url.setValue("jdbc:hsqldb:."); 
        conn.addChild(url);    
        DefaultConfiguration user = new DefaultConfiguration("user","","config/connection");
        user.setValue("sa");
        conn.addChild(user);
        
        dataSource = new XaPoolDataSource(transaction, conf, null);
    }
    
    public void tearDown()
    {
        ((Startable)transaction).stop(); 
        ((Startable)dataSource).stop(); 
    }

    public void testPlainTransaction()
        throws Exception
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE test ( id BIGINT NOT NULL )");
            stmt.execute("INSERT INTO test ( id ) VALUES ( 1 )");
            conn.rollback();
            rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
            assertFalse(rs.next());
        }
        finally
        {
            DatabaseUtils.close(conn, stmt, rs);
        }
    }

    public void testManagedTransaction()
        throws Exception 
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            boolean controller = transaction.begin();
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
            assertFalse(rs.next());
            assertEquals(Status.STATUS_ACTIVE, transaction.getUserTransaction().getStatus());
            assertEquals(Status.STATUS_ACTIVE, transaction.getTransactionManager().
                getTransaction().getStatus());
            stmt.execute("INSERT INTO test ( id ) VALUES ( 1 )");
            transaction.rollback(controller);
            rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
            assertFalse(rs.next());
        }
        finally
        {
            DatabaseUtils.close(conn, stmt, rs);
        }
    }

    public void testEnlist()
        throws Exception 
    {
        XAConnection xaConn = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            ((XADataSource)dataSource).getXAConnection();            
        }
        catch(ClassCastException e)
        {
            // not a XADataSource, skip test
            return;
        }
        try
        {
            conn = xaConn.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
            assertFalse(rs.next());
            boolean controller = transaction.begin();
            assertEquals(Status.STATUS_ACTIVE, transaction.getUserTransaction().getStatus());
            assertEquals(Status.STATUS_ACTIVE, transaction.getTransactionManager().
                getTransaction().getStatus());
            transaction.getTransactionManager().getTransaction().
                enlistResource(xaConn.getXAResource());
            stmt.execute("INSERT INTO test ( id ) VALUES ( 1 )");
            transaction.rollback(controller);
            rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
            assertFalse(rs.next());
        }
        finally
        {
            DatabaseUtils.close(conn, stmt, rs);
        }
    }

    public void testOutOfOrderTransaction()
        throws Exception 
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
            assertFalse(rs.next());
            // start global transaction after connection is acquired
            boolean controller = transaction.begin();
            assertEquals(Status.STATUS_ACTIVE, transaction.getUserTransaction().getStatus());
            assertEquals(Status.STATUS_ACTIVE, transaction.getTransactionManager().
                getTransaction().getStatus());
            stmt.execute("INSERT INTO test ( id ) VALUES ( 1 )");
            transaction.rollback(controller);
            rs = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
            // rollback was ineffective - connection did not participate in the global tx
            assertTrue(rs.next());
        }
        finally
        {
            DatabaseUtils.close(conn, stmt, rs);
        }
    }
    
    public void testMinmalXMLConfig()
        throws Exception
    {
        Configuration config = getConfig(
            "database/org.objectledge.database.XaPoolDataSource-minimal.xml",
            "org/objectledge/database/XaPoolDataSource.rng");
        XaPoolDataSource source = new XaPoolDataSource(transaction, config, null);
        Connection conn = source.getConnection();
        conn.close();
        ((Startable)source).stop();
    }

    public void testFullXMLConfig()
        throws Exception
    {
        Configuration config = getConfig(
            "database/org.objectledge.database.XaPoolDataSource-full.xml",
            "org/objectledge/database/XaPoolDataSource.rng");
        XaPoolDataSource source = new XaPoolDataSource(transaction, config, null);
        //Connection conn = source.getConnection();
        //conn.close();
        ((Startable)source).stop();
    }

    /**
     * Constructor for XaPoolDataSourceTest.
     * @param arg0
     */
    public XaPoolDataSourceTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Load configuration. 
     * 
     * This should really be refactored somewhere visible.
     * 
     * @param configPath
     * @param schemaPath
     * @return
     * @throws Exception
     */
    public Configuration getConfig(String configPath, String schemaPath)
        throws Exception
    {
        FileSystem fs = FileSystem.getStandardFileSystem("src/test/resources");
        
        URL configUrl = fs.getResource(configPath);
        URL schemaUrl = fs.getResource(schemaPath);
        
        XMLValidator validator = new XMLValidator(new XMLGrammarCache());
        validator.validate(configUrl, schemaUrl);
        InputSource source = new InputSource(configUrl.toString());
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SAXConfigurationHandler handler = new SAXConfigurationHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(source);
        return handler.getConfiguration();
    }
}
