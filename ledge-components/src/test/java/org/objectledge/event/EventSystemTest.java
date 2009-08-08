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

package org.objectledge.event;

import java.lang.reflect.Method;

import org.apache.log4j.BasicConfigurator;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jmock.Mock;
import org.objectledge.cache.CacheFactory;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.ThreadPool;
import org.objectledge.utils.LedgeTestCase;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EventSystemTest extends LedgeTestCase
    implements FooInterface
{
    private EventWhiteboardFactory event;
    
    private EventWhiteboard whiteboard;
    
    private String testString;
    
    private Mock mockCacheFactory;
    private CacheFactory cacheFactory;
    
    
    public void setUp()
    {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Context context = new Context();
        Configuration config = new DefaultConfiguration("config", "", "/config");
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        Valve cleanup = null;
        ThreadPool pool = new ThreadPool(cleanup, context, config, logger);
        mockCacheFactory = mock(CacheFactory.class);
        mockCacheFactory.stubs().method("registerForPeriodicExpunge").isVoid();
        cacheFactory = (CacheFactory)mockCacheFactory.proxy();        
        event = new EventWhiteboardFactory(config,logger,pool,cacheFactory);
        whiteboard = event.newInstance();        
    }
    
    public void testGetForwarder()
    {
        EventWhiteboard ef = event.newInstance();
        assertNotNull(ef);
    }

    public void testAddListener()
    {
        whiteboard.addListener(FooInterface.class,this,this);
    }

    public void testRemoveListener()
    {
        whiteboard.removeListener(FooInterface.class,this,this);
    }

    public void testAddRemoteListener()
    {
        // TODO implement or remove
    }

    public void testRemoveRemoteListener()
    {
        // TODO implement or remove
    }

    public void testFireEvent()
        throws Exception
    {
        testString = "";
        assertEquals(testString,"");
        
        Method method = FooInterface.class.getMethod("callBar",new Class[]{String.class});
        whiteboard.fireEvent(method, new String[]{"foo"},this);
        assertEquals(testString,"");
        whiteboard.addListener(FooInterface.class,this,this);
        whiteboard.fireEvent(method, new String[]{"foo"},this);
        assertEquals(testString,"foo");
    }

    public void testInboundOutboundForwarder()
        throws Exception
    {
        EventWhiteboard ef = event.newInstance();
        EventWhiteboard outF = new OutboundEventWhiteboard(ef);
        EventWhiteboard inF = new InboundEventWhiteboard(ef);
        try
        {
            outF.addListener(null,null,null);
            fail("should throw the exception");
        }
        catch(IllegalStateException e)
        {
            //ok!
        }
        try
        {
            outF.removeListener(null,null,null);
            fail("should throw the exception");
        }
        catch(IllegalStateException e)
        {
            //ok!
        }
        try
        {
            outF.addRemoteListener(null,null,null);
            fail("should throw the exception");
        }
        catch(IllegalStateException e)
        {
            //ok!
        }
        try
        {
            outF.removeRemoteListener(null,null,null);
            fail("should throw the exception");
        }
        catch(IllegalStateException e)
        {
            //ok!
        }
        try
        {
            inF.fireEvent(null,null,null);
            fail("should throw the exception");
        }
        catch(IllegalStateException e)
        {
            //ok!
        }
        testString = "";
        inF.removeListener(FooInterface.class,this,this);
        Method method = FooInterface.class.getMethod("callBar",new Class[]{String.class});
        outF.fireEvent(method, new String[]{"foo"},this);
        assertEquals(testString,"");
        inF.addListener(FooInterface.class,this,this);
        outF.fireEvent(method, new String[]{"foo"},this);
        assertEquals(testString,"foo");
    }

    // foo interface implementation 
    public void callBar(String arg0)
    {
        testString = arg0;
    }
    
    
}
