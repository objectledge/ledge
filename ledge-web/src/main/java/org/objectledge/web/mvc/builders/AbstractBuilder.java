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
package org.objectledge.web.mvc.builders;

import org.objectledge.context.Context;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;

/**
 * Abstract builder implementation, which does not route and only merges templates.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractBuilder.java,v 1.3 2004-01-15 09:40:28 fil Exp $
 */
public abstract class AbstractBuilder implements Builder
{
	/**
	 * Application context used by this builder instance.
	 */
	protected Context context;
	
	/**
	 * Builders only need context.
	 * 
	 * @param context application context for use by this builder.
	 */
	public AbstractBuilder(Context context)
	{
		this.context = context;
	}
	
	/**
	 * {@inheritDoc}
	 */
    public Builder route()
    {
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    public String build(Template template, String embeddedBuildResults)
    throws BuildException
    {
    	try
    	{
			return template.merge(TemplatingContext.getTemplatingContext(context));
    	}
        catch(MergingException e)
        {
        	throw new BuildException(e);
        }
    }

	/**
	 * {@inheritDoc}
	 */
    public ViewPair getEnclosingViewPair()
    {
        return new ViewPair(null, null);
    }

	/**
	 * {@inheritDoc}
	 */
    public Template getTemplate()
    {
        return null;
    }
}
