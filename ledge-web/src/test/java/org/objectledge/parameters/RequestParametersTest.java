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

package org.objectledge.parameters;

import java.util.Vector;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.tools.TestHttpServletRequest;
import org.objectledge.web.mvc.tools.TestHttpServletResponse;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RequestParametersTest extends TestCase
{
    private Context context;

    /**
     * Constructor for RequestParametersTest.
     * @param arg0
     */
    public RequestParametersTest(String arg0)
    {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
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
            Configuration config = configFactory.getConfig(WebConfigurator.class, WebConfigurator.class);
            WebConfigurator webConfigurator = new WebConfigurator(config);
            TestHttpServletRequest request = new TestHttpServletRequest();
            TestHttpServletResponse response = new TestHttpServletResponse();
            request.setupGetContentType("text/html");
            Vector parameterNames = new Vector();
            parameterNames.add("foo");
            request.setupGetParameterNames(parameterNames.elements());
            request.setupAddParameter("foo","bar");
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
        }
        catch (Exception e)
        {
            throw new Error(e);
        }
    }

    public void testInit()
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        assertNotNull(parameters);
        
    }

}
