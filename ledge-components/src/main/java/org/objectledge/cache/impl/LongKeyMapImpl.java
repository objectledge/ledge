package org.objectledge.cache.impl;

import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;

import bak.pcj.adapter.LongKeyMapToMapAdapter;
import bak.pcj.map.LongKeyMap;

public abstract class LongKeyMapImpl
    extends DelegateMap
    implements ConfigurableMap
{

    public void configure(CacheFactorySPI caching, String name, String config)
    {
        String[] configTokens = config.split(",");
        double loadFactor = 0.75d;
        int capacity = 11;
        double growthFactor = 1.0d;
        if(configTokens.length >= 1)
        {
            capacity = Integer.parseInt(configTokens[0]);
        }
        if(configTokens.length >= 1)
        {
            loadFactor = Double.parseDouble(configTokens[1]);
        }
        if(configTokens.length >= 3)
        {
            growthFactor = Double.parseDouble(configTokens[2]);
        }
        setDelegate(new LongKeyMapToMapAdapter(newLongKeyMap(capacity, loadFactor, growthFactor)));
    }

    protected abstract LongKeyMap newLongKeyMap(int capacity, double loadFactor, double growthFactor);
}
