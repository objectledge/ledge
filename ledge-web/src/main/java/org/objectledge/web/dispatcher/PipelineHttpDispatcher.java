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

package org.objectledge.web.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ConcurrencyControlValve;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.HttpDispatcher;

/**
 * A dispatcher that process the request using pipeline.
 * <p>
 * Created on Dec 23, 2003
 * </p>
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PipelineHttpDispatcher.java,v 1.13 2005-07-07 08:29:25 zwierzem Exp $
 */
public class PipelineHttpDispatcher
    implements HttpDispatcher
{
    /** the pipeline */
    private Valve pipeline;

    /** thread context. */
    private Context context;

    /**
     * Creates a new pipeline dipspatcher.
     * 
     * @param pipeline the pipeline
     * @param context the thread context
     * @throws ConfigurationException if the configuration is malformed.
     */
    public PipelineHttpDispatcher(Valve pipeline, Context context)
        throws ConfigurationException
    {
        this.pipeline = pipeline;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public boolean dispatch(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        HttpContext httpContext = new HttpContext(request, response);
        context.setAttribute(HttpContext.class, httpContext);
        final HttpSession session = request.getSession(false);
        if(session != null)
        {
            Object privilegedSession = session
                .getAttribute(ConcurrencyControlValve.PRIVILEGED_SESSION_MARKER);
            if(privilegedSession != null)
            {
                context.setAttribute(ConcurrencyControlValve.PRIVILEGED_SESSION_MARKER,
                    privilegedSession);
            }
        }
        try
        {
            pipeline.process(context);
            if(context.getAttribute(ConcurrencyControlValve.DROPPED_REQUEST_MARKER) != null)
            {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            }
        }
        catch(ProcessingException e)
        {
            throw new ServletException("processing failed", e);
        }
        finally
        {
            context.clearAttributes();
        }
        return httpContext.getDirectResponse();
    }
}
