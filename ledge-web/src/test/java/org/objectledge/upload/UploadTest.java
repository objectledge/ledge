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

package org.objectledge.upload;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.xml.DOMConfigurator;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.LedgeTestCase;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.LocaleLoaderValve;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.mail.MailSystem;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.threads.ThreadPool;
import org.objectledge.web.HttpContext;
import org.objectledge.web.TestHttpServletRequest;
import org.objectledge.web.TestHttpServletResponse;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.TestHttpSession;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.mockobjects.servlet.MockServletInputStream;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class UploadTest extends LedgeTestCase
{
    private Context context;

    private FileUploadValve uploadValve;

    private FileUpload fileUpload;

    public void setUp() throws Exception
    {
        context = new Context();
        FileSystem fs = getFileSystem();
        Configuration config = getConfig(fs,"config/org.objectledge.web.WebConfigurator.xml");
        WebConfigurator webConfigurator = new WebConfigurator(config);
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(FileUploadValve.class));
        
        // thread pool
        ThreadPool threadPool = new ThreadPool(null, context, null, logger);

        // templating
        config = getConfig(fs,"config/org.objectledge.templating.Templating.xml");
        Templating templating = new VelocityTemplating(config, logger, fs);

        // mailsystem
        config = getConfig(fs,"config/org.objectledge.mail.MailSystem.xml");
        MailSystem mailSystem = new MailSystem(config, logger, fs, templating, threadPool);

        config = getConfig(fs,"config/org.objectledge.upload.FileUpload.xml");
        fileUpload = new FileUpload(config, context);

        //file upload valve
        uploadValve = new FileUploadValve(webConfigurator, logger, fileUpload, mailSystem);
        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
        //request.setupGetContentType("text/html");
        request.setupGetContentType(fs.read("up_ct.txt", "ISO-8859-2"));
        request.setupGetParameterNames((new Vector()).elements());
        request.setupPathInfo("view/Default");
        request.setupGetContextPath("/test");
        request.setupGetServletPath("ledge");
        request.setupGetRequestURI("");
        request.setupServerName("www.objectledge.org");
        request.setSession(new TestHttpSession());
        request.setupGetContentLength((int)fs.length("up.txt"));
        MockServletInputStream servletIS = new MockServletInputStream();
        byte[] reqContent = fs.read("up.txt");

        if (reqContent == null)
        {
            throw new Exception("file up.txt not found");
        }
        servletIS.setupRead(reqContent);
        request.setupGetInputStream(servletIS);
        HttpContext httpContext = new HttpContext(request, response);
        httpContext.setEncoding(webConfigurator.getDefaultEncoding());
        context.setAttribute(HttpContext.class, httpContext);
        RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
        paramsLoader.process(context);

    }

    public void testUploadValve() throws Exception
    {
        uploadValve.process(context);
        UploadContainer container = fileUpload.getContainer("foo");
        assertNull(container);
        container = fileUpload.getContainer("item1");
        assertNotNull(container);
        assertEquals(container.getFileName(), "foo.txt");
        assertEquals(container.getMimeType(), "text/plain");
        assertEquals(container.getString(), "bar");
        assertEquals(container.getString("ISO-8859-2"), "bar");
        assertNotNull(container.getInputStream());
        int size = container.getSize();
        assertEquals(container.getBytes().length, size);
        assertEquals(container.getName(), "item1");
    }

}
