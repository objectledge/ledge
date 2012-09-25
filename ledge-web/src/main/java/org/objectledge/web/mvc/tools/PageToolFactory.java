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
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.tools.ContextToolFactory;
import org.objectledge.web.HttpContext;

/**
 * Context tool factory component to build the page tool.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageToolFactory.java,v 1.10 2007-11-18 21:20:34 rafal Exp $
 */
public class PageToolFactory implements ContextToolFactory
{
    /** The linktool factory. */
	protected LinkToolFactory linkToolFactory;

    /** The context component. */
    protected Context context;
    
    /** page tool configuration. */
    protected PageTool.Configuration pageToolConfiguration;
   
    
	/**
	 * Component constructor.
	 * @param linkToolFactory factory for creating
	 * 		{@link LinkTool}s tools used by {@link PageTool}s.
 	 */
    public PageToolFactory(Configuration config, LinkToolFactory linkToolFactory, Context context)
        throws ConfigurationException
    {
        this.linkToolFactory = linkToolFactory;
        this.context = context;
        this.pageToolConfiguration = new PageTool.Configuration(config);
    }
	
    /**
	 * {@inheritDoc}
	 */
	public Object getTool()
        throws ProcessingException
	{
		return new PageTool((LinkTool) linkToolFactory.getTool(), 
            HttpContext.getHttpContext(context), pageToolConfiguration);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void recycleTool(Object tool)
        throws ProcessingException
	{
		linkToolFactory.recycleTool( ((PageTool) tool).getLinkTool() );
	}

	/**
	 * {@inheritDoc}
	 */
	public String getKey()
	{
		return "pageTool";
    }    
}
