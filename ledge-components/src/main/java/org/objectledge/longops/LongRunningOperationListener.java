package org.objectledge.longops;

public interface LongRunningOperationListener
{
    void receive(LongRunningOperationEvent event);
}
