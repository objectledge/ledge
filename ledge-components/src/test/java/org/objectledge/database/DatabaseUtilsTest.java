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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.jcontainer.dna.impl.DefaultConfiguration;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DatabaseUtilsTest.java,v 1.4 2004-03-10 14:29:35 fil Exp $
 */
public class DatabaseUtilsTest extends TestCase
{    
    private DataSource dataSource;

    public void setUp()
        throws Exception
    {
        dataSource = getDataSource();    
    }
    
    /**
     * Constructor for DatabaseUtilsTest.
     * @param arg0
     */
    public DatabaseUtilsTest(String arg0)
    {
        super(arg0);
    }

    /*
     * Test for void close(Connection)
     */
    public void testCloseConnection()
        throws Exception
    {
        Connection conn = null;
        DatabaseUtils.close(conn);
        conn = dataSource.getConnection();
        DatabaseUtils.close(conn);
        DatabaseUtils.close(conn);
    }

    /*
     * Test for void close(Statement)
     */
    public void testCloseStatement()
        throws Exception
    {
        Connection conn = dataSource.getConnection();
        Statement stmt = null;
        DatabaseUtils.close(stmt);
        stmt = conn.createStatement();
        DatabaseUtils.close(stmt);
        DatabaseUtils.close(stmt);
    }

    /*
     * Test for void close(ResultSet)
     */
    public void testCloseResultSet()
        throws Exception
    {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        DatabaseUtils.close(rs);
        rs = stmt.executeQuery("CHECKPOINT");
        DatabaseUtils.close(rs);
        DatabaseUtils.close(rs);
    }

    /*
     * Test for void close(Connection, Statement, ResultSet)
     */
    public void testCloseConnectionStatementResultSet()
        throws Exception
    {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("CHECKPOINT");
        DatabaseUtils.close(conn, stmt, rs);
    }

    public void testUnescapeSqlString()
        throws Exception
    {
        // Shalom!
        assertEquals("!\u05DD\u05D5\u05DC\u05E9",
            DatabaseUtils.unescapeSqlString("!\\u05DD\\u05D5\\u05DC\\u05E9"));
    }

    public void testEscapeSqlString()
        throws Exception
    {
        assertEquals("foo\\'s home directory is c:\\\\users\\\\foo",
            DatabaseUtils.escapeSqlString("foo's home directory is c:\\users\\foo"));
    }

    public void testRunScript()
        throws Exception
    {
        DatabaseUtils.runScript(dataSource, getScript("runScript.sql"));
        try
        {
            DatabaseUtils.runScript(dataSource, getScript("runScriptUnterminated.sql"));
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("unterminated statement at line 34", e.getMessage());
        }
        try
        {
            DatabaseUtils.runScript(dataSource, getScript("runScriptFailing.sql"));
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("error executing statement at line 34", e.getMessage());
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    private Reader getScript(String name)
        throws IOException
    {
        return new InputStreamReader(
            new FileInputStream("src/test/resources/database/"+name), "UTF-8");
    }
    
    private DataSource getDataSource()
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration("config","","/");
        DefaultConfiguration url = new DefaultConfiguration("url","","/config");
        url.setValue("jdbc:hsqldb:."); 
        conf.addChild(url);    
        DefaultConfiguration user = new DefaultConfiguration("user","","/config");
        user.setValue("sa");
        conf.addChild(user);
        return new HsqldbDataSource(conf);    
    }    
}
