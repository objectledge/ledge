package org.objectledge.filesystem.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.objectledge.ComponentInitializationError;

/**
 * An implementation of the FileSystemProvider that reads resources from the classpath.  
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ClasspathFileSystemProvider.java,v 1.1 2003-11-25 11:14:15 fil Exp $
 */
public class ClasspathFileSystemProvider 
    extends ReadOnlyFileSystemProvider
{
    // instance variables /////////////////////////////////////////////////////////////////////////

	/** the classloader this provider loads data from. */
	private ClassLoader classLoader;
	
    // initialization /////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates an new instance of the provider.
     * 
     * @param name the name of the provider.
     * @param classLoader the class loader to load resources from.
     */
    public ClasspathFileSystemProvider(String name, ClassLoader classLoader)
    {
        super(name);
        this.classLoader = classLoader;
        String location = null;
        for(int i=0; i < LISTING_LOCATION.length; i++)
        {
            location = LISTING_LOCATION[i];
            if(location.charAt(0) == '/')
            {
                location = location.substring(1);
            }
            URL listing = null;
            try
            {
                
                Enumeration listings = classLoader.getResources(location);
                while(listings.hasMoreElements())
                {
                    listing = (URL)listings.nextElement();
                    InputStream is = listing.openStream();
                    processListing(listing.toString(), is);
                }
            }
            catch(IOException e)
            {
                throw new ComponentInitializationError("failed to load listing "+location+" from "+
                    listing, e);
            }
        }
    }
    
    // public interface ///////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(String path)
    {
        if(path.charAt(0) == '/')
        {
            path = path.substring(1);
        }
		return classLoader.getResourceAsStream(path);
    }
}
