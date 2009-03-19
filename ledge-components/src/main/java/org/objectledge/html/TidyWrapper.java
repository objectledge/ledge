package org.objectledge.html;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.w3c.tidy.Tidy;

/**
 * Wrapper for jTidy objects that allows pooling of those.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TidyWrapper.java,v 1.2 2005-01-20 16:44:55 pablo Exp $
 */
public class TidyWrapper
{
    private Tidy tidy;

    /** Creates a new instance of TidyWrapper */
    public TidyWrapper()
    {
        tidy = new org.w3c.tidy.Tidy();
    }

    /**
     * Getter for property tidy.
     * 
     * @return Value of property tidy.
     */
    public Tidy getTidy()
    {
        return tidy;
    }

    // pooling

    public static class Factory
        extends BasePoolableObjectFactory
    {
        public Object makeObject()
        {
            return new TidyWrapper();
        }
    }

    private static ObjectPool pool = new GenericObjectPool(new Factory());

    public static TidyWrapper getInstance()
    {
        try
        {
            return (TidyWrapper)pool.borrowObject();
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unexpected pool failure");
        }
    }

    public void release()
    {
        try
        {
            pool.returnObject(this);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unexpected pool failure");
        }
    }
}
