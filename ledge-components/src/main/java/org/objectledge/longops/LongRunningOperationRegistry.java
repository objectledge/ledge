package org.objectledge.longops;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

/**
 * A registry of long running operations that may be monitored and canceled by the users.
 * <p>
 * Expected lifecycle of an operation looks as follows:
 * <ul>
 * <li>Application registers an operation using {@link #register(String, String, Principal, int)}.
 * It retains the returned reference to update the status of the operation later.</li>
 * <li>Operation starts executing. Before processing each unit of work, the application calls
 * {@link LongRunningOperation#isCanceled()} to check if the user has requested the operation to be
 * aborted. If so, it aborts the processing and calls {@link #unregister(LongRunningOperation)} to
 * discard the operation representation. Otherwise it proceeds to process the unit of work.
 * Afterwards it calls {@link #update(LongRunningOperation, int)} to inform about the advancement.
 * Notice that {@link IllegalStateException} may be thrown by that method in case when the operation
 * has been concurrently cancelled. The application should handle this exception by aborting the
 * processing loop and unregistering the operation.</li>
 * <li>After all units of work have been processed, {@link #unregister(LongRunningOperation)} is
 * called to discard the operation data.</li>
 * </ul>
 * While the operation is executing, other threads may call
 * {@link #getActiveOperations(Principal, Principal, Principal)} and
 * {@link #getActiveOperations(String, Principal)} to inquire about active operations and their
 * progress. The user may be presented with an option to cancel a selected operation. In this case,
 * {@link #cancel(LongRunningOperation} is called. The thread executing the operation will become
 * aware of the cancel request after it's current unit of work is completed, by calling
 * {@link LongRunningOperation#isCanceled()} or {@link #update(LongRunningOperation, int)}.
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 */
public interface LongRunningOperationRegistry
{
    /**
     * Register a new long running operation.
     * 
     * @param code A code used to group related operations together. Mandatory, {@code null} value
     *        is not allowed.
     * @param description Human-readable description of a specific operation. Optional may be
     *        {@code null}.
     * @param user User that initiated the operation. Optional may be {@code null}.
     * @param totalUnitsOfWork Total number of units of work to be performed in this operation. When
     *        a non-positive integer is provided, the number is assumed to be non determined.
     * @return a representation of the operation.
     */
    LongRunningOperation register(String code, String description, Principal user,
        int totalUnitsOfWork);

    /**
     * Register a new long running operation.
     * 
     * @param code A code used to group related operations together. Mandatory, {@code null} value
     *        is not allowed.
     * @param description Human-readable description of a specific operation. Optional may be
     *        {@code null}.
     * @param user User that initiated the operation. Optional may be {@code null}.
     * @param totalUnitsOfWork Total number of units of work to be performed in this operation. When
     *        a non-positive integer is provided, the number is assumed to be non determined.
     * @param securityCallback a callback object that allows expressing security constraings for a
     *        particular operation.
     * @return a representation of the operation.
     */
    LongRunningOperation register(String code, String description, Principal user,
        int totalUnitsOfWork, LongRunningOperationSecurityCallback securityCallback);

    /**
     * Update the progress information for the operation.
     * 
     * @param operation representation of the operation that should be updated.
     * @param completedUnitsOfWork the number of units of work that was completed at this time.
     * @throws OperationCancelledException if the operation has been already canceled.
     */
    void update(LongRunningOperation operation, int completedUnitsOfWork)
        throws OperationCancelledException;

    /**
     * Update the progress information, and total work amount information for the operation.
     * 
     * @param operation representation of the operation that should be updated.
     * @param completedUnitsOfWork the number of units of work that was completed at this time.
     * @param totalUnitsOfWork Total number of units of work to be performed in this operation. When
     *        a non-positive integer is provided, the number is assumed to be non determined.
     * @throws OperationCancelledException if the operation has been already canceled.
     */
    void update(LongRunningOperation operation, int completedUnitsOfWork, int totalUnitsOfWork)
        throws OperationCancelledException;

    /**
     * Request the operation to be canceled.
     * <p>
     * Calling this method on an operation that has been canceled before has no further effect, nor
     * throws an exception.
     * </p>
     * 
     * @param operation representation of the operation that should be cancelled.
     * @param requestor the user that requests the operation to be cancelled.
     * @throws SecurityException when the {@code requestor} is not authorized to cancel the
     *         operation, according to {@link LongRunningOperationSecurityCallback} provided while
     *         registering the operation.
     */
    void cancel(LongRunningOperation operation, Principal requestor)
        throws SecurityException;

