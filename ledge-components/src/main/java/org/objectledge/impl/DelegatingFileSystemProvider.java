package org.objectledge.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.RandomAccessFile;


/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DelegatingFileSystemProvider.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public abstract class DelegatingFileSystemProvider 
    implements FileSystemProvider
{
    // instance variables ///////////////////////////////////////////////////
    
    protected String name;

    // public interface /////////////////////////////////////////////////////
    
    protected DelegatingFileSystemProvider(String name)
    {
        this.name = name;
    }
    
	/**
	 * Returns the provider that should handle the incoming request.
	 * 
	 * <p>This method should never return <code>null</code>, otherwise
	 * NPE will be thrown.</p> 
	 * 
	 * @return the provider.
	 */
	protected abstract FileSystemProvider getProvider();

    public String getName()
    {
        return name;
    }

	public boolean exists(String path)
	{
		return getProvider().exists(path);
	}

	public boolean isFile(String path)
	{
		return getProvider().isFile(path);
	}

	public boolean isDirectory(String path)
	{
		return getProvider().isDirectory(path);
	}

	public boolean canRead(String path)
	{
		return getProvider().canRead(path);
	}

	public boolean canWrite(String path)
	{
		return getProvider().canWrite(path);
	}

	public String[] list(String dir) 
		throws IllegalArgumentException
	{
		return getProvider().list(dir);
	}

    public boolean createNewFile(String path)
        throws IOException
    {
        return getProvider().createNewFile(path);   
    }

	public void mkdirs(String path) throws IOException
	{
		getProvider().mkdirs(path);
	}

	public void delete(String path) throws IOException
	{
		getProvider().delete(path);
	}

	public void rename(String from, String to) throws IOException
	{
		getProvider().rename(from, to);
	}

	public InputStream getInputStream(String path)
	{
		return getProvider().getInputStream(path);
	}

	public OutputStream getOutputStream(String path, boolean append)
	{
		return getProvider().getOutputStream(path, append);
	}

	public RandomAccessFile getRandomAccess(String path, String mode)
	{
		return getProvider().getRandomAccess(path, mode);
	}

    public long lastModified(String path)
	{
		return getProvider().lastModified(path);
	}

	public long length(String path)
	{
		return getProvider().length(path);
	}
}
