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

import java.security.Principal;
import java.util.Vector;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.pipeline.ErrorHandlingPipeline;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.TestHttpServletRequest;
import org.objectledge.web.TestHttpServletResponse;
import org.objectledge.web.WebConfigurator;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class MVCTest extends TestCase
{
    private Context context;

    private MVCInitializerValve mvcInitializer;
    
    
    /**
     * Constructor for MVCInitializerValveTest.
     * @param arg0
     */
    public MVCTest(String arg0)
    {
        super(arg0);
    }
    
    public void setUp()
    {
        try
        {
            context = new Context();
            //prepare test
            String root = System.getProperty("ledge.root");
            if (root == null)
            {
                throw new Error("system property ledge.root undefined. " +
                 "use -Dledge.root=.../ledge-container/src/test/resources");
            }
            FileSystem fs = FileSystem.getStandardFileSystem(root + "/tools");
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
            mvcInitializer = new MVCInitializerValve(webConfigurator);
            mvcInitializer.process(context);    
        }
        catch (Exception e)
        {
            throw new Error(e);
        }
    }

    public void testMVCInitializerValveTest() throws Exception
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        assertNotNull(mvcContext);
        assertNull(mvcContext.getAction());
        Principal userPrincipal = new DefaultPrincipal("user");
        mvcContext.setUserPrincipal(userPrincipal, true);
        assertEquals(mvcContext.getUserPrincipal(),userPrincipal);
        assertEquals(mvcContext.isUserAuthenticated(),true);
        mvcContext.setMedia("PLAIN");
        assertEquals(mvcContext.getMedia(),"PLAIN");
        MVCResultsValve mvcResult = new MVCResultsValve();
        mvcResult.process(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        assertEquals(httpContext.getDirectResponse(),true);
    }
    
    public void testResultValve()
        throws Exception
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        mvcContext.setBuildResult("TEST");
        MVCResultsValve mvcResult = new MVCResultsValve();
        mvcResult.process(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        assertEquals(httpContext.getDirectResponse(),true);
    }
    
    public void testSimpleCatchProcessingExceptionTest()
        throws Exception
    {   
        HttpContext httpContext = HttpContext.getHttpContext(context);
            assertEquals(httpContext.getDirectResponse(),false);
        SimpleCatchProcessingExceptionValve catchValve = 
            new SimpleCatchProcessingExceptionValve();
        catchValve.process(context);
        assertEquals(httpContext.getDirectResponse(),false);
        context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION,
            new ProcessingException("TEST"));
        catchValve.process(context);
        httpContext = HttpContext.getHttpContext(context);
        assertEquals(httpContext.getDirectResponse(),true);
    }

}
