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

import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;

/**
 * Builder class represents a builder of a single view element.
 * 
 * <p>
 * The builder may:
 * </p>
 * <ul>
 * <li>redirect the control to another view - {@link #route(String)}</li>
 * <li>build the contents of a view - {@link #build(Template, String)} and,</li>
 * <li>choose to embed the generated contents in an enclosing view -
 * {@link #getEnclosingView(String)}</li>
 * </ul>  
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: Builder.java,v 1.10 2005-07-22 17:25:48 pablo Exp $
 */
///CLOVER:OFF
public interface Builder
{	
	/**
	 * This method is called to allow the view builder to redirect the control to another view
	 * without executing the build method.
	 * 
     * @param thisViewName the name of currently processed view (builder)
     *  (from request, route calls or enclosure)
	 * 
     * @return the name of the view which will be executed instead of current builder, or
     *  <code>null</code> to execute this builder.
     */
    public String route(String thisViewName)
        throws ProcessingException;
    
	/**
	 * Returns a manually chosen builder and template in which currently executed builder will be
	 * embedded in. 
     * 
     * <p>Three options are available: returning {@link EnclosingView#DEFAULT} to proceed with 
     * default behaviour, returning {@link EnclosingView#TOP} to terminate enclosure loop, or 
     * returing an EnclosingView object created using {@link EnclosingView#EnclosingView(String)} to
     * request enclosing withing a specific view.</p>    
     * 
     * @param thisViewName the actual template used to build current builder (the one on which the 
     * method is called).
	 * @return the enclosing encosing view.
	 */
	public EnclosingView getEnclosingView(String thisViewName);

    /**
     * Build method executes builder logic which should return rendered <code>String</code>.
     * 
     * @param template template to be used during building.
     * @param embeddedBuildResults string containing results of embedded view's rendering (results
     *  of enclosed view rendering).
     * 
     * @return string containing rendered view.
     * @throws BuildException on problems with view building.
     * @throws ProcessingException on problems while preparing information to be shown.
     */
    public String build(Template template, String embeddedBuildResults)
       throws BuildException, ProcessingException;    
}
