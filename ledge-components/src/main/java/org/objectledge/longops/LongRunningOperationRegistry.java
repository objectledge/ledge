package org.objectledge.longops;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

public interface LongRunningOperationRegistry
{
    LongRunningOperation register(String code, String description, Principal user,
        int totalUnitsOfWork);

    void update(LongRunningOperation operation, int completedUnitsOfWork);

    void update(LongRunningOperation operation, int completedUnitsOfWork, int totalUnitsOfWork);

    void cancel(LongRunningOperation operation);

    void unregister(LongRunningOperation operation);

    LongRunningOperation getOperation(String identifier);

    Collection<LongRunningOperation> getCurrentOperations();

    Collection<LongRunningOperation> getOperations(String codePrefix);

    void addListener(LongRunningOperationListener listener,
        Set<LongRunningOperationEvent.Type> types, String codePrefix);

    void removeListener(LongRunningOperationListener listener);
}
