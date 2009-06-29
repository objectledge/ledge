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
    private final List<U> elements;

    private final State state;

    Sequence(State state)
    {
        elements = new ArrayList<U>();
        this.state = state;
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

    U get(int index)
    {
        return elements.get(index);
    }
}
