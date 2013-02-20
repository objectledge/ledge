package org.objectledge.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

public class LazyValueTest
    extends TestCase
{
    // this many threads will be used in the test
    private static final int NUM_PROBES = 100;

    public void testLazyValue()
        throws InterruptedException, ExecutionException
    {
        // this is our stateful test object
        final AtomicInteger counter = new AtomicInteger(0);
        final LazyValue<Integer> value = new LazyValue<Integer>()
            {
                protected Integer compute()
                {
                    return counter.getAndIncrement();
                }
            };

        // ramping up threads may take some time. the barrier ensures that all probes will attempt
        // to retrieve the value concurrently
        final CyclicBarrier barrier = new CyclicBarrier(NUM_PROBES);
        List<Callable<Integer>> probes = new ArrayList<>();
        for(int i = 0; i < NUM_PROBES; i++)
        {
            probes.add(new Callable<Integer>()
                {
                    @Override
                    public Integer call()
                        throws Exception
                    {
                        barrier.await();
                        return value.get();
                    }
                });
        }

        // off go the probes
        ExecutorService executor = Executors.newFixedThreadPool(NUM_PROBES);
        List<Future<Integer>> results = executor.invokeAll(probes);

        // check that all probes have seen the counter value at 0, which is true for first
        // invocation of compute()
        for(int i = 0; i < NUM_PROBES; i++)
        {
            assertEquals(0, results.get(i).get().intValue());
        }
        // and that the counter was incremented only once
        assertEquals(1, counter.get());
    }
}
