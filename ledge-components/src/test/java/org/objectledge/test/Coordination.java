package org.objectledge.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Coordinates execution of a number of {@link Participant}s.
 * <P>
 * The following methods will be executed in a separate thread for each of the participants:
 * <UL>
 * <LI>setup()</LI>
 * <LI>step(1)</LI>
 * <LI>step(2) etc until {@code step} parameter passed to {@code Coordination} constructor</LI>
 * <LI>cleanup()</LI>
 * </UL>
 * It is guaranteed that for each value of {@code i > 1} @{code step(i)} may be called on any
 * participant only after {@code step(i - 1)} finished executing on all participants.
 * </P>
 * <p>
 * Throwing of an exception by the participant {@link Participant} from {@code} setup of
 * {@code step} methods disrupts the execution, and all other participants will fail with
 * TimeoutException.
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class Coordination
{
    /**
     * A Coordination participant.
     */
    public abstract static class Participant
    {
        /**
         * Will be invoked once before first execution of {@link #step(int)}.
         * <P>
         * Throwing exception from this method will disrupt other participants.
         * </p>
         */
        public void setup()
            throws Exception
        {

        }

        /**
         * Will be invoked with parameters in range [1..Coordination.steps]. *
         * <P>
         * Throwing exception from this method will disrupt other participants.
         * </p>
         * 
         * @param num current step number.
         */
        public abstract void step(int num)
            throws Exception;

        /**
         * Will be invoked once, after {@link #step(int)} terminates by throwing an exception, or
         * after all steps are completed.
         * <P>
         * Throwing exception from this method will be reported by {@link Coordination#run()}, but
         * will not disrupt other participants. If cleanup failure can be safely ignored, consider
         * suppressing the exception.
         * </p>
         */
        public void cleanup()
            throws Exception
        {

        }
    }

    /**
     * A Coordination monitor.
     */
    public interface Monitor
    {
        /**
         * Will be invoked before each {@link Participant#step(int)} invocation.
         * 
         * @param participant the participant.
         * @param step step number.
         */
        void before(Participant participant, int step);
    }

    private final int steps;

    private final Participant[] participants;

    private final Monitor monitor;

    private final CyclicBarrier barrier;

    private final long timeout;

    private final TimeUnit timeUnit;

    /**
     * Creates a new Coordination object.
     * 
     * @param steps number of steps to be executed.
     * @param timeout maximum execution time of each step.
     * @param timeUnit TimeUnit of {@code timeout} parameter.
     * @param monitor an optional execution monitor, may be {@code null}.
     * @param participants the participants.
     */
    public Coordination(int steps, long timeout, TimeUnit timeUnit, Monitor monitor,
        Participant... participants)
    {
        this.steps = steps;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.monitor = monitor;
        this.participants = participants;
        barrier = new CyclicBarrier(participants.length);
    }

    /**
     * Execute the coordinated run.
     * 
     * @return exceptions thrown by {@link Participant} methods, or {@code TimeoutException}s if
     *         participants were not able to reach coordination checkpoints in the specified time.
     */
    public Collection<Exception> run()
    {
        ExecutorService executor = Executors.newFixedThreadPool(participants.length);
        try
        {
            List<Future<Exception>> results = new ArrayList<>();
            for(Participant participant : participants)
            {
                results.add(executor.submit(new Helper(barrier, participant, steps)));
            }
            List<Exception> exceptions = new ArrayList<>();
            for(Future<Exception> result : results)
            {
                try
                {
                    Exception e = result.get(timeout * steps, timeUnit);
                    if(e != null)
                    {
                        exceptions.add(e);
                    }
                }
                catch(Exception e)
                {
                    exceptions.add(e);
                }
            }
            return exceptions;
        }
        finally
        {
            executor.shutdownNow();
        }
    }

    private class Helper
        implements Callable<Exception>
    {
        private final Participant participant;

        public Helper(CyclicBarrier barrier, Participant participant, int steps)
        {
            this.participant = participant;
        }

        @Override
        public Exception call()
        {
            for(int i = 1; i <= steps; i++)
            {
                try
                {
                    if(monitor != null)
                    {
                        monitor.before(participant, i);
                    }
                    participant.step(i);
                    barrier.await(timeout, timeUnit);
                }
                catch(Exception e)
                {
                    try
                    {
                        participant.cleanup();
                    }
                    catch(Exception ce)
                    {
                        e.addSuppressed(ce);
                    }
                    return e;
                }
            }
            try
            {
                participant.cleanup();
            }
            catch(Exception ce)
            {
                return ce;
            }
            return null;
        }
    }
}
