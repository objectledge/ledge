//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, 
//are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//nor the names of its contributors may be used to endorse or promote products 
//derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
//POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.filesystem;

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
import org.objectledge.filesystem.impl.LocalRandomAccessFile;

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
 * @version $Id: LocalFileSystemProvider.java,v 1.1 2004-01-08 10:12:02 fil Exp $
 */
public class LocalFileSystemProvider 
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
    public LocalFileSystemProvider(String name, String root)
    {
        this.name = name;
        if(root.startsWith("~"+fs))
        {
            root = System.getProperty("user.home") + fs + root.substring(2);
        }
        if(!root.startsWith(fs) && (root.length() <= 1 || root.charAt(1) != ':'))
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
    	throws IOException
    {
		File file = getFile(dir);
		if(!file.exists())
		{
			throw new IOException(dir+" does not exist");
		}
		if(!file.canRead())
		{
			throw new IOException(dir+" is not readable");
		}
		if(!file.isDirectory())
		{
			throw new IOException(dir+" is not a directory");
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
		if(!file.mkdirs())
		{
			throw new IOException("failed to create "+path); 
		}
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String path) throws IOException
    {
		File file = getFile(path);
		if(!file.delete())
		{
			throw new IOException("failed to delete "+path);
		}
    }

    /**
     * {@inheritDoc}
     */
    public void rename(String from, String to) throws IOException
    {
		File fromFile = getFile(from);
		File toFile = getFile(to);
		if(!fromFile.renameTo(toFile))
		{
			throw new IOException("failed to rename "+from+" to "+to);
		}
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
