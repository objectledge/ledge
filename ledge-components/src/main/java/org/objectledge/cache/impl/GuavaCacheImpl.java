package org.objectledge.cache.impl;

import java.text.NumberFormat;

import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.GuavaCache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;

public class GuavaCacheImpl<K, V>
    extends DelegateMap<K, V>
    implements GuavaCache<K, V>
{
    private CacheStats stats;
    
    private String name;

    @Override
    public void configure(CacheFactorySPI spi, String name, String config)
    {
        Cache<K, V> gCache = CacheBuilder.from(config).build();
        this.stats = gCache.stats();
        this.name = name;
        setDelegate(gCache.asMap());
        spi.addStatisticsMap(this);
    }

    @Override
    public String getStatistics()
    {
        StringBuilder buff = new StringBuilder();
        buff.append(name).append(": ");
        buff.append(delegate.size()).append(" items, ");
        buff.append(stats.requestCount()).append(" requests");
        if(stats.requestCount() != 0)
        {
            buff.append(", ").append(stats.requestCount()).append(" hits, ");
            buff.append(stats.missCount()).append(" misses, ");
            NumberFormat percent = NumberFormat.getPercentInstance();
            buff.append(percent.format(stats.hitRate() * 100)).append(" hit ratio");
        }
        buff.append("\n");
        return buff.toString();
    }

    @Override
    public int getRequestCount()
    {
        return (int)stats.requestCount();
    }

    @Override
    public int getHitCount()
    {        
        return (int)stats.requestCount();
    }

    @Override
    public void resetCount()
    {
        // not supported
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

}
