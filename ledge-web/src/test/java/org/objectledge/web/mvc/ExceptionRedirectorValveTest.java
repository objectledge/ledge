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

package org.objectledge.web.mvc;

import java.util.Vector;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.pipeline.ErrorHandlingPipeline;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.templating.TemplatingContextLoaderValve;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.web.HttpContext;
import org.objectledge.web.TestHttpServletRequest;
import org.objectledge.web.TestHttpServletResponse;
import org.objectledge.web.WebConfigurator;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExceptionRedirectorValveTest extends TestCase
{
    private ExceptionRedirectorValve exceptionRedirectorValve;

    private Context context;

    private TemplatingContextLoaderValve templatingLoader;
    /**
     * Constructor for ExceptionRedirectorValveTest.
     * @param arg0
     */
    public ExceptionRedirectorValveTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        try
        {
            context = new Context();
            context.clearAttributes();
            //prepare test
            String root = System.getProperty("ledge.root");
            if (root == null)
            {
                throw new Error("system property ledge.root undefined. " + 
                                 "use -Dledge.root=.../ledge-container/src/test/resources");
            }
            FileSystem fs = FileSystem.getStandardFileSystem(root + "/exception-redirector");
            XMLValidator validator = new XMLValidator();
            ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");
            Configuration config = configFactory.getConfig(WebConfigurator.class, 
                                                           WebConfigurator.class);
            WebConfigurator webConfigurator = new WebConfigurator(config);
            
            TestHttpServletRequest request = new TestHttpServletRequest();
            TestHttpServletResponse response = new TestHttpServletResponse();
            request.setupGetContentType("text/html");
            request.setupGetParameterNames((new Vector()).elements());
            request.setupPathInfo("view/Default");
            request.setupGetContextPath("/test");
            request.setupGetServletPath("ledge");
            request.setupGetRequestURI("");
            request.setupServerName("www.objectledge.org");
            HttpContext httpContext = new HttpContext(request, response);
            httpContext.setEncoding(webConfigurator.getDefaultEncoding());
            context.setAttribute(HttpContext.class, httpContext);
            RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
            paramsLoader.process(context);
            MVCInitializerValve mvcInitializer = new MVCInitializerValve(webConfigurator);
            mvcInitializer.process(context);

            config = configFactory.getConfig(ExceptionRedirectorValve.class, 
                                            ExceptionRedirectorValve.class);
            LoggerFactory loggerFactory = new LoggerFactory();
            Logger logger = loggerFactory.getLogger(ExceptionRedirectorValve.class);
            exceptionRedirectorValve = new ExceptionRedirectorValve(config, logger);
            
            config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
            logger = loggerFactory.getLogger(Templating.class);
            Templating templating = new VelocityTemplating(config, logger, fs);
            templatingLoader = new TemplatingContextLoaderValve(templating);        
        }
        catch (Exception e)
        {
            throw new Error(e);
        }
    }

    public void testExceptionRedirectorValveProcess() throws Exception
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        mvcContext.setView("foo");
        Throwable t = (Throwable)context.getAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION);
        if(t != null)
        {
            context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION, null);
        }
        exceptionRedirectorValve.process(context);
        assertEquals(mvcContext.getView(), "foo");
        context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION, new Error("foo"));
        exceptionRedirectorValve.process(context);
        assertEquals(mvcContext.getView(), "DefaultError");
        context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION, new Exception("bar"));
        exceptionRedirectorValve.process(context);
        assertEquals(mvcContext.getView(), "Error");
        context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION, 
                             new ProcessingException("foo"));
        exceptionRedirectorValve.process(context);
        assertEquals(mvcContext.getView(), "PrError");
        context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION, 
                             new UnsupportedOperationException("foo"));
        exceptionRedirectorValve.process(context);
        assertEquals(mvcContext.getView(), "FooError");
        //check the orginal view in context.
        templatingLoader.process(context);
        context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION, 
                             new UserUnknownException("foo"));
        exceptionRedirectorValve.process(context);
        assertEquals(mvcContext.getView(), "BarError");
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        assertEquals((String)templatingContext.get("original_view"),"FooError");
    }
}
