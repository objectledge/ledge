package org.objectledge.longops;

import java.security.Principal;
import java.util.Date;

/**
 * A representation of a long running operation.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public interface LongRunningOperation
{
    /**
     * A system assigned unique identifier of the operation.
     */
    String getIdentifier();

    /**
     * A code used to group related operations together.
     */
    String getCode();

    /**
     * Human-readable description of a specific operation.
     */
    String getDescription();

    /**
     * User that initiated the operation.
     */
    Principal getUser();

    /**
     * Total number of units of work to be performed in this operation.
     */
    int getTotalUnitOfWork();

    /**
     * Number of units of work completed in this operation until now.
     */
    int getCompletedUnitsOfWork();

    /**
     * Has the user requested to cancel this operation?
     */
    boolean isCanceled();

    /**
     * The time when operation was initiated.
     */
    Date getStartTime();

    /**
     * Time the last time operation progress information was updated.
     */
    Date getLastUpdateTime();

    /**
     * The estimated time of completion of the operation.
     * 
     * @return returns {@code null} no work has been completed yet.
     */
    Date getEstimatedEndTime();
}
