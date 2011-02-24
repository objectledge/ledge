// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.authentication;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.ProcessingStage;

/**
 * A valve that executes it's nested valve (possibly a pipeline) only if an authentication.* action 
 * was called in this request. 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PostAuthenticationValve.java,v 1.1 2005-05-06 09:36:29 rafal Exp $
 */
public class PostAuthenticationValve
    implements Valve
{
    private final Valve nested;

    /**
     * Creates new PostAuthenticationValve instance.
     * 
     * @param nestedArg the nested valve.
     */
    public PostAuthenticationValve(Valve nestedArg)
    {
        this.nested = nestedArg;
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        if(mvcContext.getAction() != null && mvcContext.getAction().startsWith("authentication."))
        {
            ProcessingStage current = mvcContext.getStage();
            mvcContext.setStage(ProcessingStage.POST_AUTHENTICATION);
            try
            {
                nested.process(context);
            }
            finally
            {
                mvcContext.setStage(current);
            }
        }
    }
}
