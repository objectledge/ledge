package org.objectledge.cache.spi;

/**
 * Cache implementation backed by Guava cache.
 * 
 * 
 * @author rafal.krzewski@caltha.pl
 *
 * @param <K> map key type.
 * @param <V> map value type.
 */
public interface GuavaCache<K, V>
    extends ConfigurableMap<K, V>, StatisticsMap<K, V>
{

}
