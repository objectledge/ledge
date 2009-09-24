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

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jmock.Mock;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.pipeline.ErrorHandlingPipeline;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.web.ContentTypeInitializerValve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.PrintExceptionValve;
import org.objectledge.web.WebConfigurator;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class MVCTest extends LedgeTestCase
{
    private Context context;

    private ContentTypeInitializerValve contentTypeInitializerValve;
    private MVCInitializerValve mvcInitializer;
    
	private Mock mockHttpServletRequest;
    private HttpServletRequest httpServletRequest;
    private Mock mockHttpServletResponse;
    private HttpServletResponse httpServletResponse;
    
    public void setUp()
    {
        try
        {
            context = new Context();
            context.clearAttributes();
            FileSystem fs = FileSystem.getStandardFileSystem("src/test/resources/tools");
            XMLValidator validator = new XMLValidator(new XMLGrammarCache());
            ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");
            Configuration config = configFactory.getConfig(WebConfigurator.class,
                  WebConfigurator.class);
            WebConfigurator webConfigurator = new WebConfigurator(config);

            mockHttpServletRequest = mock(HttpServletRequest.class);
            httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
            mockHttpServletRequest.stubs().method("getCharacterEncoding").
                will(returnValue("ISO-8859-1"));
            mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
            mockHttpServletRequest.stubs().method("getParameterNames").
                will(returnValue((new Vector<String>()).elements()));
            mockHttpServletRequest.stubs().method("getQueryString").will(returnValue(""));
            mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue("view/Default"));
            mockHttpServletRequest.stubs().method("getContextPath").will(returnValue("/test"));
            mockHttpServletRequest.stubs().method("getServletPath").will(returnValue("ledge"));
            mockHttpServletRequest.stubs().method("getRequestURI").will(returnValue(""));
            mockHttpServletRequest.stubs().method("getServerName").
                will(returnValue("objectledge.org"));

            mockHttpServletResponse = mock(HttpServletResponse.class);
            httpServletResponse = (HttpServletResponse)mockHttpServletResponse.proxy();
            mockHttpServletResponse.stubs().method("setContentLength").with(ANYTHING).isVoid();
            mockHttpServletResponse.stubs().method("setContentType").with(ANYTHING).isVoid();
            ServletOutputStream sos = new ServletOutputStream()
            {
                public void write(int b) throws IOException
                {
                    // ignore
                }
            };
            mockHttpServletResponse.stubs().method("getOutputStream").will(returnValue(sos));
            
            HttpContext httpContext = new HttpContext(httpServletRequest, httpServletResponse);
            context.setAttribute(HttpContext.class, httpContext);

            RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
            paramsLoader.process(context);

            contentTypeInitializerValve = new ContentTypeInitializerValve(webConfigurator);
            contentTypeInitializerValve.process(context);    
            
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
        PrintExceptionValve catchValve = 
            new PrintExceptionValve();
        catchValve.process(context);
        assertEquals(httpContext.getDirectResponse(), false);
        context.setAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION,
            new ProcessingException("TEST"));
        mockHttpServletResponse.expects(once()).method("setContentLength").with(ANYTHING).isVoid();
        catchValve.process(context);
        httpContext = HttpContext.getHttpContext(context);
        assertEquals(httpContext.getDirectResponse(),true);
    }

}
