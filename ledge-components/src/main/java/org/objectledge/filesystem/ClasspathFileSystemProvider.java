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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.impl.ReadOnlyFileSystemProvider;

/**
 * An implementation of the FileSystemProvider that reads resources from the classpath.  
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ClasspathFileSystemProvider.java,v 1.2 2004-01-28 14:10:01 pablo Exp $
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
        if(path.length() > 0 && path.charAt(0) == '/')
        {
            path = path.substring(1);
        }
		return classLoader.getResourceAsStream(path);
    }
    
    /**
     * {@inheritDoc}
     */
    public URL getResource(String path)
    {
        return classLoader.getResource(path);
    }
}
