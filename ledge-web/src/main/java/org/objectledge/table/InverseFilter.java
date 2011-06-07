package org.objectledge.table;

/**
 * A simple wrapper class that inverts the result of accept check of the embedded table filter.
 * 
 * @author rafal
 * @param <T> table row type.
 */
public class InverseFilter<T>
    implements TableFilter<T>
{
    /** the embedded filter */
    private final TableFilter<T> filter;

    /**
     * Creates a new instance of the wrapper.
     * 
     * @param filter the embedded filter.
     */
    public InverseFilter(TableFilter<T> filter)
    {
        this.filter = filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(T object)
    {
        return !filter.accept(object);
    }
}
