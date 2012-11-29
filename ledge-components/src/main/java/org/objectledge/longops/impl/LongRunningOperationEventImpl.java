package org.objectledge.longops.impl;

import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationEvent;

public class LongRunningOperationEventImpl
    implements LongRunningOperationEvent
{
    private final Type type;

    private final LongRunningOperation operation;

    LongRunningOperationEventImpl(Type type, LongRunningOperation operation)
    {
        this.type = type;
        this.operation = operation;
    }

    @Override
    public Type getType()
    {
        return type;
    }

    @Override
    public LongRunningOperation getOperation()
    {
        return operation;
    }
}
