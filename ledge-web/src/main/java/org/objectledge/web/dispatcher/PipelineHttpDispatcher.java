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

import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.pipeline.Pipeline;
import org.objectledge.web.HttpContext;
import org.objectledge.web.HttpContextImpl;
import org.objectledge.web.HttpDispatcher;

/**
 *
 * <p>Created on Dec 23, 2003</p>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a> 
 * @version $Id: PipelineHttpDispatcher.java,v 1.2 2004-01-08 10:20:48 fil Exp $
 */
public class PipelineHttpDispatcher 
    implements HttpDispatcher
{
	/** the pipeline */
    private Pipeline pipeline;
    
    /** thead context. */
    private Context context;
    
    /**
     * Creates a new pipeline dipspatcher.
     * 
     * @param pipeline the pipeline
     * @param context the thread context
     * @throws ConfigurationException if the configuration is malformed.
     */
    public PipelineHttpDispatcher(Pipeline pipeline, Context context)
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
        HttpContext httpContext = new HttpContextImpl(request,response);
        context.setAttribute(HttpContext.CONTEXT_KEY, httpContext);
        pipeline.run();
        // TODO decide what to return...
        return true;
    }
}
