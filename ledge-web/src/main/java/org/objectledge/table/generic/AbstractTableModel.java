// 
//Copyright (c) 2006, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;

/**
 * A table model implementing the most basic methods to create a skeleton of the ExtendedTableModel.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractTableModel.java,v 1.1 2006-05-29 14:03:39 zwierzem Exp $
 */
public abstract class AbstractTableModel<T>
    implements ExtendedTableModel<T>
{
    /** The columns of the list. */
    protected TableColumn<T>[] columns;

    /**
     * Constructs a new model.
     *
     * @param columns the columns of the table, <code>null</code> to disable sorting.
     */
    @SuppressWarnings("unchecked")
    public AbstractTableModel(TableColumn<T> ... columns)
    {
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
     * Returns array of column definitions. They are created on every call,
     * because they can get modified durig it's lifecycle.
     *
     * @return array of <code>TableColumn</code> objects
     */
    public TableColumn<T>[] getColumns()
    {
        return columns;
    }
    
    /**
     * Returns a column with the given name.
     * 
     * @param name name of the column.
     * @return a TableColumn, or {@code null} when no such column is present in the model.
     */
    public TableColumn<T> getColumn(String name)
    {
        for(TableColumn<T> column : columns)
        {
            if(column.getName().equals(name))
            {
                return column;
            }
        }
        return null;
    }
}
