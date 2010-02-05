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

package org.objectledge.filesystem;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;

import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.impl.ReadOnlyFileSystemProvider;

/**
 * An implementation of FileSystem provider that operates on the ServletContext.
 * 
 * <p>This is a read-only implementation. It is able to use WEB-INF/files for
 * listing functionality. </p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ServletFileSystemProvider.java,v 1.7 2008-02-25 23:08:23 rafal Exp $
 */
public class ServletFileSystemProvider 
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
    public ServletFileSystemProvider(String name, ServletContext context)
    {
        super(name, findListings(context));
        this.context = context;
    }
    
    private static Collection<URL> findListings(ServletContext context)
    {   
        List<URL> listings = new ArrayList<URL>();
        for (String location : LISTING_LOCATIONS)
        {
            try
            {
                if(context.getResourceAsStream(location) != null)
                {
                    listings.add(context.getResource(location));
                    break;
                }
            }
            catch(MalformedURLException e)
            {
                throw new ComponentInitializationError("failed to check for listing at location "
                    + location, e);
            }
        }
        return listings;
    }
    
    // public interface ///////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(String path) 
    {
        return context.getResourceAsStream(normalizedPath(path));
    }
    
    /**
     * {@inheritDoc}
     */
    public URL getResource(String path)
        throws MalformedURLException
    {
        String normalized = normalizedPath(path);
        if(context.getResourceAsStream(normalized) != null)
        {
            return context.getResource(normalized);
        }
        else
        {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String normalizedPath(String path)
    {
        // ensure leading slash
        return FileSystem.normalizedPath(path);
    }
}
