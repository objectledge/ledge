package org.objectledge.longops.impl;

import static org.objectledge.longops.impl.LongRunningOperationCodeMatcher.opCodeMatching;
import static org.objectledge.longops.impl.LongRunningOperationEventMatcher.event;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationEvent;
import org.objectledge.longops.LongRunningOperationListener;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.longops.OperationCancelledException;

public class LongOpsRegistryImplTest
    extends MockObjectTestCase
{
    @Mock
    private Clock clock;

    @Mock
    private Principal user;

    @Mock
    private LongRunningOperationListener listener;

    private LongRunningOperationRegistry reg = new LongRunningOperationRegistryImpl(clock);

    private long clockStart = System.currentTimeMillis();

    @Override
    public void setUp()
    {
        checking(new Expectations()
            {
                {
                    allowing(clock).currentTimeMillis();
                    will(onConsecutiveCalls(returnValue(clockStart),
                        returnValue(clockStart + 1000), returnValue(clockStart + 2000),
                        returnValue(clockStart + 3000), returnValue(clockStart + 4000)));
                }
            });
    }

    public void testRegisterUnregister()
    {
        LongRunningOperation op = reg.register("op", null, null, 100);
        reg.unregister(op);
    }

    public void testRegisterNullCode()
    {
        try
        {
            reg.register(null, null, null, 0);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testUnregisterNull()
    {
        try
        {
            reg.unregister(null);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testUnregisterMultiple()
    {
        LongRunningOperation op = reg.register("op", null, null, 100);
        reg.unregister(op);
        reg.unregister(op);
        reg.unregister(op);
    }

    public void testDescriptionAndUser()
    {
        LongRunningOperation op1 = reg.register("op", null, null, -1);
        assertNull(op1.getDescription());
        assertNull(op1.getUser());
        LongRunningOperation op2 = reg.register("op", "desc", user, -1);
        assertEquals("desc", op2.getDescription());
        assertSame(user, op2.getUser());
    }

    public void testUpdate()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        try
        {
            assertEquals(0, op.getCompletedUnitsOfWork());
            assertEquals(3, op.getTotalUnitsOfWork());
            reg.update(op, 1);
            assertEquals(1, op.getCompletedUnitsOfWork());
            assertEquals(3, op.getTotalUnitsOfWork());
            reg.update(op, 2);
            assertEquals(2, op.getCompletedUnitsOfWork());
            assertEquals(3, op.getTotalUnitsOfWork());
            reg.update(op, 3);
            assertEquals(3, op.getCompletedUnitsOfWork());
            assertEquals(3, op.getTotalUnitsOfWork());
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
        reg.unregister(op);
    }

    public void testUpdateWithTotal()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        try
        {
            assertEquals(0, op.getCompletedUnitsOfWork());
            assertEquals(3, op.getTotalUnitsOfWork());
            reg.update(op, 1, 2);
            assertEquals(1, op.getCompletedUnitsOfWork());
            assertEquals(2, op.getTotalUnitsOfWork());
            reg.update(op, 2);
            assertEquals(2, op.getCompletedUnitsOfWork());
            assertEquals(2, op.getTotalUnitsOfWork());
            reg.update(op, 3);
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
        reg.unregister(op);
    }

    public void testUpdateNull()
    {
        try
        {
            reg.update(null, 1);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

    public void testUpdateUnregistered()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        reg.unregister(op);
        try
        {
            reg.update(op, 1);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

    public void testUpdateTotalNull()
    {
        try
        {
            reg.update(null, 1, 3);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

    public void testUpdateTotalUnregistered()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        reg.unregister(op);
        try
        {
            reg.update(op, 1, 3);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

    public void testCancel()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        try
        {
            assertFalse(op.isCanceled());
            reg.update(op, 1);
            assertFalse(op.isCanceled());
            reg.update(op, 2);
            reg.cancel(op);
            assertTrue(op.isCanceled());
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
        reg.unregister(op);
    }

    public void testCancelUpdateException()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        try
        {
            assertFalse(op.isCanceled());
            reg.update(op, 1);
            assertFalse(op.isCanceled());
            reg.update(op, 2);
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
        try
        {
            reg.cancel(op);
            assertTrue(op.isCanceled());
            reg.update(op, 3);
            fail("expected an OperationCancelledException");
        }
        catch(OperationCancelledException e)
        {
            // OK
        }
        reg.unregister(op);
    }

    public void testCancelUpdateTotalException()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        try
        {
            assertFalse(op.isCanceled());
            reg.update(op, 1);
            assertFalse(op.isCanceled());
            reg.update(op, 2);
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
        try
        {
            reg.cancel(op);
            assertTrue(op.isCanceled());
            reg.update(op, 2, 5);
            fail("expected an OperationCancelledException");
        }
        catch(OperationCancelledException e)
        {
            // OK
        }
        reg.unregister(op);
    }

    public void testMultipleCancel()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        try
        {
            assertFalse(op.isCanceled());
            reg.update(op, 1);
            assertFalse(op.isCanceled());
            reg.update(op, 2);
            reg.cancel(op);
            reg.cancel(op);
            reg.cancel(op);
            assertTrue(op.isCanceled());
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
        reg.unregister(op);
    }

    public void testCancelNull()
    {
        try
        {
            reg.cancel(null);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testCancelUnregistered()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        reg.unregister(op);
        try
        {
            reg.cancel(op);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testGetById()
    {
        LongRunningOperation op1 = reg.register("op", null, null, 3);
        LongRunningOperation op2 = reg.getOperation(op1.getIdentifier());
        assertEquals(op1.getIdentifier(), op2.getIdentifier());
    }

    public void testGetByIdInvalid()
    {
        try
        {
            reg.getOperation("INVALID");
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testOperationEquivalence()
    {
        LongRunningOperation op1 = reg.register("op", null, null, 3);
        LongRunningOperation op2 = reg.getOperation(op1.getIdentifier());
        assertTrue(op2.equals(op1));
        assertTrue(op1.equals(op2));
        assertEquals(op1.hashCode(), op2.hashCode());
    }

    public void testGetActiveOperations()
    {
        LongRunningOperation op1 = reg.register("op", null, null, 3);
        LongRunningOperation op2 = reg.register("op", null, null, 3);
        Collection<LongRunningOperation> all = reg.getActiveOperations();
        assertEquals(2, all.size());
        assertTrue(all.contains(op1));
        assertTrue(all.contains(op2));
    }

    public void testGetActiveOperationsByCode()
    {
        LongRunningOperation op1a = reg.register("op.1.a", null, null, 3);
        LongRunningOperation op1b = reg.register("op.1.b", null, null, 3);
        LongRunningOperation op2 = reg.register("op.2", null, null, 3);
        Collection<LongRunningOperation> ops1 = reg.getActiveOperations("op.1");
        assertEquals(2, ops1.size());
        assertTrue(ops1.contains(op1a));
        assertTrue(ops1.contains(op1b));
        Collection<LongRunningOperation> ops2 = reg.getActiveOperations("op.2");
        assertEquals(1, ops2.size());
        assertTrue(ops2.contains(op2));
        Collection<LongRunningOperation> ops3 = reg.getActiveOperations("op.3");
        assertEquals(0, ops3.size());
    }

    public void testGetActiveOperationsByNullCode()
    {
        try
        {
            reg.getActiveOperations((String)null);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testGetActiveOperationsByUser()
    {
        LongRunningOperation op1a = reg.register("op.1.a", null, user, 3);
        LongRunningOperation op1b = reg.register("op.1.b", null, null, 3);
        LongRunningOperation op2 = reg.register("op.2", null, user, 3);
        Collection<LongRunningOperation> userOps = reg.getActiveOperations(user);
        assertEquals(2, userOps.size());
        assertTrue(userOps.contains(op1a));
        assertTrue(userOps.contains(op2));
        Collection<LongRunningOperation> noUserOps = reg.getActiveOperations((Principal)null);
        assertEquals(1, noUserOps.size());
        assertTrue(noUserOps.contains(op1b));
    }

    public void testRegisterUnregisterListener()
    {
        reg.addListener(listener, EnumSet.allOf(LongRunningOperationEvent.Type.class), "");
        reg.removeListener(listener);
    }

    public void testRegisterNullListener()
    {
        try
        {
            reg.addListener(null, EnumSet.allOf(LongRunningOperationEvent.Type.class), "");
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testRegisterListenerNullTypes()
    {
        try
        {
            reg.addListener(listener, null, "");
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testRegisterListenerNullCodePrefix()
    {
        try
        {
            reg.addListener(listener, EnumSet.allOf(LongRunningOperationEvent.Type.class), null);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testUnRegisterNullListener()
    {
        try
        {
            reg.removeListener(null);
            fail("expected an exception");
        }
        catch(RuntimeException e)
        {
            // OK
        }
    }

    public void testRegistrationListener()
    {
        reg.addListener(listener, EnumSet.of(LongRunningOperationEvent.Type.REGISTERED), "");
        checking(new Expectations()
            {
                {
                    oneOf(listener)
                        .receive(
                            with(event(LongRunningOperationEvent.Type.REGISTERED,
                                opCodeMatching("op"))));
                }
            });
        reg.register("op", null, user, 3);
    }

    public void testRegistrationListenerForCodePrefix()
    {
        reg.addListener(listener, EnumSet.of(LongRunningOperationEvent.Type.REGISTERED), "op.1");
        checking(new Expectations()
            {
                {
                    exactly(2).of(listener).receive(
                        with(event(LongRunningOperationEvent.Type.REGISTERED,
                            opCodeMatching("^op\\.1.*"))));
                }
            });
        reg.register("op.1.a", null, user, 3);
        reg.register("op.1.b", null, user, 3);
        reg.register("op.2.a", null, user, 3);
        reg.register("op.2.b", null, user, 3);
    }

    public void testUpdateListener()
    {
        reg.addListener(listener, EnumSet.of(LongRunningOperationEvent.Type.UPDATED), "");
        checking(new Expectations()
            {
                {
                    oneOf(listener).receive(
                        with(event(LongRunningOperationEvent.Type.UPDATED, opCodeMatching("op"))));
                }
            });
        LongRunningOperation op = reg.register("op", null, user, 3);
        try
        {
            reg.update(op, 1);
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

    public void testCancelListener()
    {
        reg.addListener(listener, EnumSet.of(LongRunningOperationEvent.Type.CANCELLED), "");
        checking(new Expectations()
            {
                {
                    oneOf(listener)
                        .receive(
                            with(event(LongRunningOperationEvent.Type.CANCELLED,
                                opCodeMatching("op"))));
                }
            });
        LongRunningOperation op = reg.register("op", null, user, 3);
        reg.cancel(op);
    }

    public void testUnregisterListener()
    {
        reg.addListener(listener, EnumSet.of(LongRunningOperationEvent.Type.UNREGISTERED), "");
        checking(new Expectations()
            {
                {
                    oneOf(listener).receive(
                        with(event(LongRunningOperationEvent.Type.UNREGISTERED,
                            opCodeMatching("op"))));
                }
            });
        LongRunningOperation op = reg.register("op", null, user, 3);
        reg.unregister(op);
    }

    public void testGetStartTime()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        assertEquals(new Date(clockStart), op.getStartTime());
    }

    public void testLastUpdateTime()
    {
        LongRunningOperation op = reg.register("op", null, null, 3);
        assertEquals(new Date(clockStart), op.getStartTime());
        assertEquals(new Date(clockStart), op.getLastUpdateTime());
        try
        {
            reg.update(op, 1);
            assertEquals(new Date(clockStart), op.getStartTime());
            assertEquals(new Date(clockStart + 1000), op.getLastUpdateTime());
            reg.update(op, 2);
            assertEquals(new Date(clockStart), op.getStartTime());
            assertEquals(new Date(clockStart + 2000), op.getLastUpdateTime());
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

    public void testEstimatedEndTimeUndefinedTotal()
    {
        LongRunningOperation op = reg.register("op", null, null, -1);
        try
        {
            assertNull(op.getEstimatedEndTime());
            reg.update(op, 1);
            assertNull(op.getEstimatedEndTime());
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

    public void testEstimatedEndTime()
    {
        LongRunningOperation op = reg.register("op", null, null, 3); // cl 0s
        try
        {
            assertNull(op.getEstimatedEndTime());
            reg.update(op, 1); // cl 1s
            assertEquals(new Date(clockStart + 6000), op.getEstimatedEndTime()); // cl 2s
            reg.update(op, 2); // cl 3s
            assertEquals(new Date(clockStart + 6000), op.getEstimatedEndTime()); // cl 4s
        }
        catch(OperationCancelledException e)
        {
            fail("unexpected OperationCancelledException");
        }
    }

}
