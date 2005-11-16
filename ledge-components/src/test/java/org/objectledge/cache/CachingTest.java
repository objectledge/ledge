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

package org.objectledge.cache;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.cache.impl.DelegateMap;
import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.LRUMap;
import org.objectledge.cache.spi.StatisticsMap;
import org.objectledge.context.Context;
import org.objectledge.database.Database;
import org.objectledge.database.DefaultDatabase;
import org.objectledge.database.HsqldbDataSource;
import org.objectledge.database.IdGenerator;
import org.objectledge.database.JotmTransaction;
import org.objectledge.database.persistence.DefaultPersistence;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.notification.Notification;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.ThreadPool;
import org.objectledge.utils.LedgeTestCase;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class CachingTest extends LedgeTestCase
{
    private static final String TEST_CHANNEL = "test_channel";
    
    private CacheFactorySPI caching;

    private Notification notification;

    public void setUp()
    throws Exception
    {
        Context context = new Context();
        Valve cleanup = null;
        Configuration config = new DefaultConfiguration("config", "", "/config");
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        ThreadPool pool = new ThreadPool(cleanup, context, config, logger);
        DataSource dataSource = getDataSource();
        IdGenerator idGenerator = new IdGenerator(dataSource);
        JotmTransaction transaction = new JotmTransaction(0, 120, new Context(), logger, null);
        Database database = new DefaultDatabase(dataSource, idGenerator, transaction);
        Persistence persistence = new DefaultPersistence(database, logger);
        notification = new Notification();

        FileSystemProvider lfs = new LocalFileSystemProvider("local", "src/test/resources");
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
                                                getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        config = getConfig(fs, "config/org.objectledge.cache.CacheFactory.xml");
        caching = new DefaultCacheFactory(config, logger, pool, notification, persistence);
    }

    public void testCaching()
    {
        assertNotNull(caching);
    }

    public void testGetMap()
    {
        Map map = caching.getMap("LRUMap");
        assertNotNull(map);
        assertNotNull(caching.getMap("foo"));
        try
        {
            caching.getMap("bar");
            fail("should throw the exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
    }

    /*
     * Test for Map getInstance(String)
     */
    public void testGetInstanceString()
    {
        Object cache = caching.getInstance("instance1"); 
        assertNotNull(cache);
        assertNotNull(((DelegateMap)cache).getDelegate());
        assertNotNull(caching.getInstance("instance2"));
        try
        {
            caching.getInstance("not_configured");
            fail("should throw the exception");    
        }
        catch(IllegalArgumentException e)
        {
            //ok!
        }
    }

    /*
     * Test for Map getInstance(String, String)
     */
    public void testGetInstanceStringString()
        throws Exception
    {
        Object cache = caching.getInstance("not_configured","alias1"); 
        assertNotNull(cache);
        assertNotNull(((DelegateMap)cache).getDelegate());
        Object cache2 = caching.getInstance("not_configured","aliasXXX");
        assertNotNull(cache2);
        assertEquals(cache, cache2);
        assertEquals(caching.getInstance("not_configured"),cache);
        try
        {
            caching.getInstance("not_configured_2","alias_not_configured");
            fail("should throw the exception");    
        }
        catch(IllegalArgumentException e)
        {
            //ok!
        }
    }
    
    public void testGetMaps()
    {
        assertNotNull(caching.getHashMap());
        assertNotNull(caching.getLRUMap(4));
        assertNotNull(caching.getSoftMap(4));
        assertNotNull(caching.getStatisticsMap("xxx",new HashMap()));
        assertNotNull(caching.getTimeoutMap(1000));
        ValueFactory valueFactory = caching.getPersitenceValueFactory(TestValue.class); 
        assertNotNull(valueFactory);
        assertNotNull(caching.getFactoryMap(valueFactory, new HashMap()));
        assertNotNull(caching.getDistributedMap("yyy", new HashMap()));
    }
    
    

    public void testHash()
    {
        caching.getInstance("hash");
    }

    public void testTimeout()
    {
        try
        {
            Map map = caching.getInstance("timeout");
            map.put("k1","v");
            Thread.sleep(500);
            assertNotNull("@500", map.get("k1"));
            Thread.sleep(1500);
            assertNull("@2000", map.get("k1"));
        }
        catch(InterruptedException e)
        {
            fail("thread was interrupted?!");
        }
    }

    public void testLRU()
    {
        Map map = caching.getInstance("LRU");
        map.put("k1","v");
        map.put("k2","v");
        map.put("k3","v");
        map.put("k4","v");
        map.put("k5","v");
        map.get("k3");
        map.get("k4");
        map.get("k2");
        map.get("k1");
        map.get("k5");
        map.put("k6", "v");
        assertNotNull("k1 in", map.get("k1"));
        assertNotNull("k2 in", map.get("k2"));
        assertNull("k3 out", map.get("k3"));
        assertNotNull("k4 in", map.get("k4"));
        assertNotNull("k5 in", map.get("k5"));
        assertNotNull("k6 in",map.get("k6"));
    }

    public void testSoft()
    {
        Map map = caching.getInstance("soft");
        // allocate 100MB. The test fails with OutOfMemoryError when the
        // "hash" instance is used instead.
        int count = 100;
        for(int i=0; i<count; i++)
        {
             map.put(new Integer(i), new byte[1024*1024]);
        }
        for(int i=1; i<=5; i++)
        {
            assertNotNull("last - "+i, map.get(new Integer(count-i)));
        }
        for(int i=0; i<count; i++)
        {
             map.remove(new Integer(i));
        }        
    }

    private String stats = "statistics: 3 items, 3 requests, 2 hits, 1 misses, 67% hit ratio\n";

    public void testStatistics()
    {
        Map map = caching.getInstance("statistics");
        map.put("k1","v");
        map.put("k2","v");
        map.put("k3","v");
        map.get("k1");
        map.get("k2");
        map.get("k4");
        assertEquals(stats,((StatisticsMap)map).getStatistics());
    }

    public void testDistributed()
    {
        Map map = caching.getInstance("distributed");
        /*
        byte[] msg = "proceed".getBytes();
        map.put("k","v");
        //TODO when notification implemented
        notification.sendNotification(TEST_CHANNEL, msg, false);
        try
        {
            // wait for the things to settle down
            Thread.sleep(500);
        }
        catch(InterruptedException e)
        {
            // oh really
        }
        assertNull("remote remove", map.get("k"));
        notification.sendNotification(TEST_CHANEL, msg, false);
        */
    }
    
    public void testFactory()
    {
        Map map = caching.getInstance("factory");
        /**
        TestValue v = (TestValue)map.get(new Long(1));
        assertNotNull("v@1",v);
        assertEquals("apples",v.getName());
        v = (TestValue)map.get(new Long(11));
        assertNull("v@11",v);
        */
    }

    public void testGlobalStatistics()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        caching.getStatus(pw);
        String status = sw.getBuffer().toString();
        //you dont know the test sequence
        //assertEquals("global stats", stats, status);
    }

    public void testConfigAlias()
    {
        Map map = caching.getInstance("shared");
        assertTrue("shared=LRUMap", LRUMap.class.isAssignableFrom(map.getClass()));
    }

    public void testCustom()
    {
        Map map = caching.getInstance("custom");
        assertTrue("custom=LRUMap", LRUMap.class.isAssignableFrom(map.getClass()));
    }

    private int updateCounter = 0;

    private class Delayed
        implements DelayedUpdate
    {
        public long getUpdateLatency()
        {
            return 100;
        }
        
        public void update()
        {
            updateCounter++;    
        }
    }           

    public void testDelayedUpdate()
    {
        try
        {
            Delayed d = new Delayed();
            caching.register(d);
            Thread.sleep(200);
            caching.register(d);
            Thread.sleep(200);
            caching.register(d);
            Thread.sleep(200);
            assertEquals("delayed 3", 3, updateCounter);
            caching.register(d);
            Thread.sleep(20);
            caching.register(d);
            Thread.sleep(20);
            caching.register(d);
            Thread.sleep(200);
            assertEquals("delayed 4", 4, updateCounter);
        }
        catch(InterruptedException e)
        {
            // oh really?
        }
    }

    public void testGetNotification()
    {
        assertNotNull(caching.getNotification());
    }

    // private
    private DataSource getDataSource() throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration("config", "", "/");
        DefaultConfiguration url = new DefaultConfiguration("url", "", "/config");
        url.setValue("jdbc:hsqldb:.");
        conf.addChild(url);
        DefaultConfiguration user = new DefaultConfiguration("user", "", "/config");
        user.setValue("sa");
        conf.addChild(user);
        return new HsqldbDataSource(conf);
    }
}
