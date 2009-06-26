/**
 * 
 */
package org.objectledge.diff;

import java.util.ArrayList;
import java.util.List;

public class Sequence<T>
{
    private List<Element<T>> elements;

    private State state;

    public Sequence()
    {
        elements = new ArrayList<Element<T>>();
        state = State.EQUAL;
    }

    public Sequence(List<Element<T>> elem)
    {
        elements = elem;
        state = State.EQUAL;
    }

    public Sequence(List<Element<T>> elem, State s)
    {
        elements = elem;
        state = s;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public void add(Element<T> elem)
    {
        elements.add(elem);
    }
    
    public void add(T left, T right, State state)
    {
        elements.add(new Element<T>(left, right, state));
    }

    public void addAll(List<Element<T>> elem)
    {
        elements = elem;
    }

    public List<Element<T>> getElements()
    {
        return elements;
    }
}