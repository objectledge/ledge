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

package org.objectledge.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.objectledge.container.LedgeContainer;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.filesystem.ServletFileSystemProvider;

/**
 * LedgeServlet is the entry point of a Ledge application in the servlet environment.
 * 
 * <p>
 * It performs startup and shutdown of the system and forwards HTTP requests to designated
 * components through {@link org.objectledge.web.HttpDispatcher} interface.
 * </p>
 * 
 * <h3>Configuration parameters</h3>
 * <table>
 * <tr>
 * <th>name</th>
 * <th>default</th>
 * <th width="100%">description</th>
 * </tr>
 * <tr>
 * <td>root</td>
 * <td><code>javax.servlet.context.tempdir</code> context attribute, or <code>user.dir</code>
 * system property if the former is not available.</td>
 * <td>The root directory of the local file system td use.</td>
 * </tr>
 * <tr>
 * <td>config</td>
 * <td>/config</td>
 * <td>The base path of system configuration, within Ledge FileSystem.</td>
 * </tr>
 * </table>
 * 
 * <p>
 * The configuration parameters may be given as servlet initailizaion parameters in the
 * <code>web.xml</code> file, or as servlet context attributes. In the latter case the actual name
 * of the attribute is composed of the servlet-name under which LedgeServlet is registered, a dot
 * and the actual parameter name. Consider the following <code>web.xml</code> file:
 * </p>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;web-app&gt;
 *   &lt;servlet&gt;
 *     &lt;servlet-name&gt;ledge&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;org.objectledge.web.LedgeServlet&lt;/servlet-class&gt;
 *   &lt;/servlet&gt;
 *   &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;ledge&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;/ledge/*&lt;/url-pattern&gt;
 *   &lt;/servlet-mapping&gt;
 * &lt;/web-app&gt;
 * </pre>
 * 
 * <p>
 * The root directory can be used at deployment time with the following Tomcat application
 * definition file:
 * </p>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;Context path="/app"
 *       docBase="/home/app/production/app.war"
 *       reloadable="false"&gt;
 *   &lt;Parameter name="ledge.root" 
 *       value="/home/app/production/work" 
 *       override="false"/&gt;
 * &lt;/Context&gt;
 * </pre>
 * 
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeServlet.java,v 1.20 2008-10-28 16:09:06 rafal Exp $
 */
public class LedgeServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The request dispatcher. */
    protected HttpDispatcher dispatcher;
    
    /** The container. */
    protected static LedgeContainer container;
    
    protected ServletConfig servletConfig;
    
    /**
     * {@inheritDoc}
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if(!dispatcher.dispatch(request, response, this.servletConfig))
        {
            super.service(request, response);
        }
    }
    
     /**
     * {@inheritDoc}
     */
    public void init(ServletConfig servletConfig) throws ServletException
    {
        BasicConfigurator.configure();
        this.servletConfig = servletConfig;        
        
        Logger log = Logger.getLogger(LedgeServlet.class);                

        FileSystem fs = fileSystem(servletConfig, getClass().getClassLoader());
        
        ServletContext context = servletConfig.getServletContext();
        String ctxConfigParam = servletConfig.getServletName()+".config";
        String config = servletConfig.getInitParameter("config");
        if(config == null)
        {
            config = context.getInitParameter(ctxConfigParam);
        }
        if(config == null)
        {
            config = "/config";
        }
        String root = ((LocalFileSystemProvider)fs.getProvider("local")).getBasePath();
        
        log.info("starting up "+servletConfig.getServletName()+" servlet: root="+root+" config="+config);
       if(container == null) {
            try
            {
                container = new LedgeContainer(fs, config, getClass().getClassLoader());    
            }
            catch(Exception e)
            {
                log.error("failed to initialize container", e);
                throw new ServletException("failed to initialize container", e);
            }
       }
    }
    
     /**
     * {@inheritDoc}
     */
    public void init(ServletConfig servletConfig) throws ServletException
    {
        Logger log = Logger.getLogger(LedgeServlet.class);
    	configure(servletConfig); //throws ServletException
    	
       dispatcher = (HttpDispatcher)container.getContainer().
           getComponentInstance("cmsDispatcher");
        
       if(dispatcher == null)
        {
            log.error("dispatcher component is missing");
            throw new ServletException("cmsDispatcher dispatcher component is missing");
        }
    }

    @Override
    public void destroy()
    {
        container.killContainer();
        super.destroy();
    }

    /**
     * Initializes FileSystem using local, servlet and classpath providers.
     * <p>
     * The FileSystem will be composed of LocalFilesystemProvider, ServletFileSystemProvicer and
     * ClasspathFileSystemProvider instances with this specific order. This means that if a file
     * with the same virtual pathname is found in the local file system, it will overshadow a file
     * with the same pathname in web application archive, which in turn will overshadow a file with
     * the same pathname on the classpath.
     * </p>
     * <p>
     * Root directory of the local filesystem is determined using web application initialization
     * parameters that are defined either in web application descriptor (web.xml) or application
     * server's specific application deployment descriptors. The following parameters are checked:
     * <ul>
     * <li>servlet parameter named "root"</li>
     * <li>context parameter named "<em>servlet name</em>.root"</li>
     * <li>context parameter named "root"</li>
     * <li>context parameter named "javax.servlet.context.tempdir"</li>
     * <li>System property "java.io.tmpdir"</li>
     * </ul>
     * </p>
     * 
     * @param servletConfig a ServletConfig object for determinig servlet name and parmameter
     *        access.
     * @param classLoader a ClassLoader for the classpath provider.
     * @return
     */
    public static FileSystem fileSystem(ServletConfig servletConfig, ClassLoader classLoader)
    {
        ServletContext context = servletConfig.getServletContext();
        String root = servletConfig.getInitParameter("root");
        if(root == null)
        {
            root = context.getInitParameter(servletConfig.getServletName()+".root");
        }
        if(root == null)
        {
            root = context.getInitParameter("root");
        }
        if(root == null)
        {
            File tempDir = (File)context.getAttribute("javax.servlet.context.tempdir");
            if(tempDir == null)
            {
                root = System.getProperty("java.io.tmpdir");
            }
            else
            {
                root = tempDir.getAbsolutePath(); 
            }
        }        

        LocalFileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        ServletFileSystemProvider sfs = new ServletFileSystemProvider("servlet", context);
        ClasspathFileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            classLoader);
        return new FileSystem(new FileSystemProvider[] { lfs, sfs, cfs }, 4096, 4194304);
    }
}
