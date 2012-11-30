package org.objectledge.longops;

/**
 * Thrown from {@link LongRunningOperationRegistry#update(LongRunningOperation, int)} and
 * {@link LongRunningOperationRegistry#update(LongRunningOperation, int, int)} to indicate that the
 * operation has been concurrently cancelled.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class OperationCancelledException
    extends Exception
{
    public OperationCancelledException(String message)
    {
        super(message);
    }
}
