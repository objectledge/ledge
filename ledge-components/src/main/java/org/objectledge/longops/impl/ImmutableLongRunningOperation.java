package org.objectledge.longops.impl;

import java.security.Principal;
import java.util.Date;

import org.objectledge.longops.LongRunningOperation;

public class ImmutableLongRunningOperation
    implements LongRunningOperation
{
    private final LongRunningOperation delegate;

    ImmutableLongRunningOperation(LongRunningOperation op)
    {
        this.delegate = op;
    }

    @Override
    public String getIdentifier()
    {
        return delegate.getIdentifier();
    }

    @Override
    public String getCode()
    {
        return delegate.getCode();
    }

    @Override
    public String getDescription()
    {
        return delegate.getDescription();
    }

    @Override
    public Principal getUser()
    {
        return delegate.getUser();
    }

    @Override
    public int getTotalUnitsOfWork()
    {
        return delegate.getTotalUnitsOfWork();
    }

    @Override
    public int getCompletedUnitsOfWork()
    {
        return delegate.getCompletedUnitsOfWork();
    }

    @Override
    public boolean isCanceled()
    {
        return delegate.isCanceled();
    }

    @Override
    public Date getStartTime()
    {
        return delegate.getStartTime();
    }

    @Override
    public Date getLastUpdateTime()
    {
        return delegate.getLastUpdateTime();
    }

    @Override
    public Date getEstimatedEndTime()
    {
        return delegate.getEstimatedEndTime();
    }
}
