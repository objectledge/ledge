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
package org.objectledge.web.mvc;

import java.io.IOException;
import java.io.PrintWriter;

import org.objectledge.context.Context;
import org.objectledge.pipeline.Pipeline;
import org.objectledge.pipeline.PipelineProcessingException;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;

/**
 * Pipeline component for executing MVC view building.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SimpleCatchProcessingExceptionValve.java,v 1.1 2004-01-21 13:23:43 pablo Exp $
 */
public class SimpleCatchProcessingExceptionValve implements Runnable
{
	/** context */
	protected Context context;
	
	/**
	 * Component constructor.
	 * 
     * @param context used application context 
	 */
	public SimpleCatchProcessingExceptionValve(Context context)
	{
		this.context = context;
	}
	
	/**
	 * Run view building starting from a view builder chosen in request parameters.
	 */
	public void run()
	{
		MVCContext mvcContext = MVCContext.getMVCContext(context);
		HttpContext httpContext = HttpContext.getHttpContext(context);
        Throwable t = (Throwable)context.getAttribute(Pipeline.PIPELINE_EXCEPTION);
        if(t instanceof PipelineProcessingException)
        {
        	try
			{
                String result = t.toString();
				httpContext.setContentType("text/html");
				httpContext.getResponse().setContentLength(
			       	StringUtils.getByteCount(result, httpContext.getEncoding()));
				PrintWriter out = httpContext.getPrintWriter();
				out.write(result);
				out.flush();
			}
			catch(IOException e)
			{
				throw new PipelineProcessingException("Cannot write the response",e);
			}
		}
	}
}
