package org.objectledge.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.objectledge.filesystem.FileSystem;

/**
 * An implementation of URLConnection that delegates it's functionality
 * to the Labeo FileService.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: URLConnectionImpl.java,v 1.2 2003-11-24 15:55:44 fil Exp $
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
