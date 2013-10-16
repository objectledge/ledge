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

package org.objectledge.scheduler.db;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;

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
import org.objectledge.logging.LedgeDOMConfigurator;
import org.objectledge.scheduler.AbstractJobDescriptor;
import org.objectledge.scheduler.AbstractScheduler;
import org.objectledge.scheduler.AtScheduleFactory;
import org.objectledge.scheduler.InvalidScheduleException;
import org.objectledge.scheduler.JobModificationException;
import org.objectledge.scheduler.JobNotFoundException;
import org.objectledge.scheduler.Schedule;
import org.objectledge.scheduler.ScheduleFactory;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.threads.ThreadPool;
import org.objectledge.threads.DefaultThreadPool;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class DBSchedulerTest
    extends LedgeTestCase
{
    private DBScheduler scheduler;

    private BitronixTransactionManager btm;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        InputSource source = new InputSource(getFileSystem().getInputStream(
            "config/org.objectledge.logging.LoggingConfigurator.xml"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document logConfig = builder.parse(source);
        LedgeDOMConfigurator configurator = new LedgeDOMConfigurator(getFileSystem());
        configurator.doConfigure(logConfig.getDocumentElement(), LogManager.getLoggerRepository());
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(DBScheduler.class));
        Configuration config = getConfig("config/org.objectledge.threads.ThreadPool.xml");
        Context context = new Context();
        ThreadPool threadPool = new DefaultThreadPool(null, context, config, logger);
        config = getConfig("config/org.objectledge.scheduler.TransientScheduler.xml");
        ScheduleFactory[] scheduleFactories = new ScheduleFactory[1];
        scheduleFactories[0] = new AtScheduleFactory();
        btm = new BitronixTransactionManager("hsql", "org.hsqldb.jdbc.pool.JDBCXADataSource",
            getDsProperties(), getFileSystem(), logger);
        DataSource dataSource = new BitronixDataSource("hsql", btm);
        prepareDataSource(dataSource);
        Transaction transaction = new BitronixTransaction(btm, new Context(), logger, null);
        IdGenerator idGenerator = new SequenceIdGenerator(dataSource);
        Database database = new DefaultDatabase(dataSource, idGenerator, transaction);

        Persistence persistence = new DefaultPersistence(database, logger);
        MutablePicoContainer container = new DefaultPicoContainer();
        scheduler = new DBScheduler(container, config, logger, threadPool, scheduleFactories,
            persistence);
        scheduler.start();
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
        if(!DatabaseUtils.hasTable(ds, "ledge_scheduler"))
        {
            DatabaseUtils.runScript(ds,
                getFileSystem().getReader("sql/scheduler/DBSchedulerTables.sql", "UTF-8"));
            DatabaseUtils.runScript(ds,
                getFileSystem().getReader("sql/scheduler/DBSchedulerTest.sql", "UTF-8"));
        }
    }

    public void testCreateJobDescriptor()
        throws Exception
    {
        assertNotNull(scheduler);
        Schedule schedule = scheduler.createSchedule("at", "");
        scheduler.createJobDescriptor("bar", schedule, "org.objectledge.scheduler.FooJob");
        try
        {
            scheduler.createJobDescriptor("bar", schedule, "org.objectledge.scheduler.FooJob");
            fail("should throw the exception");
        }
        catch(IllegalArgumentException e)
        {
            // ok!
        }
    }

    public void testDeleteJobDescriptor()
        throws Exception
    {
        // Schedule schedule = scheduler.createSchedule("at", "");
        // scheduler.createJobDescriptor("foo", schedule, "org.objectledge.scheduler.FooJob");
        AbstractJobDescriptor job = scheduler.getJobDescriptor("foo");
        assertNotNull(job);
        scheduler.deleteJobDescriptor(job);
    }

    public void testAllowsModifications()
    {
        assertEquals(true, scheduler.allowsModifications());
    }

    public void testEnable()
        throws Exception
    {
        Schedule schedule = scheduler.createSchedule("at", "");
        scheduler.createJobDescriptor("bar2", schedule, "org.objectledge.scheduler.FooJob");
        AbstractJobDescriptor job = scheduler.getJobDescriptor("bar2");
        assertNotNull(job);
        assertEquals(false, job.isEnabled());
        scheduler.enable(job);
        assertEquals(true, job.isEnabled());
    }

    public void testGetJobDescriptors()
        throws Exception
    {
        Schedule schedule = scheduler.createSchedule("at", "");
        scheduler.createJobDescriptor("a", schedule, "org.objectledge.scheduler.FooJob");
        scheduler.createJobDescriptor("b", schedule, "org.objectledge.scheduler.FooJob");
        AbstractJobDescriptor[] jobs = scheduler.getJobDescriptors();
        assertEquals(3, jobs.length);
        Arrays.sort(jobs, new Comparator<AbstractJobDescriptor>()
            {
                @Override
                public int compare(AbstractJobDescriptor o1, AbstractJobDescriptor o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        assertEquals("a", jobs[0].getName());
        assertEquals("b", jobs[1].getName());
        assertEquals("foo", jobs[2].getName());
    }

    public void testGetScheduleTypes()
    {
        String[] types = scheduler.getScheduleTypes();
        assertEquals(1, types.length);
        assertEquals("at", types[0]);
    }

    public void testCreateSchedule()
        throws Exception
    {
        Schedule schedule = scheduler.createSchedule("at", "");
        assertNotNull(schedule);
        assertEquals("", schedule.getConfig());
        assertEquals("at", schedule.getType());
        assertNull(null, schedule.getNextRunTime(new Date(), new Date()));
        try
        {
            scheduler.createSchedule("foo", "");
            fail("should throw the exception");
        }
        catch(InvalidScheduleException e)
        {
            // ok!
        }
    }

    public void testGetDateFormat()
    {
        assertEquals(new SimpleDateFormat(AbstractScheduler.DATE_FORMAT_DEFAULT),
            scheduler.getDateFormat());
    }

    public void testDBJobDescriptor()
        throws Exception
    {
        Schedule schedule = scheduler.createSchedule("at", "");
        scheduler.createJobDescriptor("foo.bar", schedule, "org.objectledge.scheduler.FooJob");
        DBJobDescriptor job = (DBJobDescriptor)scheduler.getJobDescriptor("foo.bar");
        assertEquals(false, job.isRunning());
        job.setSchedule(schedule);
        job.setJobClassName("foo.bar");
        job.setAutoClean(false);
        assertEquals("foo.bar", job.getJobClassName());
        assertEquals(false, job.getAutoClean());
    }

    public void testAdditional()
    {
        JobNotFoundException e = new JobNotFoundException("foo");
        assertEquals("foo", e.getMessage());
        JobNotFoundException ee = new JobNotFoundException("bar", e);
        assertEquals("bar", ee.getMessage());
        Exception eee = new JobModificationException("bar", e);
        assertEquals("bar", eee.getMessage());
    }

    // ////////////////////////////

    private Configuration getConfig(String name)
        throws Exception
    {
        return getConfig(getFileSystem(), name);
    }

}
