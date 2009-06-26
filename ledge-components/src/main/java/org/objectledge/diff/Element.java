/**
 * 
 */
package org.objectledge.diff;

public class Element<T>
{
    T left;

    T right;

    private State state;

    public Element(T left, T right)
    {
        this.left = left;
        this.right = right;
        this.state = State.EQUAL;
    }

    public Element(T left, T right, State state)
    {
        this.left = left;
        this.right = right;
        this.state = state;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public T getLeft()
    {
        return left;
    }

    public T getRight()
    {
        return right;
    }
}
