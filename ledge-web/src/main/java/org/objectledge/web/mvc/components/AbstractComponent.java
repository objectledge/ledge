// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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
package org.objectledge.web.mvc.components;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.BuildException;

/**
 * Base class of Component interface implementations.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractComponent.java,v 1.1 2005-07-26 12:10:26 rafal Exp $
 */
public abstract class AbstractComponent 
    implements Component
{
	/** Application context used by this component instance. */
	protected Context context;
	
	/**
	 * Constructs a component instance.
	 * 
	 * @param context application context for use by this component.
	 */
	public AbstractComponent(Context context)
	{
		this.context = context;
	}	

    /**
     * Perform processing and store values in the tmplating context.
     * 
     * @param templatingContext the TemplatingContext.
     * @throws ProcessingException if the processing fails.
     */
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        // do nothing in base implementation
    }

    /**
     * {@inheritDoc}
     */
    public String build(Template template)
    	throws BuildException, ProcessingException
    {
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        process(templatingContext);
		try
		{
			return template.merge(templatingContext);
		}
		catch(MergingException e)
		{
			throw new BuildException("failed to merge template: "+template.getName(), e);
		}
    }

    /**
     * {@inheritDoc}
     */
    public Template getTemplate()
        throws ProcessingException
    {
        return null;
    }
}
