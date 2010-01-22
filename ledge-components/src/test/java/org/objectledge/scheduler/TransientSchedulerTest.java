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

package org.objectledge.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.I18n;
import org.objectledge.i18n.xml.XMLI18n;
import org.objectledge.logging.LedgeDOMConfigurator;
import org.objectledge.mail.MailSystem;
import org.objectledge.scheduler.cron.TokenMgrError;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.threads.ThreadPool;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class TransientSchedulerTest extends LedgeTestCase
{
    private FileSystem fs = null;

    private TransientScheduler scheduler;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        fs = FileSystem.getStandardFileSystem("src/test/resources");
        InputSource source = new InputSource(fs.getInputStream(
            "config/org.objectledge.logging.LoggingConfigurator.xml"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document logConfig = builder.parse(source);
        LedgeDOMConfigurator configurator = new LedgeDOMConfigurator(fs);
        configurator.doConfigure(logConfig.getDocumentElement(), LogManager.getLoggerRepository());
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(MailSystem.class));
        Configuration config = getConfig("config/org.objectledge.threads.ThreadPool.xml");
        Context context = new Context();
        ThreadPool threadPool = new ThreadPool(null, context,config, logger);
        config = getConfig("config/org.objectledge.scheduler.TransientScheduler.xml");
        XMLValidator validator = new XMLValidator(new XMLGrammarCache());
        I18n i18n = new XMLI18n(config, logger, fs, validator);
        
        ScheduleFactory[] scheduleFactories = new ScheduleFactory[2];
        scheduleFactories[0] = new AtScheduleFactory();
        scheduleFactories[1] = new CronScheduleFactory(i18n);
        MutablePicoContainer container = new DefaultPicoContainer();
        scheduler = new TransientScheduler(container, config, 
            logger, threadPool, scheduleFactories);
        scheduler.start();
    }

    public void testCreateJobDescriptor()
        throws Exception
    {
        assertNotNull(scheduler);
        try
        {
            scheduler.createJobDescriptor("foo",null,null);
            fail("should throw the exception");
        }
        catch(UnsupportedOperationException e)
        {
            //ok!
        }        
    }
    

    public void testDeleteJobDescriptor()
        throws Exception
    {
        try
        {
            scheduler.deleteJobDescriptor(null);
            fail("should throw the exception");
        }
        catch(UnsupportedOperationException e)
        {
            //ok!
        }
    }

    public void testAllowsModifications()
    {
        assertEquals(false,scheduler.allowsModifications());
    }

    public void testLoadJobs()
    {
        // TODO implement or remove
    }

    
    public void testEnable()
        throws Exception
    {
        AbstractJobDescriptor job = scheduler.getJobDescriptor("foo");
        assertNotNull(job);
        assertEquals(true, job.isEnabled());
        scheduler.disable(job);
        assertEquals(false, job.isEnabled());
        scheduler.enable(job);
        assertEquals(true, job.isEnabled());
    }

    public void testGetJobDescriptor()
    {
        AbstractJobDescriptor job = scheduler.getJobDescriptor("foo");
        try
        {
            job.setArgument("");
            fail("should throw the exception");
        }
        catch(JobModificationException e)
        {
            //ok!
        }
        try
        {
            job.setSchedule(null);
            fail("should throw the exception");
        }
        catch(JobModificationException e)
        {
            //ok!
        }
        try
        {
            job.setAutoClean(true);
            fail("should throw the exception");
        }
        catch(JobModificationException e)
        {
            //ok!
        }
        try
        {
            job.setJobClassName("");
            fail("should throw the exception");
        }
        catch(JobModificationException e)
        {
            //ok!
        }
        try
        {
            job.setRunCountLimit(1);
            fail("should throw the exception");
        }
        catch(JobModificationException e)
        {
            //ok!
        }
        try
        {
            job.setTimeLimit(null,null);
            fail("should throw the exception");
        }
        catch(JobModificationException e)
        {
            //ok!
        }
        try
        {
            job.setReentrant(false);
            fail("should throw the exception");
        }
        catch(JobModificationException e)
        {
            //ok!
        }
        
        
        
    }


    public void testGetJobDescriptors()
    {
        AbstractJobDescriptor[] jobs = scheduler.getJobDescriptors();
        assertEquals(2, jobs.length);
    }

    public void testGetScheduleTypes()
    {
        String[] types = scheduler.getScheduleTypes();
        assertEquals(2, types.length);
    }

    public void testCreateSchedule()
        throws Exception
    {
        Schedule schedule = scheduler.createSchedule("at","");
        assertNotNull(schedule);
        assertEquals("",schedule.getConfig());
        assertEquals("at",schedule.getType());
        assertNull(null,schedule.getNextRunTime(new Date(), new Date()));
        try
        {
            scheduler.createSchedule("foo","");
            fail("should throw the exception");
        }
        catch(InvalidScheduleException e)
        {
            //ok!
        }
        schedule = scheduler.createSchedule("cron","* * * * *");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,15);
        calendar.set(Calendar.MILLISECOND,0);
        Date currentTime = calendar.getTime();
        
        calendar.add(Calendar.MINUTE,-2);
        Date lastRun = calendar.getTime();
        calendar.add(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND,0);
        Date expected = calendar.getTime();
        assertEquals(expected,schedule.getNextRunTime(currentTime, lastRun));
        assertEquals("cron",schedule.getType());
        assertEquals("* * * * *",schedule.getConfig());
        
        
        //TODO add some checking
        schedule = scheduler.createSchedule("cron","* * * jan mon");
        schedule = scheduler.createSchedule("cron","0-15/60 * * jan mon");
        schedule = scheduler.createSchedule("cron","@yearly");
        try
        {
            schedule = scheduler.createSchedule("cron","foo bar");
            fail("should throw the exception");
        }
        catch(TokenMgrError e)
        {
            //ok!
        }
        try
        {
            schedule = scheduler.createSchedule("cron","\ufefe");
            fail("should throw the exception");
        }
        catch(TokenMgrError e)
        {
            //ok!
        }
        
        
        schedule = scheduler.createSchedule("at","2003-12-01 10:22");
        assertNotNull(schedule);
        calendar.set(Calendar.YEAR, 2000);
        Date oldDate = calendar.getTime();
        calendar.set(Calendar.YEAR, 2003);
        calendar.set(Calendar.MINUTE,22);
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MONTH,11);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        assertEquals(calendar.getTime(),schedule.getNextRunTime(oldDate, oldDate));
        calendar.set(Calendar.YEAR,2005);
        oldDate = calendar.getTime();
        assertNull(schedule.getNextRunTime(oldDate, oldDate));
        try
        {
            schedule.setConfig("foo-bar");
            fail("should throw the exception");
        }
        catch(InvalidScheduleException e)
        {
            //ok!
        }
    }

    public void testGetDateFormat()
    {
        assertEquals(new SimpleDateFormat(AbstractScheduler.DATE_FORMAT_DEFAULT),
                       scheduler.getDateFormat());
    }


    //////////////////////////////

    private Configuration getConfig(String name)
        throws Exception
    {
        return getConfig(fs, name);
    }

}
