//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.templating.velocity;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

/**
 * Simple templating implementation based on velocity engine.
 *
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: VelocityTemplating.java,v 1.2 2003-12-05 22:50:06 pablo Exp $
 */
public class VelocityTemplating extends ResourceLoader
   implements Templating, LogSystem
{
	/** file system */
    private FileSystem fileSystem;

	/** component configuration */
    private Configuration config;
    
    /** logger */
    private Logger logger;

	/** velocity engine */
    private VelocityEngine engine;
    
    /** template paths */
    private String[] paths;
    
    /** template file extension */
	private String extension = ".vt";

    /**
     * Creates a new instance of the templating system.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param fileSystem the filesystem to read files from.
     */
    public VelocityTemplating(Configuration config, Logger logger, FileSystem fileSystem)
    {
    	this.logger = logger;
        this.fileSystem = fileSystem;
        this.config = config;
        engine = new VelocityEngine();
		extension = config.getChild("extension").getValue("*.vt");
		try
		{
			Configuration[] path = config.getChild("path").getChildren("paths");
			paths = new String[path.length];
			for(int i = 0; i < path.length; i++)
			{
				paths[i] = path[i].getValue();
			}
		}
		catch(ConfigurationException e)
		{
			throw new ComponentInitializationError("failed to initialze Velocity", e);
		}

		engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		engine.setProperty(VelocityEngine.RESOURCE_LOADER, "objectledge");
		engine.setProperty("objectledge.resource.loader.class", this);
		engine.setProperty(VelocityEngine.ENCODING_DEFAULT,
						   config.getChild("encoding").getValue("ISO-8859-1"));
	
        try
        {
            engine.init();
        }
        catch (VirtualMachineError e)
        {
            throw e;
        }
        catch (ThreadDeath e)
        {
            throw e;
        }
        catch (Throwable t)
        {
            throw new ComponentInitializationError("failed to initialze Velocity", t);
        }
    }

	/**
	 * {@inheritDoc}
	 */
    public TemplatingContext createContext()
    {    	
    	// TODO use pooling here...
		return new VelocityContext();
    }
    
	/**
	 * {@inheritDoc}
	 */
    public boolean templateExists(String name)
    {
        try
		{
			for(int i=0 ; i<paths.length; i++)
			{
				String path = paths[i]+name+extension;
				if(engine.templateExists(path))
				{
				    return true;
				}
			}
			return false;
		}
	    catch(Exception e)
	    {
		     throw new RuntimeException("Velocity internal error", e);
	    }
    }

	/**
	 * {@inheritDoc}
	 */
    public Template getTemplate(String name) throws TemplateNotFoundException
    {
		VelocityTemplate template = null;
		try
		{
			for(int i=0 ; i<paths.length; i++)
			{
				 String path = paths[i]+name+extension;
				 if(engine.templateExists(path))
				 {
					 template = new VelocityTemplate(this, engine.getTemplate(path));
				 }
			 }
		}
		catch(Exception e)
		{
			throw new RuntimeException("Velocity internal error", e);
		}
		if(template != null)
		{
			 return template;
		}
		throw new TemplateNotFoundException("template "+name+extension+" not found");
    }

	/**
	 * {@inheritDoc}
	 */
    public void evaluate(TemplatingContext context, Reader source, Writer target, String logTag)
    	throws MergingException
    {
    	boolean success = false;
    	try
		{
			success = engine.evaluate(((VelocityContext)context).getContext(),
									  target, logTag, source);
		}
		catch(Exception e)
		{
		    throw new MergingException("failed to render template", e);
		}			
		if(!success)
		{
			throw new MergingException("failed to render template, cause in the log");
		}
    }
    
	
	/**
	 * {@inheritDoc}
	 */
	public void merge(TemplatingContext context, Template template, Writer target) 
		throws MergingException
	{
		boolean success = false;
		try
		{
			((VelocityTemplate)template).getTemplate().
				merge(((VelocityContext)context).getContext(), target);
		}
		catch(Exception e)
		{
			throw new MergingException("failed to render template", e);
		}	
	}
    
	/**
	 * {@inheritDoc}
	 */
    public String getTemplateEncoding()
    {
        return null;
    }

	// Velocity logging interface implementation

	/**
	 * {@inheritDoc}
	 */
	public void init(RuntimeServices services)
	{   
	}

	/**
	 * {@inheritDoc}
	 */
	public void logVelocityMessage(int level, String message)
	{
		//TODO - adapt to ledge logging
		if(level < 5)
		{
			logger.info(message);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void init(ExtendedProperties properties)
	{		
	}

	/**
	 * {@inheritDoc}
	 */
	public InputStream getResourceStream(String name) 
			throws ResourceNotFoundException
	{
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
