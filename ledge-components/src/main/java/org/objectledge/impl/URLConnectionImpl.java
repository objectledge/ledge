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
 * @version $Id: URLConnectionImpl.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public class URLConnectionImpl extends URLConnection
{
	private URL url;
	
	private FileSystem fileSystem;
	
	public URLConnectionImpl(URL url, FileSystem fileSystem)
	{
		super(url);
		this.url = url;
        this.fileSystem = fileSystem;
	}

    public void connect() throws IOException
    {
		if(!fileSystem.exists(url.getFile()))
		{
			throw new IOException("file "+url.getFile()+" not found");
		}
    }
    
    public InputStream getInputStream()
    	throws IOException
    {
    	if(!fileSystem.canRead(url.getFile()))
    	{
    		throw new IOException("file "+url.getFile()+": access denied");
    	}
    	return fileSystem.getInputStream(url.getFile());
    }

	public OutputStream getOutputStream()
		throws IOException
	{
		if(!fileSystem.canWrite(url.getFile()))
		{
			throw new IOException("file "+url.getFile()+": access denied");
		}
		return fileSystem.getOutputStream(url.getFile());
	}
	
	public long getLastModified()
	{
		return fileSystem.lastModified(url.getFile());
	}
}
