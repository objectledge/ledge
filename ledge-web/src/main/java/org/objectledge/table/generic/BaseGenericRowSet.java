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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableState;


/**
 * This class provides a base implementation of a TableRowSet interface.
 * It ensures that rows collection is built only once.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseGenericRowSet.java,v 1.13 2006-03-16 17:57:03 zwierzem Exp $
 */
public abstract class BaseGenericRowSet<T> extends BaseRowSet<T>
{
    /** Table model which provides data for this row set. */
    protected ExtendedTableModel<T> model;

    /** Root row for this row set. */
    protected TableRow<T> rootRow;

    /** Keeps total number of rows in this row set. */
    protected int totalRowCount;

    /** Local row set cache - keeps page of rows. */
    protected TableRow<T>[] rows;

    /** This map allows quick child row lookup.
     * Used in {@link #hasMoreChildren(TableRow,TableRow)}. */
    protected HashMap<TableRow<T>,TableRow<T>[]> rowsByParent = new HashMap<TableRow<T>,TableRow<T>[]>();

    /** This map allows quick parent row lookup.
     * Used in {@link #hasMoreChildren(TableRow,TableRow)} and {@link #getParentRow(TableRow)}. */
    protected HashMap<TableRow<T>,TableRow<T>> rowsByChild = new HashMap<TableRow<T>,TableRow<T>>();

    /**
     * Constructs the rowset.
     *
     * @param state the state of the table instance.
     * @param filters a list of filters to be used while creating the rows set.
     * @param model the table model.
     */
    public BaseGenericRowSet(TableState state, TableFilter<T>[] filters, ExtendedTableModel<T> model)
    {
        super(state, filters);
        this.model = model;
    }

	// TableRowSet interface implementation -------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
    @SuppressWarnings("unchecked")
    public TableRow<T>[] getRows()
    {
        if(rows == null)
        {
            List<TableRow<T>> list = getAllRows();

            list = getCurrentPageRows(list);

            rows = list.toArray(new TableRow[list.size()]);
        }

        return rows;
    }

	/**
	 * {@inheritDoc}
	 */
    public TableRow<T> getRootRow()
    {
        // in case rows were not drawn from the model
        getRows();
        return rootRow;
    }

	/**
	 * {@inheritDoc}
	 */
    public TableRow<T> getParentRow(TableRow<T> childRow)
    {
        // in case rows were not drawn from the model
        getRows();
        return rowsByChild.get(childRow);
    }

	/**
	 * {@inheritDoc}
	 */
    public boolean hasMoreChildren(TableRow<T> ancestorRow, TableRow<T> descendantRow)
    {
        // in case rows were not drawn from the model
        getRows();

        if(descendantRow == ancestorRow)
        {
            throw new IllegalStateException("Ancestor and descendant rows are the same object");
        }

        // get a direct child of ancestor which is also an ancestor of descendant
        TableRow<T> childRow = null;
        while(descendantRow != null && descendantRow != ancestorRow)
        {
            childRow = descendantRow;
            descendantRow = rowsByChild.get(descendantRow);
        }

        if(descendantRow == null)
        {
            throw new IllegalStateException("Ancestor is not a real ancestor of a given row");
        }

        // get filtered and sorted ancestor's children
        TableRow<T>[] children = rowsByParent.get(ancestorRow);

        // check whether child that was found is on the end of children list
        TableRow<T> lastChildRow = children[children.length-1];
        return (childRow != lastChildRow);
    }

	/**
	 * {@inheritDoc}
	 */
    public int getPageRowCount()
    {
        // in case rows were not drawn from the model
        TableRow<T>[] list = getRows();
        return list.length;
    }

	/**
	 * {@inheritDoc}
	 */
    public int getTotalRowCount()
    {
        // in case rows were not drawn from the model
        getRows();
        return totalRowCount;
    }

    // implementation -----------------------------------------------------------------------------

    /**
     * Returns a sublist of of a given list which corresponds to a view
     * of table state's current page in this row set.
     * 
     * @param list a lit of table rows to be snipped to current view page
     * @return a list of table nodes containing only current page rows.
     */
    protected List<TableRow<T>> getCurrentPageRows(List<TableRow<T>> list)
    {
        int page = state.getCurrentPage();
        int perPage = state.getPageSize();

        if(page > 0 && perPage > 0)
        {
            int start = (page-1)*perPage; // inclusive
            int end = page*perPage;		  // exclusive
            int listSize = list.size();

            // automatic page number reset
            // TODO: Page sanitization here?????
            if(start >= listSize)
            {
                int numPages = listSize / perPage;
                numPages += (listSize % perPage > 0) ? 1: 0;
                start = (numPages-1)*perPage;
                start = (start < 0)? 0: start;
                end = start + perPage;
                state.setCurrentPage(start/perPage+1);
            }
            end = (end < listSize)? end: listSize;

            list = list.subList(start, end);
        }

        return list;
    }

