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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.objectledge.cache.spi.CachingSPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.LRUMap;

/**
 * An implementation of soft cache.
 *
 * <p>This implementation performs LRU ordering on each get() operation
 * with o(n) complexity. This approach is slower on get-intensive maps,
 * but is faster on put-intensive maps. This map is supposed to be used as a
 * limited space storage below a factory map, so there is a good chance that
 * it will be put-intensive. Moreover, this approach is by far simpler to
 * implement than put() LRU ordering ;-)</p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LRUMapImpl.java,v 1.1 2004-02-12 11:41:26 pablo Exp $
 */
public class LRUMapImpl
    extends DelegateMap
    implements LRUMap, ConfigurableMap
{
    // constatns ////////////////////////////////////////////////////////////

    /** The default map capacity. (10) */
    public static final int CAPACITY_DEFAULT = 10;

    // instance variables ///////////////////////////////////////////////////

    /** The capacity limit. */
    private int capacity;

    /** The access list. */
    private LinkedList access = new LinkedList();

    // initailization ////////////////////////////////////////////////////////

    /**
     * Constructs a soft chache.
     */
    public LRUMapImpl()
    {
        super(new HashMap(CAPACITY_DEFAULT));
    }

    // ConfigurableMap interface /////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void configure(CachingSPI caching, String name, String config)
    {
        try
        {
            setCapacity(Integer.parseInt(config));
        }
        catch(NumberFormatException e)
        {
            throw new IllegalArgumentException("invalid capacity spec "+config);
        }
    }

    // LRUMap SPI interface //////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void setCapacity(int capacity)
    {
        if(capacity < access.size())
        {
            access = new LinkedList(access.subList(0, capacity-1));
        }
        this.capacity = capacity;
    }

    /**
     * {@inheritDoc}
     */
    public int getCapacity()
    {
        return capacity;
    }

    // Map interface /////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public Object put(Object key, Object value)
    {
        if(delegate.size() == capacity)
        {
            delegate.remove(access.removeLast());
        }
        access.remove(key);
        access.addFirst(key);
        return delegate.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Object key)
    {
        Object obj = delegate.get(key);
        if(obj != null)
        {
            access.remove(key);
            access.addFirst(key);
        }
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(Object key)
    {
        Object old = delegate.remove(key);
        access.remove(key);
        return old;
    }

    /**
     * {@inheritDoc}
     */
    public Set entrySet()
    {
        final Set entries = delegate.entrySet();
        
        return new AbstractSet()
            {
                public Iterator iterator()
                {
                    return getEntryIterator();
                }
                
                public boolean contains(Object o)
                {
                    if(o instanceof Map.Entry)
                    {
                        Object key = ((Map.Entry)o).getKey();
                        access.remove(key);
                        access.addFirst(key);
                    }
                    return entries.contains(o);
                }
                
                public boolean remove(Object o)
                {
                    if(o instanceof Map.Entry)
                    {
                        Object key = ((Map.Entry)o).getKey();
                        access.remove(key);
                    }
                    return entries.remove(o);
                }
                
                public int size()
                {
                    return delegate.size();
                }
                
                public void clear()
                {
                    delegate.clear();
                    access.clear();
                }
            };
    }

    /**
     * {@inheritDoc}
     */
    public Collection values()
    {
        return new AbstractCollection()
            {
                public Iterator iterator()
                {
                    return getValueIterator();
                }
                
                public boolean contains(Object value)
                {
                    return containsValue(value);
                }
                
                public int size()
                {
                    return delegate.size();
                }
                
                public void clear()
                {
                    delegate.clear();
                    access.clear();
                }
            };
    }

    /**
     * {@inheritDoc}
     */
    private Iterator getValueIterator()
    {
        if(delegate.isEmpty())
        {
            return new EmptyIterator();
        }
        else
        {
            final Iterator i = entrySet().iterator();
            return new Iterator()
                {
                    public boolean hasNext()
                    {
                        return i.hasNext();
                    }
                    
                    public Object next()
                    {
                        return ((Map.Entry)i.next()).getValue();
                    }
                    
                    public void remove()
                    {
                        i.remove();
                    }
                };
        }
    }

    /**
     * {@inheritDoc}
     */
    private Iterator getEntryIterator()
    {
        if(delegate.isEmpty())
        {
            return new EmptyIterator();
        }
        else
        {
            final Iterator i = delegate.entrySet().iterator();
            return new Iterator()
                {
                    private Object lastKey = null;
                    public boolean hasNext()
                    {
                        return i.hasNext();
                    }
                    
                    public Object next()
                    {
                        Map.Entry mapEntry = (Map.Entry)i.next();
                        lastKey = mapEntry.getKey();
                        access.remove(lastKey);
                        access.addFirst(lastKey);
                        return mapEntry;
                    }
                    
                    public void remove()
                    {
                        i.remove();
                        access.remove(lastKey);
                    }
                };
        }
    }
}
