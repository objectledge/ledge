//
//Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//
//* Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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
package org.objectledge.filesystem.table;

import java.util.Date;

import org.objectledge.filesystem.FileSystem;

/**
 * Simple object representing file.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FileObject.java,v 1.2 2005-02-08 21:19:20 rafal Exp $
 */
public class FileObject
{
	private final String path;
	private final String name;
	private final Date lastModified;
	private final long length;
	private final boolean isDirectory;

    /**
     * Creates new FileObject instance.
     * 
     * @param fullPath the full path of the file.
     * @param path the path of the file.
     * @param fileSystem Ledge file system.
     */
	public FileObject(final String fullPath, final String path, final FileSystem fileSystem)
	{
		this.path = path;
		this.name = path.substring(path.lastIndexOf('/')+1);
		this.lastModified = new Date(fileSystem.lastModified(fullPath));
		this.length = fileSystem.length(fullPath);
		this.isDirectory = fileSystem.isDirectory(fullPath);
	}

    /**
     * Returns the path of the file.
     * 
     * @return the path of the file.
     */
    public String getPath()
    {
        return path;
    }
 
    /**
     * Returns the name of the file.
     * 
     * @return the name of the file.
     */
	public String getName()
	{
		return name;
	}

    /**
     * Returns <code>true</code> if the object denotes a directory.
     * 
     * @return <code>true</code> if the object denotes a directory.
     */
    public boolean isDirectory()
    {
        return isDirectory;
    }

    /**
     * Returns the object's last modification date.
     * 
     * @return the object's last modification date.
     */
    public Date getLastModified()
    {
        return lastModified;
    }

    /**
     * Returns the file length.
     * 
     * @return the file length. 
     */
    public long getLength()
    {
        return length;
    }
}
