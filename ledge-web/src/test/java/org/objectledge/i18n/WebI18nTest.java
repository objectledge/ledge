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

package org.objectledge.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.Cookie;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.LedgeTestCase;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.TestHttpServletRequest;
import org.objectledge.web.TestHttpServletResponse;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.MVCInitializerValve;
import org.objectledge.web.mvc.TestHttpSession;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class WebI18nTest extends LedgeTestCase
{
    private Context context;

    private LocaleLoaderValve localeLoaderValve;

    public void setUp() throws Exception
    {
        context = new Context();
        FileSystem fs = getFileSystem();
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(LocaleLoaderValve.class));
        Configuration config = getConfig(fs,"tools/org.objectledge.web.WebConfigurator.xml");
        WebConfigurator webConfigurator = new WebConfigurator(config);
        Map sessionMap = new HashMap();
        TestHttpServletRequest request = new TestHttpServletRequest(sessionMap);
        request.setupGetContentType("text/html");
        request.setupGetParameterNames((new Vector()).elements());
        request.setupPathInfo("view/Default");
        request.setupGetContextPath("/test");
        request.setupGetServletPath("ledge");
        request.setupGetRequestURI("");
        request.setupGetRemoteAddr("www.objectledge.com");
        request.setupServerName("www.objectledge.org");
        TestHttpSession session = new TestHttpSession();
        request.setSession(session);
        TestHttpServletResponse response = new TestHttpServletResponse(sessionMap);
        HttpContext httpContext = new HttpContext(request, response);
        httpContext.setEncoding(webConfigurator.getDefaultEncoding());
        context.setAttribute(HttpContext.class, httpContext);
        RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
        paramsLoader.process(context);
        MVCInitializerValve mvcInitializer = new MVCInitializerValve(webConfigurator);
        mvcInitializer.process(context);
        LoggerFactory loggerFactory = new LoggerFactory();
        localeLoaderValve = new LocaleLoaderValve(logger, webConfigurator);
    }

    public void testLocaleLoaderTest() throws Exception
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);

        localeLoaderValve.process(context);
        Cookie[] cookies = httpContext.getRequest().getCookies();
        assertNotNull(cookies);
        assertEquals(cookies.length, 2);
        localeLoaderValve.process(context);
        cookies = httpContext.getRequest().getCookies();
        assertEquals(cookies.length, 2);
        mvcContext.setUserPrincipal(new DefaultPrincipal("foo"), true);
        localeLoaderValve.process(context);
        cookies = httpContext.getRequest().getCookies();
        assertEquals(cookies.length, 4);
    }

}
