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
 * While the operation is executing, other threads may call {@link #getCurrentOperations()} and
 * {@link #getOperations(String)} to inquire about active operations and their progress. The user
 * may be presented with an option to cancel a selected operation. In this case, {@link
 * #cancel(LongRunningOperation} is called. The thread executing the operation will become aware of
 * the cancel request after it's current unit of work is completed, by calling
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
     * Update the progress information for the operation.
     * 
     * @param operation representation of the operation that should be updated.
     * @param completedUnitsOfWork the number of units of work that was completed at this time.
     * @throws IllegalStateException if the operation has been already canceled.
     */
    void update(LongRunningOperation operation, int completedUnitsOfWork)
        throws IllegalStateException;

    /**
     * Update the progress information, and total work amount information for the operation.
     * 
     * @param operation representation of the operation that should be updated.
     * @param completedUnitsOfWork the number of units of work that was completed at this time.
     * @param totalUnitsOfWork Total number of units of work to be performed in this operation. When
     *        a non-positive integer is provided, the number is assumed to be non determined.
     * @throws IllegalStateException if the operation has been already canceled.
     */
    void update(LongRunningOperation operation, int completedUnitsOfWork, int totalUnitsOfWork)
        throws IllegalStateException;

    /**
     * Request the operation to be canceled.
     * <p>
     * Calling this method on an operation that has been canceled before has no further effect, nor
     * throws an exception.
     * </p>
     * 
     * @param operation representation of the operation that should be canceled.
     */
    void cancel(LongRunningOperation operation);

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
     */
    LongRunningOperation getOperation(String identifier);

    /**
     * Returns all operations that are currently active.
     * 
     * @return all operations that are currently active.
     */
    Collection<LongRunningOperation> getCurrentOperations();

    /**
     * Returns all active operations that share a common code prefix.
     * 
     * @return all active operations that share a common code prefix.
     */
    Collection<LongRunningOperation> getOperations(String codePrefix);

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
