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

package org.objectledge.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing table column.
 * 
 * <p>It allows template designers to access
 * meta data for columns and provide generic macros for table header generation.
 *  * Apart from that it provides comparators which can be used as:</p>
 * <ul>
 * <li>internal information about soring for TableModel implementations</li>
 * <li>Comparator provider for {@link org.objectledge.table.generic.GenericListRowSet} and 
 * {@link org.objectledge.table.generic.GenericTreeRowSet}
 * - {@link TableRowSet} implementations</li>
 * </ul>
 * 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableColumn.java,v 1.7 2006-04-03 18:38:51 zwierzem Exp $
 */
public class TableColumn<T> implements Comparable<TableColumn<T>>
{
    /** properties map */
    private Map<String,Object> properties = new HashMap<String,Object>();

    private String name;

    private Comparator<T> comparator;
    private Comparator<T> reverseComparator;

    /**
     * Constructs non sortable columns.
     *
     * @param name name of this column - required.
     * @throws TableException if column name is <code>null</code> or empty
     */
    public TableColumn(String name)
        throws TableException
    {
        this(name, null);
    }

    /**
     * Constructs TableColumn from a given name and comparator object.
     * Name is required. If comparator is <code>null</code> column is
     * considered as not sortable.
     *
     * @param name name of this column - required.
     * @param comparator comparator object for this column, may be <code>null</code>.
     * @throws TableException if column name is <code>null</code> or empty
     */
    public TableColumn(String name, Comparator<T> comparator)
        throws TableException
    {
        if(name == null || name.length() == 0)
        {
            throw new TableException("TableColumn has to have a name");
        }
        this.name = name;
        this.comparator = comparator;
    }

    /**
     * Constructs TableColumn from a given name and comparator objects.
     * Name is required. If both comparators are <code>null</code> column is
     * considered as not sortable.
     *
     * @param name name of this column - required.
     * @param comparator comparator object for this column, may be <code>null</code>.
     * @param reverseComparator reverse comparator object for this column, may be <code>null</code>.
     * @throws TableException if column name is <code>null</code> or empty
     */
    public TableColumn(String name, Comparator<T> comparator, Comparator<T> reverseComparator)
        throws TableException
    {
        this(name, comparator);
        this.reverseComparator = reverseComparator;
    }

    /**
     * Table columns allow template designers set some values in a chained way before they are
     * used. Because setting values happens in a template it cannot leave
     * any artifacts - that is why <code>toString()</code> has to return an empty string.
     * 
     * @return an empty string
     */
    public String toString()
    {
        return "";
    }

    /**
     * Retrieve column property.
     * 
     * Column properties can be set using {@link #set(Object, Object)} method.
     *
     * @param key of the property.
     * @return the property value.
     */
    public Object get(String key)
    {
        return properties.get(key);
    }

    /**
     * Sets the column property, can be called ina chained way.
     * Example of calling the method in a chained way:
     * <pre>
     * $tableTool.getColumn('name').set('label','Full name').set('class','large')
     * </pre>
     *
     * @param key the key of property
     * @param value the value of the property
     * @return this column object for easy use in multiple calls to this method
     */
    @SuppressWarnings("unchecked")
    public TableColumn<T> set(String key, Object value)
    {
        properties.put(key,value);
        return this;
    }

    /** 
     * @return unique name of the colum as used in {@link TableTool#getColumn(String)}. 
     */
    public String getName()
    {
        return name;
    }

    /** Checks wether column is sortable.
     * @return <code>true</code> if this column is sortable
     */
    public boolean isSortable()
    {
        return (comparator != null || reverseComparator != null);
    }

    /** 
     * @return comparator defined for the column, should be used only by {@link TableModel} and
     * {@link TableRowSet} implementations, comparator is used for ascending sorting.
     */
    public Comparator<T> getComparator()
    {
        if(comparator == null && reverseComparator != null)
        {
            comparator = Collections.reverseOrder(reverseComparator);
        }
        return comparator;
    }

    /** 
     * @return reverse comparator defined for the column, should be used only by {@link TableModel}
     *  and {@link TableRowSet} implementations, reverse comparator is used for descending sorting.
     */
    public Comparator<T> getReverseComparator()
    {
        if(reverseComparator == null && comparator != null)
        {
            reverseComparator = Collections.reverseOrder(comparator);
        }
        return reverseComparator;
    }

    /**
     * @param o compared column
     * @return result of column names comparison.
     */
    public int compareTo(TableColumn o)
    {
        return this.name.compareTo(o.name);
    }
}
