//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// nor the names of its contributors may be used to endorse or promote products 
// derived from this software without specific prior written permission.
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

package org.objectledge.filesystem.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.objectledge.filesystem.FileSystem;

/**
 * A subclass of URLStreamHandler that handles Ledge's "ledge:" protocol
 * using FileSystem.
 * 
 *  @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 *  @version $Id: URLStreamHandlerImpl.java,v 1.4 2003-12-05 18:50:02 pablo Exp $
 */
public class URLStreamHandlerImpl extends URLStreamHandler
{
    private FileSystem fileSystem;
    
    /**
     * Creates a new instance of the stream handler implementation.
     * 
     * @param fileSystem the file system to use.
     */
    public URLStreamHandlerImpl(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem ;
    }

    /**
     * Opens a connection to the file denoted by the URL.
     * 
     * @param url the url to connect to.
     * @return an URL connection.
     * @throws IOException if the URL is not using the right protocol.
     */    
    protected URLConnection openConnection(URL url) 
        throws IOException
    {
		if(url.getProtocol().equals(fileSystem.getProtocol()))
		{
			return new URLConnectionImpl(url, fileSystem);
		}
		else
		{
			throw new IOException("unsupported protocol "+url.getProtocol());
		}
    }
}
