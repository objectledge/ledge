package org.objectledge.filesystem.impl;

import java.io.InputStream;

import javax.servlet.ServletContext;

/**
 * An implementation of FileService provider that operates on the ServletContext
 * 
 * <p>This is a read-only implementation. It is able to use WEB-INF/files for
 * listing functionality. </p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ServletFileProvider.java,v 1.2 2003-12-02 14:07:01 fil Exp $
 */
public class ServletFileProvider 
	extends ReadOnlyFileSystemProvider
{
	// instance variables /////////////////////////////////////////////////////////////////////////

    /** the servlet context used for reading resources. */	
	private ServletContext context;

    // initialization /////////////////////////////////////////////////////////////////////////////
    
    /**
     * Crates a new instance of the provider.
     * 
     * @param name the name of the provider.
     * @param context the servlet context to read resources from.
     */
    public ServletFileProvider(String name, ServletContext context)
    {
        super(name);
        this.context = context;
        processListings();
    }
    
    // public interface ///////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(String path) 
    {
		return context.getResourceAsStream(path);
    }
}
