package org.objectledge.filesystem.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.RandomAccessFile;

/**
 * An implementaion of FileProvider that operates on the local filesystem. 
 * 
 * <p>Note! You may use ~ character at the beginning of the path to explicitly 
 * denote the container running user's home directory.</p>
 * 
 * <p>Paths that don't start with a file separator nor drive specification are
 * considererd to be relative the the running user's current directory.</p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LocalFileProvider.java,v 1.1 2003-11-25 11:01:39 fil Exp $
 */
public class LocalFileProvider 
	implements FileSystemProvider
{
	// constants ////////////////////////////////////////////////////////////

	/** Platform specific directory spearator character. */
	private static String fs = System.getProperty("file.separator");

	// instance variables ///////////////////////////////////////////////////

	/** The base directory. */	
	private File baseDir;
    
    /** The provider's name. */
    private String name;

	// initialization ///////////////////////////////////////////////////////
    
    /**
     * Crates a new instance of the provider.
     * 
     * @param name the name of the provider.
     * @param root directory.
     */
    public LocalFileProvider(String name, String root)
    {
        this.name = name;
        if(root.startsWith("~"+fs))
        {
            root = System.getProperty("user.home") + fs + root.substring(2);
        }
        if(!root.startsWith(fs) && root.charAt(1) != ':')
        {
            root = System.getProperty("user.dir") + fs + root;
        }
        baseDir = new File(root);
        if(!baseDir.exists())
        {
            throw new ComponentInitializationError(root+" does not exist");
        }
        if(!baseDir.canRead())
        {
            throw new ComponentInitializationError(root+" is not readable");
        }
        if(!baseDir.isDirectory())
        {
            throw new ComponentInitializationError(root+" is not a directory");
        }
    }

	// FileProvider interface ////////////////////////////////////////

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
    public boolean isReadOnly()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(String path)
    {
		File file = getFile(path);
        return file.exists();
    }

    /**
     * {@inheritDoc}
     */
	public boolean isFile(String path)
	{
		File file = getFile(path);
		return file.isFile();
	}

    /**
     * {@inheritDoc}
     */
    public boolean isDirectory(String path)
    {
		File file = getFile(path);
		return file.isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canRead(String path)
    {
		File file = getFile(path);
		return file.canRead();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canWrite(String path)
    {
		File file = getFile(path);
		return file.canWrite();
    }

    /**
     * {@inheritDoc}
     */
    public String[] list(String dir) 
    	throws IllegalArgumentException
    {
		File file = getFile(dir);
		if(!file.exists())
		{
			throw new IllegalArgumentException(dir+" does not exist");
		}
		if(!file.canRead())
		{
			throw new IllegalArgumentException(dir+" is not readable");
		}
		if(!file.isDirectory())
		{
			throw new IllegalArgumentException(dir+" is not a directory");
		}
		return file.list();
    }


    /**
     * {@inheritDoc}
     */
    public boolean createNewFile(String path)
        throws IOException
    {
        File file = getFile(path);
        return file.createNewFile();
    }

    /**
     * {@inheritDoc}
     */
    public void mkdirs(String path) throws IOException
    {
		File file = getFile(path);
		file.mkdirs();
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String path) throws IOException
    {
		File file = getFile(path);
		file.delete();
    }

    /**
     * {@inheritDoc}
     */
    public void rename(String from, String to) throws IOException
    {
		File fromFile = getFile(from);
		File toFile = getFile(to);
		fromFile.renameTo(toFile);
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(String path) 
    {
		File file = getFile(path);
		if(file.exists() && file.isFile() && file.canRead())
		{
			try
			{
				return new BufferedInputStream(new FileInputStream(file.getPath()));
			}
			catch(FileNotFoundException e)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream(String path, boolean append)
    {
		File file = getFile(path);
		if(file.exists())
		{
			if(file.isFile() && file.canRead())
			{
				try
				{
					return new BufferedOutputStream(new FileOutputStream(file.getPath(), append));
				}
				catch(FileNotFoundException e)
				{
					throw (IllegalStateException) 
                        new IllegalStateException("unexpected exception").initCause(e);
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			File dir = file.getParentFile();
			if(dir.exists() && dir.isDirectory() && dir.canWrite())
			{
				try
				{
					return new BufferedOutputStream(new FileOutputStream(file.getPath(), append));
				}
				catch(FileNotFoundException e)
				{
					throw (IllegalStateException)
                        new IllegalStateException("unexpected exception").initCause(e);
				}
			}
			else
			{
				return null;
			}
		}
    }

    /**
     * {@inheritDoc}
     */
    public RandomAccessFile getRandomAccess(String path, String mode)
    {
		File file = getFile(path);
		if(file.exists())
		{
            boolean needWrite = (mode.indexOf('w') != -1);
            
			if(file.isFile() && file.canRead() && (!needWrite || (needWrite && file.canWrite())))
			{
				try
				{
					return new LocalRandomAccessFile(file, mode);
				}
				catch(FileNotFoundException e)
				{
					throw (IllegalStateException)
                        new IllegalStateException("unexpected exception").initCause(e);
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			File dir = file.getParentFile();
			if(dir.exists() && dir.isDirectory() && dir.canWrite())
			{
				try
				{
                    return new LocalRandomAccessFile(file, mode);
				}
				catch(FileNotFoundException e)
				{
					throw (IllegalStateException) 
                        new IllegalStateException("unexpected exception").initCause(e);
				}
			}
			else
			{
				return null;
			}
		}
    }
    
    /**
     * {@inheritDoc}
     */
	public long lastModified(String path)
	{
		File file = getFile(path);
		return file.lastModified();
	}

    /**
     * {@inheritDoc}
     */
	public long length(String path)
	{
		File file = getFile(path);
		return file.length();		
	}
    
    // implementation ///////////////////////////////////////////////////////
    
    /**
     * Returns a java.io.File object at the given abstract path.
     * 
     * @param path the pathname.
     * @return java.io.File object.
     */
    protected File getFile(String path)
    {
    	return new File(baseDir, path.replace(fs.charAt(0), '/'));
    }
}
