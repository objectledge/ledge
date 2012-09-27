package org.objectledge.web;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.container.LedgeContainer;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.filesystem.ServletFileSystemProvider;

public class LedgeServletContextListener
    implements ServletContextListener
{

    public static final String CONFIG_PARAM = "config";

    public static final String CONFIG_DEFAULT = "/config";

    public static final String CONTAINER_CONTEXT_KEY = "org.objectledge.container";

    public static final String ROOT_PARAM = "root";

    public static final String SERVLET_TEMPDIR_ATTR = "javax.servlet.context.tempdir";

    public static final String PLATFORM_TEMPDIR_PROPERTY = "java.io.tempdir";

    private Logger log;

    private volatile boolean initalized = false;

    private LedgeContainer ledgeContainer;

    public LedgeServletContextListener()
        throws ConfigurationException
    {
        BasicConfigurator.configure();
        log = Logger.getLogger(LedgeServletContextListener.class);
    }

    @Override
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        if(initalized)
        {
            log.warn("ServletContext is already initalized");
            return;
        }

        final ServletContext context = contextEvent.getServletContext();

        try
        {
            ledgeContainer = createLedgeContainer(context);
            context.setAttribute(CONTAINER_CONTEXT_KEY, ledgeContainer.getContainer());
        }
        catch(ServletException e)
        {
            log.error("Failed to create container.", e);
        }

        initalized = true;
        log.info("ServletContext initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent)
    {
        ledgeContainer.killContainer();
    }

    /**
     * Create a LedgeContainer suitable for ServletContext environment.
     * 
     * @param context ServletContext
     * @return LedgeContainer
     * @throws ServletException
     */
    private static LedgeContainer createLedgeContainer(ServletContext context)
        throws ServletException
    {
        String config = context.getInitParameter(CONFIG_PARAM);
        if(config == null)
        {
            config = CONFIG_DEFAULT;
        }
        try
        {
            final ClassLoader classLoader = LedgeContainer.class.getClassLoader();
            return new LedgeContainer(createFileSystem(context, classLoader), config, classLoader,
                Collections.singletonMap(ServletContext.class, context));
        }
        catch(Exception e)
        {
            throw new ServletException("failed to initialize container", e);
        }
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
    private static FileSystem createFileSystem(ServletContext context, ClassLoader classLoader)
    {
        String fsRoot = context.getInitParameter(ROOT_PARAM);
        if(fsRoot == null)
        {
            fsRoot = (String)context.getAttribute(SERVLET_TEMPDIR_ATTR);
        }
        if(fsRoot == null)
        {
            fsRoot = System.getProperty(PLATFORM_TEMPDIR_PROPERTY);
        }
        if(fsRoot == null)
        {
            throw new IllegalStateException("cannot determine filesystem root");
        }

        LocalFileSystemProvider lfs = new LocalFileSystemProvider("local", fsRoot);
        ServletFileSystemProvider sfs = new ServletFileSystemProvider("servlet", context);
        ClasspathFileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", classLoader);
        return new FileSystem(new FileSystemProvider[] { lfs, sfs, cfs }, 4096, 4194304);
    }
}
