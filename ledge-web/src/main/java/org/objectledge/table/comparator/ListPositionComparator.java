package org.objectledge.table.comparator;

import java.util.Comparator;
import java.util.List;

/**
 * Sorts elements according to a pre-defined order given by a list.
 * <p>
 * This is a companion class for ListTableModel. It allows the user to switch back to original
 * "unsorted" ordering after tweaking sorting columns.
 * </p>
 * <p>
 * Elements are assumed to have reliable equals() semantics and occur only once in the list. The
 * list is assumed to stay unaltered after the comparator and model creation.
 * </p>
 */
public class ListPositionComparator<T>
    implements Comparator<T>
{
    private final List<T> list;

    public ListPositionComparator(List<T> list)
    {
        this.list = list;
    }

    @Override
    public int compare(T o1, T o2)
    {
        return list.indexOf(o1) - list.indexOf(o2);
    }
}
