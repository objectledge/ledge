package org.objectledge.table.comparator;

import java.util.Comparator;

/**
 * A composite comparator that uses a sequence of delegate comparators to perform it's function.
 * <p>
 * Comparators further in the sequence are invoked only if the earlier comparators consider the
 * object pair in question to be equal.
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 * @param <T> type being compared.
 */
public class CompositeComparator<T>
    implements Comparator<T>
{
    private final Comparator<? super T>[] comparators;

    @SafeVarargs
    public CompositeComparator(Comparator<? super T>... comparators)
    {
        this.comparators = comparators;

    }

    @Override
    public int compare(T o1, T o2)
    {
        for(Comparator<? super T> comparator : comparators)
        {
            int d = comparator.compare(o1, o2);
            if(d != 0)
            {
                return d;
            }
        }
        return 0;
    }
}
