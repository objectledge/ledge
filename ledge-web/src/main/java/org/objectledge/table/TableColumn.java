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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing table column. It allows template designers to access
 * meta data for columns and provide generic macros for table header generation.
 * Apart from that it provides comparators which can be used as:
 * <ul>
 * <li>internal information about soring for TableModel implementations</li>
 * <li>Comparator provider for {@link GenericListRowSet} and {@link GenericTreeRowSet}
 * - {@link TableRowSet} implementations</li>
 * </ul>
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableColumn.java,v 1.2 2004-12-23 07:17:47 rafal Exp $
 */
public class TableColumn
{
    /** properties map */
    private Map properties = new HashMap();

    private String name;

    private Comparator comparator;
    private Comparator reverseComparator;

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
    public TableColumn(String name, Comparator comparator)
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
    public TableColumn(String name, Comparator comparator, Comparator reverseComparator)
        throws TableException
    {
        this(name, comparator);
        this.reverseComparator = reverseComparator;
    }

    /**
     * Table columns allow template designers set some values before they are
     * used. Because setting values happens in a template it cannot leave
     * any artifacts - that is why <code>toString()</code> has to return an empty string.
     * @return an empty string
     */
    public String toString()
    {
        return "";
    }

    /**
     * Retrieve column property.
     *
     * @param key of the column
     * @return the object value
     */
    public Object get(String key)
    {
        return properties.get(key);
    }

    /**
     * Sets the column property.
     *
     * @param key the key of property
     * @param value the value of the property
     * @return this column object for easy use in multiple calls to this method
     */
    public TableColumn set(Object key, Object value)
    {
        properties.put(key,value);
        return this;
    }

    /** Getter for property name.
     * @return Value of property name.
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

    /** Getter for property comparator.
     * @return Value of property comparator.
     */
    public Comparator getComparator()
    {
        if(comparator == null && reverseComparator != null)
        {
            comparator = new ReverseComparator(reverseComparator);
        }
        return comparator;
    }

    /** Getter for property reverseComparator.
     * @return Value of property reverseComparator.
     */
    public Comparator getReverseComparator()
    {
        if(reverseComparator == null && comparator != null)
        {
            reverseComparator = new ReverseComparator(comparator);
        }
        return reverseComparator;
    }

    /** Class for construction of reverse comparators. */
    public class ReverseComparator implements Comparator
    {
        private Comparator comparator;

		/**
		 * Constructs a reverse comparator for a given comparator.
		 * @param comparator the comparator to be reversed.
		 */
        public ReverseComparator(Comparator comparator)
        {
            this.comparator = comparator;
        }

		/** 
		 * {@inheritDoc}
		 */
        public int compare(Object o1, Object o2)
        {
            return - comparator.compare(o1, o2);
        }
    }
}
