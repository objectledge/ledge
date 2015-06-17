package org.objectledge.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ImmutableSet implementation based on {@code java.util.HashSet}.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class ImmutableHashSet<E>
    implements ImmutableSet<E>
{
    private final Set<E> delegate;

    public ImmutableHashSet()
    {
        delegate = new HashSet<E>();
    }

    public ImmutableHashSet(Collection<? extends E> c)
    {
        delegate = new HashSet<E>(c);
    }

    private ImmutableHashSet(Set<E> delegate)
    {
        this.delegate = delegate;
    }

    private Set<E> cloneDelegate()
    {
        return new HashSet<E>(delegate);
    }

    @Override
    public ImmutableSet<E> add(E e)
    {
        Set<E> newDelegate = cloneDelegate();
        newDelegate.add(e);
        return new ImmutableHashSet<E>(newDelegate);
    }

    @Override
    public ImmutableSet<E> addAll(Collection<? extends E> c)
    {
        Set<E> newDelegate = cloneDelegate();
        newDelegate.addAll(c);
        return new ImmutableHashSet<E>(newDelegate);
    }

    @Override
    public ImmutableSet<E> clear()
    {
        return new ImmutableHashSet<E>(Collections.<E> emptySet());
    }

    @Override
    public boolean contains(Object o)
    {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<? extends E> c)
    {
        return delegate.containsAll(c);
    }

    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<E> iterator()
    {
        return new Iterator<E>()
            {
                private final Iterator<E> i = delegate.iterator();

                @Override
                public boolean hasNext()
                {
                    return i.hasNext();
                }

                @Override
                public E next()
                {
                    return i.next();
                }

                @Override
                public void remove()
                {
                    throw new UnsupportedOperationException("remove operation is not supported");
                }
            };
    }

    @Override
    public ImmutableSet<E> remove(Object o)
    {
        Set<E> newDelegate = cloneDelegate();
        newDelegate.remove(o);
        return new ImmutableHashSet<E>(newDelegate);
    }

    @Override
    public ImmutableSet<E> removeAll(Collection<?> c)
    {
        Set<E> newDelegate = cloneDelegate();
        newDelegate.removeAll(c);
        return new ImmutableHashSet<E>(newDelegate);
    }

    @Override
    public ImmutableSet<E> retainAll(Collection<?> c)
    {
        Set<E> newDelegate = cloneDelegate();
        newDelegate.retainAll(c);
        return new ImmutableHashSet<E>(newDelegate);
    }

    @Override
    public int size()
    {
        return delegate.size();
    }

    @Override
    public Object[] toArray()
    {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return delegate.toArray(a);
    }

    @Override
    public Set<E> unmodifiableSet()
    {
        return Collections.unmodifiableSet(delegate);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof ImmutableHashSet)
        {
            @SuppressWarnings("unchecked")
            final ImmutableHashSet<E> that = (ImmutableHashSet<E>)o;
            return delegate.equals(that.delegate);
        }
        else
        {
            return false;
        }
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

    public static <E> ImmutableSet<E> singletonSet(E singletonElement)
    {
        return new ImmutableHashSet<>(Collections.singleton(singletonElement));
    }
}
