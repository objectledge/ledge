package org.objectledge.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A lazily computed value. {@link #compute()} method will be called only once, by the first thread
 * that calls {@link #get()} method. If any other threads call {@link #get()} before
 * {@link #compute()} completes, they will be blocked. Otherwise, they will return the previously
 * computed value immediately.
 * 
 * @author rafal.krzewski@caltha.pl
 * @param <T> type of the value.
 */
public abstract class LazyValue<T>
{
    private final AtomicReference<FutureTask<T>> futureRef = new AtomicReference<>();

    /**
     * Returns the value, calling {@link #compute} if necessary.
     * 
     * @return the value.
     * @throws InterruptedException if the calling thread is interrupted while waiting for
     *         completion of value computation in another thread.
     * @throws ExecutionException when {@link #compute()} invocation failed. Use
     *         {@link Exception#getCause()} to examine the actual exception.
     */
    public final T get()
        throws InterruptedException, ExecutionException
    {
        FutureTask<T> future = new FutureTask<>(new Callable<T>()
            {
                public T call()
                    throws Exception
                {
                    return compute();
                }
            });
        if(futureRef.compareAndSet(null, future))
        {
            future.run();
        }
        else
        {
            future = futureRef.get();
        }
        return future.get();
    }

    /**
     * Computes the value. This method will be called only once for a {@code LazyValue} instance.
     * 
     * @return the value.
     * @throws Exception when computation fails
     */
    protected abstract T compute()
        throws Exception;
}
