//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, 
//are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//nor the names of its contributors may be used to endorse or promote products 
//derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
//POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.templating.velocity;

import java.util.Arrays;

import org.objectledge.templating.TemplatingContext;

/**
 * An implementation of templating context interface based on velocity context.
 * templating engine.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: VelocityContext.java,v 1.3 2008-10-23 14:44:30 rafal Exp $
 */
public class VelocityContext
    extends TemplatingContext
{
    /** velocity context */ 
    private org.apache.velocity.context.Context context;

    /**
     * Contstructs a VelocityContext.
     */
    public VelocityContext()
    {
    	context = new org.apache.velocity.VelocityContext();
    }

    /**
     * Gets an object from the Context.
     *
     * @param key the object's key.
     * @return the object
     */
    public Object get(String key)
    {
        return context.get(key);
    }

    /**
     * Puts an object into the context.
     *
     * @param key the object's key.
     * @param value the object
     * @return the previous object with that key.
     */
    public Object put(String key, Object value)
    {
        return context.put(key, value);
    }

    /**
     * Removes an object from the context.
     *
     * @param key the object's key.
     * @return the object in the context.
     */
    public Object remove(String key)
    {
        return context.remove(key);
    }

    /**
     * Checks if the context contains an object.
     *
     * @param key the object's key.
     * @return <code>true</code> if object is stored in the context.
     */
    public boolean containsKey(String key)
    {
        return context.containsKey(key);
    }

    /**
     * Returns keys of all objects.
     *
     * @return keys of all objects.
     */
    public String[] getKeys()
    {
    	Object[] source = context.getKeys();
    	String[] target = new String[source.length];
    	System.arraycopy(source,0,target,0,source.length);
        return target;
    }
    
    /**
     * Returns a velocity context.
     * 
     * @return a view of this object.
     */
    org.apache.velocity.context.Context getContext()
    {
        return context;
    }
    
    /**
     * Returns a string dump of the context's contents, for debugging purposes.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder buff = new StringBuilder();
        String[] keys = getKeys();
        Arrays.sort(keys);
        for(String k : keys)
        {
            buff.append(k).append(" = ");
            if(context.get(k) != this) // avoid infinite recursion on self refernces
            {
                buff.append(context.get(k).toString());
            }
            buff.append("\n");              
        }
        return buff.toString();
    }
}
