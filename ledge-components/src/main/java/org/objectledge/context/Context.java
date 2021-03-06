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
 * <p>The bindings are local to the calling thread. This is a convenient way of passing data 
 * associated with a stream of processing without putting them into method attributes. This is 
 * especialy convenient with data that are not needed at a specific stage of processing, but may 
 * be needed by the nested stages - for example security credentials.</p>
 * 
 * <p><b>Note!</b>It is important to clear the context before the thread departs from within 
 * application boundaries - otherwise the references may prevent the objects (and as a result 
 * large graphs of objects including classes and classloaders) from being garbage collected, while
 * being otherwise being eligible to collection.</p>  
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Context.java,v 1.8 2005-11-18 11:39:33 rafal Exp $
 */
public class Context
{
    /** storage of the context attributes, specific to a thread. */
	private static ThreadLocal<Map<String, Object>> threadAttributes = new ThreadLocal<Map<String, Object>>();

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
     * Return the value of a context attribute.
     * 
     * <p>Class object and the class name String are considered to be equivalent keys.</p>
     * 
     * @param <T> the type reflected by key Class object, to avoid casting on the caller's side.
     * @param key a Class key of the attribute.
     * @return the value of the attribute.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(Class<T> key)
    {
        return (T)getAttributes().get(key.getName()); 
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
     * Sets a new value of a context attribute.
     *
     * <p>Class object and the class name String are considered to be equivalent keys.</p>
     *       
     * @param key Class key of the attribute.
     * @param value the new value of the attribute, must be castable to key class.
     * @return the old value of the attribute.
     */    
    @SuppressWarnings("unchecked")
    public <T> T setAttribute(Class<T> key, T value)
    {
        if(!key.isAssignableFrom(value.getClass()))
        {
            throw new ClassCastException();
        }                        
        return (T)getAttributes().put(key.getName(), value);
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
     * Removes a context attribute.
     * 
     * <p>Class object and the class name String are considered to be equivalent keys.</p>
     *       
     * @param key Class key of the attribute.
     * @return the old value of the attribute.
     */
    @SuppressWarnings("unchecked")
    public <T> T removeAttribute(Class<T> key)
    {
        return (T)getAttributes().remove(key.getName());
    }
    
    /**
     * Removes all context attributes.
     */
    public void clearAttributes()
    {
        getAttributes().clear();
    }
    
    // - implementation -----------------------------------------------------
    
    private Map<String, Object> getAttributes()
    {
        Map<String, Object> attributes = threadAttributes.get();
        if(attributes == null)
        {
            attributes = new HashMap<String, Object>();
            threadAttributes.set(attributes);
        }
        return attributes;
    }
}
