/**
 * 
 */
package org.objectledge.diff;

public class Element
{
    String left;

    String right;

    private State state;

    public Element(String left, String right)
    {
        this.left = left;
        this.right = right;
        this.state = State.EQUAL;
    }

    public Element(String left, String right, State state)
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

    public String getLeft()
    {
        return left;
    }

    public String getRight()
    {
        return right;
    }
}
