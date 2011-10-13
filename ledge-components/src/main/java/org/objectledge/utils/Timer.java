/**
 * 
 */
package org.objectledge.utils;

/**
 * Simple timer class that can be used for application profiling
 * 
 * @author rafal
 */
public class Timer
{
    private long time;

    /**
     * Create a new Timer instance.
     */
    public Timer()
    {
        reset();
    }

    /**
     * Reset the timer.
     */
    public void reset()
    {
        time = System.currentTimeMillis();
    }

    /**
     * Return the numbers of milliseconds elapsed since this Timer instance was created or {@link #reset()} was last called.
     *
     * @return number of milliseconds since creation or reset.
     */
    public long getElapsedMillis()
    {
        long lastTime = time;
        reset();
        return time - lastTime;
    }

    /**
     * Return the numbers of seconds elapsed since this Timer instance was created or {@link #reset()} was last called.
     *
     * @return number of seconds since creation or reset.
     */
    public long getElapsedSeconds()
    {
        return getElapsedMillis() / 1000;
    }
}
