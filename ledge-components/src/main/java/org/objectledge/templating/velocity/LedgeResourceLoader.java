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

package org.objectledge.templating.velocity;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.objectledge.filesystem.FileSystem;

/**
 * A helper class that implements the velocity resource loader class.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LedgeResourceLoader.java,v 1.1 2003-12-08 10:34:43 pablo Exp $
 */
public class LedgeResourceLoader extends ResourceLoader
{
	/** the properties key */
	public static final String LEDGE_FILE_SYSTEM = "filesystem";
	 
	/** the file system */
	private FileSystem fileSystem;
	
	/**
	 * {@inheritDoc}
	 */
	public void init(ExtendedProperties properties)
	{		
		fileSystem = (FileSystem)properties.get(LEDGE_FILE_SYSTEM);
	}

	/**
	 * {@inheritDoc}
	 */
	public InputStream getResourceStream(String name) 
			throws ResourceNotFoundException
	{
		System.out.println("Szukam:"+name);
		InputStream is = fileSystem.getInputStream(name);
		if(is == null)
		{
			throw new ResourceNotFoundException("resource '"+name+"' not found");
		}
		return is;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSourceModified(Resource resource)
	{
		if(fileSystem.exists(resource.getName()))
		{
			return fileSystem.lastModified(resource.getName()) > resource.getLastModified();
		}
		else
		{
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public long getLastModified(Resource resource)
	{
		if(fileSystem.exists(resource.getName()))
		{
			return fileSystem.lastModified(resource.getName());
		}
		else
		{
			return -1;
		}
	}	
}
