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
package org.objectledge.web.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.selector.EvaluationException;
import org.objectledge.selector.IntrospectionVariables;
import org.objectledge.selector.Selector;
import org.objectledge.selector.Variables;
import org.objectledge.web.HttpDispatcher;

/**
 * A dispatcher that provides rule based selection of other dispatchers.
 * The dispatchers are selected by means of the {@link org.objectledge.selector.Selector} component.
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: SelectorHttpDispatcher.java,v 1.3 2005-07-22 17:25:57 pablo Exp $
 */
public class SelectorHttpDispatcher
    implements HttpDispatcher
{
    /** Selector of the dispatchers. */
    private Selector selector;
    
    /**
     * Constructs a dispatcher instance.
     * 
     * @param config the selection rules.
     * @param dispatchers the delegate dispatchers.
     * @throws ConfigurationException if the rules configuration is malformed.
     */
    public SelectorHttpDispatcher(Configuration config, HttpDispatcher[] dispatchers)
        throws ConfigurationException
    {
        selector = new Selector(config, dispatchers);
    }

    /**
     * {@inheritDoc}
     */
    public boolean dispatch(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        Variables vars = new IntrospectionVariables(request);
        try
        {
            HttpDispatcher dispatcher = (HttpDispatcher)selector.select(vars);
            if(dispatcher != null)
            {
                return dispatcher.dispatch(request, response);
            }
            else
            {
                return false;
            }
        }
        catch(EvaluationException e)
        {
            throw new ServletException("dispatcher selection failed", e);
        }
    }
}
    