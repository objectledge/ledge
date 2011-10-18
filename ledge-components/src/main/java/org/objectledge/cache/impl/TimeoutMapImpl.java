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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.objectledge.cache.DelayedUpdate;
import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.TimeoutMap;

/**
 * An implementation of timeout cache.
 *
 * <p>Expired items are removed as part of get()/put() method calls to take
 * advantage of sychronization performed by the client code. Cleanups occur no
 * more frequently than {@link #ttl} milliseconds.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: TimeoutMapImpl.java,v 1.2 2004-02-26 11:34:28 fil Exp $
 */
public class TimeoutMapImpl<K, V>
    extends WrappingMap<K, V>
    implements TimeoutMap<K, V>, ConfigurableMap<K, V>, DelayedUpdate
{
    
    // constants /////////////////////////////////////////////////////////////

    /** The default value of item time to live. (10s)*/
    public static final long TTL_DEFAULT = 10000;

    // instance variables ////////////////////////////////////////////////////

    /** The entry TTL. */
    private long ttl = TTL_DEFAULT;

    /** The DefaultCacheFactory. */
    private CacheFactorySPI caching;
    
    /** Time when last update (cache cleanup occured). */
    private long lastUpdate;    

    // initailization ////////////////////////////////////////////////////////

    /**
     * Constructs a timeout chache.
     */
    public TimeoutMapImpl()
    {
        super(Collections.synchronizedMap(new HashMap<K, WrappingEntry<V>>()));
        lastUpdate = -1;
    }
 
    // ConfigurableMap interface /////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void configure(CacheFactorySPI caching, String name, String config)
    {
        this.caching = caching;
        try
        {
            setTimeout(Integer.parseInt(config));
        }
        catch(NumberFormatException e)
        {
            throw new IllegalArgumentException("invalid timeout spec "+config);
        }
    }

    // TimeoutMap SPI interface //////////////////////////////////////////////

    /**
     * Sets the item time to live.
     *
     * @param timeoutMillis item time to live in milliseconds.
     */
    public void setTimeout(long timeoutMillis)
    {
        ttl = timeoutMillis;
        if(timeoutMillis < ttl)
        {
            scheduleUpdate();
        }
    }

    /**
     * Returns the item time to live.
     *
     * @return item time to live in milliseconds.
     */
    public long getTimeout()
    {
        return ttl;
    }

    // DelayedUpdate interface //////////////////////////////////////////////

    /**
     * Schedule an upcoming update.
     */
    public void scheduleUpdate()
    {
        long now = System.currentTimeMillis();
        if(lastUpdate > 0 && now - lastUpdate > ttl)
        {
            update();
        }
        else
        {
            caching.register(this);
        }
        if(lastUpdate < 0)
        {
            lastUpdate = now;
        }
    }

    /**
     * Cleanup the map.
     */
    public void update()
    {
        long now = System.currentTimeMillis();
        long limit = now - ttl;
        synchronized(delegate)
        {
            Iterator<Map.Entry<K, WrappingEntry<V>>> i = delegate.entrySet().iterator();
            while(i.hasNext())
            {
                Map.Entry<K, WrappingEntry<V>> mapEntry = i.next();
                Entry<V> entry = (Entry<V>)mapEntry.getValue();
                if(entry.expired(limit))
                {
                    i.remove();
                }
            }              
        }
        lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * {@inheritDoc}
     */
    public long getUpdateLatency()
    {
        return ttl;
    }

    // Entry ////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */    
    protected WrappingEntry<V> newWrappingEntry(V value)
    {
        return new Entry<V>(value);
    }

    /**
     * {@inheritDoc}
     */    
    private class Entry<T>
        extends WrappingEntry<T>
    {
        private long last;

        public Entry(T value)
        {
            super(value);
            last = System.currentTimeMillis();
        }
                
        public T getValue()
        {
            last = System.currentTimeMillis();
            return super.getValue();
        }
        
        public boolean expired(long limit)
        {
            return last <= limit;
        }
        
        public void touch()
        {
            last = System.currentTimeMillis();
            scheduleUpdate();
        }
    }
    
    // Map interface ////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public V put(K key, V value)
    {
        V old = super.put(key, value);
        scheduleUpdate();
        return old;
    }
}
