package org.objectledge.mail.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.objectledge.filesystem.FileSystem;

/**
 * An implementation of <code>DataSource</code> interface.
 *
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 * @version $Id: FileSystemDataSource.java,v 1.1 2004-01-09 14:44:33 pablo Exp $
 */
public class FileSystemDataSource
    implements DataSource
{
    /** The file system. */
    private FileSystem fileSystem;

    /** The content type. */
    private String contentType;
    
    /** The file's pathname. */
    private String name;
    
    /**
     * Creates a FileSystemDataSource.
     *
     * @param fileSystem the file system.
     * @param name the file name.
     * @param contentType the content type.
     */
    public FileSystemDataSource(FileSystem fileSystem, String name, String contentType)
    {
    	this.fileSystem = fileSystem;
        this.name = name;
        this.contentType = contentType;
    }
    
    // DataSource interface //////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
    public InputStream getInputStream()
        throws IOException
    {
        return fileSystem.getInputStream(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public OutputStream getOutputStream()
        throws IOException
    {
        return fileSystem.getOutputStream(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public String getContentType()
    {
        return contentType;
    }

	/**
	 * {@inheritDoc}
	 */
    public String getName()
    {
        int idx = name.lastIndexOf('/');
        if(idx < 0)
        {
            return name;
        }
        if(idx == name.length() -1)
        {
            return "";
        }
        return name.substring(idx+1);
    }
}
