package org.objectledge.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Immutable map inspired by Scala collections.
 * <p>
 * It follows the general contract of {@code java.util.Map} with the exception that each mutative
 * operation creates a new map instance that represents the result of applying the operation to the
 * original map.
 * </p>
 * <p>
 * Immutable objects can be safely shared among threads, but care should taken to ensure correct
 * operation ordering using {@code volatile} modifier, or
 * {@code java.util.concurrent.atomic.AtomicReference}.
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 */
public interface ImmutableMap<K, V>
{
    ImmutableMap<K, V> clear();

    boolean containsKey(Object o);

    boolean containsValue(Object o);

    Set<Map.Entry<K, V>> entrySet();

    V get(Object k);

    boolean isEmpty();

    Set<K> keySet();

    ImmutableMap<K, V> put(K k, V v);

    ImmutableMap<K, V> putAll(Map<? extends K, ? extends V> m);

    ImmutableMap<K, V> remove(Object k);

    int size();

    Collection<V> values();
    
    Map<K, V> unmodifiableMap();
}
