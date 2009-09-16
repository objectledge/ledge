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
import java.util.Map;
import java.util.Set;

/**
 * Map implementation that provides wrapping of values additonal objects,
 * and mapping access tracking.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: WrappingMap.java,v 1.4 2005-02-10 17:49:44 rafal Exp $
 */
public abstract class WrappingMap<K, V> 
    implements Map<K, V>
{
    protected final Map<K, WrappingEntry<V>> delegate;
    
    // initialization ///////////////////////////////////////////////////////

    /**
     * Map constructor.
     */    
    public WrappingMap()
    {
        delegate = new HashMap<K, WrappingEntry<V>>();
    }

    public WrappingMap(Map<K, WrappingEntry<V>> delegate)
    {
        this.delegate = delegate;
    }
    
    // Map interface ////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public V get(Object key)
    { 
        WrappingEntry<V> entry = delegate.get(key);
        if(entry != null)
        {
            entry.touch();
            return entry.getValue();
        }
        else
        {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */    
    public V put(K key, V value)
    {
        WrappingEntry<V> entry = newWrappingEntry(value);
        entry = delegate.put(key, entry);
        if(entry != null)
        {
            return entry.getValue();
        }
        else
        {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected abstract WrappingEntry<V> newWrappingEntry(V value);
    
    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value)
    {
        for(WrappingEntry<V> entry : delegate.values())
        {
            if(equals(entry.getValue(), value))
            {
                entry.touch();
                return true;
            }
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key)
    {
        if(delegate.containsKey(key))
        {
            WrappingEntry<V> we = delegate.get(key);
            we.touch();
            return true;
        }
        else
        {
            return false;
        }
    }

    // entry set 

    private Set<Map.Entry<K, V>> entrySet;
    
    /**
     * {@inheritDoc}
     */
    public Set<Map.Entry<K, V>> entrySet()
    {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null ? es : (entrySet = new EntrySet()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        delegate.clear();        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet()
    {
        return delegate.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        for(Map.Entry<K, V> me : ((Map<K, V>)m).entrySet())
        {
            put(me.getKey(), me.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key)
    {
        return delegate.remove(key).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return delegate.size();
    }
    
    // implementation classes ------------------------------------------------

    /**
     * EntrySet 
     *
     */    
    private class EntrySet
        extends AbstractSet<Map.Entry<K, V>>
    {
        public int size()
        {
            return delegate.size();
        }

        public Iterator<Map.Entry<K, V>> iterator()
        {
            return newEntryIterator();
        }
        
        public boolean contains(Object entry)
        {
            V value = get(((Map.Entry<K, V>)entry).getKey());
            return WrappingMap.this.equals(value, ((Map.Entry<?, ?>)entry).getValue());
        }
        
        public boolean remove(Object entry)
        {
            K key = ((Map.Entry<K, V>)entry).getKey();
            boolean present = delegate.containsKey(key);
            delegate.remove(key);
            return present;
        }
        
        public void clear()
        {
            delegate.clear();
        }
    }
    
    Iterator<Map.Entry<K, V>> newEntryIterator()
    {
        return new EntryIterator();
    }
    
    /**
     * Entry iterator.
     * 
     */
    private class EntryIterator
        extends WrappingMapIterator<Map.Entry<K, V>>
    {
        public Map.Entry<K, V> next()
        {
            Map.Entry<K, V> me = nextEntry();
            return me;
        }
    }

    // values
    
    private Collection<V> values;
    
    /**
     * {@inheritDoc}
     */    
    public Collection<V> values()
    {
        Collection<V> v = values;
        return (v != null ? v : (values = new Values()));
    }

    /**
     * {@inheritDoc}
     */    
    private class Values
        extends AbstractCollection<V>
    {
        public int size()
        {
            return delegate.size();
        }
        
        public void clear()
        {
            delegate.clear();        
        }
        
        public boolean contains(Object v)
        {
            return containsValue(v);
        }
        
        public Iterator<V> iterator()
        {
            return newValuesIterator();
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    Iterator<V> newValuesIterator()
    {
        return new ValuesIterator();
    }
    
    /**
     * {@inheritDoc}
     */
    private class ValuesIterator
        extends WrappingMapIterator<V>
    {        
        public V next()
        {
            Map.Entry<K, V> me = nextEntry();            
            return me.getValue();
        }
    }
    
    // inner classes ////////////////////////////////////////////////////////
    /**
     * Wrapping entry.
     * 
     */
    protected abstract class WrappingEntry<T>
    {
        private T value;
        
        /**
         * Creates a new WrappingEntry instance.
         * 
         * @param value the value.
         */
        public WrappingEntry(T value)
        {
            this.value = value;
        }
        
        /**
         * Returns the entry's value.
         * 
         * @return the object
         */
        public T getValue()
        {
            return value;
        }
        
        /**
         * Updates the entry's timestamp.
         */
        public abstract void touch();
    }
    
    /**
     * Customized Iterator implementation for the WrappingMap.
     */
    protected abstract class WrappingMapIterator<T>
        implements Iterator<T>
    {
        private Iterator<Map.Entry<K, WrappingEntry<V>>> i;
       
        /**
         * Creates a new WrappingMapIterator instance. 
         */ 
        public WrappingMapIterator()
        {
            i = delegate.entrySet().iterator();
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public boolean hasNext()
        {
            return i.hasNext();
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void remove()
        {
            i.remove();
        }
        
        /**
         * Returns the next Map entry.
         * 
         * @return the map entry.
         */
        protected Map.Entry<K, V> nextEntry()
        {
            Map.Entry<K, WrappingEntry<V>> me = i.next();
            me.getValue().touch();
            return new Entry(me.getKey(), me.getValue().getValue());
        }
    }
    
    /**
     * Entry
     */
    private class Entry
        implements Map.Entry<K, V>
    {
        private K key;
        
        private V value;

        public Entry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        public K getKey()
        {
            return key;
        }

        public V getValue()
        {
            return value;
        }

        public V setValue(V value)
        {
            V old = value;
            this.value = value;
            return old;
        }

        public boolean equals(Object o) 
        {
            if (!(o instanceof Map.Entry<?, ?>))
            {
                return false;
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) 
            {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                { 
                    return true;
                }
            }
            return false;
        }
    
        public int hashCode() 
        {
            return (key==null ? 0 : key.hashCode()) ^
                   (value==null   ? 0 : value.hashCode());
        }
    }
    
    boolean equals(Object o1, Object o2)
    {
        if(o1 == null)
        {
            return o2 == null;
        }
        if(o2 == null)
        {
            return false;
        }
        return o1.equals(o2);
    }
}
