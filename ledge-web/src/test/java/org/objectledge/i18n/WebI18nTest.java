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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jmock.Mock;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.utils.AddToList;
import org.objectledge.utils.LedgeTestCase;
import org.objectledge.utils.ReturnListValuesAsArray;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.MVCInitializerValve;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class WebI18nTest extends LedgeTestCase
{
    private Context context;

    private LocaleLoaderValve localeLoaderValve;
    
    private Mock mockHttpServletRequest;
    private HttpServletRequest httpServletRequest;
    private Mock mockHttpSession;
    private HttpSession httpSession;
    private Mock mockHttpServletResponse;
    private HttpServletResponse httpServletResponse;
    private Mock mockI18n;
    private I18n i18n;

    public void setUp() throws Exception
    {
        context = new Context();
        FileSystem fs = getFileSystem();
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(LocaleLoaderValve.class));
        Configuration config = getConfig(fs,"tools/org.objectledge.web.WebConfigurator.xml");
        WebConfigurator webConfigurator = new WebConfigurator(config);

        mockHttpServletRequest = mock(HttpServletRequest.class);
        httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
        mockHttpServletRequest.stubs().method("setCharacterEncoding");
        mockHttpServletRequest.stubs().method("getCharacterEncoding").
            will(returnValue("ISO-8859-1"));
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        mockHttpServletRequest.stubs().method("getParameterNames").will(returnValue((new Vector()).
            elements()));
        mockHttpServletRequest.stubs().method("getQueryString").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue("view/Default"));
        mockHttpServletRequest.stubs().method("getContextPath").will(returnValue("/test"));
        mockHttpServletRequest.stubs().method("getServletPath").will(returnValue("ledge"));
        mockHttpServletRequest.stubs().method("getRequestURI").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getRemoteAddr").will(returnValue("objectledge.org"));
        mockHttpServletRequest.stubs().method("getServerName").will(returnValue("objectledge.org"));

        mockHttpSession = mock(HttpSession.class);
        httpSession = (HttpSession)mockHttpSession.proxy();
        mockHttpServletRequest.stubs().method("getSession").will(returnValue(httpSession));
        
        mockHttpServletResponse = mock(HttpServletResponse.class);
        httpServletResponse = (HttpServletResponse)mockHttpServletResponse.proxy();

        mockI18n = mock(I18n.class);
        i18n = (I18n)mockI18n.proxy();
        mockI18n.stubs().method("getDefaultLocale").will(returnValue(Locale.US));
        mockI18n.stubs().method("getPreferedLocale").will(returnValue(Locale.US));

        HttpContext httpContext = new HttpContext(httpServletRequest, httpServletResponse);
        context.setAttribute(HttpContext.class, httpContext);
        
        RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
        paramsLoader.process(context);
        MVCInitializerValve mvcInitializer = new MVCInitializerValve(webConfigurator);
        mvcInitializer.process(context);
        LoggerFactory loggerFactory = new LoggerFactory(null);
        localeLoaderValve = new LocaleLoaderValve(logger, i18n);
        AuthenticationContext authenticationContext = new AuthenticationContext();
        context.setAttribute(AuthenticationContext.class, authenticationContext);
    }

    public void testLocaleLoaderTest() throws Exception
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        AuthenticationContext authenticationContext = 
            AuthenticationContext.getAuthenticationContext(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        List cookieList = new ArrayList();
        
        mockHttpServletRequest.stubs().method("getCookies").
            will(new ReturnListValuesAsArray(cookieList));
        mockHttpServletResponse.stubs().method("addCookie").with(ANYTHING).
            will(new AddToList(cookieList));
        mockHttpSession.stubs().method("getAttribute").with(ANYTHING).will(returnValue(null));
        mockHttpSession.stubs().method("setAttribute").with(ANYTHING, ANYTHING).isVoid();

        localeLoaderValve.process(context);
        Cookie[] cookies = httpContext.getRequest().getCookies();
        assertNotNull(cookies);
        assertEquals(cookies.length, 1);
        localeLoaderValve.process(context);
        cookies = httpContext.getRequest().getCookies();
        assertEquals(cookies.length, 1);
        authenticationContext.setUserPrincipal(new DefaultPrincipal("foo"), true);
        localeLoaderValve.process(context);
        cookies = httpContext.getRequest().getCookies();
        assertEquals(cookies.length, 2);
    }
}
