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

package org.objectledge.parameters.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.context.Context;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.DefaultDatabase;
import org.objectledge.database.HsqldbDataSource;
import org.objectledge.database.IdGenerator;
import org.objectledge.database.JotmTransaction;
import org.objectledge.database.persistence.DefaultPersistence;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.database.persistence.Persistent;
import org.objectledge.database.persistence.PersistentFactory;
import org.objectledge.database.persistence.TestObject;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.UndefinedParameterException;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class DBParametersTest extends TestCase
{
    private DBParametersManager manager;
    protected long anyTimeStamp = 123123132L;
    protected long anyTimeStamp2 = 232342445L;

    private Persistence persistence;

    /**
     * Constructor for DBParametersTest.
     * @param arg0
     */
    public DBParametersTest(String arg0) throws Exception
    {
        super(arg0);
        DataSource dataSource = getDataSource();
        IdGenerator idGenerator = new IdGenerator(dataSource);
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        JotmTransaction transaction = new JotmTransaction(0, 120, new Context(), logger, null);
        Database database = new DefaultDatabase(dataSource, idGenerator, transaction);
        persistence = new DefaultPersistence(database, logger);
        manager = new DefaultDBParametersManager(database, logger);
    }

    public void testDBParameters() throws Exception
    {
        Parameters parameters = manager.createContainer();
        assertNotNull(parameters);
        parameters.add("foo", "bar");
        parameters.set("bar", "foo");
        parameters.add("bar", "foo2");
        parameters = manager.getParameters(((DBParameters)parameters).getId());
        assertEquals(parameters.get("foo", "foo"), "bar");

        manager.deleteParameters(((DBParameters)parameters).getId());
        try
        {
            parameters = manager.getParameters(((DBParameters)parameters).getId());
            fail("should throw the exception");
        }
        catch (DBParametersException e)
        {
            //ok!.
        }

        parameters = manager.getParameters(1000);

        new DBParametersException("", null);
    }

    /**
      * Test for String get(String)
      */
    public void testGet() throws Exception
    {
        Parameters params = manager.createContainer();
        try
        {
            assertEquals(params.get("foo"), "bar");
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", "bar");
        assertEquals(params.get("foo"), "bar");
        params.add("foo", "true");
        try
        {
            assertEquals(params.get("foo"), "bar");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
    }

    /**
     * Test for String get(String, String)
     */
    public void testGetStringString() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.get("foo", "bar"), "bar");
        params.add("foo", "bar");
        assertEquals(params.get("foo", "buzz"), "bar");
        params.add("foo", "buzz");
        try
        {
            assertEquals(params.get("foo", "buzz"), "bar");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            //expected
        }
    }

    /**
     * Test for String getStrings(String)
     */
    public void testGetStrings() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getStrings("foo").length, 0);
        params.add("foo", "bar");
        params.add("foo", "true");
        assertEquals(params.getStrings("foo").length, 2);
        assertEquals(params.getStrings("foo")[0], "bar");
        assertEquals(params.getStrings("foo")[1], "true");
    }

    /**
     * Test for boolean getBoolean(String)
     */
    public void testGetBooleanString() throws Exception
    {
        Parameters params = manager.createContainer();
        try
        {
            assertEquals(params.getBoolean("foo"), false);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", "bar");
        assertEquals(params.getBoolean("foo"), false);
        params.set("foo", "true");
        assertEquals(params.getBoolean("foo"), true);
    }

    /**
     * Test for boolean getBoolean(String, boolean)
     */
    public void testGetBooleanStringboolean() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getBoolean("foo", false), false);
        params.add("foo", "bar");
        assertEquals(params.getBoolean("foo"), false);
        params.set("foo", "true");
        assertEquals(params.getBoolean("foo", false), true);
        params.set("foo", true);
        assertEquals(params.getBoolean("foo", false), true);
    }

    /**
     * Test for boolean getBooleans(String)
     */
    public void testGetBooleans() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getBooleans("foo").length, 0);
        params.add("foo", "bar");
        params.add("foo", "true");
        assertEquals(params.getBooleans("foo").length, 2);
        assertEquals(params.getBooleans("foo")[0], false);
        assertEquals(params.getBooleans("foo")[1], true);
    }

    /**
     * Test for boolean getDate(String)
     */
    public void testGetDateString() throws Exception
    {
        Parameters params = manager.createContainer();
        try
        {
            assertEquals(params.getDate("foo"), new Date());
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", new Date(anyTimeStamp));
        assertEquals(params.getDate("foo"), new Date(anyTimeStamp));
        params.add("foo", new Date(anyTimeStamp2));
        try
        {
            params.getDate("foo");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
        params.set("foo", "bar");
        try
        {
            params.getDate("foo");
            fail("Should throw NumberFormatException");
        }
        catch (NumberFormatException e)
        {
            // expected
        }
    }

    /**
     * Test for boolean getDate(String, Date)
     */
    public void testGetDateStringDate() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getDate("foo", new Date(anyTimeStamp)), new Date(anyTimeStamp));
        params.add("foo", new Date(anyTimeStamp2));
        assertEquals(params.getDate("foo", new Date(anyTimeStamp)), new Date(anyTimeStamp2));
    }

    /**
     * Test for boolean getDates(String)
     */
    public void testGetDates() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getDates("foo").length, 0);
        params.add("foo", new Date(anyTimeStamp));
        params.add("foo", new Date(anyTimeStamp2));
        assertEquals(params.getDates("foo").length, 2);
        assertEquals(params.getDates("foo")[0], new Date(anyTimeStamp));
        assertEquals(params.getDates("foo")[1], new Date(anyTimeStamp2));
    }
    
    /**
     * Test for float getFloat(String)
     */
    public void testGetFloatString() throws Exception
    {
        Parameters params = manager.createContainer();
        try
        {
            assertEquals(params.getFloat("foo"), 1, 1);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", 1);
        assertEquals(params.getFloat("foo"), 1, 1);
        params.add("foo", 2);
        try
        {
            params.getFloat("foo");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
        params.set("foo", "bar");
        try
        {
            params.getFloat("foo");
            fail("Should throw NumberFormatException");
        }
        catch (NumberFormatException e)
        {
            // expected
        }
    }

    /**
     * Test for float getFloat(String, float)
     */
    public void testGetFloatStringfloat() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getFloat("foo", 1.5F), 1.5F, 1.5F);
        params.add("foo", 2.5F);
        assertEquals(params.getFloat("foo", 1.5F), 2.5F, 2.5F);
    }

    /**
     * Test for float getFloats(String)
     */
    public void testGetFloats() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", 2.5F);
        assertEquals(params.getFloats("foo")[0], 2.5F, 3.5F);
        params.add("foo", 2.5F);
        assertEquals(params.getFloats("foo")[1], 2.5F, 2.5F);
    }

    /**
     * Test for int getInt(String)
     */
    public void testGetIntString() throws Exception
    {
        Parameters params = manager.createContainer();
        try
        {
            assertEquals(params.getInt("foo"), 1, 1);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", 1);
        assertEquals(params.getInt("foo"), 1, 1);
        params.add("foo", 2);
        try
        {
            params.getInt("foo");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
        params.set("foo", "bar");
        try
        {
            params.getInt("foo");
            fail("Should throw NumberFormatException");
        }
        catch (NumberFormatException e)
        {
            // expected
        }

    }

    /**
     * Test for int getInt(String, int)
     */
    public void testGetIntStringint() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getInt("foo", 1), 1, 1);
        params.add("foo", 1);
        assertEquals(params.getInt("foo", 1), 1, 1);
    }

    /**
     * Test for get ints.
     */
    public void testGetInts() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", 2);
        assertEquals(params.getInts("foo")[0], 2, 2);
        params.add("foo", 2);
        assertEquals(params.getInts("foo")[1], 2, 2);
    }

    /**
     * Test for long getLong(String)
     */
    public void testGetLongString() throws Exception
    {
        Parameters params = manager.createContainer();
        try
        {
            assertEquals(params.getLong("foo"), 1, 1);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", 1);
        assertEquals(params.getLong("foo"), 1, 1);
        params.add("foo", 2);
        try
        {
            params.getLong("foo");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
        params.set("foo", "bar");
        try
        {
            params.getLong("foo");
            fail("Should throw NumberFormatException");
        }
        catch (NumberFormatException e)
        {
            // expected
        }
    }

    /**
     * Test for long getLong(String, long)
     */
    public void testGetLongStringlong() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.getLong("foo", 1), 1, 1);
        params.add("foo", 1);
        assertEquals(params.getLong("foo", 1), 1, 1);
    }

    /**
     * Test for long getLongs()
     */
    public void testGetLongs() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", 2);
        assertEquals(params.getLongs("foo")[0], 2, 2);
        params.add("foo", 2);
        assertEquals(params.getLongs("foo")[1], 2, 2);
    }

    /**
     * Test for getParameterNames()
     */
    public void testGetParameterNames() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", "bar");
        params.set("foo", "buzz");
        params.set("bar", "buzz");
        params.set("buzz", "bar");
        assertEquals(params.getParameterNames().length, 3);
    }

    /**
     * Test for boolean isDefined()
     */
    public void testIsDefined() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.isDefined("foo"), false);
        params.set("foo", "bar");
        assertEquals(params.isDefined("foo"), true);
        params.remove("foo");
        assertEquals(params.isDefined("foo"), false);
    }

    /**
     * Test for void remove()
     */
    public void testRemove() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", "bar");
        params.set("bar", "foo");
        params.remove();
        assertEquals(params.isDefined("foo"), false);
        assertEquals(params.isDefined("foo"), false);
    }

    /**
     * Test for void remove(String)
     */
    public void testRemoveString() throws Exception
    {
        //already tested
    }

    /**
     * Test for void remove(String, String)
     */
    public void testRemoveStringString() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", "bar");
        params.set("foo", "foo");
        params.remove("foo", "bar");
        params.remove("bar", "foo");
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.get("foo", "bar"), "foo");
    }

    /**
     * Test for void remove(String, float)
     */
    public void testRemoveStringfloat() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", 1F);
        params.set("foo", 2F);
        params.remove("foo", 1F);
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.getFloat("foo", 1F), 2F, 2F);
    }

    /**
     * Test for void remove(String, int)
     */
    public void testRemoveStringint() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", 1);
        params.set("foo", 2);
        params.remove("foo", 1);
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.getFloat("foo", 1), 2, 2);
    }

    /**
     * Test for void remove(String, long)
     */
    public void testRemoveStringlong() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", 1L);
        params.set("foo", 2L);
        params.remove("foo", 1L);
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.getFloat("foo", 1L), 2L, 2L);
    }

    /**
     * Test for void remove(Set)
     */
    public void testRemoveSet() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", "bar");
        params.set("bar", "foo");
        Set set = new HashSet();
        set.add("foo");
        params.remove(set);
        assertEquals(params.isDefined("foo"), false);
        assertEquals(params.isDefined("bar"), true);
    }

    /**
     * Test for void remove(Set)
     */
    public void testRemoveExcept() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", "bar");
        params.set("bar", "foo");
        Set set = new HashSet();
        set.add("foo");
        params.removeExcept(set);
        assertEquals(params.isDefined("bar"), false);
        assertEquals(params.isDefined("foo"), true);
    }

    /**
     * Test for void set(String, String)
     */
    public void testSetStringString() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", "bar");
        assertEquals("bar".equals(params.get("foo")), true);
        params.set("foo", "buzz");
        assertEquals("bar".equals(params.get("foo")), false);
        params.add("foo", "bar");
        try
        {
            params.get("foo");
            fail("should throw the exception");
        }
        catch (AmbiguousParameterException e)
        {
            //was expected
        }
    }

    /**
     * Test for void set(String, String[])
     */
    public void testSetStringStringArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", new String[] { "foo", "bar" });
        String[] result = params.getStrings("foo");
        if (result[0].equals("foo"))
        {
            assertEquals(result[1], "bar");
        }
        else
        {
            assertEquals(result[0], "bar");
            assertEquals(result[1], "foo");
        }
    }

    /**
     * Test for void set(String, boolean)
     */
    public void testSetStringboolean() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", true);
        assertEquals(params.getBoolean("foo", false), true);
        params.set("foo", false);
        assertEquals(params.getBoolean("foo", true), false);
    }

    /**
     * Test for void set(String, boolean[])
     */
    public void testSetStringbooleanArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", new boolean[] { true, false, true });
        boolean[] result = params.getBooleans("foo");
        assertEquals(result.length, 3);
    }

    /**
     * Test for void set(String, float)
     */
    public void testSetStringfloat() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", 1F);
        assertEquals(params.getFloat("foo", 2F), 1F, 1F);
    }

    /**
     * Test for void set(String, float[])
     */
    public void testSetStringfloatArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", new float[] { 1, 2, 3 });
        assertEquals(params.getFloats("foo").length, 3);
    }

    /**
     * Test for void set(String, int)
     */
    public void testSetStringint() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", 1);
        assertEquals(params.getInt("foo", 2), 1);
    }

    /**
     * Test for void set(String, int[])
     */
    public void testSetStringintArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", new int[] { 1, 2, 3 });
        assertEquals(params.getInts("foo").length, 3);
    }

    /**
     * Test for void set(String, long)
     */
    public void testSetStringlong() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", 1L);
        assertEquals(params.getLong("foo", 2L), 1L, 1L);
    }

    /**
     * Test for void set(String, long[])
     */
    public void testSetStringlongArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.set("foo", new long[] { 1, 2, 3 });
        assertEquals(params.getInts("foo").length, 3);
    }

    /**
     * Test for void add(String, String)
     */
    public void testAddStringString() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", "bar");
        params.add("foo", "bar");
        params.add("bar", "foo");
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getStrings("foo").length, 2);
    }

    /**
     * Test for void add(String, String[])
     */
    public void testAddStringStringArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", new String[] { "bar" });
        params.add("foo", new String[] { "foo", "buz" });
        params.add("bar", new String[] { "foo" });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getStrings("foo").length, 3);
    }

    /**
     * Test for void add(String, boolean)
     */
    public void testAddStringboolean() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", true);
        params.add("foo", false);
        params.add("bar", true);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getBooleans("foo").length, 2);
        assertEquals(params.getBoolean("bar", false), true);
    }

    /**
     * Test for void add(String, boolean[])
     */
    public void testAddStringbooleanArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", new boolean[] { true });
        params.add("foo", new boolean[] { false, true });
        params.add("bar", new boolean[] { true });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getBooleans("foo").length, 3);
    }

    /**
     * Test for void add(String, float)
     */
    public void testAddStringfloat() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", 1F);
        params.add("foo", 2F);
        params.add("bar", 1F);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getFloats("foo").length, 2);
        assertEquals(params.getFloat("bar", 2F), 1F, 1F);
    }

    /**
     * Test for void add(String, float[])
     */
    public void testAddStringfloatArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", new float[] { 1 });
        params.add("foo", new float[] { 2, 3 });
        params.add("bar", new float[] { 1 });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getFloats("foo").length, 3);
    }

    /**
     * Test for void add(String, int)
     */
    public void testAddStringint() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", 1);
        params.add("foo", 2);
        params.add("bar", 1);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getInts("foo").length, 2);
        assertEquals(params.getInt("bar", 2), 1);
    }

    /**
     * Test for void add(String, int[])
     */
    public void testAddStringintArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", new int[] { 1 });
        params.add("foo", new int[] { 2, 3 });
        params.add("bar", new int[] { 1 });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getInts("foo").length, 3);
    }

    /**
     * Test for void add(String, long)
     */
    public void testAddStringlong() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", 1L);
        params.add("foo", 2L);
        params.add("bar", 1L);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getLongs("foo").length, 2);
        assertEquals(params.getLong("bar", 2L), 1L, 1L);
    }

    /**
     * Test for void add(String, long[])
     */
    public void testAddStringlongArray() throws Exception
    {
        Parameters params = manager.createContainer();
        params.add("foo", new long[] { 1 });
        params.add("foo", new long[] { 2, 3 });
        params.add("bar", new long[] { 1 });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getLongs("foo").length, 3);
    }

    /**
     * Test for void add(Parameters, boolean)
     */
    public void testAddParametersboolean() throws Exception
    {
        Parameters params = manager.createContainer();
        Parameters temp = new DefaultParameters();
        temp.add("foo", 2);
        temp.add("bar", 2);
        params.add("foo", 1);
        params.add("bar", 1);
        params.add(temp, false);
        assertEquals(params.getInts("foo").length, 2);
        assertEquals(params.getInts("bar").length, 2);
        params = new DefaultParameters();
        params.add("foo", 1);
        params.add("bar", 1);
        params.add(temp, true);
        assertEquals(params.getInts("foo").length, 1);
        assertEquals(params.getInts("bar").length, 1);
        assertEquals(params.getInt("foo"), 2);
        assertEquals(params.getInt("bar"), 2);
    }

    /**
     * Test for void add(Parameters, boolean)
     */
    public void testToString() throws Exception
    {
        Parameters params = manager.createContainer();
        assertEquals(params.toString(), "");
        assertEquals(params.getChild("prefix").getParameterNames().length, 0);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    private DataSource getDataSource() throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration("config", "", "/");
        DefaultConfiguration url = new DefaultConfiguration("url", "", "/config");
        url.setValue("jdbc:hsqldb:.");
        conf.addChild(url);
        DefaultConfiguration user = new DefaultConfiguration("user", "", "/config");
        user.setValue("sa");
        conf.addChild(user);
        DataSource ds = new HsqldbDataSource(conf);
        FileSystem fs = FileSystem.getStandardFileSystem(".");
        if(!DatabaseUtils.hasTable(ds, "ledge_id_table"))
        {
            DatabaseUtils.runScript(ds, fs.getReader("sql/database/IdGeneratorTables.sql", "UTF-8"));
        }
        if(!DatabaseUtils.hasTable(ds, "ledge_parameters"))
        {        
            DatabaseUtils.runScript(ds, 
                fs.getReader("sql/parameters/db/DBParametersTables.sql", "UTF-8"));
        }
        DatabaseUtils.runScript(ds, 
            fs.getReader("sql/parameters/db/DBParametersTest.sql", "UTF-8"));
        return ds;
    }

    private PersistentFactory testFactory = new PersistentFactory()
    {
        public Persistent newInstance()
        {
            return new TestObject();
        }
    };

}
