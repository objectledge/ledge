/**
 * 
 */
package org.objectledge.diff;

public class Element<T>
{
    private final T left;

    private final T right;

    private final State state;

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

    public T getLeft()
    {
        return left;
    }

    public T getRight()
    {
        return right;
    }
}
