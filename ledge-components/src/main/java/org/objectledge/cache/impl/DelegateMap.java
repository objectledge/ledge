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

package org.objectledge.cache.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.objectledge.cache.spi.LayeredMap;

/**
 * A map that delegates all it's methods to another Map object.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DelegateMap.java,v 1.1 2004-02-12 11:41:26 pablo Exp $
 */
public abstract class DelegateMap
    implements LayeredMap
{
    // memeber objects ///////////////////////////////////////////////////////
    
    /** The delegate object. */
    protected Map delegate;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Constructs a new DelegateMap object without initial delegate.
     */
    public DelegateMap()
    {
    }

    /**
     * Constructs a new DelegateMap object.
     *
     * @param delegate the delegate object.
     */
    public DelegateMap(Map delegate)
    {
        this.delegate = delegate;
    }

    /**
     * Sets the map that provides value storage.
     *
     * @param map the delegate map.
     */
    public void setDelegate(Map map)
    {
        delegate = map;
    }

    /**
     * Returns the map that provides value storage.
     *
     * @return the delegate map.
     */
    public Map getDelegate()
    {
        return delegate;
    }
    
    // Map interaface ////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        delegate.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key)
    {
        return delegate.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value)
    {
        return delegate.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public Set entrySet()
    {
        return delegate.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o)
    {
        return delegate.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Object key)
    {
        return delegate.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return delegate.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Set keySet()
    {
        return delegate.keySet();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object put(Object key, Object value)
    {
        return delegate.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map map)
    {
        Set entries = map.entrySet();
        Iterator i = entries.iterator();
        while(i.hasNext())
        {
            Map.Entry entry = (Map.Entry)i.next();
            put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object remove(Object key)
    {
        return delegate.remove(key);
    }
    
    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return delegate.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public Collection values()
    {
        return delegate.values();
    }   
}

