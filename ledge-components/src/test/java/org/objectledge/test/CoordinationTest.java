package org.objectledge.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.objectledge.test.Coordination.Participant;

public class CoordinationTest
    extends TestCase
{
    private final class TestParticipant
        extends Coordination.Participant
    {
        private final String name;

        public TestParticipant(String name)
        {
            this.name = name;
        }

        @Override
        public void step(int num)
            throws Exception
        {
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    public void testOrdering()
    {
        Coordination.Participant p1 = new TestParticipant("p1");
        Coordination.Participant p2 = new TestParticipant("p2");
        Coordination.Participant p3 = new TestParticipant("p3");
        final List<String> scoreboard = new ArrayList<>();
        Coordination.Monitor m = new Coordination.Monitor()
            {
                @Override
                public void before(Participant participant, int step)
                {
                    scoreboard.add(participant.toString() + " " + step);
                }
            };
        Coordination c = new Coordination(3, 100, TimeUnit.MILLISECONDS, m, p1, p2, p3);
        Collection<Exception> ex = c.run();
        assertTrue(ex.isEmpty());
        assertTrue(scoreboard.indexOf("p1 2") > scoreboard.indexOf("p1 1"));
        assertTrue(scoreboard.indexOf("p1 2") > scoreboard.indexOf("p2 1"));
        assertTrue(scoreboard.indexOf("p1 2") > scoreboard.indexOf("p3 1"));
        assertTrue(scoreboard.indexOf("p1 3") > scoreboard.indexOf("p1 2"));
        assertTrue(scoreboard.indexOf("p1 3") > scoreboard.indexOf("p2 2"));
        assertTrue(scoreboard.indexOf("p1 3") > scoreboard.indexOf("p3 2"));
    }

    public void testStepException()
    {
        Coordination.Participant p1 = new Coordination.Participant()
            {
                @Override
                public void step(int num)
                    throws Exception
                {
                    throw new Exception("step");
                }
            };
        Coordination.Participant p2 = new Participant()
            {
                @Override
                public void step(int num)
                    throws Exception
                {
                }
            };
        Coordination c = new Coordination(3, 100, TimeUnit.MILLISECONDS, null, p1, p2);
        Collection<Exception> ex = c.run();
        assertEquals(2, ex.size());
        for(Exception e : ex)
        {
            assertTrue(e.getMessage() != null && e.getMessage().equals("step")
                || (e instanceof TimeoutException));
        }
    }

    public void testCleanupException()
    {
        Coordination.Participant p1 = new Coordination.Participant()
            {
                @Override
                public void step(int num)
                    throws Exception
                {
                }

                @Override
                public void cleanup()
                    throws Exception
                {
                    throw new Exception("cleanup");
                }
            };
        Coordination.Participant p2 = new Participant()
            {
                @Override
                public void step(int num)
                    throws Exception
                {
                }
            };
        Coordination c = new Coordination(3, 100, TimeUnit.MILLISECONDS, null, p1, p2);
        Collection<Exception> ex = c.run();
        assertEquals(1, ex.size());
        for(Exception e : ex)
        {
            assertTrue(e.getMessage() != null && e.getMessage().equals("cleanup"));
        }
    }
}
