/**
 * 
 */
package org.objectledge.diff;

public class DetailElement<T> implements Element
{
    private final T left;

    private final T right;

    private final State state;

    DetailElement(T left, T right, State state)
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
