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
 * @version $Id: DelegatingFileSystemProvider.java,v 1.2 2003-11-24 15:55:44 fil Exp $
 */
public abstract class DelegatingFileSystemProvider 
    implements FileSystemProvider
{
    // instance variables ///////////////////////////////////////////////////
    
    /** then name of the provider. */
    protected String name;

    // public interface /////////////////////////////////////////////////////
    
    /**
     * Creates a new instance of the provider.
     * 
     * @param name the name of the provider. 
     */
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

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
	public boolean exists(String path)
	{
		return getProvider().exists(path);
	}

    /**
     * {@inheritDoc}
     */
	public boolean isFile(String path)
	{
		return getProvider().isFile(path);
	}

    /**
     * {@inheritDoc}
     */
	public boolean isDirectory(String path)
	{
		return getProvider().isDirectory(path);
	}

    /**
     * {@inheritDoc}
     */
	public boolean canRead(String path)
	{
		return getProvider().canRead(path);
	}

    /**
     * {@inheritDoc}
     */
	public boolean canWrite(String path)
	{
		return getProvider().canWrite(path);
	}

    /**
     * {@inheritDoc}
     */
	public String[] list(String dir) 
		throws IllegalArgumentException
	{
		return getProvider().list(dir);
	}

    /**
     * {@inheritDoc}
     */
    public boolean createNewFile(String path)
        throws IOException
    {
        return getProvider().createNewFile(path);   
    }

    /**
     * {@inheritDoc}
     */
	public void mkdirs(String path) throws IOException
	{
		getProvider().mkdirs(path);
	}

    /**
     * {@inheritDoc}
     */
	public void delete(String path) throws IOException
	{
		getProvider().delete(path);
	}

    /**
     * {@inheritDoc}
     */
	public void rename(String from, String to) throws IOException
	{
		getProvider().rename(from, to);
	}

    /**
     * {@inheritDoc}
     */
	public InputStream getInputStream(String path)
	{
		return getProvider().getInputStream(path);
	}

    /**
     * {@inheritDoc}
     */
	public OutputStream getOutputStream(String path, boolean append)
	{
		return getProvider().getOutputStream(path, append);
	}

    /**
     * {@inheritDoc}
     */
	public RandomAccessFile getRandomAccess(String path, String mode)
	{
		return getProvider().getRandomAccess(path, mode);
	}

    /**
     * {@inheritDoc}
     */
    public long lastModified(String path)
	{
		return getProvider().lastModified(path);
	}

    /**
     * {@inheritDoc}
     */
	public long length(String path)
	{
		return getProvider().length(path);
	}
}
