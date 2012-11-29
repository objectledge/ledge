package org.objectledge.longops;

public interface LongRunningOperationEvent
{
    public enum Type
    {
        REGISTERED, UPDATED, CANCELED, UNREGISTERED
    }

    Type getType();

    LongRunningOperation getOperation();
}
