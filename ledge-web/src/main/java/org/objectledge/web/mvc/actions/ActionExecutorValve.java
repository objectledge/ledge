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
package org.objectledge.web.mvc.actions;

import org.objectledge.context.Context;
import org.objectledge.pipeline.PipelineProcessingException;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCClassFinder;

/**
 * Pipeline component for executing MVC actions.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ActionExecutorValve.java,v 1.5 2004-01-20 08:28:36 fil Exp $
 */
public class ActionExecutorValve implements Runnable
{
	/** context */
	protected Context context;
	/** Finder for builder objects. */
	protected MVCClassFinder classFinder;

	/**
	 * Component constructor.
	 * 
	 * @param context used application context
	 * @param classFinder finder for runnable action objects
	 */
	public ActionExecutorValve(Context context, MVCClassFinder classFinder)
	{
		this.context = context;
		this.classFinder = classFinder;
	}
	
    /**
     * Finds and executes an action for current request.
     */
    public void run()
    {
		// setup used contexts
		MVCContext mvcContext = MVCContext.getMVCContext(context);
        String actionName = mvcContext.getAction(); 
        if(actionName != null)
        {
            try
            {
                // get and execute action
                Runnable action = classFinder.findAction(actionName);
                // TODO access control
                action.run();
            }
            catch(ClassNotFoundException e)
            {
                throw new PipelineProcessingException("invalid action "+actionName, e);
            }
        }
    }
}
