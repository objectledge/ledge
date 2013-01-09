package org.objectledge.longops.impl;

import java.security.Principal;
import java.util.Date;

import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationSecurityCallback;

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

    private final LongRunningOperationSecurityCallback securityCallback;

    MutableLongRunningOperation(String identifier, String code, String description, Principal user,
        int totalUnitsOfWork, Clock clock, LongRunningOperationSecurityCallback securityCallback)
    {
        this.identifier = identifier;
        this.code = code;
        this.description = description;
        this.user = user;
        this.totalUnitsOfWork = totalUnitsOfWork;
        this.clock = clock;
        this.securityCallback = securityCallback;
        this.startTime = clock.currentTimeMillis();
        this.lastUpdateTime = startTime;
    }

    LongRunningOperationSecurityCallback getSecurityCallback()
    {
        return securityCallback;
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
        final int curCompletedUnitsOfWork = completedUnitsOfWork;
        final int curTotalUnitsOfWork = totalUnitsOfWork;
        if(totalUnitsOfWork > 0 && curCompletedUnitsOfWork > 0)
        {
            final long now = clock.currentTimeMillis();
            final long estTotal = (long)(((float)(now - startTime) / curCompletedUnitsOfWork) * curTotalUnitsOfWork);
            return new Date(startTime + estTotal);
        }
        else
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return "mutable operation #" + identifier + " code: " + code;
    }
}
