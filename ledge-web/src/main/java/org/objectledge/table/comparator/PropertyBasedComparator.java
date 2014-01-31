package org.objectledge.table.comparator;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Compares object according to a specified property
 * 
 * @param <O> Type of the objects being compared.
 * @param <V> type of the property used as the criterion of comparison.
 * @author rafal.krzewski@caltha.pl
 */
public abstract class PropertyBasedComparator<O, V>
    implements Comparator<O>
{
    private final Direction direction;

    private final PropertyAccessor<O, V> accessor;

    public PropertyBasedComparator(PropertyAccessor<O, V> accessor, Direction direction)
    {
        this.accessor = accessor;
        this.direction = direction;
    }

    /**
     * Compare a pair of non-null values of type V.
     * 
     * @param v1 first value.
     * @param v2 second value.
     * @return the result of the comparison.
     */
    protected abstract int compareTo(V v1, V v2);

    /**
     * Compares two attributes values. Values may be null, the contract is:
     * <ul>
     * <li>If both values are not null, they are simply compared, but when sort direction is DESC,
     * comparison is reversed.</li>
     * <li>If both are null, values are considered equal</li>
     * <li>If one of the values is null, and sort direction is ASC, null value is considered
     * greater, and when direction is DESC, null values is considered lesser than then non-null
     * value.</li>
     * </ul>
     * 
     * @param v1 first value.
     * @param v2 second value.
     * @return the result of the comparison.
     */
    public int compareValues(V v1, V v2)
    {
        if(v1 != null && v2 != null)
        {
            return direction == Direction.ASC ? compareTo(v1, v2) : compareTo(v2, v1);
        }

        if(v1 == null)
        {
            if(v2 == null)
            {
                return 0;
            }
            else
            {
                // d1 == null && d2 != null
                return 1;
            }
        }
        // d1 != null && d2 == null
        return -1;
    }

    /**
     * Compares two objects using values of the property attribute:
     * <ul>
     * <li>If both values are defined, they are simply compared, but when sort direction is DESC,
     * comparison is reversed.</li>
     * <li>If one of the values is undefined, and sort direction is ASC, object with undefined value
     * is considered greater, and when direction is DESC, resource with undefined value is
     * considered lesser than then the object with defined value.</li>
     * </ul>
     * 
     * @param res1 first resource.
     * @param res2 second resource.
     * @return the result of the comparison.
     */
    @Override
    public int compare(O o1, O o2)
    {
        V v1 = accessor.getValue(o1);
        V v2 = accessor.getValue(o1);
        return compareValues(v1, v2);
    }

    /**
     * Retrieves a property of an object.
     * 
     * @param <O> Type of the object.
     * @param <V> Type of the property.
     */
    public interface PropertyAccessor<O, V>
    {
        /**
         * Returns the the type of the property.
         * 
         * @return
         */
        Class<V> getType();

        /**
         * Returns the value of the property.
         * 
         * @param o the object instance.
         * @return the value of the property.
         */
        V getValue(O o);
    }

    /**
     * Provide an instance of PropertyBasedComparator for a given property accessor.
     * <p>
     * The following java types are supported:
     * <ul>
     * <li>Boolean</li>
     * <li>Integer</li>
     * <li>Long</li>
     * <li>BigDecimal</li>
     * <li>Date</li>
     * <li>String</li>
     * </ul>
     * </p>
     * 
     * @param accessor the property accessor
     * @param locale the locale used for comparing Strings
     * @param direction sort direction
     * @return a PropertyBasedComparator instance
     */
    @SuppressWarnings("unchecked")
    public static <O, V> PropertyBasedComparator<O, V> getInstance(
        final PropertyAccessor<O, V> accessor, final Locale locale, final Direction direction)
    {
        Class<V> cl = accessor.getType();

        if(cl.equals(Boolean.class))
        {
            return (PropertyBasedComparator<O, V>)new PropertyBasedComparator<O, Boolean>(
                            (PropertyAccessor<O, Boolean>)accessor, direction)
                {
                    @Override
                    protected int compareTo(Boolean v1, Boolean v2)
                    {
                        return v1.compareTo(v2);
                    }
                };
        }
        if(cl.equals(Integer.class))
        {
            return (PropertyBasedComparator<O, V>)new PropertyBasedComparator<O, Integer>(
                            (PropertyAccessor<O, Integer>)accessor, direction)
                {
                    @Override
                    protected int compareTo(Integer v1, Integer v2)
                    {
                        return v1 - v2;
                    }
                };
        }
        if(cl.equals(Long.class))
        {
            return (PropertyBasedComparator<O, V>)new PropertyBasedComparator<O, Long>(
                            (PropertyAccessor<O, Long>)accessor, direction)
                {
                    @Override
                    protected int compareTo(Long v1, Long v2)
                    {
                        return (int)(v1 - v2);
                    }
                };
        }
        if(cl.equals(BigDecimal.class))
        {
            return (PropertyBasedComparator<O, V>)new PropertyBasedComparator<O, BigDecimal>(
                            (PropertyAccessor<O, BigDecimal>)accessor, direction)
                {
                    @Override
                    protected int compareTo(BigDecimal v1, BigDecimal v2)
                    {
                        return v1.compareTo(v2);
                    }
                };
        }
        if(Date.class.isAssignableFrom(cl))
        {
            return (PropertyBasedComparator<O, V>)new PropertyBasedComparator<O, Date>(
                            (PropertyAccessor<O, Date>)accessor, direction)
                {
                    @Override
                    protected int compareTo(Date v1, Date v2)
                    {
                        return v1.compareTo(v2);
                    }
                };
        }
        if(cl.equals(String.class))
        {
            return (PropertyBasedComparator<O, V>)new PropertyBasedComparator<O, String>(
                            (PropertyAccessor<O, String>)accessor, direction)
                {
                    private Collator col = Collator.getInstance(locale);

                    @Override
                    protected int compareTo(String v1, String v2)
                    {
                        return col.compare(v1, v2);
                    }
                };
        }
        if(Comparable.class.isAssignableFrom(cl))
        {
            return (PropertyBasedComparator<O, V>)new PropertyBasedComparator<O, Comparable<V>>(
                            (PropertyAccessor<O, Comparable<V>>)accessor, direction)
                {
                    @Override
                    protected int compareTo(Comparable<V> v1, Comparable<V> v2)
                    {
                        return v1.compareTo((V)v2);
                    }
                };
        }
        throw new IllegalArgumentException("unsupported attribute type " + cl.getName());
    }
}
