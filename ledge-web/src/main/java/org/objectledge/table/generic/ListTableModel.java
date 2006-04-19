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

import java.util.Arrays;
import java.util.List;

import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;

/**
 * A table model for wrapping a <code>java.util.List</code>
 *
 * <p>Item indices are used as ids.</p>
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ListTableModel.java,v 1.8 2006-04-19 12:56:51 rafal Exp $
 */
public class ListTableModel<T>
    implements ExtendedTableModel<T>
{
    /** The embeded list. */
    protected List<T> list;

    /** The columns of the list. */
    protected TableColumn<T>[] columns;

    /**
     * Constructs a new model.
     *
     * @param list the list to build model for.
     * @param columns the columns of the table, <code>null</code> to disable sorting.
     */
    @SuppressWarnings("unchecked")
    public ListTableModel(List<T> list, TableColumn<T> ... columns)
    {
        this.list = list;
        if(columns == null)
        {
            this.columns = new TableColumn[0];
        }
        else
        {
            this.columns = columns;
        }
    }

    /**
     * Constructs a new model.
     *
     * @param array the array to build model for.
     * @param columns the columns of the table, <code>null</code> to disable sorting.
     */
    public ListTableModel(T[] array, TableColumn<T> ... columns)
    {
        this(Arrays.asList(array), columns);
    }

    /**
     * {@inheritDoc}
     */
    public TableRowSet<T> getRowSet(TableState state, TableFilter<T>[] filters)
    {
        if(state.getTreeView())
        {
			return new GenericTreeRowSet<T>(state, filters, this);
        }
        else
        {
			return new GenericListRowSet<T>(state, filters, this);
        }
    }

    /**
     * Gets all children of the parent, may return empty array.
     *
     * @param parent the parent
     * @return table of children
     */
    @SuppressWarnings("unchecked")
    public T[] getChildren(T parent)
    {
        if(parent == null)
        {
            return (T[]) list.toArray();
        }
        else
        {
            return (T[]) new Object[0];
        }
    }

    /**
     * Returns the model dependent object by its id.
     *
     * @param id the id of the object
     * @return model object
     */
    public T getObject(String id)
    {
        try
        {
            int index = Integer.parseInt(id);
            return list.get(index);
        }
        catch(NumberFormatException e)
        {
            return null;
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * Returns the id of the object.
     * 
     * @param parent the id of the parent.
     * @param child model object.
     * @return the id of the object.
     */
    public String getId(String parent, T child)
    {
        int index = list.indexOf(child);
        if(index >= 0)
        {
            return Integer.toString(index);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns array of column definitions. They are created on every call,
     * because they can get modified durig it's lifecycle.
     *
     * @return array of <code>TableColumn</code> objects
     */
    public TableColumn<T>[] getColumns()
    {
        return columns;
    }
}
