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
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.mail.MailSystem;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class TransientSchedulerTest extends TestCase
{
    private FileSystem fs = null;

    private TransientScheduler scheduler;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        String root = System.getProperty("ledge.root");
        fs = FileSystem.getStandardFileSystem(root);
        InputSource source = new InputSource(fs.getInputStream("config/org.objectledge.logging.LoggingConfigurator.xml"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document logConfig = builder.parse(source);
        DOMConfigurator.configure(logConfig.getDocumentElement());
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(MailSystem.class));
        Configuration config = getConfig("config/org.objectledge.threads.ThreadPool.xml");
        Context context = new Context();
        ThreadPool threadPool = new ThreadPool(null, context,config, logger);
        config = getConfig("config/org.objectledge.scheduler.TransientScheduler.xml");
        ScheduleFactory[] scheduleFactories = new ScheduleFactory[1];
        scheduleFactories[0] = new AtScheduleFactory();
        //scheduleFactories[1] = new CronScheduleFactory(i18n);
        MutablePicoContainer container = new DefaultPicoContainer();
        scheduler = new TransientScheduler(container, config, logger, threadPool, scheduleFactories);
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

    public void testGetJobDescriptors()
    {
        AbstractJobDescriptor[] jobs = scheduler.getJobDescriptors();
        assertEquals(1, jobs.length);
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
        InputSource source = new InputSource(fs.
            getInputStream(name));
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SAXConfigurationHandler handler = new SAXConfigurationHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(source);
        return handler.getConfiguration();
    }

}
