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

/**
 * A class that may be used for ad-hoc UI building with {@link PathTreeTableModel}.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PathTreeElement.java,v 1.1 2004-02-10 17:17:46 zwierzem Exp $
 */
public class PathTreeElement
{
    /** Name of the element. */
    private String name;

    /** Type of the element. */
    private String type;

    /** Prpoperties of the element. */
    private Map properties;

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
        this.properties = new HashMap();
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
     * @return proprety value.
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
     * @return an instance of Comparator interface.
     */
    public static Comparator getComparator(final String property)
    {
        return new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                PathTreeElement e1 = (PathTreeElement)o1;
                PathTreeElement e2 = (PathTreeElement)o2;
                Comparable p1 = (Comparable)e1.get(property);
                Comparable p2 = (Comparable)e2.get(property);
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
     * Collator implementation, as defined by Collatror.getAvailableLocales().</p>
     *
     * @param property the property to compare on.
     * @param locale the locale to be used for comparisons.
     * @return an instance of Comparator interface.
     */
    public static Comparator getComparator(final String property, final Locale locale)
    {
        return new Comparator()
        {
            private Collator collator = Collator.getInstance(locale);

            public int compare(Object o1, Object o2)
            {
                PathTreeElement e1 = (PathTreeElement)o1;
                PathTreeElement e2 = (PathTreeElement)o2;
                String s1 = (String)e1.get(property);
                String s2 = (String)e2.get(property);
                return collator.compare(s1, s2) ;
            }
        };
    }
}
