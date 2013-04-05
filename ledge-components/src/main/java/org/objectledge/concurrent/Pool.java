package org.objectledge.concurrent;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple fixed capacity object pool.
 * 
 * @param <P> type of the pooled objects.
 * @author rafal.krzewski@caltha.pl
 */
public abstract class Pool<P>
{
    private final int capacity;

    private final AtomicInteger size = new AtomicInteger(0);

    private final BlockingDeque<P> pool = new LinkedBlockingDeque<>();

    private final long timeout;

    private final TimeUnit unit;

    /**
     * Create a new pool with specified capacity.
     * 
     * @param capacity maximum pool capacity.
     * @param timeout the maximum time to wait for an object to become available when the pool is
     *        exhausted.
     * @param unit unit for the timeout parameter.
     */
    protected Pool(int capacity, long timeout, TimeUnit unit)
    {
        this.capacity = capacity;
        this.timeout = timeout;
        this.unit = unit;
    }

    /**
     * Called when no objects are available in the pool, but maximum pool capacity has not yet been
     * reached.
     * 
     * @return a new object.
     */
    protected abstract P make()
        throws Exception;

    /**
     * Retrieves an object from the pool. When no objects are available either a new object will be
     * created using {@link #make()} method, or if pool capacity has been already reached the thread
     * will wait for an object to become available for the time specified in the pool's constructor.
     * 
     * @return {@code null} if no objects became available during the specified time.
     * @throws InterruptedException if the thread is interrupted while waiting for an object.
     * @throws InvocationTargetException if {@link #make()} or throw an exception.
     */
    public final P take()
        throws InterruptedException, InvocationTargetException
    {
        P item = pool.pollFirst();
        if(item == null)
        {
            final int curSize = size.get();
            if(curSize < capacity)
            {
                if(size.compareAndSet(curSize, curSize + 1))
                {
                    try
                    {
                        return make();
                    }
                    catch(Exception e)
                    {
                        size.decrementAndGet();
                        throw new InvocationTargetException(e);
                    }
                }
            }
            item = pool.pollFirst(timeout, unit);
        }
        return item;
    }

    /**
     * Release taken object back to the pool. Make sure to call this method in a finally block so
     * that unexpected exception don't cause pool exhaustion.
     * 
     * @param item object to be released.
     * @throws NullPointerExceptoin if item is {@code null}
     */
    public final void release(P item)
    {
        pool.addLast(item);
    }
}
