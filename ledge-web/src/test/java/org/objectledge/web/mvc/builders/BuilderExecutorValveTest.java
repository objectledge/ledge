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
package org.objectledge.web.mvc.builders;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;
import org.objectledge.web.mvc.finders.NameSequenceFactory;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: BuilderExecutorValveTest.java,v 1.4 2004-01-21 14:48:18 fil Exp $
 */
public class BuilderExecutorValveTest extends TestCase
{
    private Templating templating;
    
    private Context context;
    
    private BuilderExecutorValve executor;
    
    /**
     * Constructor for MVCFinderTest.
     * @param arg0
     */
    public BuilderExecutorValveTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
        throws Exception
    {
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
            "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystem fs = FileSystem.getStandardFileSystem(root+"/builders");
        XMLValidator validator = new XMLValidator();        
        ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");
        Configuration config = configFactory.
            getConfig(NameSequenceFactory.class, NameSequenceFactory.class);
        NameSequenceFactory nameSequenceFactory = new NameSequenceFactory(config);
        config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
        LoggerFactory loggerFactory = new LoggerFactory();
        Logger logger = loggerFactory.getLogger(Templating.class);
        templating = new VelocityTemplating(config, logger, fs);
        logger = loggerFactory.getLogger(MVCFinder.class);
        MutablePicoContainer container = new DefaultPicoContainer();
        container.registerComponentImplementation(Context.class);
        MVCFinder finder = new MVCFinder(container, logger, templating, nameSequenceFactory);
        container.registerComponentInstance(MVCFinder.class, finder);
        context = new Context();
        context.clearAttributes();
        executor = new BuilderExecutorValve(context, finder, finder, 8, 8);
    }
    
    public void testEnclosure()
    {
        MVCContext mvcContext = new MVCContext();
        mvcContext.setView("foo.Bar");
        context.setAttribute(MVCContext.class, mvcContext);
        context.setAttribute(TemplatingContext.class, templating.createContext());
        executor.run();
        assertEquals("Default(foo/Default(foo/Bar()))", mvcContext.getBuildResult());
    }
    
    public void testRoute()
    {
        MVCContext mvcContext = new MVCContext();
        mvcContext.setView("Router");
        context.setAttribute(MVCContext.class, mvcContext);
        context.setAttribute(TemplatingContext.class, templating.createContext());
        executor.run();
        assertEquals("Default(RoutedTo())", mvcContext.getBuildResult());
    }
    
    public void testInfiniteRoute()
    {
        MVCContext mvcContext = new MVCContext();
        mvcContext.setView("RouteToSelf");
        context.setAttribute(MVCContext.class, mvcContext);
        context.setAttribute(TemplatingContext.class, templating.createContext());
        try
        {
            executor.run();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
    }
    
    public void testInfiniteEnclosure()
    {
        MVCContext mvcContext = new MVCContext();
        mvcContext.setView("EncloseSelf");
        context.setAttribute(MVCContext.class, mvcContext);
        context.setAttribute(TemplatingContext.class, templating.createContext());
        try
        {
            executor.run();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
    }
    
    public void testOverride()
    {
        MVCContext mvcContext = new MVCContext();
        mvcContext.setView("Overrider");
        context.setAttribute(MVCContext.class, mvcContext);
        context.setAttribute(TemplatingContext.class, templating.createContext());
        executor.run();
        assertEquals("Default(OverridenTo())", mvcContext.getBuildResult());
    }
}
