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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.web.HttpDispatcher;
import org.picocontainer.PicoContainer;

/**
 * A dispatcher that delegates to other dispatchers, selected with HTTP method names.
 *
 * <p>Created on Dec 22, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: HttpMethodDispatcher.java,v 1.2 2003-12-23 16:55:36 fil Exp $
 */
public class HttpMethodDispatcher
    implements HttpDispatcher
{
    /** the dispatcher map */
    protected Map dispatchers = new HashMap();
    
    /**
     * Creates a Http Method dispatcher
     * 
     * @param config method - nested dispatcher mapping configuration.
     * @param container the container to resolve dispatcher components.
     * @throws ConfigurationException if the configuration file is malformed.
     */
    public HttpMethodDispatcher(Configuration config, PicoContainer container)
        throws ConfigurationException
    {
        Configuration[] elems = config.getChildren();
        for(int i=0; i<elems.length; i++)
        {
            String method = elems[i].getAttribute("name");
            String key = elems[i].getValue();
            Object component = container.getComponentInstance(key);
            if(component == null)
            {
                throw new ComponentInitializationError("component "+key+" is missing");
            }
            if(!(component instanceof HttpDispatcher))
            {
                throw new ComponentInitializationError("component "+key+" of clas "+
                    component.getClass().getName()+" does not implement HttpDispatcher interface");
            }
            dispatchers.put(method, component);   
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean dispatch(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        HttpDispatcher dispatcher = (HttpDispatcher)dispatchers.get(request.getMethod());
        if(dispatcher == null)
        {
            return false;       
        }
        else
        {
            return dispatcher.dispatch(request, response);
        }
    }
}
