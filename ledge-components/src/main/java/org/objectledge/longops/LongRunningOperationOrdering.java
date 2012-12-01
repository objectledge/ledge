package org.objectledge.longops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Sorts operations according to their start and estimated end times.
 * <p>
 * The rules for sorting are as follows:
 * <ul>
 * <li>operations with definite estimated end time are sorted before those without.</li>
 * <li>operations with definite estimated end time are sorted by estimated end time, ascending.</li>
 * <li>operations without definite estimated end time are sorted by start time, ascending.</li>
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class LongRunningOperationOrdering
{
    /**
     * Prevent instantiation
     */
    private LongRunningOperationOrdering()
    {
    }

    public static List<LongRunningOperation> sortOperations(Collection<LongRunningOperation> in)
    {
        List<OperationDescriptor> descritors = new ArrayList<>(in.size());
        for(LongRunningOperation operation : in)
        {
            descritors.add(new OperationDescriptor(operation));
        }
        Collections.sort(descritors, new OperationDescriptorComparator());
        List<LongRunningOperation> out = new ArrayList<>(in.size());
        for(OperationDescriptor descriptor : descritors)
        {
            out.add(descriptor.operation);
        }
        return out;
    }

    private static final class OperationDescriptor
    {
        final LongRunningOperation operation;

        final long startTime;

        final long estimatedEndTime;

        public OperationDescriptor(LongRunningOperation operation)
        {
            this.operation = operation;
            startTime = operation.getStartTime().getTime();
            final Date curEstimatedEndTime = operation.getEstimatedEndTime();
            estimatedEndTime = curEstimatedEndTime == null ? -1l : curEstimatedEndTime.getTime();
        }
    }

    private static final class OperationDescriptorComparator
        implements Comparator<OperationDescriptor>
    {
        @Override
        public int compare(OperationDescriptor o1, OperationDescriptor o2)
        {
            if(o1.estimatedEndTime == -1l)
            {
                if(o2.estimatedEndTime == -1l)
                {
                    return (int)(o1.startTime - o2.startTime);
                }
                else
                {
                    return +1;
                }
            }
            else
            {
                if(o2.estimatedEndTime == -1l)
                {
                    return -1;
                }
                else
                {
                    return (int)(o1.estimatedEndTime - o2.estimatedEndTime);
                }
            }
        }
    }
}
