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

import org.objectledge.pipeline.Valve;
import org.objectledge.web.mvc.builders.Builder;
import org.objectledge.web.mvc.components.Component;

/**
 * A class finder for finding MVC model classes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCClassFinder.java,v 1.14 2005-03-29 10:53:24 zwierzem Exp $
 */
public interface MVCClassFinder
{
    // actions //////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a runnable action instance based on a given name. If no runnable is found, a
     * <code>null</code> is returned.
     * 
     * @param actionName name of an action class
     * @return found runnable action instance
     */
    public Valve getAction(String actionName);
    
    // builders /////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a builder instance based on a given name. If no builder is found, a
     * <code>null</code> is returned.
     * 
     * @param builderName part of a name of a builder class to be instantiated
     * @return found builder instance
     */
    public Builder findBuilder(String builderName);

	/**
	 * Returns an enclosing view name for a given view name. If no name is found, a
	 * <code>null</code> is returned.
	 * 
	 * @param viewName name of a view for which an enclosing view must be found 
     * @return found view name
	 */
	public String findEnclosingViewName(String viewName);

	// components /////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a component instance based on a given name. If no component is found, a
	 * {@link org.objectledge.web.mvc.components.DefaultComponent} is returned.
	 * 
	 * @param componentName part of a name of a component class to be found/instantiated
	 * @return found component instance
	 */
	public Component getComponent(String componentName);
}
