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

package org.objectledge.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.impl.ReadOnlyFileSystemProvider;

/**
 * An implementation of the FileSystemProvider that reads resources from the classpath.  
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ClasspathFileSystemProvider.java,v 1.8 2008-02-25 22:04:39 rafal Exp $
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
        path = normalizedPath(path);
		return classLoader.getResourceAsStream(path);
    }
    
    /**
     * {@inheritDoc}
     */
    public URL getResource(String path)
    {
        path = normalizedPath(path);
        return classLoader.getResource(path);
    }
    
    @Override
    public boolean isFile(String path)
    {
        return checkItemType(path, false);
    }
    
    @Override
    public boolean isDirectory(String path)
    {
        return checkItemType(path, true);
    }

    private boolean checkItemType(String path, boolean directory)
    {
        URL url = getResource(path);
        if(url != null)
        {
            if(url.getProtocol().equals("file"))
            {
                try
                {
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    // URLConnection does not provide a way to determine if the target object
                    // is a file or directory in the API, so we need to pry the lid open...
                    Field f = conn.getClass().getDeclaredField("isDirectory");
                    f.setAccessible(true);
                    return directory == f.getBoolean(conn);
                }
                catch(Exception e)
                {
                    Logger log = Logger.getLogger(this.getClass());
                    log.error("faield to check item type for "+path, e);
                    return false;
                }
            }
            if(url.getProtocol().equals("jar"))
            {
                // jar protocol supports only files - if item exists it must be a file
                return !directory;
            }
            return false;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public long lastModified(String path)
    {
        URL url = getResource(path);
        if(url != null)
        {
            URLConnection conn;
            try
            {
                conn = url.openConnection();
                conn.connect();
                return conn.getLastModified();
            }
            catch(IOException e)
            {
                return -1L;
            }
        }
        else
        {
            return -1L;
        }
    }
    
    @Override
    public long length(String path)
    {
        URL url = getResource(path);
        if(url != null)
        {
            URLConnection conn;
            try
            {
                conn = url.openConnection();
                conn.connect();
                return conn.getContentLength();
            }
            catch(IOException e)
            {
                return -1;
            }
        }
        else
        {
            return -1;
        }
    }
    
    @Override
    public Set<String> list(String path)
    {
        if(isDirectory(path))
        {
            HashSet<String> res;
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int c;
                InputStream is = getInputStream(path);
                while((c = is.read()) > 0)
                {
                    baos.write(c);
                }
                is.close();
                String dir = baos.toString("UTF-8");
                String[] items = dir.split("\n");
                res = new HashSet<String>(items.length);
                for(String item : items)
                {
                    res.add(item.trim());
                }
                return res;
            }
            catch(IOException e)
            {
                Logger log = Logger.getLogger(this.getClass());
                log.error("faield to list directory "+path, e);
                // fall trough to return statement below 
            }
        }
        return Collections.EMPTY_SET;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    protected String normalizedPath(String path)
    {
        // strip leading slash
        return FileSystem.normalizedPath(path).substring(1);
    }    
}
