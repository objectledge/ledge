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
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCInitializerValve;
import org.objectledge.xml.XMLValidator;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.servlet.MockHttpServletResponse;

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
    
    public void setUp()
         throws Exception
     {
         String root = System.getProperty("ledge.root");
         if(root == null)
         {
             throw new Exception("system property ledge.root undefined. "+
             "use -Dledge.root=.../ledge-container/src/test/resources");
         }
         FileSystem fs = FileSystem.getStandardFileSystem(root+"/tools");
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
         MockHttpServletRequest request = new TestHttpServletRequest();
         MockHttpServletResponse response = new TestHttpServletResponse();
         request.setupGetContentType("text/html");
         request.setupGetParameterNames((new Vector()).elements());
         request.setupPathInfo("view/Default");
         request.setupGetContextPath("/test");
         request.setupGetServletPath("ledge");
         request.setupServerName("www.objectledge.org");
         HttpContext httpContext = new HttpContext(request,response);
         httpContext.setEncoding(webConfigurator.getDefaultEncoding());
         context.setAttribute(HttpContext.class, httpContext);
         RequestParametersLoaderValve paramsLoader =
            new RequestParametersLoaderValve();
         paramsLoader.process(context);            
         MVCInitializerValve mVCInitializer = 
            new MVCInitializerValve(webConfigurator);
         mVCInitializer.process(context);
     }
     
     public void testFactoryTest()
     {
         LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
         assertNotNull(linkTool);
         linkToolFactory.recycleTool(linkTool);
         assertEquals(linkToolFactory.getKey(),"link");
         assertEquals(linkTool.toString(),
                      "/test/ledge/view/Default");
         assertEquals(linkTool.absolute().toString(),
                      "http://www.objectledge.org/test/ledge/view/Default");
     }
    

}
