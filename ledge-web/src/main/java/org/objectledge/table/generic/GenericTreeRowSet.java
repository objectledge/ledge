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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableState;


/**
 * An implementation of a rowset which prepares rows to be displayed as tree.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: GenericTreeRowSet.java,v 1.4 2004-07-01 11:40:06 zwierzem Exp $
 */
public class GenericTreeRowSet extends BaseGenericRowSet
{
    /**
     * Constructs the object.
     *
     * @param state the state of the table instance
     * @param model the table model
     */
    public GenericTreeRowSet(TableState state, TableFilter[] filters, ExtendedTableModel model)
    {
        super(state, filters, model);
    }

    // utility methods ///////////////////////////////////////////////////////

    /**
     * Check whether the object with a given id is expanded.
     * Tree and List view imeplementations will differ.
     *
     * @param id the id of an object to be check for being expaned.
     * @return <code>true</code> if expanded.
     */
    protected boolean expanded(String id)
    {
        return state.isExpanded(id);
    }

    /**
     *   Sorts rows collection for list view
     *
     * @param rowsList list of table rows for current view.
     */
    protected void sortAllRows(List rowsList)
    {
        // WARN: must not sort a final list for tree view
    }

    /**
     *   Sorts children collection of a row (node) for tree or forest view.
     *
     * @param childrenList list of children nodes for current subtree.
     */
    protected void sortChildren(List childrenList)
    {
        Comparator comparator = getObjectComparator();
        if(comparator != null)
        {
            Collections.sort(childrenList, comparator);
        }
    }

    // utility methods ///////////////////////////////////////////////////////

    /**
     * Returns a comparator for model's objects that compares them according
     * to the selected sort column and direction.
     *
     * <p>If no sort column was chosen, <code>null</code> will be
     * returned.</p>
     *
     * @return a comparator for model's objects, or <code>null</code>.
     */
    protected Comparator getObjectComparator()
    {
        TableColumn column = getSortColumn();
        if(column == null)
        {
            return null;
        }

        if(state.getAscSort())
        {
            return column.getComparator();
        }
        else
        {
            return column.getReverseComparator();
        }
    }
}
