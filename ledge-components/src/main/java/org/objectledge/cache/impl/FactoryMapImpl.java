//
// Copyright (c) 2003-2008, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//   this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//   this list of conditions and the following disclaimer in the documentation 
//   and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//   nor the names of its contributors may be used to endorse or promote products 
//   derived from this software without specific prior written permission.
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

package org.objectledge.cache.impl;

import java.util.Map;

import org.objectledge.cache.ValueFactory;
import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.FactoryMap;

/**
 * An implementation of factory map.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: FactoryMapImpl.java,v 1.4 2008-01-02 22:36:37 rafal Exp $
 */
public class FactoryMapImpl<K, V>
    extends DelegateMap<K, V>
    implements FactoryMap<K, V>, ConfigurableMap<K, V>
{
    // member objects ////////////////////////////////////////////////////////

    /** The value factory. */
    private ValueFactory<K, V> factory;
    
    // initialization ///////////////////////////////////////////////////////

    /**
     * Constructs a FactoryMapImpl.
     */
    public FactoryMapImpl()
    {
        super();
    }
    
    /**
     * Constructs a FactoryMapImpl.
     *
     * @param factory the value factory.
     * @param delegate the storage delegate
     */
    public FactoryMapImpl(ValueFactory<K, V> factory, Map<K, V> delegate)
    {
        super(delegate);
        this.factory = factory;
    }

    // ConfigurableMap interface /////////////////////////////////////////////

    /**
     * Configures the map according to properties file settings.
     *
     * @param caching the cache system.
     * @param name the name of the map.
     * @param config an opaque String.
     */
    public void configure(CacheFactorySPI caching, String name, String config)
    {
        //TODO config/name -> name/config
        this.factory = caching.getValueFactory(config, name);
    }

    // FactoryMap SPI interface //////////////////////////////////////////////

    /**
     * Sets the <code>ValueFactory</code> to use.
     *
     * @param factory the factory.
     */
    public void setFactory(ValueFactory<K, V> factory)
    {
        this.factory = factory;
    }

    /**
     * Returns the current factory in use.
     *
     * @return the factory.
     */
    public ValueFactory<K, V> getFactory()
    {
        return factory;
    }
    
    // Map interface /////////////////////////////////////////////////////////

    /**
     * Returns the value to which this map maps the specified key.
     *
     * @param key the key.
     * @return the value
     */
    public V get(Object key)
    {
        V value = delegate.get(key);
        if(value == null && factory != null)
        {
            value = factory.getValue((K)key);
            if(value != null)
            {
                delegate.put((K)key, value);
            }
        }
        return value;
    }
}
