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

package org.objectledge.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Context gives the application an ability to reference arbitrary objects using String names.
 * 
 * <p>The bindings are local the calling thread. This is a convinient way of passing data 
 * associated with a stream of processing without putting them into method attributes. This is 
 * especialy convinient with data that are not needed at a specific stage of processing, but may 
 * be needed by the nested stages - for example security credentials.</p>
 * 
 * <p><b>Note!</b>It is important to clear the context before the thread departs from within 
 * application boundaries - otherwise the references may prevent the objects (and as a result 
 * large graphs of objects including classes and classloaders) from being garbage collected, while
 * being otherwise being eligible to collection.</p>  
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Context.java,v 1.3 2004-01-08 10:20:49 fil Exp $
 */
public class Context
{
    /** storage of the context attributes, specific to a thread. */
    private static InheritableThreadLocal threadAttributes = new InheritableThreadLocal();

    /**
     * Return the value of a context attribute.
     * 
     * @param name the attribute name.
     * @return the value of the attribute.
     */
    public Object getAttribute(String name)
    {
        return getAttributes().get(name); 
    }

    /**
     * Sets a new value of a context attribute.
     * 
     * @param name the name of the attribute.
     * @param value the new value of the attribute.
     * @return the old value of the attribute.
     */    
    public Object setAttribute(String name, Object value)
    {
        return getAttributes().put(name, value);
    }
    
    /**
     * Removes a context attribute.
     * 
     * @param name the name of the attribute.
     * @return the old value of the attribute.
     */
    public Object removeAttribute(String name)
    {
        return getAttributes().remove(name);
    }
    
    /**
     * Removes all context attributes.
     */
    public void clearAttributes()
    {
        getAttributes().clear();
    }
    
    // - implementation -----------------------------------------------------
    
    private Map getAttributes()
    {
        Map attributes = (Map)threadAttributes.get();
        if(attributes == null)
        {
            attributes = new HashMap();
            threadAttributes.set(attributes);
        }
        return attributes;
    }
}
