// 
//Copyright (c) 2003, 2004 Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A Velocity context table view tool to help build the list and tree.
 * 
 * <p>
 * TableTool is a core part of the template designer API. It provides access to data of the
 * presented list or tree. Following is a description of methods used to access this data.
 * </p>
 * 
 * <h2>
 * Basic data access
 * </h2>
 * <ul>
 * <li>{@link #getRows()}</li>
 * <li>{@link #getPageRowCount()}</li>
 * <li>{@link #getTotalRowCount()}</li>
 * <li>{@link #getId()}</li>
 * </ul>
 *  
 * <h2>
 * Paging information
 * </h2>
 * <ul>
 * <li>{@link #getNumPages()}</li>
 * <li>{@link #getCurrentPage()}</li>
 * <li>{@link #getPageSize()}</li>
 * <li>{@link #getStartRow()}</li>
 * <li>{@link #getEndRow()}</li>
 * <li>{@link #getPageNumber(int)}</li>
 * <li>{@link #getRelativePageNumber(int)}</li>
 * </ul>
 * 
 * <h2>
 * Sorting information
 * </h2>
 * <ul>
 * <li>{@link #getSortColumn()}</li>
 * <li>{@link #getAscSort()}</li>
 * <li>{@link #getColumn(String)}</li>
 * <li>{@link #getColumns()}</li>
 * </ul>
 * 
 * <h2>
 * Tree data access
 * </h2>
 * <ul>
 * <li>{@link #getAncestors(TableRow)}</li>
 * <li>{@link #getParent(TableRow)}</li>
 * <li>{@link #getRootRow()}</li>
 * <li>{@link #getShowRoot()}</li>
 * <li>{@link #getViewAsTree()}</li>
 * <li>{@link #hasMoreChildren(TableRow, TableRow)}</li>
 * <li>{@link #isAllExpanded()}</li>
 * <li>{@link #isExpanded(TableRow)}</li>
 * <li>{@link #linesAndFolders(TableRow)}</li>
 * </ul>
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: TableTool.java,v 1.18 2006-03-29 15:10:57 zwierzem Exp $
 */
public class TableTool<T>
{
    /** table state */
    private TableState state;

    /** table model */
    private TableRowSet<T> rowSet;

    /** column map */
    private Map<String, TableColumn<T>> columnsByName = new HashMap<String, TableColumn<T>>();

    /** cached list of rows */
    private List<TableRow<T>> rows;
    
    /**
     * Constructor for basic implementation of TableRowSet.
     *
     * @param state the state of the table instance
     * @param filters a list of filters to be used while creating the rows set
     * @param model the table model
     * @throws TableException on construction errors
     */
    @SuppressWarnings("unchecked")
    public TableTool(TableState state, List<TableFilter<T>> filters, TableModel<T> model)
    throws TableException
    {
        this.state = state;
        TableFilter<T>[] filtersArray = null;
        if(filters != null)
        {
            filtersArray = new TableFilter[filters.size()];
            filters.toArray(filtersArray);
        }
        this.rowSet = model.getRowSet(state, filtersArray);

        // prepare the column map
        TableColumn<T>[] columns = model.getColumns();
        for(int i=0; i<columns.length; i++)
        {
            TableColumn<T> column = columns[i];
            if(columnsByName.containsKey(column.getName()))
            {
                throw new TableException("Duplicate table column name '"+column.getName()+"'");
            }
            columnsByName.put(column.getName(), column);
        }
    }

    /**
     * Gets the numeric table state id, this value has to be used as
     * {@link TableConstants#TABLE_ID_PARAM_KEY} parameter value used by table toolki actions,
     * such as {@link org.objectledge.modules.actions.table.SetPage}. 
     *
     * @return the id of the table instance
     */
    public int getId()
    {
        return state.getId();
    }

    /**
     * Informs about the current view type of the table - list or tree.
     *
     * @return <code>true</code> if the list of rows is prepared in tree mode. 
     */
    public boolean getViewAsTree()
    {
        return state.getTreeView();
    }

    /**
     * Returns the root row of the table.
     *
     * @return the root row of the table, may be null.
     */
    public TableRow<T> getRootRow()
    {
        return rowSet.getRootRow();
    }

    /**
     * Informs about root visibility.
     *
     * @return <code>true</code> if root row is included in the list of rows.
     */
    public boolean getShowRoot()
    {
        return state.getShowRoot();
    }

    /**
     * Returns the list of columns defined in this tool.
     * <p>
     * Returned columns list contains alphabetically sorted column definition objects created
     * by the {@link TableModel} and added by the TableTool user.
     * </p>
     *
     * @return the sorted list of <code>TableColumn</code> objects.
     */
    public List<TableColumn<T>> getColumns()
    throws TableException
    {
        List<TableColumn<T>> lcolumns = new ArrayList<TableColumn<T>>(columnsByName.values());
        Collections.sort(lcolumns);
        return lcolumns;
    }

    /**
     * Returns the column definition.
     *
     * @param name the name of the column
     * @return the <code>TableColumn</code> object
     */
    public TableColumn<T> getColumn(String name)
    throws TableException
    {
        TableColumn<T> column = columnsByName.get(name);
        if(column == null)
        {
            column = new TableColumn<T>(name);
            columnsByName.put(name, column);
        }
        return column;
    }

    /**
     * Returns the column definition by which the table rows are sorted.
     * This method can be used to build <i>sorter</i> interface links.
     *
     * @return the <code>TableColumn</code> object
     */
    public TableColumn<T> getSortColumn()
    {
        String sortColumnName = state.getSortColumnName();
        return columnsByName.get(sortColumnName);
    }

    /**
     * Tells about the direction of sorting.
     * This method can be used to build <i>sorter</i> interface links.
     *
     * @return <code>true</code> if sorting direction is set to ascending.
     */
    public boolean getAscSort()
    {
        return state.getAscSort();
    }

    /**
     * Return the number of the current page of the table, the number is sanitized (ie. cannot be
     * smaller than 1 and larger than {@link #getNumPages()}) before return.
     * This method should be used to build <i>pager</i> interface.
     * 
	 * @return the current page
     */
    public int getCurrentPage()
    {
        return sanitizeCurrentPage();
    }

    /**
     * Return the number of the first row shown on the current page.
     * This method can be used to build <i>pager</i> interface.
     * 
     * @return the number of the start row.
     */
    public int getStartRow()
    {
        int page = state.getCurrentPage();
        int perPage = state.getPageSize();

        if(page > 0 && perPage > 0)
        {
            return (page-1)*perPage + 1; // inclusive, indexing from 1
        }
        return 1;
    }
    
    /**
     * Return the number of the last row shown on the current page.
     * This method can be used to build <i>pager</i> interface.
     * 
     * @return the number of the last row.
     */
    public int getEndRow()
    {
        int page = state.getCurrentPage();
        int perPage = state.getPageSize();

        int endRow = getTotalRowCount();
        if(page > 0 && perPage > 0)
        {
            int pageEndRow = page*perPage;  // exclusive, indexing from 1
            if(pageEndRow < endRow)
            {
                endRow = pageEndRow;
            }
        }
        return endRow;
    }

    /**
     * Return the currently set size of the page.
     * This method can be used to build <i>pager</i> interface.
     *
     * @return the size of the page
     */
    public int getPageSize()
    {
        return state.getPageSize();
    }

    /**
     * Return the total number of pages in represented table.
     * This method can be used to build <i>pager</i> interface.
     *
     * @return the total number of pages.
     */
    public int getNumPages()
    {
        int perPage = state.getPageSize();
        if(perPage == 0)
        {
            return 1;
        }
        int listSize = rowSet.getTotalRowCount();
		int numPages = listSize / perPage;
		numPages += (listSize % perPage > 0) ? 1: 0;
        return numPages;
    }

    /**
     * For a requested page number, return the sanitized page number.
     * This method can be used to build <i>pager</i> interface.
     * 
     * @param i a requested page number to be sanitized.
     * @return the sanitized page number.
     */
    public int getPageNumber(int i)
    {
        if(i < 1)
        {
            return 1;
        }
        int max = getNumPages();
        if(i > max)
        {
            return max;
        }
        return i;
    }

    /**
     * Return the sanitized page number relative to current page.
     * This method can be used to build <i>pager</i> interface.
     * 
     * @param page a page number to be added to current page and sanitized.
     * @return the page number
     */
    public int getRelativePageNumber(int page)
    {
        return getPageNumber(page + getCurrentPage());
    }

    /**
     * Return the number of rows in the list returned by {@link #getRows()}. For the last page the
     * number of rows may be smaller than the current size of the page ({@link #getPageSize()})
     * since the total number of rows ({@link #getTotalRowCount()} may be not divisible by the page
     * size.  
     *
     * @return the number of presented rows.
     */
    public int getPageRowCount()
    {
        return rowSet.getPageRowCount();
    }

    /**
     * Return the total number of rows in row set represented by this table tool.
     *
     * @return total number of rows.
     */
    public int getTotalRowCount()
    {
        return rowSet.getTotalRowCount();
    }

    /**
     * Returns the list of {@link TableRow} objects representing the tree or list of objects.
     * <p>
     * The list contains only those rows which are presented on the current page. The list is
     * filtered and sorted. In case of trees the list is ordered in a way which allows proper
     * display in file explorer manner.
     * </p>
     *
     * @return a list of tree/list nodes.
     */
    public List<TableRow<T>> getRows()
    {
        if(rows == null)
        {
            rows = Arrays.asList(rowSet.getRows());  
        }
        return rows;
    }

    /**
     * Returns the list of ancestors for the row, it does include root row,
     * but does not include current row.
     * This method can be used to build tree with lines.
     *
     * @param row the examinated row
     * @return the list of the ancestors
     */
    public List<TableRow<T>> getAncestors(TableRow<T> row)
    {
        List<TableRow<T>> ancestors = new LinkedList<TableRow<T>>();
        TableRow<T> rootRow = rowSet.getRootRow();

        TableRow<T> parentRow = row;
        while(parentRow != null && parentRow != rootRow)
        {
            parentRow = rowSet.getParentRow(parentRow);
            ancestors.add(0, parentRow);
        }
        return ancestors;
    }

    /**
     * Return the list of ancestors for the row in reversed order.
     * This method is used to build trees with closing markup - for instance
     * XML documents.
     *
     * @param row the examinated row
     * @return the list of the ancestors
     */
    public List<TableRow<T>> getReverseAncestors(TableRow<T> row)
    {
        // a little bit slower, but less code
        List<TableRow<T>> list = getAncestors(row);
        Collections.reverse(list);
        return list;
    }

    /**
     * Checks whether the ancestor has more children
     * (ie. the decendant is not a descendant of last child of ancestor).
     * - delegate this method to model.
     * 
     * @param ancestor the ancestor row
     * @param descendant the descendant row
     * @return <code>true</code> if ancestor has more children.
     */
    public boolean hasMoreChildren(TableRow<T> ancestor, TableRow<T> descendant)
    {
        return rowSet.hasMoreChildren(ancestor, descendant);
    }

    /**
     * Return the parent row of a given row.
     *
     * @param row the child row
     * @return the parent row, or null for root node.
     */
    public TableRow<T> getParent(TableRow<T> row)
    {
        return rowSet.getParentRow(row);
    }

    /**
     * Checks if a row is expanded.
     *
     * @param row the row
     * @return <code>true</code> if row is expanded
     */
    public boolean isExpanded(TableRow<T> row)
    {
        return state.isExpanded(row.getId());
    }
    
    /**
     * Checks if the tree is forced to expand completely.
     * 
     * @return <code>true</code> if the tree is forced to expand completely.
     */
    public boolean isAllExpanded()
    {
        return state.getAllExpanded();
    }

    /**
     * Returns a list of {@link LinesAndFoldersBox} objects for a given row to help building tree 
     * like structures. 
     * @param row the row for which the lines and folders data will be generated
     * @return list of {@link LinesAndFoldersBox} 
     */
    public List<LinesAndFoldersBox> linesAndFolders(TableRow<T> row)
    {
		List<LinesAndFoldersBox> linesAndFolders = new ArrayList<LinesAndFoldersBox>();
		if( ! getViewAsTree() )
		{
			// -- list view
			if (row.getChildCount() == 0) 
			{
				linesAndFolders.add(LF_FILE);
			}
			else if(row.getVisibleChildCount() > 0)
			{
				linesAndFolders.add(LF_FOLDEROPEN);
			}
			else
			{
				linesAndFolders.add(LF_FOLDER);
			}
			return linesAndFolders;
		}

		// -- tree view
		
		// only root
		if(getRootRow() == row)
		{
			linesAndFolders.add(LF_FOLDEROPEN);
			return linesAndFolders;
		}

		// other rows
		TableRow<T> parent = getParent(row);
		// -- lines generation START
		for (TableRow<T> ancestor : getAncestors(row))
        {
			if(ancestor != parent)
			{
				if(hasMoreChildren(ancestor, row))
				{
					linesAndFolders.add(LF_I);
				}
				else
				{
					linesAndFolders.add(LF_BLANK);
				}
			}
        }
		// -- lines generation END
		// -- plus minus and file/folder generation START
		if (row.getChildCount() == 0)
		{
            if (hasMoreChildren(parent, row))
            {
                linesAndFolders.add(LF_T);
            }
            else
            {
                linesAndFolders.add(LF_L);
            }
            linesAndFolders.add(LF_FILE);
        }
        else // getChildCount() > 0
        {
            // allow toggle expand links attachment
            if (hasMoreChildren(parent, row))
            {
                if (row.getVisibleChildCount() > 0)
                {
                    linesAndFolders.add(LF_T_MINUS); // link on this element
                    linesAndFolders.add(LF_FOLDEROPEN);
                }
                else
                {
                    linesAndFolders.add(LF_T_PLUS); // link on this element
                    linesAndFolders.add(LF_FOLDER);
                }
            }
            else
            {
                if (row.getVisibleChildCount() > 0)
                {
                    linesAndFolders.add(LF_L_MINUS); // link on this element
                    linesAndFolders.add(LF_FOLDEROPEN);
                }
                else
                {
                    linesAndFolders.add(LF_L_PLUS); // link on this element
                    linesAndFolders.add(LF_FOLDER);
                }
            }
        }
        // -- plus minus and file/folder generation END
		return linesAndFolders;
    }

	private static final LinesAndFoldersBox LF_I = new LinesAndFoldersBox("I", true);
	private static final LinesAndFoldersBox LF_L = new LinesAndFoldersBox("L", false);
	private static final LinesAndFoldersBox LF_T = new LinesAndFoldersBox("T", true);
	private static final LinesAndFoldersBox LF_BLANK = new LinesAndFoldersBox("blank", false);
	private static final LinesAndFoldersBox LF_L_PLUS = 
		new LinesAndFoldersBox("Lplus", "toggle-expand", false);
	private static final LinesAndFoldersBox LF_L_MINUS = 
		new LinesAndFoldersBox("Lminus", "toggle-expand", false);
	private static final LinesAndFoldersBox LF_T_PLUS = 
		new LinesAndFoldersBox("Tplus", "toggle-expand", true);
	private static final LinesAndFoldersBox LF_T_MINUS = 
		new LinesAndFoldersBox("Tminus", "toggle-expand", true);
	private static final LinesAndFoldersBox LF_FOLDER = new LinesAndFoldersBox("folder", true);
	private static final LinesAndFoldersBox LF_FILE = new LinesAndFoldersBox("file", false);
	private static final LinesAndFoldersBox LF_FOLDEROPEN = new LinesAndFoldersBox("folderopen", 
        true);

	/** 
	 * Represents an element of "lines and folders" line generated for a tree row.
	 */  
	public static class LinesAndFoldersBox
	{
		private String type;
		private String linkType = "none";
        private boolean extended;

		/**
		 * Creates a box of a given type with undefined link type.
		 * @param type type of the box
         * @param extended should the box be extended with I on the next line.
		 */
		public LinesAndFoldersBox(String type, boolean extended)
		{
			this.type = type;
            this.extended = extended;
		}
		
		/**
		 * Creates a box of a given type and link type.
		 * @param type type of the box
		 * @param linkType type of the link
		 * @param extended should the box be extended with I on the next line.
		 */
		public LinesAndFoldersBox(String type, String linkType, boolean extended)
		{
			this(type, extended);
			this.linkType = linkType;
		}

        /**
         * Different from <code>none</code> if this box needs to have a link.
         * 
         * @return name of the type of the link.
         */
        public String getLinkType()
        {
            return linkType;
        }

		/**
         * Returns box type.
         * 
		 * Type is one of:
		 * <ul>
		 * <li><code>I</code> - a line</li>
		 * <li><code>L</code> - a line</li>
		 * <li><code>T</code> - a line</li>
		 * <li><code>blank</code> - empty element</li>
		 * <li><code>Lplus</code> - a widget with <code>toggle-expand</code> link type</li>
		 * <li><code>Lminus</code> - a widget with <code>toggle-expand</code> link type</li>
		 * <li><code>Tplus</code> - a widget with <code>toggle-expand</code> link type</li>
		 * <li><code>Tminus</code> - a widget with <code>toggle-expand</code> link type</li>
		 * <li><code>folder</code> - an icon</li>
		 * <li><code>file</code> - an icon</li>
		 * <li><code>folderopen</code> - an icon</li>
		 * </ul> 
		 * 
		 * @return name of the type of the box.
		 */
        public String getType()
        {
            return type;
        }
        
        /**
         * Checks if the box contains a folder or file icon.
         * 
         * @return <code>true</code> if the box contains a folder or file icon.
         */
        public boolean isIcon()
        {
            return type.equals("folder") || type.equals("file") || type.equals("folderopen");
        }
        
        /**
         * Should the box be extended with an I on the next line, when displaying a multi line
         * item.
         * 
         * @return code>true</code> if the box should be extended with an I on the next line. 
         */
        public boolean isExtended()
        {
            return extended;
        }
	}

	/**
	 * Sanitizes the number of the current page of the table - set a proper number in the state.
	 *
	 * @return the sanitized page number 
	 */
	private int sanitizeCurrentPage()
	{
		int perPage = state.getPageSize();
		if(perPage == 0)
		{
			// automatic page number reset
			state.setCurrentPage(0);
		}
		else
		{
			int maxPage = getNumPages();
			int currentPage = state.getCurrentPage();
			// automatic page number reset
			if(currentPage > maxPage)
			{
				int start = (maxPage-1)*perPage;
				start = (start < 0)? 0: start;
				state.setCurrentPage(start/perPage+1);
			}
		}
		return state.getCurrentPage();
	}
}
