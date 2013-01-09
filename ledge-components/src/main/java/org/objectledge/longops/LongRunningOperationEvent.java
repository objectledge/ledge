package org.objectledge.longops;

public interface LongRunningOperationEvent
{
    public enum Type
    {
        REGISTERED, UPDATED, CANCELLED, UNREGISTERED
    }

    Type getType();

    LongRunningOperation getOperation();
}
