package org.objectledge.table.comparator;

import java.util.Comparator;

/**
 * Sorts elements according to a pre-defined order given by an array.
 * <p>
 * This is a companion class for ListTableModel. It allows the user to switch back to original
 * "unsorted" ordering after tweaking sorting columns.
 * </p>
 * <p>
 * Elements are assumed to have reliable equals() semantics and occur only once in the array. The
 * array is assumed to stay unaltered after the comparator and model creation.
 * </p>
 */
public class ArrayPositionComparator<T>
    implements Comparator<T>
{
    private final T[] array;

    public ArrayPositionComparator(T[] array)
    {
        this.array = array;
    }

    @Override
    public int compare(T o1, T o2)
    {
        return indexOf(o1) - indexOf(o2);
    }

    private int indexOf(T o)
    {
        for (int i = 0; i < array.length; i++)
        {
            if(array[i].equals(o))
            {
                return i;
            }
        }
        return -1;
    }
}
