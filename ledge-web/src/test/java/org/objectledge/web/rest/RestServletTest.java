// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.web.rest;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.objectledge.LedgeWebTestCase;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.web.mvc.security.PolicySystem;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 *
 * <p>Created on Dec 23, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: RestServletTest.java,v 1.8 2004-06-28 10:08:36 fil Exp $
 */
public class RestServletTest extends LedgeWebTestCase
{
    public void testRestServlet()
        throws Exception
    {    	
    	
    	String root = "src/test/resources/servlet/config/";
        FileSystem fs = FileSystem.getStandardFileSystem(root);
    	//Configuration config = getConfig(fs, JerseyRestValve.class, JerseyRestValve.class);
        ServletRunner runner = getRunner("servlet");
        ServletUnitClient client = runner.newClient();
        WebRequest request = new GetMethodWebRequest("http://localhost/f/hello");
        WebResponse response = client.getResponse(request);
        assertNotNull(response);
        assertEquals(200, response.getResponseCode());
        assertEquals("Hello World!", response.getText());
    }
    
    public ServletRunner getRunner(String resources)
        throws Exception
    {
        String root = "src/test/resources/"+resources;
        FileSystem fs = FileSystem.getStandardFileSystem(root);
        InputStream webXml = fs.getInputStream("/WEB-INF/web.xml");
        if(webXml == null)
        {
            throw new Exception(root+"/WEB-INF/web.xml not found");
        }
        return new ServletRunner(webXml);
    }
}
