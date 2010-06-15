package org.objectledge.table;


/**
 * A table filter that accepts all items.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: AcceptAllTableFilter.java,v 1.1 2005-11-18 12:38:54 rafal Exp $
 */
public class AcceptAllTableFilter<T> implements TableFilter<T>
{   
    /**
     * Private ctor to enforce {@link #INSTANCE} use.
     */
    public AcceptAllTableFilter()
    {
        // intentionally left blank
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean accept(T object)
    {
        return true;
    }
}