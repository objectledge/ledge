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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jmock.Mock;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;
import org.objectledge.web.mvc.finders.NameSequenceFactory;
import org.objectledge.web.mvc.security.SecurityHelper;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: BuilderExecutorValveTest.java,v 1.18 2005-10-10 14:06:45 rafal Exp $
 */
public class BuilderExecutorValveTest 
	extends LedgeTestCase
{
    private Templating templating;
    
    private Context context;
    
    private MVCContext mvcContext;
    
    private BuilderExecutorValve executor;
    
    public void setUp(String base)
        throws Exception
    {
        FileSystem fs = FileSystem.getStandardFileSystem("src/test/resources/"+base);
        XMLValidator validator = new XMLValidator(new XMLGrammarCache());        
        ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");
        Configuration config = configFactory.
            getConfig(NameSequenceFactory.class, NameSequenceFactory.class);
        NameSequenceFactory nameSequenceFactory = new NameSequenceFactory(config);
        config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
        LoggerFactory loggerFactory = new LoggerFactory(new LoggingConfigurator());
        Logger logger = loggerFactory.getLogger(Templating.class);
        templating = new VelocityTemplating(config, logger, fs);
        logger = loggerFactory.getLogger(MVCFinder.class);
        MutablePicoContainer container = new DefaultPicoContainer();
        container.registerComponentImplementation(Context.class);
        MVCFinder finder = new MVCFinder(container, logger, templating, nameSequenceFactory);
        SecurityHelper securityHelper = new SecurityHelper();
        container.registerComponentInstance(MVCFinder.class, finder);
        context = new Context();
        context.clearAttributes();
        ViewEnclosureManager viewEnclosureManager = new ViewEnclosureManager(context);
        executor = new BuilderExecutorValve(context, finder, finder, securityHelper, viewEnclosureManager, 8, 8);
        mvcContext = new MVCContext();
        Mock mockServletRequest = mock(HttpServletRequest.class);
        Mock mockServletResponse = mock(HttpServletResponse.class);
        HttpContext httpContext = new HttpContext((HttpServletRequest)mockServletRequest.proxy(), 
            (HttpServletResponse)mockServletResponse.proxy());
        mockServletResponse.stubs().method("isCommitted").will(returnValue(false));
        context.setAttribute(MVCContext.class, mvcContext);
        TemplatingContext templatingContext = templating.createContext();
        templatingContext.put(viewEnclosureManager.getKey(), viewEnclosureManager.getTool());
        context.setAttribute(TemplatingContext.class, templatingContext);
        context.setAttribute(HttpContext.class, httpContext);
    }
    
    public void testEnclosure()
        throws Exception
    {
        setUp("builders");
        mvcContext.setView("foo.Bar");
        executor.process(context);
        assertEquals("Default(foo/Default(foo/Bar()))", mvcContext.getBuildResult());
    }
    
    public void testRoute()
        throws Exception
    {
        setUp("builders");
        mvcContext.setView("Router");
        executor.process(context);
        assertEquals("Default(RoutedTo())", mvcContext.getBuildResult());
    }
    
    public void testInfiniteRoute()
        throws Exception
    {
        setUp("builders");
        mvcContext.setView("RouteToSelf");
        try
        {
            executor.process(context);
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals(ProcessingException.class, e.getClass());
        }
    }
    
    public void testInfiniteEnclosure()
        throws Exception
    {
        setUp("builders");
        mvcContext.setView("EncloseSelf");
        try
        {
            executor.process(context);
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals(ProcessingException.class, e.getClass());
        }
    }
    
    public void testFailing()
        throws Exception
    {
        setUp("builders");
        mvcContext.setView("Failing");
        try
        {
            executor.process(context);
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals(ProcessingException.class, e.getClass());
            assertEquals(BuildException.class, e.getCause().getClass());
        }
    }
    
    public void testTemplateOnlyEnclosure()
        throws Exception
    {
        setUp("builders-templateonly");
        mvcContext.setView("foo.Bar");
        executor.process(context);
        assertEquals("Default(foo/Default(foo/Bar()))", mvcContext.getBuildResult());
    }
}
