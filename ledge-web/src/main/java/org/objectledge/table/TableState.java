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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Table state object represents a state of a tabular data view. It defines a subset of visible
 * rows by providing:
 * <ul>
 * <li>the id of a root node - it may be <code>null</code> for forest and flat 
 * list type of data,</li>
 * <li>TableFilter elements and,</li>
 * <li>expansion state of nodes (does not apply to flat list).</li>
 * </ul>
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableState.java,v 1.2 2004-02-12 13:50:15 zwierzem Exp $
 */
public class TableState
implements Cloneable
{
    /** Id under which this table state is mapped in TableService. */
    private int id;

    /** Table root object id,  default: <code>empty string</code>. */
    private String rootId = "";

    /** Set of selected node ids. */
    private Set selected = new HashSet();

    /** Expanded all switch, default: <code>false</code>. */
    private boolean allExpanded = false;

    /** Set of expanded nodes ids. */
    private Set expanded = new HashSet();

    /** Name of a column selected for sorting, default: <code>null</code>. */
    private String sortColumnName;

    /** Current sorting direction, default: <code>{@link TableConstants#SORT_ASC}</code>. */
    private int sortDir = TableConstants.SORT_ASC;

    /** Sorting comparators by tree level map. */
    private Map sorting = new HashMap();

    /** Filters for this table view. */
    private TableFilter[] filters = new TableFilter[0];

    /** Current table page, default: <code>1</code>. */
    private int currentPage = 1;

    /** Current table page size, default: <code>0</code>. */
    private int pageSize = 0;

    /** Current table viewType, default: <code>{@link
     * TableConstants#VIEW_AS_LIST}</code>. */
    private int viewType = TableConstants.VIEW_AS_LIST;

    /** Multiselect switch, default: <code>false</code>. */
    private boolean multiSelect = false;

    /** ShowRoot switch, default: <code>false</code>, which is good for lists and forests. */
    private boolean showRoot = false;

    /** Maxmimal tree depth of elements visible, default:
     * <code>0</code>, which means any depth. */
    private int maxVisibleDepth = 0;

    /** <code>true</code> if the state object was created during this
     * request. */
    private boolean newState;

	/** 
	 * Default constructor for table state.
	 * @param id numeric id of a state as assigned by {@link TableStateManager}
	 */
    public TableState(int id)
    {
        this.id = id;
        newState = true;
    }

    /**
     * Returns <code>true</code> if the state object was created during this
     * request.
     *
     * <p>Use this method to determine if you need to perform setup steps on
     * the state or not.</p>
     * 
     * @return <code>true</code> if the state has not been modified since its creation
     */
    public boolean isNew()
    {
        return newState;
    }

    /**
     * Called by the table service to mark the state as used previously.
     *
     * <p>You have no interest in calling this method yourself.</p>
     */
    public void setOld()
    {
        newState = false;
    }

    /**
     * Returns this table state's id.
     *
     * @return Value of property id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the root id of the table
     *
     * @return the root id
     */
    public String getRootId()
    {
        return rootId;
    }

    /**
     * Sets root id
     *
     * @param rootId the root id
     */
    public void setRootId(String rootId)
    {
        this.rootId = rootId;
    }

    /**
     * Checks whether a row with a given id is selected
     *
     * @param rowId id of a checked row
     * @return <code>true</code> if a row with a given id is selected.
     */
    public boolean isSelected(String rowId)
    {
        return selected.contains(rowId);
    }

    /**
     * Sets a row's selected state. -
     * if this state is <i>not</i> multiselectable,
     * other selected rows are cleared.
     *
     * @param rowId of node
     */
    public void setSelected(String rowId)
    {
        if(!multiSelect)
        {
            selected.clear();
        }
        selected.add(rowId);
    }

	/**
	 * Sets selected state on a number of rows 
	 * - works only for multiselect set to <code>false</code>.
	 *
	 * @param rowIds the ids of rows.
	 */
	public void setSelected(String[] rowIds)
	{
		if(multiSelect)
		{
			for(int i=0; i < rowIds.length; i++)
			{
				selected.add(rowIds[i]);
			}
		}
	}

    /**
     * Toggles a row's selected state. -
     * if this state is <i>not</i> multiselectable and the row is getting selected,
     * other selected rows are cleared.
     *
     * @param rowId of node
     */
    public void toggleSelected(String rowId)
    {
        if(selected.contains(rowId))
        {
            selected.remove(rowId);
        }
        else
        {
            if(!multiSelect)
            {
                selected.clear();
            }
            selected.add(rowId);
        }
    }

    /**
     * Clears all selected rows.
     */
    public void clearSelected()
    {
        selected.clear();
    }

    /**
     * Checks whether a row with a given id is expanded, rows with empty
     * id are treated as expanded, this allows for easy implementation of
     * flat lists, and forest views.
     *
     * @param rowId id of a checked row
     * @return <code>true</code> if a row with a given id is expanded.
     */
    public boolean isExpanded(String rowId)
    {
        if(rowId.length() == 0 || allExpanded)
        {
            return true;
        }
        else
        {
            return expanded.contains(rowId);
        }
    }

    /**
     * Sets a row's expanded state.
     *
     * @param rowId the id of the row.
     */
    public void setExpanded(String rowId)
    {
        expanded.add(rowId);
    }

    /**
     * Sets expanded state on a number of rows.
     *
     * @param rowIds the ids of rows.
     */
    public void setExpanded(String[] rowIds)
    {
        for(int i=0; i < rowIds.length; i++)
        {
            setExpanded(rowIds[i]);
        }
    }

    /**
     * Clears row's expanded state.
     *
     * @param rowId the id of the row.
     */
    public void setCollapsed(String rowId)
    {
        expanded.remove(rowId);
    }

    /**
     * Toggles a row's expanded state.
     *
     * @param rowId of node
     */
    public void toggleExpanded(String rowId)
    {
        if(expanded.contains(rowId))
        {
            expanded.remove(rowId);
        }
        else
        {
            expanded.add(rowId);
        }
    }

    /**
     * Set expaded all switch.
     *
     * @param allExpanded the all expanded switch.
     */
    public void setAllExpanded(boolean allExpanded)
    {
        this.allExpanded = allExpanded;
    }

    /**
     * Get all expanded state.
     *
     * @return all expanded state.
     */
    public boolean getAllExpanded()
    {
        return allExpanded;
    }

    /**
     * Clears all expanded rows.
     */
    public void clearExpanded()
    {
        expanded.clear();
    }

    /**
     * Returns name of a column selected for sorting.
     *
     * @return name of a column by which table is sorted
     */
    public String getSortColumnName()
    {
        return sortColumnName;
    }

    /**
     * Sets a name of a column selected for sorting.
     *
     * @param sortColumnName name of a column selected for sorting.
     */
    public void setSortColumnName(String sortColumnName)
    {
        this.sortColumnName = sortColumnName;
    }

    /**
     * Returns current sorting direction, default is: <code>{@link
     * TableConstants#SORT_ASC}</code>.
     * @see TableConstants#SORT_ASC
     * @see TableConstants#SORT_DESC
     * @return the current sorting direction
     */
    public int getSortDir()
    {
        return sortDir;
    }

    /**
     * Sets the sorting direction, if a given value is not equal to
     * <code>{@link TableConstants#SORT_ASC}</code> or
     * <code>{@link TableConstants#SORT_DESC}</code>,
     * a default value (<code>{@link TableConstants#SORT_ASC}</code>)
     * is set.
     *
     * @param sortDir the sortDir constant
     */
    public void setSortDir(int sortDir)
    {
        if(   sortDir == TableConstants.SORT_ASC
           || sortDir == TableConstants.SORT_DESC)
        {
            this.sortDir = sortDir;
        }
        else
        {
            this.sortDir = TableConstants.SORT_ASC;
        }
    }

    /**
     * Reverses the sorting direction.
     */
    public void toggleSortDir()
    {
        if(this.sortDir == TableConstants.SORT_ASC)
        {
            this.sortDir = TableConstants.SORT_DESC;
        }
        else
        {
            this.sortDir = TableConstants.SORT_ASC;
        }
    }

    /**
     * Returns the filters defined for this.
     *
     * @return the array of filters
     */
    public TableFilter[] getFilters()
    {
        return filters;
    }

    /**
     * Sets a filter for this table view.
     *
     * @param filter a new filter
     */
    public void addFilter(TableFilter filter)
    {
        List filtersList = new ArrayList(Arrays.asList(filters));
        int i = filtersList.indexOf(filter);
        if(i == -1)
        {
            filtersList.add(filter);
        }
        filters = new TableFilter[filtersList.size()];
        filters = (TableFilter[])(filtersList.toArray(filters));
    }

    /**
     * Removes a filter from this table view.
     *
     * @param filter a removed filter
     */
    public void removeFilter(TableFilter filter)
    {
        List filtersList = new ArrayList(Arrays.asList(filters));
        int i = filtersList.indexOf(filter);
        if(i != -1)
        {
            filtersList.remove(filter);
        }
        filters = new TableFilter[filtersList.size()];
        filters = (TableFilter[])(filtersList.toArray(filters));
    }

    /**
     * Removes all filters from this table view.
     */
    public void clearFilters()
    {
        filters = new TableFilter[0];
    }

    /**
     * Returns the current page
     *
     * @return the current page
     */
    public int getCurrentPage()
    {
        return currentPage;
    }

    /**
     * Sets the current page
     *
     * @param currentPage the current page
     */
    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    /**
     * Returns the page size
     *
     * @return the page size
     */
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * Sets the maximal number of pages
     *
     * @param pageSize the number of pages
     */
    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    /**
     * Returns current viewType, default is: <code>{@link
     * TableConstants#VIEW_AS_LIST}</code>.
     * @see TableConstants#VIEW_AS_LIST
     * @see TableConstants#VIEW_AS_TREE
     * @return the current viewType
     */
    public int getViewType()
    {
        return viewType;
    }

    /**
     * Sets the viewType, if a given value is not equal to
     * <code>{@link TableConstants#VIEW_AS_LIST}</code> or
     * <code>{@link TableConstants#VIEW_AS_TREE}</code>,
     * a default value (<code>{@link TableConstants#VIEW_AS_LIST}</code>)
     * is set.
     *
     * @param viewType the viewType constant
     */
    public void setViewType(int viewType)
    {
        if(   viewType == TableConstants.VIEW_AS_LIST
           || viewType == TableConstants.VIEW_AS_TREE)
        {
            this.viewType = viewType;
        }
        else
        {
            this.viewType = TableConstants.VIEW_AS_LIST;
        }
    }

    /**
     * Returns the multi select option.
     *
     * @return the multi select.
     */
    public boolean getMultiSelect()
    {
        return multiSelect;
    }

    /**
     * Sets the multiselect switch.
     *
     * @param multi the multiselect switch.
     */
    public void setMultiSelect(boolean multi)
    {
        this.multiSelect = multi;
    }

    /**
     * Returns the showRoot option.
     *
     * @return the showRoot option value.
     */
    public boolean getShowRoot()
    {
        return showRoot;
    }

    /**
     * Sets the showRoot switch.
     *
     * @param showRoot the showRoot switch.
     */
    public void setShowRoot(boolean showRoot)
    {
        this.showRoot = showRoot;
    }

    /** Getter for property maxVisibleDepth, zero value means that there is
     * no limit.
     * @return Value of property maxVisibleDepth.
     */
    public int getMaxVisibleDepth()
    {
        return maxVisibleDepth;
    }

    /** Setter for property maxVisibleDepth.
     * @param maxVisibleDepth New value of property maxVisibleDepth.
     */
    public void setMaxVisibleDepth(int maxVisibleDepth)
    {
        this.maxVisibleDepth = maxVisibleDepth;
    }
}
