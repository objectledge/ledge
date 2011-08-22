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
 * @version $Id: MVCClassFinder.java,v 1.17 2005-07-22 17:25:53 pablo Exp $
 */
public interface MVCClassFinder
{
    // actions //////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a runnable action instance based on a given name. If no runnable exists, a
     * <code>null</code> is returned.
     * 
     * @param actionName name of an action class
     * @return found runnable action instance
     */
    public Valve getAction(String actionName);
    
    // builders /////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a result object containing reference to the found builder for a given view
     * name. If no builder is found, a <code>null</code> builder is returned in the result object.
     * 
     * @param builderName part of a name of a builder class to be instantiated
     * @return found builder instance with accompanying info.
     */
    public Result findBuilder(String builderName);

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
	 * Returns a component instance based on a given name. If no component exists, a
	 * <code>null</code> is returned.
	 * 
	 * @param componentName part of a name of a component class to be found/instantiated
	 * @return found component instance
	 */
	public Component getComponent(String componentName);

    /**
     * The search result for builder find method.
     *  
     * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
     */
    public static class Result
    {
        private final String originalView;
        private final Builder builder;
        private final String actualView;
        private final boolean last;
        
        /**
         * Creates a new Result instance.
         * 
         * @param originalView originally requested builder.
         * @param builder resolved builder.
         * @param actualView the actual view associated with the resolved builder.
         * @param last this result is the last element of the fallback sequence.
         */
        public Result(String originalView, Builder builder, String actualView, boolean last)
        {
            this.originalView = originalView;
            this.builder = builder;
            this.actualView = actualView;
            this.last = last;
        }

        /**
         * @return Returns the original view used to search for the builder.
         */
        public String getOriginalView()
        {
            return originalView;
        }

        /**
         * @return Returns the actual view name computed during search.
         */
        public String getActualView()
        {
            return actualView;
        }

        /**
         * @return Returns the found builder.
         */
        public Builder getBuilder()
        {
            return builder;
        }
        
        /**
         * @return Tells whether fallback on view name was preformed during builder search.
         */
        public boolean fallbackPerformed()
        {
            return !originalView.equals(actualView);
        }
        
        /**
         * @return Tell wether this result is the last element of the fallback sequece.
         */
        public boolean isLastFallback()
        {
            return last;
        }
    }
}
