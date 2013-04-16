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

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.btm.BitronixDataSource;
import org.objectledge.btm.BitronixTransaction;
import org.objectledge.btm.BitronixTransactionManager;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;
import org.objectledge.test.LedgeTestCase;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ThreadDataSourceTest.java,v 1.7 2005-10-09 19:43:21 rafal Exp $
 */
public class ThreadDataSourceTest
    extends LedgeTestCase
{
    private BitronixTransactionManager btm;

    @Override
    public void setUp()
        throws Exception
    {
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        btm = new BitronixTransactionManager("hsql", "org.hsqldb.jdbc.pool.JDBCXADataSource",
            getDsProperties(), getFileSystem(), logger);
        dataSource = new BitronixDataSource("hsql", btm);
        transaction = new BitronixTransaction(btm, new Context(), logger, null);
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
    
    private ThreadDataSource threadDataSource;
    
    private Valve guardValve;
    
    private Context context;
    
    private Logger log;

    private DataSource dataSource;

    private Transaction transaction;
    
    public void setUp(int tracing)
        throws Exception
    {
        context = new Context();
        context.clearAttributes();
        log = new Log4JLogger(org.apache.log4j.Logger.getLogger(ThreadDataSource.class));
        threadDataSource = new ThreadDataSource(dataSource, new ThreadDataSource.Config()
            .withTracing(tracing).withValidationQuery("SELECT * FROM (VALUES(1))"), context, null,
            null, log);
        guardValve = new ThreadDataSource.GuardValve(log);
    }
    
    public void testBasic()
        throws Exception
    {
        setUp(0);
        Connection c1 = threadDataSource.getConnection();
        Connection c2 = threadDataSource.getConnection();
        assertSame(c1, c2);
        c2.close();
        c1.close();
        guardValve.process(context);
    }

    public void testUser()
        throws Exception
    {
        setUp(0);
        Connection c1 = threadDataSource.getConnection("sa","");
        Connection c2 = threadDataSource.getConnection("sa","");
        assertSame(c1, c2);
        c2.close();
        c1.close();
        guardValve.process(context);
    }
    
    public void testTooManyCloses()
        throws Exception
    {
        setUp(0);
        Connection c1 = threadDataSource.getConnection();
        Connection c2 = threadDataSource.getConnection();
        assertSame(c1, c2);
        c2.close();
        c1.close();
        try
        {
            c1.close();
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("too many close() calls", e.getMessage());
        }
        guardValve.process(context);
    }
    
    public void testTooFewCloses()
        throws Exception
    {
        setUp(1);
        Connection c1 = threadDataSource.getConnection();
        Connection c2 = threadDataSource.getConnection();
        assertSame(c1, c2);
        c2.close();
        guardValve.process(context);
    }

    public void testUserTooFewCloses()
        throws Exception
    {
        setUp(4);
        Connection c1 = threadDataSource.getConnection("sa","");
        Connection c2 = threadDataSource.getConnection("sa","");
        assertSame(c1, c2);
        c2.close();
        guardValve.process(context);
    }
    
    public void testNoTracingTooFewCloses()
        throws Exception
    {
        setUp(0);
        Connection c1 = threadDataSource.getConnection();
        Connection c2 = threadDataSource.getConnection();
        assertSame(c1, c2);
        c2.close();
        guardValve.process(context);
    }
    
    public void testOutOfOrderTransaction()
        throws Exception
    {
        setUp(0);
        Connection c1 = threadDataSource.getConnection();
        try
        {
            transaction.begin();
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("Thread owns open database connection(s) " +
                    "that would ignore global transaction (see log)", e.getMessage());
        }
        finally
        {
            DatabaseUtils.close(c1);
        }
    }
}
