package org.objectledge.table.comparator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Comparator;

public class BeanComparator<T>
    implements Comparator<T>
{
    private final Comparator<Object> propertyComparator;

    private final Method readMethod;

    private final String property;

    private final Class<T> beanClass;

    public BeanComparator(Class<T> beanClass, String property, Comparator<Object> propertyComparator)
        throws IntrospectionException
    {
        this.beanClass = beanClass;
        this.property = property;
        this.propertyComparator = propertyComparator;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        Method readMethod = null;
        for(PropertyDescriptor desc : info.getPropertyDescriptors())
        {
            if(desc.getName().equals(property))
            {
                readMethod = desc.getReadMethod();
            }
        }
        if(readMethod == null)
        {
            throw new IntrospectionException("bean class " + beanClass.getName()
                + " does not have " + property + " property");
        }
        else
        {
            this.readMethod = readMethod;
        }
    }

    @Override
    public int compare(T o1, T o2)
    {
        Object v1;
        Object v2;
        try
        {
            v1 = readMethod.invoke(o1, (Object[])null);
            v2 = readMethod.invoke(o2, (Object[])null);
        }
        catch(Exception e)
        {
            throw new RuntimeException("failed to introspect value of property " + property
                + " of bean class " + beanClass.getName(), e);
        }
        return propertyComparator.compare(v1, v2);
    }
}
