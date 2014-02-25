package org.objectledge.table.comparator;

import java.lang.reflect.Field;

public class ReflectionPropertyAccessor<O, V>
    implements PropertyBasedComparator.PropertyAccessor<O, V>
{
    private Field field;

    private final Class<V> fieldClass;

    public ReflectionPropertyAccessor(Class<O> objectClass, String fieldName, Class<V> fieldClass)
        throws NoSuchFieldException, SecurityException
    {
        this.fieldClass = fieldClass;
        this.field = objectClass.getDeclaredField(fieldName);
        if(!field.getType().isAssignableFrom(fieldClass))
        {
            throw new IllegalArgumentException(fieldName + " has type " + field.getClass()
                + " not compatible with " + fieldClass);
        }
        this.field.setAccessible(true);
    }

    @Override
    public Class<V> getType()
    {
        return fieldClass;
    }

    @Override
    public V getValue(O o)
    {
        try
        {
            return fieldClass.cast(field.get(o));
        }
        catch(IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
