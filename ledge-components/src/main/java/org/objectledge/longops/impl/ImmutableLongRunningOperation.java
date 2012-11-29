package org.objectledge.longops.impl;

import java.security.Principal;
import java.util.Date;

import org.objectledge.longops.LongRunningOperation;

public class ImmutableLongRunningOperation
    implements LongRunningOperation
{
    private final String identifier;

    private final String code;

    private final String description;

    private final Principal user;

    private final int totalUnitsOfWork;

    private final int completedUnitsOfWork;

    private final boolean canceled;

    private final long startTime;

    private final long estimatedEndTime;

    ImmutableLongRunningOperation(LongRunningOperation op)
    {
        identifier = op.getIdentifier();
        code = op.getCode();
        description = op.getDescription();
        user = op.getUser();
        totalUnitsOfWork = op.getTotalUnitOfWork();
        completedUnitsOfWork = op.getCompletedUnitsOfWork();
        canceled = op.isCanceled();
        startTime = op.getStartTime().getTime();
        final Date estEndTime = op.getEstimatedEndTime();
        estimatedEndTime = estEndTime == null ? -1l : estEndTime.getTime();
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public Principal getUser()
    {
        return user;
    }

    @Override
    public int getTotalUnitOfWork()
    {
        return totalUnitsOfWork;
    }

    @Override
    public int getCompletedUnitsOfWork()
    {
        return completedUnitsOfWork;
    }

    @Override
    public boolean isCanceled()
    {
        return canceled;
    }

    @Override
    public Date getStartTime()
    {
        return new Date(startTime);
    }

    @Override
    public Date getEstimatedEndTime()
    {
        return estimatedEndTime == -1l ? null : new Date(estimatedEndTime);
    }
}
