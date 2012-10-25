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

package org.objectledge.database.persistence;

import java.io.Reader;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

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
import org.objectledge.filesystem.FileSystem;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class PersistenceTest extends TestCase
{
    private Persistence persistence;

    private BitronixTransactionManager btm;

    @Override
    public void setUp()
        throws Exception
    {
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        btm = new BitronixTransactionManager("hsql", "org.hsqldb.jdbc.pool.JDBCXADataSource",
            getDsProperties());
        DataSource dataSource = new BitronixDataSource("hsql", btm);
        prepareDataSource(dataSource);
        Transaction transaction = new BitronixTransaction(btm, new Context(), logger, null);
        IdGenerator idGenerator = new SequenceIdGenerator(dataSource);
        Database database = new DefaultDatabase(dataSource, idGenerator, transaction);
        persistence = new DefaultPersistence(database, logger);
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
        FileSystem fs = FileSystem.getStandardFileSystem(".");
        Reader reader;
        if(!DatabaseUtils.hasTable(ds, "test_object"))
        {
            reader = fs.getReader("sql/database/persistence/TestObject.sql", "UTF-8");
            DatabaseUtils.runScript(ds, reader);
        }
        else
        {
            reader = fs.getReader("sql/database/persistence/TruncateTestObject.sql", "UTF-8");
            DatabaseUtils.runScript(ds, reader);
        }
    }

    /*
     * Test for Persistent load(long, PersistentFactory)
     */
    public void testLoadlongPersistentFactory() throws Exception
    {
        TestObject object = persistence.load(testFactory, 1);
        assertNull(object);
        object = persistence.load(testFactory, 0);
        assertNull(object);
        List<TestObject> list;
        list = persistence.load(testFactory);
        assertEquals(list.size(), 0);
        object = new TestObject("foo", new Date());
        persistence.save(object);
        object = persistence.load(testFactory, object.getId());
        assertNotNull(object);
        list = persistence.load(testFactory);
        assertEquals(list.size(), 1);
        list = persistence.load(testFactory, "id = ?", object.getId());
        assertEquals(list.size(), 1);
        list = persistence.load(testFactory, "id = ?", -1);
        assertEquals(list.size(), 0);
        list = persistence.load(testFactory, "value = ?", "bar");
        assertEquals(list.size(), 0);
        list = persistence.load(testFactory, "value = ?", "foo");
        assertEquals(list.size(), 1);
        object.setValue("bar");
        persistence.save(object);
        list = persistence.load(testFactory, "value = ?", "foo");
        assertEquals(list.size(), 0);
        list = persistence.load(testFactory, "value = ?", "bar");
        assertEquals(list.size(), 1);
        object.setValue("foo");
        assertEquals(object.getValue(), "foo");
        persistence.revert(object);
        assertEquals(object.getValue(), "bar");

        TestObject object2 = new TestObject("foo", null);
        try
        {
            persistence.revert(object2);
            fail("should throw the exception");
        }
        catch (IllegalStateException e)
        {
            //ok!            
        }
        object2.setSaved(10);
        try
        {
            persistence.revert(object2);
            fail("should throw the exception");
        }
        catch(SQLException e)
        {
            //ok!            
        }
        object2.setSaved(-1);
        
        assertEquals(persistence.exists("test_object",null),true);
        assertEquals(persistence.exists("test_object","id = 10"),false);
        assertEquals(persistence.count("test_object",null),1);
        
        assertEquals(list.size(),1);
        persistence.delete(object);
        list = persistence.load(testFactory);
        assertEquals(list.size(),0);
        
        assertEquals(persistence.exists("test_object",null),false);
        assertEquals(persistence.exists("test_object","id = 10"),false);
        assertEquals(persistence.count("test_object",null),0);
        assertEquals(persistence.count("test_object","id = 10"),0);
        
        
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    private PersistentFactory<TestObject> testFactory = new PersistentFactory<TestObject>()
    {
        public TestObject newInstance()
        {
            return new TestObject();
        }
    };

}
