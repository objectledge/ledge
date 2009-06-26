package org.objectledge.diff;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class DiffUtilTest
    extends TestCase
{
    private static final Splitter COMMA_SPLITTER = new RegexSplitter(",");

    private static final Splitter SEMI_SPLITTER = new RegexSplitter(";");

    public void testGenericDiffEqual()
    {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(1, 2, 3);
        Sequence<Element<Integer>> diff = DiffUtil.diff(left, right);
        assertEquals(State.EQUAL, diff.getState());
        for(Element<Integer> elem : diff)
        {
            assertEquals(State.EQUAL, elem.getState());
        }
    }    

    public void testGenericDiffAddedBeginning()
    {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(0, 1, 2, 3);
        Sequence<Element<Integer>> diff = DiffUtil.diff(left, right);
        assertEquals(State.CHANGED, diff.getState());
        assertEquals(State.ADDED, diff.get(0).getState());
        assertNull(diff.get(0).getLeft());
        assertEquals(0, diff.get(0).getRight().intValue());
    }
    
    public void test1TierEqual()
    {
        Sequence<Element<String>> diff = DiffUtil.diff("a,b,c", "a,b,c", COMMA_SPLITTER);
        assertEquals(State.EQUAL, diff.getState());
        for(Element<String> elem : diff)
        {
            assertEquals(State.EQUAL, elem.getState());
        }
    }

    public void test1TierChangedMiddle()
    {
        Sequence<Element<String>> diff = DiffUtil.diff("a,b,c", "a,bb,c", COMMA_SPLITTER);
        assertEquals(State.CHANGED, diff.getState());
        assertEquals(State.EQUAL, diff.get(0).getState());
        assertEquals(State.CHANGED, diff.get(1).getState());
        assertEquals(State.EQUAL, diff.get(2).getState());
    }
    
    public void test2TierEqual()
    {
        Sequence<Sequence<Element<String>>> tier1 = DiffUtil.diff("a,b,c;a,b;a", "a,b,c;a,b;a", SEMI_SPLITTER, COMMA_SPLITTER);
        assertEquals(State.EQUAL, tier1.getState());
        for(Sequence<Element<String>> tier2 : tier1)
        {
            assertEquals(State.EQUAL, tier2.getState());
            for(Element<String> elem : tier2)
            {
                assertEquals(State.EQUAL, elem.getState());                
            }
        }
    }
}
