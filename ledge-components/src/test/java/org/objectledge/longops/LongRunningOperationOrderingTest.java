package org.objectledge.longops;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

public class LongRunningOperationOrderingTest
    extends MockObjectTestCase
{
    private List<LongRunningOperation> ops = new ArrayList<>();

    private void addOp(int id, final long start, final long estEnd)
    {
        final String id2 = Integer.toString(id);
        final LongRunningOperation op = context().mock(LongRunningOperation.class, "op" + id2);
        checking(new Expectations()
            {
                {
                    allowing(op).getIdentifier();
                    will(returnValue(id2));
                    allowing(op).getStartTime();
                    will(returnValue(new Date(start)));
                    allowing(op).getEstimatedEndTime();
                    will(returnValue(estEnd > 0 ? new Date(estEnd) : null));
                }
            });
        ops.add(op);
    }

    private void assertOrder(int... ids)
    {
        assertEquals(ids.length, ops.size());
        for(int i = 0; i < ops.size(); i++)
        {
            if(ids[i] != Integer.parseInt(ops.get(i).getIdentifier()))
            {
                StringBuilder buff = new StringBuilder();
                buff.append("expected [");
                for(int j = 0; j < ids.length; j++)
                {
                    buff.append(ids[j]);
                    if(j < ids.length - 1)
                    {
                        buff.append(", ");
                    }
                }
                buff.append("] actual [");
                for(int j = 0; j < ids.length; j++)
                {
                    buff.append(ops.get(j).getIdentifier());
                    if(j < ids.length - 1)
                    {
                        buff.append(", ");
                    }
                }
                buff.append("]");
                throw new AssertionFailedError(buff.toString());
            }
        }
    }

    public void testOrdering1()
    {
        addOp(1, 100l, -1l);
        addOp(2, 200l, 700l);
        addOp(3, 300l, -1l);
        addOp(4, 400l, 500l);
        ops = LongRunningOperationOrdering.sortOperations(ops);
        assertOrder(4, 2, 1, 3);
    }
}
