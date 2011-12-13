package org.objectledge.table;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Comparator;

import org.objectledge.table.comparator.BeanComparator;
import org.objectledge.table.comparator.ComparableComparator;

public class BeanTableColumn<T>
    extends TableColumn<T>
{
    public BeanTableColumn(Class<T> beanClass, String property,
        Comparator<Object> propertyComparator)
        throws TableException, IntrospectionException
    {
        super(property, new BeanComparator<T>(beanClass, property, propertyComparator));
    }

    public BeanTableColumn(Class<T> beanClass, String property)
        throws TableException, IntrospectionException
    {
        super(property, new BeanComparator<T>(beanClass, property, getPropertyComparator(beanClass,
            property)));
    }

    private static <T> Comparator<Object> getPropertyComparator(Class<T> beanClass, String property)
        throws IntrospectionException
    {
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        for(PropertyDescriptor desc : info.getPropertyDescriptors())
        {
            if(desc.getName().equals(property))
            {
                if(Comparable.class.isAssignableFrom(desc.getPropertyType()))
                {
                    return new ComparableComparator();
                }
                else
                {
                    throw new IntrospectionException("property " + property + " of bean class "
                        + beanClass + " is of type " + desc.getPropertyType()
                        + " which does not support java.lang.Comparable interface");
                }
            }
        }
        throw new IntrospectionException("bean class " + beanClass.getName()
            + " does not have " + property + " property");
    }
}
