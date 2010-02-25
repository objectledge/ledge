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

package org.objectledge.filesystem.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectledge.filesystem.RandomAccessFile;

/**
 * An implementation of <code>RandomAccess</code> interface using 
 * <code>java.io.RandomAccessFile</code>.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LocalRandomAccessFile.java,v 1.4 2004-12-23 07:16:36 rafal Exp $
 */
public class LocalRandomAccessFile implements RandomAccessFile
{
    /** delegate java.io object. */
    private java.io.RandomAccessFile file;
    
    /** 
     * Creates a new instance of LocalRandomAccess.
     * 
     * @param file a java.io.File object.
     * @param mode access mode 
     * @throws FileNotFoundException if the file does not exist.
     * @see java.io.RandomAccessFile#RandomAccessFile(File,String) 
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
