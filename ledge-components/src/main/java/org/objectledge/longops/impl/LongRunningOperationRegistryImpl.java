package org.objectledge.longops.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationEvent;
import org.objectledge.longops.LongRunningOperationEvent.Type;
import org.objectledge.longops.LongRunningOperationListener;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.longops.OperationCancelledException;

public class LongRunningOperationRegistryImpl
    implements LongRunningOperationRegistry
{
    private final Map<String, MutableLongRunningOperation> byId = new HashMap<>();

    private final Map<String, Set<MutableLongRunningOperation>> byCode = new HashMap<>();

    private final AtomicInteger nextIdentifier = new AtomicInteger();

    private final Set<ListenerRegistration> listeners = new HashSet<>();

    private final Clock clock;

    public LongRunningOperationRegistryImpl()
    {
        clock = new SystemClock();
    }

    LongRunningOperationRegistryImpl(Clock clock)
    {
        this.clock = clock;
    }

    @Override
    public LongRunningOperation register(String code, String description, Principal user,
        int totalUnitsOfWork)
    {
        if(code == null)
        {
            throw new IllegalArgumentException("code may not be null");
        }
        final String identifier = nextIdentifier();
        MutableLongRunningOperation op = new MutableLongRunningOperation(identifier, code,
            description, user, totalUnitsOfWork, clock);
        synchronized(byId)
        {
            byId.put(identifier, op);
        }
        synchronized(byCode)
        {
            Set<MutableLongRunningOperation> opSet = byCode.get(code);
            if(opSet == null)
            {
                opSet = new HashSet<>();
                byCode.put(code, opSet);
            }
            opSet.add(op);
        }
        final ImmutableLongRunningOperation imOp = new ImmutableLongRunningOperation(op);
        dispatchEvent(LongRunningOperationEvent.Type.REGISTERED, imOp);
        return imOp;
    }

    private String nextIdentifier()
    {
        return Integer.toString(nextIdentifier.getAndIncrement());
    }

    @Override
    public void update(LongRunningOperation operation, int completedUnitsOfWork)
        throws OperationCancelledException
    {
        if(operation == null)
        {
            throw new IllegalArgumentException("operation may not be null");
        }
        MutableLongRunningOperation op;
        synchronized(byId)
        {
            op = byId.get(operation.getIdentifier());
            if(op != null)
            {
                if(op.isCanceled())
                {
                    throw new IllegalStateException("operation #" + op.getIdentifier()
                        + " has been canceled");
                }
                op.update(completedUnitsOfWork);
            }
            else
            {
                throw new OperationCancelledException("invalid operation #"
                    + operation.getIdentifier());
            }
        }
        dispatchEvent(LongRunningOperationEvent.Type.UPDATED, op);
    }

    @Override
    public void update(LongRunningOperation operation, int completedUnitsOfWork,
        int totalUnitsOfWork)
        throws OperationCancelledException
    {
        if(operation == null)
        {
            throw new IllegalArgumentException("operation may not be null");
        }
        MutableLongRunningOperation op;
        synchronized(byId)
        {
            op = byId.get(operation.getIdentifier());
            if(op != null)
            {
                if(op.isCanceled())
                {
                    throw new OperationCancelledException("operation #" + op.getIdentifier()
                        + " has been canceled");
                }
                op.update(completedUnitsOfWork, totalUnitsOfWork);
            }
            else
            {
                throw new IllegalArgumentException("invalid operation #"
                    + operation.getIdentifier());
            }
        }
        dispatchEvent(LongRunningOperationEvent.Type.UPDATED, op);
    }

    @Override
    public void cancel(LongRunningOperation operation)
    {
        if(operation == null)
        {
            throw new IllegalArgumentException("operation may not be null");
        }
        MutableLongRunningOperation op;
        synchronized(byId)
        {
            op = byId.get(operation.getIdentifier());
            if(op != null)
            {
                op.cancel();
            }
            else
            {
                throw new IllegalArgumentException("invalid operation #"
                    + operation.getIdentifier());
            }
        }
        dispatchEvent(LongRunningOperationEvent.Type.CANCELED, op);
    }

    @Override
    public void unregister(LongRunningOperation operation)
    {
        if(operation == null)
        {
            throw new IllegalArgumentException("operation may not be null");
        }
        MutableLongRunningOperation removedOp = null;
        synchronized(byId)
        {
            removedOp = byId.remove(operation.getIdentifier());
        }
        if(removedOp != null)
        {
            synchronized(byCode)
            {
                Set<MutableLongRunningOperation> opSet = byCode.get(operation.getCode());
                if(opSet != null)
                {
                    opSet.remove(removedOp);
                }
            }
        }
        if(removedOp != null)
        {
            dispatchEvent(LongRunningOperationEvent.Type.UNREGISTERED, operation);
        }
    }

    @Override
    public LongRunningOperation getOperation(String identifier)
    {
        synchronized(byId)
        {
            MutableLongRunningOperation op = byId.get(identifier);
            if(op != null)
            {
                return new ImmutableLongRunningOperation(op);
            }
            else
            {
                throw new IllegalArgumentException("invalid operation #" + identifier);
            }
        }
    }

    @Override
    public Collection<LongRunningOperation> getActiveOperations()
    {
        synchronized(byId)
        {
            List<LongRunningOperation> list = new ArrayList<>(byId.size());
            for(Map.Entry<String, MutableLongRunningOperation> entry : byId.entrySet())
            {
                list.add(new ImmutableLongRunningOperation(entry.getValue()));
            }
            return list;
        }
    }

    @Override
    public Collection<LongRunningOperation> getActiveOperations(String codePrefix)
    {
        if(codePrefix == null)
        {
            throw new IllegalArgumentException("codePrefix may not be null");
        }
        synchronized(byCode)
        {
            List<LongRunningOperation> list = new ArrayList<>();
            for(Map.Entry<String, Set<MutableLongRunningOperation>> entry : byCode.entrySet())
            {
                if(entry.getKey().startsWith(codePrefix))
                {
                    for(MutableLongRunningOperation op : entry.getValue())
                    {
                        list.add(new ImmutableLongRunningOperation(op));
                    }
                }
            }
            return list;
        }
    }

    @Override
    public Collection<LongRunningOperation> getActiveOperations(Principal user)
    {
        synchronized(byId)
        {
            List<LongRunningOperation> list = new ArrayList<>();
            for(Map.Entry<String, MutableLongRunningOperation> entry : byId.entrySet())
            {
                final MutableLongRunningOperation op = entry.getValue();
                if(user == null ? op.getUser() == user : user.equals(op.getUser()))
                {
                    list.add(new ImmutableLongRunningOperation(op));
                }
            }
            return list;
        }
    }

    @Override
    public void addListener(LongRunningOperationListener listener, Set<Type> types,
        String codePrefix)
    {
        if(listener == null)
        {
            throw new IllegalArgumentException("listener may not be null");
        }
        if(types == null)
        {
            throw new IllegalArgumentException("types may not be null");
        }
        if(codePrefix == null)
        {
            throw new IllegalArgumentException("codePrefix may not be null");
        }
        synchronized(listeners)
        {
            listeners.add(new ListenerRegistration(listener, types, codePrefix));
        }
    }

    @Override
    public void removeListener(LongRunningOperationListener listener)
    {
        if(listener == null)
        {
            throw new IllegalArgumentException("listener may not be null");
        }
        synchronized(listeners)
        {
            listeners.remove(new ListenerRegistration(listener, null, null));
        }
    }

    private void dispatchEvent(LongRunningOperationEvent.Type type, LongRunningOperation operation)
    {
        List<LongRunningOperationListener> receivers = new ArrayList<>();
        synchronized(listeners)
        {
            for(ListenerRegistration registration : listeners)
            {
                if(registration.getTypes().contains(type)
                    && operation.getCode().startsWith(registration.getCodePrefix()))
                {
                    receivers.add(registration.getListener());
                }
            }
        }
        if(receivers.size() > 0)
        {
            LongRunningOperationEvent event = new LongRunningOperationEventImpl(type,
                operation instanceof ImmutableLongRunningOperation ? operation
                    : new ImmutableLongRunningOperation(operation));
            for(LongRunningOperationListener receiver : receivers)
            {
                receiver.receive(event);
            }
        }
    }

    private static class SystemClock
        implements Clock
    {
        @Override
        public long currentTimeMillis()
        {
            return System.currentTimeMillis();
        }
    }

    private static class ListenerRegistration
    {
        private final LongRunningOperationListener listener;

        private final Set<Type> types;

        private final String codePrefix;

        public ListenerRegistration(LongRunningOperationListener listener,
            Set<LongRunningOperationEvent.Type> types, String codePrefix)
        {
            this.listener = listener;
            this.types = types;
            this.codePrefix = codePrefix;
        }

        public LongRunningOperationListener getListener()
        {
            return listener;
        }

        public Set<Type> getTypes()
        {
            return types;
        }

        public String getCodePrefix()
        {
            return codePrefix;
        }

        @Override
        public int hashCode()
        {
            return System.identityHashCode(listener);
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof ListenerRegistration
                && ((ListenerRegistration)obj).listener == listener;
        }
    }

    public static class LongRunningOperationEventImpl
        implements LongRunningOperationEvent
    {
        private final Type type;

        private final LongRunningOperation operation;

        LongRunningOperationEventImpl(Type type, LongRunningOperation operation)
        {
            this.type = type;
            this.operation = operation;
        }

        @Override
        public Type getType()
        {
            return type;
        }

        @Override
        public LongRunningOperation getOperation()
        {
            return operation;
        }
    }
}
