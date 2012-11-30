package org.objectledge.longops.impl;

import java.security.Principal;
import java.util.Date;

import org.objectledge.longops.LongRunningOperation;

class MutableLongRunningOperation
    implements LongRunningOperation
{
    private final String identifier;

    private final String code;

    private final String description;

    private final Principal user;

    private volatile int totalUnitsOfWork;

    private volatile int completedUnitsOfWork;

    private volatile boolean canceled;

    private final long startTime;

    private volatile long lastUpdateTime;

    private final Clock clock;

    MutableLongRunningOperation(String identifier, String code, String description, Principal user,
        int totalUnitsOfWork, Clock clock)
    {
        this.identifier = identifier;
        this.code = code;
        this.description = description;
        this.user = user;
        this.totalUnitsOfWork = totalUnitsOfWork;
        this.clock = clock;
        this.startTime = clock.currentTimeMillis();
    }

    void update(int completedUnitsOfWork)
    {
        this.completedUnitsOfWork = completedUnitsOfWork;
        lastUpdateTime = clock.currentTimeMillis();
    }

    void update(int completedUnitsOfWork, int totalUnitsOfWork)
    {
        this.completedUnitsOfWork = completedUnitsOfWork;
        this.totalUnitsOfWork = totalUnitsOfWork;
        lastUpdateTime = clock.currentTimeMillis();
    }

    void cancel()
    {
        canceled = true;
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
    public int getTotalUnitsOfWork()
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
    public Date getLastUpdateTime()
    {
        return new Date(lastUpdateTime);
    }

    @Override
    public Date getEstimatedEndTime()
    {
        final int currentUnitsOfWork = completedUnitsOfWork;
        if(totalUnitsOfWork > 0 && currentUnitsOfWork > 0)
        {
            final long now = clock.currentTimeMillis();
            final long eta = (long)(((float)(now - startTime) / currentUnitsOfWork) * totalUnitsOfWork);
            return new Date(now + eta);
        }
        else
        {
            return null;
        }
    }
}
