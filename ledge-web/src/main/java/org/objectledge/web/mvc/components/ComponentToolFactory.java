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

package org.objectledge.web.mvc.components;

import org.objectledge.context.Context;
import org.objectledge.templating.tools.ContextToolFactory;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;

/**
 * Component tools factory.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 */
public class ComponentToolFactory implements ContextToolFactory
{
	/** context */
	protected Context context;
	/** The class finder for finding component objects. */
	protected MVCClassFinder classFinder;
	/** The template finder for finding component templates. */
	protected MVCTemplateFinder templateFinder;
	
	/**
	 * Component constructor.
	 *
	 * @param context the context.
	 * @param classFinder the class finder.
	 * @param templateFinder the template finder.
	 */
    public ComponentToolFactory(
        Context context,
        MVCClassFinder classFinder,
        MVCTemplateFinder templateFinder)
	{
		this.context = context;
		this.classFinder = classFinder;
		this.templateFinder = templateFinder;
	}
	
    /**
	 * {@inheritDoc}
	 */
	public Object getTool()
	{
		MVCContext pipelineContext = MVCContext.getMVCContext(context);
		return new ComponentTool(classFinder, templateFinder);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void recycleTool(Object tool)
	{
		// TODO recycle object when pooling available.
	}

	/**
	 * {@inheritDoc}
	 */
	public String getKey()
	{
		return "component";
	}
    
}
