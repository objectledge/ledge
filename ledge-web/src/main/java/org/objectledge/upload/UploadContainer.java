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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Uploaded resource container.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UploadContainer.java,v 1.1 2004-01-13 13:06:39 pablo Exp $
 */
public class UploadContainer
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
	 * @param size the size of loaded data.
	 * @param mimeType the mime type. 
	 */
	public UploadContainer(String name, String filename, int size, String mimeType)
	{
		this.name = name;
		this.size = size;
		this.data = new byte[size];
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
	 * load data to the container.
	 *
	 * @param is the data input stream.
	 * @return the number of successfully written bytes.
	 * @throws IOException if occured.
	 */
	public int load(InputStream is) throws IOException 
	{
		return is.read(data, 0, size);
	}

	/**
	 * get the name of the item.
	 *
	 * @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * get the uploaded file name. 
	 *
	 * @return the file name.
	 */
	public String getFileName() 
	{
		return filename;
	}
    
	/**
	 * get the size of the uploaded item.   
	 *
	 * @return the size.
	 */
	public int getSize() 
	{
		return size;
	}

	/**
	 * get the byte array of the uploaded item.   
	 *
	 * @return the data.
	 */
	public byte[] getData()
	{
		return data;
	}

	/**
	 * returns the uploaded item as a string.
	 *
	 * @return the uploaded item.
	 */
	public String getString()
	{
		return new String(data);
	}

	/**
	 * returns the uploaded item as a string with a given encoding.
	 *
	 * @param encoding the encoding of the item.
	 * @return the uploaded item.
	 * @throws UnsupportedEncodingException if not supported.
	 */
	public String getString(String encoding)
		throws UnsupportedEncodingException
	{
		return new String(data, encoding);
	}

	/**
	 * get the stream of bytes of the uploaded data. 
	 *
	 * @return the input stream of the item.
	 */
	public InputStream getInputStream() 
	{
		return new ByteArrayInputStream(data);
	}
    
	/**
	 * get the mime type of the uploaded item.   
	 *
	 * @return the mime type of the item.
	 */
	public String getMimeType() 
	{
		return mimeType;
	}
}
