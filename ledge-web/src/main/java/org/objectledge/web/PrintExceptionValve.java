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
package org.objectledge.web;

import java.io.IOException;
import java.io.PrintWriter;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ErrorHandlingPipeline;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.utils.StackTrace;
import org.objectledge.utils.StringUtils;

/**
 * Pipeline component for executing MVC view building.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PrintExceptionValve.java,v 1.4 2004-06-23 15:00:34 fil Exp $
 */
public class PrintExceptionValve 
    implements Valve
{
	/**
	 * Component constructor.
	 */
	public PrintExceptionValve()
	{
	}
	
	/**
	 * Run view building starting from a view builder chosen in request parameters.
     * 
     * @param context used application context 
     * @throws ProcessingException if the processing fails.
	 */
	public void process(Context context)
        throws ProcessingException
	{
		HttpContext httpContext = HttpContext.getHttpContext(context);
        Throwable t = (Throwable)context.getAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION);
        if(t != null)
        {
        	try
			{
                String result = new StackTrace(t).toString();
				httpContext.setContentType("text/plain");
				httpContext.getResponse().setContentLength(
			       	StringUtils.getByteCount(result, httpContext.getEncoding()));
				PrintWriter out = httpContext.getPrintWriter();
				out.write(result);
				out.flush();
			}
            ///CLOVER:OFF
			catch(IOException e)
			{
				throw new ProcessingException("Cannot write the response",e);
			}
            ///CLOVER:ON
		}
	}
}
