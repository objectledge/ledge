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
 * @version $Id: FileObject.java,v 1.1 2005-02-07 21:05:16 zwierzem Exp $
 */
public class FileObject
{
	protected String path;
	protected String name;
	protected Date lastModified;
	protected long length;
	protected boolean isDirectory;

	public FileObject(String fullPath, String path, FileSystem fileSystem)
	{
		this.path = path;
		this.name = path.substring(path.lastIndexOf('/')+1);
		this.lastModified = new Date(fileSystem.lastModified(fullPath));
		this.length = fileSystem.length(fullPath);
		this.isDirectory = fileSystem.isDirectory(fullPath);
	}

    public String getPath()
    {
        return path;
    }
 
	public String getName()
	{
		return name;
	}
 
    public boolean isDirectory()
    {
        return isDirectory;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public long getLength()
    {
        return length;
    }
}
