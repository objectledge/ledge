package org.objectledge.web;
 
import java.io.File;

import javax.servlet.ServletContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.jcontainer.dna.Configuration;
import org.objectledge.container.LedgeContainer;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.filesystem.ServletFileSystemProvider;

import org.jcontainer.dna.ConfigurationException;

public class LedgeServletManager implements ServletContextListener {

	protected ServletContext context;
	protected LedgeContainer container;
	protected Configuration config;
	protected String configPath;
	protected String fsRoot;
	private Logger logger;
	
	public LedgeServletManager() throws ConfigurationException {
		logger = Logger.getLogger(LedgeServletManager.class);
	}

	public void contextInitialized(ServletContextEvent contextEvent) {
		context = contextEvent.getServletContext();
		
	    configPath = context.getInitParameter("configPath");  
	    fsRoot = context.getInitParameter("root");  
		
		try {
			if(container != null) {
				context.setAttribute("container", container);
			} else {
				context.setAttribute("container", createLedgeContainer());				
			}
		} catch (ServletException e) {
            logger.error("Failed to create container.");
			e.printStackTrace();
		}
		
        logger.info("Container created and placed in context.");

	}
	
	public void contextDestroyed(ServletContextEvent contextEvent) {
		// nothing to do here
	}
	
	public LedgeContainer createLedgeContainer() throws ServletException {
		
        FileSystem fs = createContainerFileSystem();        
        
        if(container == null) {
            try
            {
                container = new LedgeContainer(fs, configPath, getClass().getClassLoader());
                logger.info("Created LedgeContainer of root=" + fsRoot + ", with configPath=" + configPath);
            }
            catch(Exception e)
            {
                logger.error("failed to initialize container", e);
                throw new ServletException("failed to initialize container", e);
            }
        }
        return container;
	}
	
    public void destroy()
    {
        container.killContainer();
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
    public FileSystem createContainerFileSystem()
    {
    	ClassLoader classLoader = getClass().getClassLoader();
        LocalFileSystemProvider lfs = new LocalFileSystemProvider("local", fsRoot);
        ServletFileSystemProvider sfs = new ServletFileSystemProvider("servlet", context);
        ClasspathFileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            classLoader);
        return new FileSystem(new FileSystemProvider[] { lfs, sfs, cfs }, 4096, 4194304);
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
    public static FileSystem createServletSpecyficSystem(ServletConfig servletConfig, ClassLoader classLoader)
    {
        ServletContext context = servletConfig.getServletContext();
        String root = createServletFsRoot(servletConfig);
        LocalFileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        ServletFileSystemProvider sfs = new ServletFileSystemProvider("servlet", context);
        ClasspathFileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            classLoader);
        return new FileSystem(new FileSystemProvider[] { lfs, sfs, cfs }, 4096, 4194304);
    }
    
    /**
     * Creates server root path from it's context
     * <p>
     * </p>
     * 
     * @param servletConfig a ServletConfig object for determinig servlet name and parmameter
     *        access.
     * @param classLoader a ClassLoader for the classpath provider.
     * @return
     */    private static String createServletFsRoot(ServletConfig servletConfig) {
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
        return root;
    }
	
}