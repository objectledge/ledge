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

import org.objectledge.templating.Template;
import org.objectledge.web.mvc.BuildException;

/**
 * Builder of a single view element.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: Builder.java,v 1.1 2003-12-30 14:41:37 zwierzem Exp $
 */
///CLOVER:OFF
public interface Builder
{	
	/**
	 * This method is called to allow the view builder to redirect the control to another builder
	 * without executing the build method.
	 * 
     * @return the builder which will be executed instead of current builder.
     */
    public Builder route();
    
	/**
	 * Build method executes builder logic which should return rendered <code>String</code>.
	 * 
     * @param template template to be used during building
     * @param embeddedBuildResults string containing results of embedded builder build (it will be
     * 	fit into templating context under <code>$embedded_placeholder</code> key).
     * 
     * @return string containing rendered view element
     * @throws BuildException on problems with view element building
     */
    public String build(Template template, String embeddedBuildResults)
	throws BuildException;
    
	/**
	 * Returns a manually chosen builder and template in which currently executed builder will be
	 * embedded in. Both builder and template may be chosen separately - if <code>null</code> is\
	 * returned for either template or builder, finder component is used to search for a proper
	 * template and/or builder for embedded builder.
	 * 
	 * @return view pair
	 */
	public ViewPair getEnclosingViewPair();

    /**
     * Gets a template chosen by this builder.
     * 
     * @return template used by this builder
     */
    public Template getTemplate(); 
}
