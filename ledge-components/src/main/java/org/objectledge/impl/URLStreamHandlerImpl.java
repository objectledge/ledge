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
 *  @version $Id: URLStreamHandlerImpl.java,v 1.2 2003-11-24 15:55:44 fil Exp $
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
