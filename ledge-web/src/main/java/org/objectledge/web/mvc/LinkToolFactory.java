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

package org.objectledge.web.mvc;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.context.Context;
import org.objectledge.templating.tools.ContextToolFactory;
import org.objectledge.web.WebConfigurator;

/**
 * Context tool factory component to build the link tool.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class LinkToolFactory implements ContextToolFactory
{
	/** the default query separator */
	public static final String DEFAULT_QUERY_SEPARATOR = "&";

	/** context */
	private Context context;
	
	/** configuration component */
	private WebConfigurator webConfigurator;
	
	/** the sticky parameters keys */
	private Set stickyKeys;
	
	/** the pathinfo parameters keys */
	private Set pathinfoKeys;
	
	/** the queryt separator */
	private String querySeparator;

	/**
	 * Component constructor.
	 *
	 * @param config the config.
	 * @param context the context.
	 * @param webConfigurator the web configurator component.
 	 */
	public LinkToolFactory(Configuration config, Context context, WebConfigurator webConfigurator)
	{
		this.context = context;
		this.webConfigurator = webConfigurator;
		stickyKeys = new HashSet();
		pathinfoKeys = new HashSet();
		try
		{
			Configuration[] keys = config.getChild("sticky").getChildren("key");
			for (int i = 0; i < keys.length; i++)
			{
				stickyKeys.add(keys[i].getValue());
			}
			keys = config.getChild("pathinfo").getChildren("key");
			for (int i = 0; i < keys.length; i++)
			{
				pathinfoKeys.add(keys[i].getValue());
			}
		}
		catch (ConfigurationException e)
		{
			throw new ComponentInitializationError("failed to configure the component", e);
		}
		querySeparator = config.getChild("query_separator").getValue(DEFAULT_QUERY_SEPARATOR);
	}
	
    /**
	 * {@inheritDoc}
	 */
	public Object getTool()
	{
		return new LinkTool(this, context, webConfigurator);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void recycleTool(Object tool)
	{
		//do nothing LinkTool is too simple object to be pooled
	}

	/**
	 * {@inheritDoc}
	 */
	public String getKey()
	{
		return "link";
	}
    
	/**
	 * Get the sticky parameters keys set.
	 * 
	 * @return the sticky keys.
	 */
	public Set getStickyKeys()
	{
		return stickyKeys;
	}

	/**
	 * Get the path info parameters keys.
	 * 
	 * @return the default encoding.
	 */
	public Set getPathInfoKeys()
	{
		return pathinfoKeys;
	}

	/**
	 *  Get the query string separator. 
	 *
	 * @return the query separator. 
	 */
	public String getQuerySeparator()
	{
		return querySeparator;
	}
}
