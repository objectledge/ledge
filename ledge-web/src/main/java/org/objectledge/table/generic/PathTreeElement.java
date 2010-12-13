// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
// 
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//  
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
// 

package org.objectledge.table.generic;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;

/**
 * A class that may be used for ad-hoc UI building with {@link PathTreeTableModel}.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PathTreeElement.java,v 1.2 2009-01-23 10:05:21 rafal Exp $
 */
public class PathTreeElement
{
    /** Name of the element. */
    private String name;

    /** Type of the element. */
    private String type;

    /** Properties of the element. */
    private Map<String, Object> properties;

    /**
     * Creates a new TreeElement.
     *
     * @param name name of the element.
     * @param type type of the element.
     */
    public PathTreeElement(String name, String type)
    {
        this.name = name;
        this.type = type;
        this.properties = new HashMap<String, Object>();
    }

    /**
     * Returns the name of the element.
     *
     * <p>Equivalent to <code>(String)elm.getProperty("name")</code></p>
     *
     * @return the name of the element.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the type of the element.
     *
     * <p>Equivalent to <code>(String)elm.getProperty("type")</code></p>
     *
     * @return the type of the element.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Provides a simple String representation of the object.
     *
     * <p>The representation has the following form
     * <code>TreeElement(&lt;name&gt;,&lt;type&gt;)</code>
     *
     * @return a String representation of the object.
     */
    public String toString()
    {
        return "TreeElement("+name+","+type+")";
    }

    /**
     * Returns the value of the TreeElement's property.
     *
     * @param property property name.
     * @return property value.
     * @see #getName()
     * @see #getType()
     */
    public Object get(String property)
    {
        if("name".equals(property))
        {
            return name;
        }
        if("type".equals(property))
        {
            return type;
        }
        return properties.get(property);
    }

    /**
     * Sets a property of the TreeElement.
     *
     * @param property property name.
     * @param value the value.
     */
    public void set(String property, Object value)
    {
        properties.put(property, value);
    }

    /**
     * Returns a Comparator that compares TreeElement objects on the selected
     * property.
     *
     * <p>The property must be defined (non-null) for both compared objects
     * and must implement Comparable interface themselves.</p>
     *
     * @param property the property to compare on.
     * @param propertyClass expected class of the property, must implement Comparable interface.
     * @return an instance of Comparator interface.
     */
    public static <T extends Comparable<T>> Comparator<PathTreeElement> getComparator(
        final String property, final Class<T> propertyClass)
    {
        return new Comparator<PathTreeElement>()
        {
            @SuppressWarnings("unchecked")
            public int compare(PathTreeElement e1, PathTreeElement e2)
            {
                T p1 = (T)e1.get(property);
                T p2 = (T)e2.get(property);
                return p1.compareTo(p2);
            }
        };
    }

    /**
     * Returns a Comparator that compares TreeElement objects on the selected
     * String property, taking locale settings into consideration.
     *
     * <p>The property must be defined (non-null) for both compared objects
     * and must be String objects. The locale must be supported by the platform's
     * Collator implementation, as defined by Collator.getAvailableLocales().</p>
     *
     * @param property the property to compare on.
     * @param locale the locale to be used for comparisons.
     * @return an instance of Comparator interface.
     */
    public static Comparator<PathTreeElement> getComparator(final String property, final Locale locale)
    {
        return new Comparator<PathTreeElement>()
        {
            private Collator collator = Collator.getInstance(locale);

            public int compare(PathTreeElement e1, PathTreeElement e2)
            {
                String s1 = (String)e1.get(property);
                String s2 = (String)e2.get(property);
                return collator.compare(s1, s2) ;
            }
        };
    }

    /**
     * Returns a TableColumn based on a property supporting Comparable interface.
     * 
     * @param <T> property type, must implement java.lang.Comparable
     * @param property name of the property.
     * @param propertyClass property class.
     * @return a TableColumn.
     * @throws TableException when TableColumn constructor terminates abnormally. 
     */
    public static <T extends Comparable<T>> TableColumn<PathTreeElement> getTableColumn(
        String property, Class<T> propertyClass)
        throws TableException
    {
        return new TableColumn<PathTreeElement>(property, getComparator(property, propertyClass));
    }
    
    /**
     * Return a TableColumn based on a String property with lexical ordering.
     * 
     * @param property name of the property.
     * @param locale java.util.Locale to use for lexical ordering.
     * @return a TableColumn
     * @throws TableException when TableColumn constructor terminates abnormally.
     */
    public static TableColumn<PathTreeElement> getTableColumn(String property, Locale locale)
        throws TableException
    {
        return new TableColumn<PathTreeElement>(property, getComparator(property, locale));
    }
}
