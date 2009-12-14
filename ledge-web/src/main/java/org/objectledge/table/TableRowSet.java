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

/**
 * TableRowSet is a companion interface for TableModel interface which may
 * provide model-specific implementations (differen from 
 * {@link org.objectledge.table.generic.GenericListRowSet} and
 * {@link org.objectledge.table.generic.GenericTreeRowSet}) to increase performance of the model.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableRowSet.java,v 1.5 2006-04-03 18:38:51 zwierzem Exp $
 */
public interface TableRowSet<T>
{
    /**
     * Returns a table state associated with this row set.
     *
     * @return table state object.
     */
    public TableState getState();

    /**
     * Returns an list of {@link TableRow} objects, which represents
     * a view of TableModel data defined by TableState object.
     *
     * @return a list of rows.
     */
    public TableRow<T>[] getRows();

    /**
     * Gets the root node of the row set.
     * @return root row provided by this row set
     */
    public TableRow<T> getRootRow();

    /**
     * Gets the parent of object.
     * @param childRow a table row, may be a <code>null</code>
     * @return parent row for a given child row 
     */
    public TableRow<T> getParentRow(TableRow<T> childRow);

    /**
     * Checks whether the ancestor has more children
     * (ie. the decendant is not a descendant of last child of ancestor).
     * Used to calculate {@link TableTool#linesAndFolders(TableRow)}.
     *
     * @param ancestorRow row which is an ancestor for the given descendant row
     * @param descendantRow row which is a descendant for the given ancestor row
     * @return <code>true</code> if ancestor has more children
     */
    public boolean hasMoreChildren(TableRow<T> ancestorRow, TableRow<T> descendantRow);

    /**
     * Return the number of elements in returned array.
     *
     * @return the size of returned array
     */
    public int getPageRowCount();

    /**
     * Return the total number of elements in this rowset.
     *
     * @return the size of this rowset
     */
    public int getTotalRowCount();
}
