package org.objectledge.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectledge.filesystem.RandomAccessFile;

/**
 * An implementation of <code>RandomAccess</code> interface using 
 * <code>java.io.RandomAccessFile</code>.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LocalRandomAccessFile.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public class LocalRandomAccessFile implements RandomAccessFile
{
    /** delegate java.io object. */
    private java.io.RandomAccessFile file;
    
    /** 
     * Creates a new instance of LocalRandomAccess
     * 
     * @param file a java.io.File object.
     * @param mode access mode {@see java.io.RandomAccessFile(java.io.File,String)}. 
     */
    public LocalRandomAccessFile(File file, String mode)
        throws FileNotFoundException
    {
        this.file = new java.io.RandomAccessFile(file, mode);
    }
    
    /**
     * {@inheritDoc}
     */
    public void close()
        throws IOException
    {
        file.close();
    }
    
    /**
     * {@inheritDoc}
     */
    public long getFilePointer()
        throws IOException
    {
        return file.getFilePointer();
    }
    
    /**
     * {@inheritDoc}
     */
    public long length()
        throws IOException
    {
        return file.length();
    }
    
    /**
     * {@inheritDoc}
     */
    public int read()
        throws IOException
    {
        return file.read();
    }
    
    /**
     * {@inheritDoc}
     */
    public int read(byte[] b)
        throws IOException
    {
        return file.read(b);
    }
    
    /**
     * {@inheritDoc}
     */
    public int read(byte[] b, int off, int len)
        throws IOException
    {
        return file.read(b, off, len);
    }
    
    /**
     * {@inheritDoc}
     */
    public void seek(long pos)
        throws IOException
    {
        file.seek(pos);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setLength(long newLength)
        throws IOException
    {
        file.setLength(newLength);
    }
    
    /**
     * {@inheritDoc}
     */
    public int skipBytes(int n)
        throws IOException
    {
        return file.skipBytes(n);
    }
    
    /**
     * {@inheritDoc}
     */
    public void write(byte[] b)
        throws IOException
    {
        file.write(b);
    }
    
    /**
     * {@inheritDoc}
     */
    public void write(int b)
        throws IOException
    {
        file.write(b);
    }
    
    /**
     * {@inheritDoc}
     */
    public void write(byte[] b, int off, int len)
        throws IOException
    {
        file.write(b, off, len);
    }
}
