package org.objectledge.diff;

public interface Element
{
    public enum State
    {
        CHANGED, EQUAL, ADDED, DELETED
    }
    
    public abstract State getState();
}
