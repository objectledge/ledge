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
import java.util.Map;
import java.util.Set;

import org.objectledge.cache.spi.LayeredMap;

/**
 * A map that delegates all it's methods to another Map object.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DelegateMap.java,v 1.2 2005-02-10 17:47:03 rafal Exp $
 */
public abstract class DelegateMap<K, V>
    implements LayeredMap<K, V>
{
    // memeber objects ///////////////////////////////////////////////////////
    
    /** The delegate object. */
    protected Map<K, V> delegate;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Constructs a new DelegateMap object without initial delegate.
     */
    public DelegateMap()
    {
        // default constructor
    }

    /**
     * Constructs a new DelegateMap object.
     *
     * @param delegate the delegate object.
     */
    public DelegateMap(Map<K, V> delegate)
    {
        this.delegate = delegate;
    }

    /**
     * Sets the map that provides value storage.
     *
     * @param map the delegate map.
     */
    public void setDelegate(Map<K, V> map)
    {
        delegate = map;
    }

    /**
     * Returns the map that provides value storage.
     *
     * @return the delegate map.
     */
    public Map<K, V> getDelegate()
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
    public Set<Map.Entry<K, V>> entrySet()
    {
        return delegate.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o)    
    {
        if(o instanceof DelegateMap)
        {
            return delegate.equals(((DelegateMap<K,V>)o).getDelegate());
        }
        else
        {
            return delegate.equals(o);
        }
    }

    /**
     * {@inheritDoc}
     */
    public V get(Object key)
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
    public Set<K> keySet()
    {
        return delegate.keySet();
    }
    
    /**
     * {@inheritDoc}
     */
    public V put(K key, V value)
    {
        return delegate.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> map)
    {        
        for(Map.Entry<? extends K, ? extends V> entry : map.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public V remove(Object key)
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
    public Collection<V> values()
    {
        return delegate.values();
    }   
}

