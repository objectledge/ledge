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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.objectledge.filesystem.FileSystem;

/**
 * An implementation of URLConnection delegating functionality
 * to Object Ledge FileSystem.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: URLConnectionImpl.java,v 1.4 2003-12-05 18:50:02 pablo Exp $
 */
public class URLConnectionImpl 
    extends URLConnection
{
	private URL url;
	
	private FileSystem fileSystem;
	
    /**
     * Returns a new connection implementation instance.
     * 
     * @param url the URL to create connection for.
     * @param fileSystem the file system to use.
     */
	public URLConnectionImpl(URL url, FileSystem fileSystem)
	{
		super(url);
		this.url = url;
        this.fileSystem = fileSystem;
	}

    /**
     * Connect to the file denoted by the URL.
     * 
     * @throws IOException if the file does not exist.
     */
    public void connect() throws IOException
    {
    }
    
    /**
     * Returns a stream for reading the file.
     * 
     * @return a stream for reading the file.
     * @throws IOException if the file does not exist.
     */
    public InputStream getInputStream()
    	throws IOException
    {
        if(!fileSystem.exists(url.getFile()))
        {
            throw new IOException("file "+url.getFile()+" not found");
        }
    	if(!fileSystem.canRead(url.getFile()))
    	{
    		throw new IOException("file "+url.getFile()+": access denied");
    	}
    	return fileSystem.getInputStream(url.getFile());
    }

    /**
     * Returns a stream for writing to the file.
     * 
     * @return a stream for writing to the file.
     * @throws IOException if the file can not be written.
     */
	public OutputStream getOutputStream()
		throws IOException
	{
		if(!fileSystem.canWrite(url.getFile()))
		{
			throw new IOException("file "+url.getFile()+": access denied");
		}
		return fileSystem.getOutputStream(url.getFile());
	}

    /**
     * Returns the last modification date of the file.
     * 
     * @return the last modification time, or -1 if the file does not exist.
     */
	public long getLastModified()
	{
        if(!fileSystem.exists(url.getFile()))
        {
            return -1;
        }
		return fileSystem.lastModified(url.getFile());
	}
}