    /**
     * Returns the list of {@link TableRow} objects representing the object tree or list.
     *
     * @return a list of tree nodes.
     */
    protected List<TableRow<T>> getAllRows()
    {
        // start row list creation
        ArrayList<TableRow<T>> rowList = new ArrayList<TableRow<T>>();
        // WARN: save root row
        String rootId = state.getRootId();
        T rootObject = model.getObject(rootId);
        this.rootRow = getSubTree(rootId, rootObject, 0, rowList); // depth = 0

        // sort rows collection for list view
        sortAllRows(rowList);

        // WARN: initialise total row count
        this.totalRowCount = rowList.size();

        return rowList;
    }

    /**
     * Builds tree nodes for the specified subtree and stores them in the
     * target list.
     *
     * @param rootId the if of root node of the subtree.
     * @param rootObject the root node of the subtree.
     * @param depth the nesting depth of the subtree root.
     * @param rowList the target node list.
     * @return the root of created subtree
     */
    @SuppressWarnings("unchecked")
    protected TableRow<T> getSubTree(String rootId, T rootObject, int depth, List<TableRow<T>> rowList)
    {
        //1. get children for this subtree root node
        T[] childrenObjects = model.getChildren(rootObject);

        // 1.1. filter them out
        List<T> childrenList = new ArrayList<T>(childrenObjects.length);
        for(int i = 0; i< childrenObjects.length; i++)
        {
            if(accept(childrenObjects[i]))
            {
                childrenList.add(childrenObjects[i]);
            }
        }

        // -------------------

        // 2.0. decide whether to continue recursion
        // CONTINUE if depth is not too big AND if root node is expanded
        boolean continueRecursion = (checkDepth(depth+1) && expanded(rootId));

        // 2.1. create current rootRow
        int childCount = childrenList.size();
        int visibleChildCount = continueRecursion ? childrenList.size() : 0;
        TableRow<T> localRootRow =
        	new TableRow<T>(rootId, rootObject, depth, childCount, visibleChildCount);

        // 2.2. add current root row to rowList
        if(rootObject != null)
        {
            // check if a MAIN root node should be shown (depth==0 and state.getShowRoot()==true)
            if(depth > 0 || state.getShowRoot())
            {
                rowList.add(localRootRow);
            }
        }

        // -------------------

        if(continueRecursion)
        {
            // 3.0. increase depth
            depth++;

            // 3.1. sort children collection for tree or forest view
            sortChildren(childrenList);

            // WARN: create TableRow array for children caching
            TableRow<T>[] children = new TableRow[childrenList.size()];

            // 4. add children to rowList collection
            for(int i = 0; i< childrenList.size(); i++)
            {
                T childObject = childrenList.get(i);
                String childId = model.getId(rootId, childObject);

                // go down the tree
                TableRow<T> childRow = getSubTree(childId, childObject, depth, rowList);

                // WARN: add TableRow to array created for children caching
                children[i] = childRow;
                // WARN: cache childRow's parent row
                rowsByChild.put(childRow, localRootRow);
            }

            // WARN: cache this rows children
            rowsByParent.put(localRootRow, children);
        }

        // -------------------

        return localRootRow;
    }

    // utility methods ----------------------------------------------------------------------------

    /**
     * Check whether the object with a given id is expanded.
     * Tree and List view imeplementations will differ.
     *
     * @param id the id of an object to be check for being expaned.
     * @return <code>true</code> if expanded.
     */
    protected abstract boolean expanded(String id);

    /**
     * Sorts rows collection for list view.
     *
     * @param rowsList list of table rows for current view.
     */
    protected abstract void sortAllRows(List<TableRow<T>> rowsList);

    /**
     * Sorts children collection for tree or forest view.
     *
     * @param childrenList list of children nodes for current subtree.
     */
    protected abstract void sortChildren(List<T> childrenList);

    /**
     * Returns the selected sorting column.
     *
     * <p>If no sort column was chosen, <code>null</code> will be
     * returned.</p>
     *
     * @return the selected sorting column, or <code>null</code>
     */
    protected TableColumn<T> getSortColumn()
    {
        String sortColumnName = state.getSortColumnName();
        TableColumn<T> column = null;
        TableColumn<T>[] columns = model.getColumns();
        for(int i=0; i<columns.length; i++)
        {
            if(columns[i].getName().equals(sortColumnName))
            {
                column = columns[i];
                break;
            }
        }
        return column;
    }
}