    /**
     * Unregister an operation that has been completed or aborted.
     * <p>
     * Calling this method on an operation that has been unregistered before has no further effect,
     * nor throws an exception.
     * </p>
     * 
     * @param operation representation of the operation that should be unregistered.
     */
    void unregister(LongRunningOperation operation);

    /**
     * Returns an operation with a specific identifier.
     * 
     * @param identifier identifier of the operation.
     * @return representation of the operation.
     * @throws SecurityException when the {@code requestor} is not authorized to view the operation,
     *         according to {@link LongRunningOperationSecurityCallback} provided while registering
     *         the operation.
     */
    LongRunningOperation getOperation(String identifier, Principal requestor)
        throws SecurityException;

    /**
     * Returns all operations that are currently active.
     * <p>
     * Operations that where registered together with {@link LongRunningOperationSecurityCallback}
     * instance will be present in the results only if
     * {@link LongRunningOperationSecurityCallback#canView(LongRunningOperation, Principal)} returns
     * {@code true} for the {@code requestor} user.
     * </p>
     * 
     * @param requestor the user that requests to view the operations.
     * @return all operations that are currently active.
     */
    Collection<LongRunningOperation> getActiveOperations(Principal requestor);

    /**
     * Returns all active operations that share a common code prefix.
     * <p>
     * Operations that where registered together with {@link LongRunningOperationSecurityCallback}
     * instance will be present in the results only if
     * {@link LongRunningOperationSecurityCallback#canView(LongRunningOperation, Principal)} returns
     * {@code true} for the {@code requestor} user.
     * </p>
     * 
     * @param codePrefix a prefix of the code shared by the operations to be reported.
     * @param requestor the user that requests to view the operations.
     * @return all active operations that share a common code prefix.
     */
    Collection<LongRunningOperation> getActiveOperations(String codePrefix, Principal requestor);

    /**
     * Returns all active operations initiated by a specific user.
     * <p>
     * Operations that where registered together with {@link LongRunningOperationSecurityCallback}
     * instance will be present in the results only if
     * {@link LongRunningOperationSecurityCallback#canView(LongRunningOperation, Principal)} returns
     * {@code true} for the {@code requestor} user.
     * </p>
     * 
     * @param user the user that initiated the operation. May be {@code null} in which case,
     *        operations that had {@code null} user passed to the
     *        {@link #register(String, String, Principal, int)} will be returned. Otherwise
     *        {@link Principal} objects will be checked for equivalence using
     *        {@link Object#equals(Object)}.
     * @param requestor the user that requests to view the operations.
     * @return all active operations initiated by a specific user.
     */
    Collection<LongRunningOperation> getActiveOperations(Principal user, Principal requestor);

    /**
     * Returns all active operations initiated by a specific user that share a common code prefix.
     * <p>
     * Operations that where registered together with {@link LongRunningOperationSecurityCallback}
     * instance will be present in the results only if
     * {@link LongRunningOperationSecurityCallback#canView(LongRunningOperation, Principal)} returns
     * {@code true} for the {@code requestor} user.
     * </p>
     * 
     * @param codePrefix a prefix of the code shared by the operations to be reported.
     * @param user the user that initiated the operation. May be {@code null} in which case,
     *        operations that had {@code null} user passed to the
     *        {@link #register(String, String, Principal, int)} will be returned. Otherwise
     *        {@link Principal} objects will be checked for equivalence using
     *        {@link Object#equals(Object)}.
     * @param requestor the user that requests to view the operations.
     * @return all active operations initiated by a specific user.
     */
    Collection<LongRunningOperation> getActiveOperations(String codePrefix, Principal user,
        Principal requestor);

    /**
     * Register a listener for operation - related events.
     * <P>
     * When a single listener object instance is passed several times to this method, the
     * registration is updated with new parameters. Listener will receive each event only once.
     * </p>
     * 
     * @param listener listener instance.
     * @param types types of events that the listener wishes to receive.
     * @param codePrefix prefix of code of the operations that the listener wishes to receive events
     *        for. Empty string may be used to receive events for all operations.
     */
    void addListener(LongRunningOperationListener listener,
        Set<LongRunningOperationEvent.Type> types, String codePrefix);

    /**
     * Remove a registered listener.
     * <P>
     * Calling this method for a listener instance that is not currently registered has no effect
     * nor throws an exception.
     * </P>
     * 
     * @param listener listener instance.
     */
    void removeListener(LongRunningOperationListener listener);
}
