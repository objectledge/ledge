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

package org.objectledge.web.mvc.tools;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jmock.Mock;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.utils.LedgeTestCase;
import org.objectledge.utils.ReturnArgument;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCInitializerValve;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LinkToolTest extends LedgeTestCase
{
    private LinkToolFactory linkToolFactory;

    private Context context;

    private Mock mockHttpServletRequest;
    private HttpServletRequest httpServletRequest;
    private Mock mockHttpServletResponse;
    private HttpServletResponse httpServletResponse;

    public void setUp() throws Exception
    {
    }

    public void testFactoryTest() throws Exception
    {
        FileSystem fs = FileSystem.getStandardFileSystem("src/test/resources/tools");
        context = new Context();
        XMLValidator validator = new XMLValidator(new XMLGrammarCache());
        ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");

        Configuration config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
        LoggerFactory loggerFactory = new LoggerFactory(null);
        Logger logger = loggerFactory.getLogger(Templating.class);
        Templating templating = new VelocityTemplating(config, logger, fs);
        config = configFactory.getConfig(WebConfigurator.class, WebConfigurator.class);
        WebConfigurator webConfigurator = new WebConfigurator(config);
        config = configFactory.getConfig(LinkToolFactory.class, LinkToolFactory.class);
        linkToolFactory = new LinkToolFactory(config, context, webConfigurator);

        mockHttpServletRequest = mock(HttpServletRequest.class);
        httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        mockHttpServletRequest.stubs().method("getParameterNames").will(returnValue((new Vector()).elements()));
        mockHttpServletRequest.stubs().method("getQueryString").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue("/view/Default"));
        mockHttpServletRequest.stubs().method("getContextPath").will(returnValue("/test"));
        mockHttpServletRequest.stubs().method("getServletPath").will(returnValue("/ledge"));
        mockHttpServletRequest.stubs().method("getRequestURI").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getServerPort").will(returnValue(80));
        mockHttpServletRequest.stubs().method("isSecure").will(returnValue(false));
        mockHttpServletRequest.stubs().method("getServerName").will(returnValue("www.objectledge.org"));

        mockHttpServletResponse = mock(HttpServletResponse.class);
        httpServletResponse = (HttpServletResponse)mockHttpServletResponse.proxy();
        mockHttpServletResponse.stubs().method("encodeURL").with(ANYTHING).will(new ReturnArgument());
        
        HttpContext httpContext = new HttpContext(httpServletRequest, httpServletResponse);
        context.setAttribute(HttpContext.class, httpContext);

        RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
        paramsLoader.process(context);
        MVCInitializerValve mVCInitializer = new MVCInitializerValve(webConfigurator);
        mVCInitializer.process(context);
        
        LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
        assertNotNull(linkTool);
        linkToolFactory.recycleTool(linkTool);
        assertEquals(linkToolFactory.getKey(), "link");
        assertEquals(linkTool.toString(), "/test/ledge/view/Default");
        linkTool = linkTool.unsetView();
        assertEquals(linkTool.action("Action").toString(), "/test/ledge?action=Action");
        assertEquals(linkTool.action("Action").unsetAction().toString(), 
                    "/test/ledge");
        assertEquals(linkTool.set("foo", "bar").toString(), "/test/ledge?foo=bar");
        assertEquals(linkTool.set("foo", 1).toString(), "/test/ledge?foo=1");
        assertEquals(linkTool.set("foo", 1L).toString(), "/test/ledge?foo=1");
        assertEquals(linkTool.set("foo", 1.6F).toString(), "/test/ledge?foo=1.6");
        Parameters params = new DefaultParameters();
        params.add("foo", "bar");
        params.add("bar", "foo");
        assertEquals(linkTool.set(params).toString(), "/test/ledge/bar/foo?foo=bar");
        assertEquals(linkTool.action("Action").set(params).toString(),
             "/test/ledge/bar/foo?action=Action&foo=bar");
        assertEquals(linkTool.view("Default").set(params).toString(),
            "/test/ledge/view/Default/bar/foo?foo=bar");
        params.add("action", "foo");
        try
        {
            linkTool.set(params);
            fail("should throw exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
        params.remove("action");
        params.set("view", "Default");
        try
        {
            linkTool.set(params);
            fail("should throw exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
        params.remove("view");
        
        assertEquals(linkTool.set(params).toString(), "/test/ledge/bar/foo?foo=bar");
        assertEquals(linkTool.set("foo", true).toString(), "/test/ledge?foo=true");
        assertEquals(linkTool.set("bar", "foo").toString(), "/test/ledge/bar/foo");
        assertEquals(linkTool.absolute().toString(), "http://www.objectledge.org/test/ledge");
        assertEquals(linkTool.http().toString(), "http://www.objectledge.org/test/ledge");
        assertEquals(linkTool.http(8080).toString(), "http://www.objectledge.org:8080/test/ledge");
        assertEquals(linkTool.https().toString(), "https://www.objectledge.org/test/ledge");
        assertEquals(linkTool.https().absolute().toString(), 
                     "https://www.objectledge.org/test/ledge");
        assertEquals(linkTool.https(8090).toString(),
                     "https://www.objectledge.org:8090/test/ledge");
        assertEquals(linkTool.sessionless().toString(), "/test/ledge");
        assertEquals(linkTool.content("foo").toString(), "/test/content/foo");
        assertEquals(linkTool.content("").toString(), "/test/content");
        assertEquals(linkTool.content("/foo").toString(), "/test/content/foo");
        assertEquals(linkTool.fragment("foo").toString(), "/test/ledge#foo");
        assertEquals(linkTool.fragment("foo").fragment(),"foo");
        assertEquals(linkTool.self().toString(),"/test/ledge");
        assertEquals(linkTool.set("foo","bar").set("bar","foo").self().toString(),
                     "/test/ledge/bar/foo");

        // test add methods

        assertEquals(linkTool.add("foo", "bar").toString(), "/test/ledge?foo=bar");
        assertEquals(linkTool.add("foo","bar").add("foo","foo").toString(),
			"/test/ledge?foo=bar&foo=foo");
        assertEquals(linkTool.add("foo", 1).toString(), "/test/ledge?foo=1");
        assertEquals(linkTool.add("foo", 1L).toString(), "/test/ledge?foo=1");
        assertEquals(linkTool.add("foo", 1.6F).toString(), "/test/ledge?foo=1.6");
        params = new DefaultParameters();
        params.add("foo", "bar");
        params.add("bar", "foo");
        assertEquals(linkTool.add(params).toString(), "/test/ledge/bar/foo?foo=bar");
        assertEquals(linkTool.action("Action").add(params).toString(),
                     "/test/ledge/bar/foo?action=Action&foo=bar");
        assertEquals(linkTool.view("Default").add(params).toString(),
                     "/test/ledge/view/Default/bar/foo?foo=bar");
        params.add("action", "foo");
        try
        {
            linkTool.add(params);
            fail("should throw exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
        params.remove("action");
        params.add("view", "Default");
        try
        {
            linkTool.add(params);
            fail("should throw exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
        params.remove("view");
        assertEquals(linkTool.add(params).toString(), "/test/ledge/bar/foo?foo=bar");
        assertEquals(linkTool.add("foo", true).toString(), "/test/ledge?foo=true");
        assertEquals(linkTool.add("bar", "foo").toString(), "/test/ledge/bar/foo");
                     
        assertEquals(linkTool.add("bar","foo").unset("bar").toString(),
                     "/test/ledge");
        try
        {
            linkTool.unset("action");
            fail("should throw exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
        try
        {
            linkTool.unset("view");
            fail("should throw exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
        
        
        // some other initial          
        fs = FileSystem.getStandardFileSystem("src/test/resources/tools2");
        context = new Context();
        validator = new XMLValidator(new XMLGrammarCache());
        configFactory = new ConfigurationFactory(fs, validator, ".");
        config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
        loggerFactory = new LoggerFactory(null);
        logger = loggerFactory.getLogger(Templating.class);
        templating = new VelocityTemplating(config, logger, fs);
        config = configFactory.getConfig(WebConfigurator.class, WebConfigurator.class);
        webConfigurator = new WebConfigurator(config);
        config = configFactory.getConfig(LinkToolFactory.class, LinkToolFactory.class);
        linkToolFactory = new LinkToolFactory(config, context, webConfigurator);

        mockHttpServletRequest = mock(HttpServletRequest.class);
        httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        mockHttpServletRequest.stubs().method("getParameterNames").will(returnValue((new Vector()).elements()));
        mockHttpServletRequest.stubs().method("getQueryString").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getContextPath").will(returnValue("/test"));
        mockHttpServletRequest.stubs().method("getServletPath").will(returnValue("/ledge"));
        mockHttpServletRequest.stubs().method("getRequestURI").will(returnValue("/foo#bar"));
        mockHttpServletRequest.stubs().method("getServerName").will(returnValue("www.objectledge.org"));
        mockHttpServletRequest.stubs().method("getServerPort").will(returnValue(443));
        mockHttpServletRequest.stubs().method("isSecure").will(returnValue(true));

        mockHttpServletResponse = mock(HttpServletResponse.class);
        httpServletResponse = (HttpServletResponse)mockHttpServletResponse.proxy();
        mockHttpServletResponse.stubs().method("encodeURL").with(ANYTHING).will(new ReturnArgument());

        httpContext = new HttpContext(httpServletRequest, httpServletResponse);

        context.setAttribute(HttpContext.class, httpContext);
        paramsLoader = new RequestParametersLoaderValve();
        paramsLoader.process(context);
        mVCInitializer = new MVCInitializerValve(webConfigurator);
        mVCInitializer.process(context);

        linkTool = (LinkTool)linkToolFactory.getTool();
        assertNotNull(linkTool);
        assertEquals(linkTool.toString(), "/test/ledge");
        assertEquals(linkTool.absolute().toString(), "https://www.objectledge.org/test/ledge");
        assertEquals(linkTool.self().toString(),"/test/ledge#bar");
        assertEquals(linkTool.set("foo","bar").set("bar","foo").toString(),
                    "/test/ledge?foo=bar&bar=foo");
    }

}
