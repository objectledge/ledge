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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.impl.ReadOnlyFileSystemProvider;

/**
 * An implementation of FileService provider that operates on the ServletContext.
 * 
 * <p>This is a read-only implementation. It is able to use WEB-INF/files for
 * listing functionality. </p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ServletFileSystemProvider.java,v 1.3 2004-01-14 11:29:39 fil Exp $
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
        super(name);
        this.context = context;
        try
        {
            processListings();
        }
        catch(IOException e)
        {
            throw new ComponentInitializationError("failed to parse listings", e);
        }
    }
    
    // public interface ///////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(String path) 
    {
		return context.getResourceAsStream(path);
    }
    
    /**
     * {@inheritDoc}
     */
    public URL getResource(String path)
        throws MalformedURLException
    {
        if(path.charAt(0) != '/')
        {
            path = "/"+path;
        }
        if(context.getResourceAsStream(path) != null)
        {
            return context.getResource(path);
        }
        else
        {
            return null;
        }
    }
}
