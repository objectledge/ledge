package org.objectledge.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Immutable set inspired by Scala collections.
 * <p>
 * It follows the general contract of {@code java.util.Set} with the exception that each mutative
 * operation creates a new set instance that represents the result of applying the operation to the
 * original set.
 * </P>
 * <p>
 * Immutable objects can be safely shared among threads, but care should taken to ensure correct
 * operation ordering using {@code volatile} modifier, or
 * {@code java.util.concurrent.atomic.AtomicReference}.
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 */
public interface ImmutableSet<E>
    extends Iterable<E>
{
    ImmutableSet<E> add(E e);

    ImmutableSet<E> addAll(Collection<? extends E> c);

    ImmutableSet<E> clear();

    boolean contains(Object o);

    boolean containsAll(Collection<? extends E> c);

    boolean isEmpty();

    Iterator<E> iterator();

    ImmutableSet<E> remove(Object o);

    ImmutableSet<E> removeAll(Collection<?> c);

    ImmutableSet<E> retainAll(Collection<?> c);

    int size();

    Object[] toArray();

    <T> T[] toArray(T[] a);
    
    Set<E> unmodifiableSet();
}
