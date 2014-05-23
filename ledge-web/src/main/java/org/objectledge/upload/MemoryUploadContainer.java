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

package org.objectledge.upload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * In-memory uploaded resource container.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UploadContainer.java,v 1.2 2004-01-14 13:18:09 fil Exp $
 */
public class MemoryUploadContainer implements UploadContainer
{
	/** resource data */
	private byte[] data;

	/** resource name */
	private String name;

	/** file name */
	private String filename;

	/** size of resource data */
	private int size;

	/** mime type */
	private String mimeType;

	/**
	 * Constructs the Upload container.
	 * 
	 * @param name the name of the resource.
	 * @param filename the file name.
     * @param mimeType the mime type. 
	 * @param size the size of data to load (-1 if unknown).
     * @param dataStream a stream to load data from.
     * @throws IOException if the data could not be loaded.
	 */
	public MemoryUploadContainer(String name, String filename, String mimeType, int size, 
        InputStream dataStream)
        throws IOException
	{
		this.name = name;
        if(size > 0)
        {
            this.size = size;
            this.data = new byte[size];
            load(dataStream, size);
        }
        else
        {
            this.size = load(dataStream);
        }
		this.mimeType = mimeType;
		if (filename == null || filename.equals("")) 
		{
			this.filename = name;
		}
		else
		{
			this.filename = filename;
		}
	}
    
	/**
	 * Load data to the container.
	 *
	 * @param is the data input stream.
     * @param count the number of bytes to read.
	 * @throws IOException if occured.
	 */
	private void load(InputStream is, int count) throws IOException 
	{
		is.read(data, 0, count);
	}

    /**
     * Load data to the container.
     *
     * @param is the data input stream.
     * @return the number of successfully written bytes.
     * @throws IOException if occured.
     */
    private int load(InputStream is) throws IOException 
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int count = 0;
        byte[] buff = new byte[4096];
        while(count > 0)
        {
            count = is.read(buff,0,buff.length);
            if(count > 0)
            {
                baos.write(buff,0,count);
            }
        }
        data = baos.toByteArray();
        return data.length;
    }
    
	@Override
    public String getName()
	{
		return name;
	}

	@Override
    public String getFileName() 
	{
		return filename;
	}
    
	@Override
    public long getSize()
	{
		return size;
	}

	@Override
    public byte[] getBytes()
	{
		return data;
	}

	@Override
    public String getString()
	{
		return new String(data);
	}

	@Override
    public String getString(String encoding)
		throws UnsupportedEncodingException
	{
		return new String(data, encoding);
	}

	@Override
    public InputStream getInputStream() 
	{
		return new ByteArrayInputStream(data);
	}
    
	@Override
    public String getMimeType() 
	{
		return mimeType;
	}
}
