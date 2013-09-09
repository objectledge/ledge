package org.objectledge.threads;

public interface ThreadPool
{

    /**
     * Run the worker task.
     * 
     * @param task the task to run.
     */
    public abstract void runWorker(Task task);

    /**
     * Run the daemon task.
     * 
     * @param task the task to run.
     */
    public abstract void runDaemon(Task task);

}
