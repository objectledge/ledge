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
package org.objectledge.web.mvc.finders;

import org.objectledge.templating.Template;

/**
 * Finds templates that should be used for rendering specific views
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCTemplateFinder.java,v 1.4 2004-01-19 14:22:13 zwierzem Exp $
 */
public interface MVCTemplateFinder
{
    // builders /////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Returns a template for a given template name. If no template is found, a <code>null</code>
	 * is returned.
	 * 
	 * @param name template name to get template.
	 * @return found template
	 */
	public Template getTemplate(String templateName);

	/**
	 * Returns an builder template for a given view name. If no template is found, a
	 * {@link org.objectledge.web.mvc.builders.DefaultTemplate} is returned.
	 * 
     * @param name view name to look up template for.
	 * @return found template
     */
    public Template findBuilderTemplate(String name);

	/**
	 * Returns an enclosing builder template for a given builder. If no template is found, a
	 * {@link org.objectledge.web.mvc.builders.DefaultTemplate} is returned.
	 * 
	 * @param builderTemplate a builder for which an eclosing builder template must be found 
	 * @return the template.
	 */
	public Template findEnclosingBuilderTemplate(Template builderTemplate);
    
    /**
     * Returns the name of the view that corresponds to the given builder template.
     * 
     * @param builderTemplate the builder template.
     * @return the view name.
     */
    public String findViewName(Template builderTemplate);
}
