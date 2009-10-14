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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.SoftMap;

/**
 * An implementation of soft cache.
 *
 * <p>Dangling links cleanups occur as a part of get()/put() operations to
 * take advantage of synchronization performed by the client code. Cleanups
 * occur only if dangling link number exceeds a defined threshold.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: SoftMapImpl.java,v 1.4 2005-02-09 22:02:24 rafal Exp $
 */
public class SoftMapImpl
    extends DelegateMap
    implements SoftMap, ConfigurableMap
{
    // constants /////////////////////////////////////////////////////////////
    
    /** The default number of items to protect from GC. (0) */
    public static final int PROTECT_DEFAULT = 0;

    // instance variables ////////////////////////////////////////////////////

    /** Nubmer of items to protect. (0 to disable) */
    private int protect = PROTECT_DEFAULT;

    /** Current number of dangling links. */
    private int dangling;

    /** The protected items. */
    private Object[] ring;

    /** The next protection ring position. */
    private int pos;

    /** The reference queue. */
    private ReferenceQueue queue = new ReferenceQueue();

    // initailization ////////////////////////////////////////////////////////

    /**
     * Constructs a soft chache.
     */
    public SoftMapImpl()
    {
        super((Map)new HashMap());
        this.ring = new Object[protect];
        this.pos = 0;
    }

    // ConfigurableMap interface /////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void configure(CacheFactorySPI caching, String name, String config)
    {
        try
        {
            setProtect(Integer.parseInt(config));
        }
        catch(NumberFormatException e)
        {
            throw new IllegalArgumentException("invalid protection spec "+config);
        }
    }

    // SoftMap SPI interface /////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void setProtect(int protect)
    {
        int oldProtect = this.protect;
        Object[] oldRing = ring;
        this.protect = protect;
        ring = new Object[protect];
        if(protect > oldProtect)
        {
            System.arraycopy(oldRing, 0, ring, 0, oldRing.length);
        }
        else
        {
            System.arraycopy(oldRing, 0, ring, 0, ring.length);
            pos = pos % protect;
        }   
    }

    /**
     * {@inheritDoc}
     */
    public int getProtect()
    {
        return protect;
    }

    // Map interface /////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value)
    {
        cleanup();
        Collection values = delegate.values();
        Iterator i = values.iterator();
        if(value == null)
        {
            while(i.hasNext())
            {
                Entry ref = (Entry)i.next();
                if(ref.isValid() && ref.getValue() == null)
                {
                    return true;
                }
            }
        }
        else
        {
            while(i.hasNext())
            {
                Entry ref = (Entry)i.next();
                if(ref.isValid() && value.equals(ref.getValue()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set entrySet()
    {
        cleanup();
        return new AbstractSet()
            {
                public Iterator iterator()
                {
                    return getEntryIterator();
                }
                
                public boolean contains(Object o)
                {
                    return delegate.containsValue(o);
                }
                
                public boolean remove(Object o)
                {
                    unprotectValue(((Entry)o).get());
                    return delegate.values().remove(o);
                }
                
                public int size()
                {
                    return delegate.size();
                }
                
                public void clear()
                {
                    delegate.clear();
                    unprotectAll();
                }
            };
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Object key)
    {
        cleanup();
        Entry ref = (Entry)delegate.get(key);
        if(ref==null || ref.isEnqueued())
        {
            return null;
        }
        else
        {
            Object obj = ref.get();
            protectValue(obj);
            return obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object put(Object key, Object value)
    {
        cleanup();
        Entry newRef = new Entry(key, value);
        Entry oldRef = (Entry)delegate.put(key, newRef);
        protectValue(value);
        if(oldRef == null || oldRef.isEnqueued())
        {
            return null;
        }
        else
        {
            Object old = oldRef.get();
            unprotectValue(old);
            return old;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(Object key)
    {
        cleanup();
        Object old = null;
        Entry oldRef = (Entry)delegate.remove(key);
        if(oldRef != null)
        {
            old = oldRef.get();
            unprotectValue(old);
        }
        return old;
    }

    /**
     * {@inheritDoc}
     */
    public Collection values()
    {
        cleanup();
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
                    unprotectAll();
                }
            };
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    Iterator getValueIterator()
    {
        if(delegate.isEmpty())
        {
            return new EmptyIterator();
        }
        else
        {
            final Iterator i = delegate.values().iterator();
            return new Iterator()
                {
                    private Entry last = null;
                    
                    public boolean hasNext()
                    {
                        return i.hasNext();
                    }
                    
                    public Object next()
                    {
                        last = (Entry)i.next();
                        return last.get();
                    }
                    
                    public void remove()
                    {
                        i.remove();
                        unprotectValue(last.get());
                    }
                };
        }
    }

    /**
     * {@inheritDoc}
     */
    Iterator getEntryIterator()
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
                    private Entry last = null;
                    
                    public boolean hasNext()
                    {
                        return i.hasNext();
                    }
                    
                    public Object next()
                    {
                        Map.Entry mapEntry = (Map.Entry)i.next();
                        last = (Entry)mapEntry.getValue();
                        return mapEntry;
                    }
                    
                    public void remove()
                    {
                        i.remove();
                        unprotectValue(last.get());
                    }
                };
        }
    }

    /**
     * {@inheritDoc}
     */
    void protectValue(Object o)
    {
        if(protect > 0)
        {
            ring[pos] = o;
            pos = (pos + 1) % protect;
        }
    }

    /**
     * {@inheritDoc}
     */
    void unprotectValue(Object o)
    {
        if(o != null && protect > 0)
        {
            for(int i=0; i<protect; i++)
            {
                if(ring[i] == o)
                {
                    ring[i] = null;
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    void unprotectAll()
    {
        for(int i=0; i<protect; i++)
        {
            ring[i] = null;
        }
        pos = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup()
    {
        int size = delegate.size();
        if(size > 0 && dangling > size / 2)
        {
            while(true)
            {
                Entry ref = (Entry)queue.poll();
                if(ref == null)
                {
                    return;
                }
                delegate.remove(ref.getKey());
                dangling--;
            }
        }
    }   
    
    // inner classes /////////////////////////////////////////////////////////

    /**
     * Entry
     */
    private class Entry
        extends SoftReference
        implements Map.Entry
    {
        private Object key;

        public Entry(Object key, Object value)
        {
            super(value, queue);
            this.key = key;
        }

        public boolean enqueue()
        {
            dangling++;
            return super.enqueue();
        }

        public Object get()
        {
            Object o = super.get();
            if(o != null)
            {
                protectValue(o);
            }
            return o;
        }
        
        public Object getKey()
        {
            return key;
        }
        
        public Object getValue()
        {
            return get();
        }
        
        public Object setValue(Object value)
        {
            throw new UnsupportedOperationException(
                "setting values through entry set is not supported"); 
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean equals(Object o)
        {
            Entry e1 = this;
            Entry e2 = (Entry)o;
            return (e1.getKey()==null ? e2.getKey()==null :
                    e1.getKey().equals(e2.getKey())) && 
                (e1.getValue()==null ? e2.getValue()==null :
                 e1.getValue().equals(e2.getValue()));
        }
        
        /**
         * {@inheritDoc}
         */
        public int hashCode()
        {
            return super.hashCode();
        }
        
        boolean isValid()
        {
            return !isEnqueued();
        }
    }
}
