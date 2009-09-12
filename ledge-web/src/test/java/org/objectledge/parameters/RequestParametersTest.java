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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mock;
import org.objectledge.context.Context;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.web.HttpContext;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RequestParametersTest extends LedgeTestCase
{
    private Context context;

    private Mock mockHttpServletRequest;
    private HttpServletRequest httpServletRequest;
    private Mock mockHttpServletResponse;
    private HttpServletResponse httpServletResponse;
    
    private RequestParameters parameters;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new Context();
        
        mockHttpServletRequest = mock(HttpServletRequest.class);
        httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        Vector parameterNames = new Vector();
        parameterNames.add("mixed");
        parameterNames.add("post");
        parameterNames.add("get");
        mockHttpServletRequest.stubs().method("getParameterNames").
            will(returnValue(parameterNames.elements()));
        mockHttpServletRequest.stubs().method("getParameterValues").with(eq("mixed")).
            will(returnValue(new String[] { "mixed1", "mixed2" }));
        mockHttpServletRequest.stubs().method("getParameterValues").with(eq("post")).
            will(returnValue(new String[] { "post" }));
        mockHttpServletRequest.stubs().method("getParameterValues").with(eq("get")).
            will(returnValue(new String[] { "get" }));
        mockHttpServletRequest.stubs().method("getQueryString").will(returnValue("get=get&mixed=mixed1"));
        mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue("path/path"));

        HttpContext httpContext = new HttpContext(httpServletRequest, httpServletResponse);
        context.setAttribute(HttpContext.class, httpContext);
        RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
        paramsLoader.process(context);
        parameters = RequestParameters.getRequestParameters(context);
    }

    public void testPath()
    {
        assertEquals("path", parameters.get("path"));
        
        assertTrue(parameters.isPathInfoParameter("path"));
        assertFalse(parameters.isQueryStringParameter("path"));
        assertFalse(parameters.isPOSTParameter("path"));
    }

    public void testPost()
    {
        assertEquals("post", parameters.get("post"));
        
        assertFalse(parameters.isPathInfoParameter("post"));
        assertFalse(parameters.isQueryStringParameter("post"));
        assertTrue(parameters.isPOSTParameter("post"));        
    }

    public void testGet()
    {
        assertEquals("get", parameters.get("get"));
        
        assertFalse(parameters.isPathInfoParameter("get"));
        assertTrue(parameters.isQueryStringParameter("get"));
        assertFalse(parameters.isPOSTParameter("get"));        
    }

    public void testMixed()
    {    
        assertEquals(parameters.getStrings("mixed")[0], "mixed1");
        assertEquals(parameters.getStrings("mixed")[1], "mixed2");

        assertFalse(parameters.isPathInfoParameter("mixed"));
        assertTrue(parameters.isQueryStringParameter("mixed"));
        assertTrue(parameters.isPOSTParameter("mixed"));        
    }
    
    public void testJSessionIDPathinfo()
    {
        mockHttpServletRequest = mock(HttpServletRequest.class);
        httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        mockHttpServletRequest.stubs().method("getParameterNames").
        	will(returnValue(new Vector().elements()));
        mockHttpServletRequest.stubs().method("getQueryString").will(returnValue(null));
        mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue("path/path;jsessionid=8435A845CF71GB5E"));
        HttpContext httpContext = new HttpContext(httpServletRequest, httpServletResponse);
        context.setAttribute(HttpContext.class, httpContext);
        RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
        paramsLoader.process(context);
        parameters = RequestParameters.getRequestParameters(context);
        
        assertTrue(parameters.getParameterNames().length == 1);
        assertEquals("path", parameters.get("path"));
    }
}
