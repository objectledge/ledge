package org.objectledge.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.objectledge.filesystem.FileSystem;

/**
 * A subclass of URLStreamHandler that handles labeo: protocol
 * using Labeo FileService.
 * 
 *  @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 *  @version $Id: URLStreamHandlerImpl.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public class URLStreamHandlerImpl extends URLStreamHandler
{
    private FileSystem fileSystem;
    
    public URLStreamHandlerImpl(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem ;
    }
    
    protected URLConnection openConnection(URL url) throws IOException
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
