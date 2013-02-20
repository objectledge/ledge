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
        FutureTask<T> future = futureRef.get();
        if(future == null)
        {
            // prepare to compute the value in this thread
            future = new FutureTask<>(new Callable<T>()
                {
                    public T call()
                        throws Exception
                    {
                        return compute();
                    }
                });
            if(futureRef.compareAndSet(null, future))
            {
                // our thread is in the lead, proceed with computation
                future.run();
            }
            else
            {
                // another thread beat us to it, wait for their computation to complete
                future = futureRef.get();
            }
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
