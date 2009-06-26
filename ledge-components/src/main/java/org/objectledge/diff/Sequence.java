/**
 * 
 */
package org.objectledge.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sequence<U>
    implements Iterable<U>
{
    private List<U> elements;

    private State state;

    public Sequence(State state)
    {
        elements = new ArrayList<U>();
        this.state = state;
    }

    public Sequence(List<U> elem)
    {
        elements = elem;
        state = State.EQUAL;
    }

    public Sequence(List<U> elem, State s)
    {
        elements = elem;
        state = s;
    }

    public State getState()
    {
        return state;
    }

    @Override
    public Iterator<U> iterator()
    {
        return elements.iterator();
    }

    // package private
    void add(U elem)
    {
        elements.add(elem);
    }    
}
