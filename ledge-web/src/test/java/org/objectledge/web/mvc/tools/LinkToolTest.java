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

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.web.HttpContext;
import org.objectledge.web.TestHttpServletRequest;
import org.objectledge.web.TestHttpServletResponse;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCInitializerValve;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LinkToolTest extends TestCase
{
    private LinkToolFactory linkToolFactory;

    private Context context;

    /**
     * Constructor for LinkToolTest.
     * @param arg0
     */
    public LinkToolTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() throws Exception
    {
    }

    public void testFactoryTest() throws Exception
    {
        //prepare test
        String root = System.getProperty("ledge.root");
        if (root == null)
        {
            throw new Exception("system property ledge.root undefined. " +
                     "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystem fs = FileSystem.getStandardFileSystem(root + "/tools");
        context = new Context();
        XMLValidator validator = new XMLValidator();
        ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");

        Configuration config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
        LoggerFactory loggerFactory = new LoggerFactory();
        Logger logger = loggerFactory.getLogger(Templating.class);
        Templating templating = new VelocityTemplating(config, logger, fs);
        config = configFactory.getConfig(WebConfigurator.class, WebConfigurator.class);
        WebConfigurator webConfigurator = new WebConfigurator(config);
        config = configFactory.getConfig(LinkToolFactory.class, LinkToolFactory.class);
        linkToolFactory = new LinkToolFactory(config, context, webConfigurator);
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
        assertEquals(linkTool.resource("foo").toString(), "/content/test/foo");
        assertEquals(linkTool.resource("").toString(), "/content/test");
        assertEquals(linkTool.resource("/foo").toString(), "/content/test/foo");
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
        fs = FileSystem.getStandardFileSystem(root + "/tools2");
        context = new Context();
        validator = new XMLValidator();
        configFactory = new ConfigurationFactory(fs, validator, ".");
        config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
        loggerFactory = new LoggerFactory();
        logger = loggerFactory.getLogger(Templating.class);
        templating = new VelocityTemplating(config, logger, fs);
        config = configFactory.getConfig(WebConfigurator.class, WebConfigurator.class);
        webConfigurator = new WebConfigurator(config);
        config = configFactory.getConfig(LinkToolFactory.class, LinkToolFactory.class);
        linkToolFactory = new LinkToolFactory(config, context, webConfigurator);
        request = new TestHttpServletRequest();
        response = new TestHttpServletResponse();
        request.setupGetContentType("text/html");
        request.setupGetParameterNames((new Vector()).elements());
        request.setupPathInfo("");
        request.setupGetContextPath("/test");
        request.setupGetServletPath("/ledge/");
        request.setupGetRequestURI("foo#bar");
        request.setupServerName("www.objectledge.org");
        request.setupIsSecure(true);
        request.setupGetServerPort(443);
        httpContext = new HttpContext(request, response);
        httpContext.setEncoding(webConfigurator.getDefaultEncoding());
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
