package org.objectledge.table;


/**
 * A table filter that accepts all items.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: AcceptAllTableFilter.java,v 1.1 2005-11-18 12:38:54 rafal Exp $
 */
public class AcceptAllTableFilter implements TableFilter
{
    /** The singleton instance of AcceptAllTableFilter */
    public static final AcceptAllTableFilter INSTANCE = new AcceptAllTableFilter();
    
    /**
     * Private ctor to enforce {@link #INSTANCE} use.
     */
    private AcceptAllTableFilter()
    {
        // intentionally left blank
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean accept(Object object)
    {
        return true;
    }
}