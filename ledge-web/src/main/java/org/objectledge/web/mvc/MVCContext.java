// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.web.mvc;

import org.objectledge.context.Context;

/**
 * The web context contains all needed information about mvc processing parameters.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCContext.java,v 1.12 2005-05-06 09:31:13 rafal Exp $
 */
public class MVCContext
{
	/**
	 *  Usefull method to retrieve http context from context.
	 *
	 * @param context the context.
	 * @return the http context.
	 */
	public static MVCContext getMVCContext(Context context)
	{
		return (MVCContext)context.getAttribute(MVCContext.class);
	}

    /** request parameters used to override params. */ 
    //private RequestParameters requestParameters;
    
	/** the action parameter. */
	private String action;

	/** the view parameter. */
	private String view;

	/** the view build result */
	private String buildResult;

    /** the current processing stage. */
    private ProcessingStage stage = ProcessingStage.PROCESSING;

	/**
	 * Construct new pipeline context.
	 */
	public MVCContext()
	{
        // TODO: Decide whether the parameters should be overriden setView/setAction ???
        //RequestParameters requestParameters
	    //this.requestParameters = requestParameters;
        // This would be nice to have setAction method removed
	}
	
    /**
     * Returns the action paremeter.
     * 
     * @return the value of action parameter.
     */
	public String getAction()
	{
		return action;
	}

    /**
     * Sets the action parameter.
     *
     * @param action the action parameter.
     */
	public void setAction(String action)
	{
		this.action = action;
	}

    /**
     * Returns the view paremeter.
     *
     * @return the value of view parameter.
     */
	public String getView()
	{
		return view;
	}

    /**
     * Sets the view parameter.
     *
     * @param view the view parameter.
     */
	public void setView(String view)
	{
		this.view = view;
	}
	
    /**
     * Gets the result of building the view part of MVC pipeline.
     * 
     * @return the result of building the MVC view
     */
    public String getBuildResult()
    {
        return buildResult;
    }

    /**
     * Sets the result of building the view part of MVC pipeline.
     * 
     * @param buildResult a string representing built view which should be sent to the browser
     */
    public void setBuildResult(String buildResult)
    {
    	this.buildResult = buildResult;
    }

    /**
     * Returns the processing stage.
     *
     * @return the stage.
     */
    public ProcessingStage getStage()
    {
        return stage;
    }

    /**
     * Sets the processing stage.
     *
     * @param stage The stage to set.
     */
    public void setStage(ProcessingStage stage)
    {
        this.stage = stage;
    }
}
