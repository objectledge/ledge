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
 * The entry point to ObjectLedge web application, that initializes the container, and uses 
 * HttpDispatcher component to handle requests.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeServlet.java,v 1.17 2005-02-03 23:26:02 pablo Exp $
 */
public class LedgeServlet extends HttpServlet
{
    /** The request dispatcher. */
    protected HttpDispatcher dispatcher;
    
    /** The container. */
    protected LedgeContainer container;
    
    /**
     * {@inheritDoc}
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if(!dispatcher.dispatch(request, response))
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
        Logger log = Logger.getLogger(ServletConfig.class);

        ServletContext context = servletConfig.getServletContext();
        String ctxRootParam = servletConfig.getServletName()+".root";
        String ctxConfigParam = servletConfig.getServletName()+".config";

        String root = servletConfig.getInitParameter("root");
        if(root == null)
        {
            root = (String)context.getAttribute(ctxRootParam);
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

        String config = servletConfig.getInitParameter("config");
        if(config == null)
        {
            config = (String)context.getAttribute(ctxConfigParam);
        }
        if(config == null)
        {
            config = "/config";
        }

        log.info("starting up: root="+root+" config="+config);

        LocalFileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        ServletFileSystemProvider sfs = new ServletFileSystemProvider("servlet", context);
        ClasspathFileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, sfs, cfs }, 4096, 4194304);
        try
        {
            container = new LedgeContainer(fs, config, getClass().getClassLoader());    
        }
        catch(Exception e)
        {
            log.error("failed to initialize container", e);
            throw new ServletException("failed to initialize container", e);
        }

        dispatcher = (HttpDispatcher)container.getContainer().
            getComponentInstance(HttpDispatcher.class);
        if(dispatcher == null)
        {
            log.error("dispatcher component is missing");
            throw new ServletException("dispatcher component is missing");
        }
    }
}
