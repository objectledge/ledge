package org.objectledge.longops;

import java.security.Principal;

/**
 * Allows the application to express security constraints on the long running operation.
 * <p>
 * Callback may be provided while registering a new long running application. When provided, the
 * callback will be invoked by {@link LongRunningOperationRegistry#getActiveOperations(Principal, Principal, Principal)} and
 * {@link LongRunningOperationRegistry#cancel(LongRunningOperation, Principal)}.
 */
public interface LongRunningOperationSecurityCallback
{
    /**
     * Check whether the specified user can view the representation of the specified operation.
     */
    boolean canView(LongRunningOperation operation, Principal requestor);

    /**
     * Check whether the specified user can cancel the specified operation.
     */
    boolean canCancel(LongRunningOperation operation, Principal requestor);
}