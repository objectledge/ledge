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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Map implementation that provides wrapping of values additonal objects,
 * and mapping access tracking.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: WrappingMap.java,v 1.3 2005-02-09 22:02:24 rafal Exp $
 */
public abstract class WrappingMap 
    extends DelegateMap
{
    // initialization ///////////////////////////////////////////////////////

    /**
     * Map constructor.
     * 
     * @param delegate the delegate map.
     */    
    public WrappingMap(Map delegate)
    {
        super(delegate);
    }

    // Map interface ////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public Object get(Object key)
    { 
        WrappingEntry entry = (WrappingEntry)delegate.get(key);
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
    public Object put(Object key, Object value)
    {
        WrappingEntry entry = newWrappingEntry(value);
        entry = (WrappingEntry)delegate.put(key, entry);
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
    protected abstract WrappingEntry newWrappingEntry(Object value);
    
    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value)
    {
        Iterator i = delegate.entrySet().iterator();
        while(i.hasNext())
        {
            Map.Entry e = (Map.Entry)i.next();
            if(equals(((WrappingEntry)e.getValue()).getValue(), value))
            {
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
            WrappingEntry we = (WrappingEntry)delegate.get(key);
            we.touch();
            return true;
        }
        else
        {
            return false;
        }
    }

    // entry set 

    private Set entrySet;
    
    /**
     * {@inheritDoc}
     */
    public Set entrySet()
    {
        Set es = entrySet;
        return (es != null ? es : (entrySet = new EntrySet()));
    }

    /**
     * EntrySet 
     *
     */    
    private class EntrySet
        extends AbstractSet
    {
        public int size()
        {
            return delegate.size();
        }

        public Iterator iterator()
        {
            return newEntryIterator();
        }
        
        public boolean contains(Object entry)
        {
            Object value = get(((Map.Entry)entry).getKey());
            return WrappingMap.this.equals(value, ((Map.Entry)entry).getValue());
        }
        
        public boolean remove(Object entry)
        {
            Object key = ((Map.Entry)entry).getKey();
            boolean present = delegate.containsKey(key);
            delegate.remove(key);
            return present;
        }
        
        public void clear()
        {
            delegate.clear();
        }
    }
    
    Iterator newEntryIterator()
    {
        return new EntryIterator();
    }
    
    /**
     * Entry iterator.
     * 
     */
    private class EntryIterator
        extends WrappingMapIterator
    {
        public Object next()
        {
            Map.Entry me = nextEntry();
            WrappingEntry we = (WrappingEntry)me.getValue();
            return new Entry(me.getKey(), we.getValue());
        }
    }

    // values
    
    private Collection values;
    
    /**
     * {@inheritDoc}
     */    
    public Collection values()
    {
        Collection v = values;
        return (v != null ? v : (values = new Values()));
    }

    /**
     * {@inheritDoc}
     */    
    private class Values
        extends AbstractCollection
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
        
        public Iterator iterator()
        {
            return newValuesIterator();
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    Iterator newValuesIterator()
    {
        return new ValuesIterator();
    }
    
    /**
     * {@inheritDoc}
     */
    private class ValuesIterator
        extends WrappingMapIterator
    {
        public Object next()
        {
            Map.Entry me = nextEntry();
            WrappingEntry we = (WrappingEntry)me.getValue();
            return we.getValue();
        }
    }
    
    // inner classes ////////////////////////////////////////////////////////
    /**
     * Wrapping entry.
     * 
     */
    protected class WrappingEntry
    {
        private Object value;
        
        /**
         * Creates a new WrappingEntry instance.
         * 
         * @param value the value.
         */
        public WrappingEntry(Object value)
        {
            this.value = value;
        }
        
        /**
         * Returns the entry's value.
         * 
         * @return the object
         */
        public Object getValue()
        {
            return value;
        }
        
        /**
         * Updates the entry's timestamp.
         */
        public void touch()
        {
        }
    }
    
    /**
     * Customized Iterator implementation for the WrappingMap.
     */
    protected abstract class WrappingMapIterator
        implements Iterator
    {
        private Iterator i;
       
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
        protected Map.Entry nextEntry()
        {
            Map.Entry me = (Map.Entry)i.next();
            WrappingEntry we = (WrappingEntry)me.getValue();
            we.touch();
            return me;
        }
    }
    
    /**
     * Entry
     */
    private class Entry
        implements Map.Entry
    {
        private Object key;
        
        private Object value;

        public Entry(Object key, Object value)
        {
            this.key = key;
            this.value = value;
        }

        public Object getKey()
        {
            return key;
        }

        public Object getValue()
        {
            return value;
        }

        public Object setValue(Object value)
        {
            Object old = value;
            this.value = value;
            return old;
        }

        public boolean equals(Object o) 
        {
            if (!(o instanceof Map.Entry))
            {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
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
