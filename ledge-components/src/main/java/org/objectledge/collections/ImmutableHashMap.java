package org.objectledge.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ImmutableSet implementation based on {@code java.util.HashSet}.
 * <P>
 * Note: entry set iterator of this implementation is not memory efficient. It creates a view object
 * over each map entry to ensure immutability during iteration. With large maps, this could place a
 * significant load on the garbage collector. Using Escape Analysis in the JVM (enabled with
 * -XX:+DoEscapeAnalysis as of JDK 7) can alleviate this problem.
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class ImmutableHashMap<K, V>
    implements ImmutableMap<K, V>
{
    private final Map<K, V> delegate;

    public ImmutableHashMap()
    {
        this.delegate = new HashMap<K, V>();
    }

    public ImmutableHashMap(Map<? extends K, ? extends V> m)
    {
        this.delegate = new HashMap<K, V>(m);
    }

    private ImmutableHashMap(Map<K, V> delegate, Object dummy)
    {
        this.delegate = delegate;
    }

    private Map<K, V> cloneDelegate()
    {
        return new HashMap<K, V>(delegate);
    }

    @Override
    public ImmutableMap<K, V> clear()
    {
        return new ImmutableHashMap<K, V>();
    }

    @Override
    public boolean containsKey(Object o)
    {
        return delegate.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o)
    {
        return delegate.containsValue(o);
    }

    @Override
    public V get(Object k)
    {
        return delegate.get(k);
    }

    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    @Override
    public ImmutableMap<K, V> put(K k, V v)
    {
        Map<K, V> newDelegate = cloneDelegate();
        newDelegate.put(k, v);
        return new ImmutableHashMap<K, V>(newDelegate, null);
    }

    @Override
    public ImmutableMap<K, V> putAll(Map<? extends K, ? extends V> m)
    {
        Map<K, V> newDelegate = cloneDelegate();
        newDelegate.putAll(m);
        return new ImmutableHashMap<K, V>(newDelegate, null);
    }

    @Override
    public ImmutableMap<K, V> remove(Object k)
    {
        Map<K, V> newDelegate = cloneDelegate();
        newDelegate.remove(k);
        return new ImmutableHashMap<K, V>(newDelegate, null);
    }

    @Override
    public int size()
    {
        return delegate.size();
    }

    @Override
    public Collection<V> values()
    {
        return Collections.unmodifiableCollection(delegate.values());
    }
    
    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return unmodifiableMap().entrySet();
    }

    @Override
    public Map<K, V> unmodifiableMap()
    {
        return Collections.unmodifiableMap(delegate);
    }

    @Override
    public boolean equals(Object o)
    {
        return delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return delegate.hashCode();
    }

    @Override
    public String toString()
    {
        return delegate.toString();
    }
}
