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

package org.objectledge.web.mvc.tools;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.templating.tools.ContextToolFactory;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;

/**
 * Context tool factory component to build the link tool.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LinkToolFactory.java,v 1.5 2004-08-19 15:21:24 zwierzem Exp $
 */
public class LinkToolFactory implements ContextToolFactory
{
	/** context */
	private Context context;
	
	/** configuration component */
	private WebConfigurator webConfigurator;
    
    private LinkTool.Configuration linkToolConfiguration;
    	
	/**
	 * Component constructor.
	 *
	 * @param config the config.
	 * @param context the context.
	 * @param webConfigurator the web configurator component.
     * @throws ConfigurationException if the configuraiton is invalid.
 	 */
	public LinkToolFactory(Configuration config, Context context, WebConfigurator webConfigurator)
        throws ConfigurationException
	{
		this.context = context;
		this.webConfigurator = webConfigurator;
        this.linkToolConfiguration = new LinkTool.Configuration(config, webConfigurator);
	}
	
    /**
	 * {@inheritDoc}
	 */
	public Object getTool()
	{
        HttpContext httpContext = HttpContext.getHttpContext(context);
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        RequestParameters requestParameters = RequestParameters.getRequestParameters(context);
		return new LinkTool(httpContext, mvcContext, requestParameters, linkToolConfiguration);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void recycleTool(Object tool)
	{
        // these simple objects do not need recycling
	}

	/**
	 * {@inheritDoc}
	 */
	public String getKey()
	{
		return "link";
	}    
}
