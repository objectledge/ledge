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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A Velocity context table view tool to help build the list and tree.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: TableTool.java,v 1.5 2004-03-16 15:36:42 zwierzem Exp $
 */
public class TableTool
{
    /** table state */
    private TableState state;

    /** table model */
    private TableRowSet rowSet;

    /** column map */
    private Map columnsByName = new HashMap();

    /**
     * Constructor for basic implementation of TableRowSet.
     *
     * @param state the state of the table instance
     * @param model the table model
     * @throws TableException on construction errors
     */
    public TableTool(TableState state, TableModel model)
    throws TableException
    {
        this.state = state;
        this.rowSet = model.getRowSet(state);

        // prepare the column map
        TableColumn[] columns = model.getColumns();
        for(int i=0; i<columns.length; i++)
        {
            TableColumn column = columns[i];
            if(columnsByName.containsKey(column.getName()))
            {
                throw new TableException("Duplicate table column name '"+column.getName()+"'");
            }
            columnsByName.put(column.getName(), column);
        }
    }

    /**
     * Gets the numeric table state id.
     *
     * @return the id of the table instance
     */
    public int getId()
    {
        return state.getId();
    }

    /**
     * Returns the current view type of the table.
     *
     * @return current view
     */
    public boolean getViewAsTree()
    {
        return state.getTreeView();
    }

    /**
     * Returns the root row of the table.
     *
     * @return the root row of the table
     */
    public TableRow getRootRow()
    {
        return rowSet.getRootRow();
    }

    /**
     * Informs about root visibility.
     *
     * @return <code>true</code> if root row should be shown
     */
    public boolean getShowRoot()
    {
        return state.getShowRoot();
    }

    /**
     * Returns the column definition.
     *
     * @param name the name of the column
     * @return the <code>TableColumn</code> object
     * @throws TableException on error in table column retrieval/construction
     */
    public TableColumn getColumn(String name)
    throws TableException
    {
        TableColumn column = (TableColumn)columnsByName.get(name);
        if(column == null)
        {
            column = new TableColumn(name);
            columnsByName.put(name, column);
        }
        return column;
    }

    /**
     * Returns the column definition by which the table is sorted.
     *
     * @return the <code>TableColumn</code> object
     */
    public TableColumn getSortColumn()
    {
        String sortColumnName = state.getSortColumnName();
        return (TableColumn)columnsByName.get(sortColumnName);
    }

    /**
     * Returns the direction of sorting.
     *
     * @return <code>true</code> if sorting direction is set to ascending.
     */
    public boolean getAscSort()
    {
        return state.getAscSort();
    }

    /**
     * Return the number of the current page of the table.
	 * TODO: Add pageNumber sanity check either here or in RowSet.
	 * @return the current page
     */
    public int getCurrentPage()
    {
        return state.getCurrentPage();
    }

    /**
     * Return the size of the page.
     *
     * @return the size of the page
     */
    public int getPageSize()
    {
        return state.getPageSize();
    }

    /**
     * Return the number of pages in table.
     *
     * @return the number of visible pages
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
     * Return the sanitized page number.
     * @param i a page number to be sanitized.
     * @return the page number
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
     * @param page a page number to be added to current page and sanitized.
     * @return the page number
     */
    public int getRelativePageNumber(int page)
    {
        return getPageNumber(page + getCurrentPage());
    }

    /**
     * Return the number of elements in returned array.
     *
     * @return the size of returned array
     */
    public int getPageRowCount()
    {
        return rowSet.getPageRowCount();
    }

    /**
     * Return the total number of rows in row set represented by this
     * table tool.
     *
     * @return total number of rows
     */
    public int getTotalRowCount()
    {
        return rowSet.getTotalRowCount();
    }

    /**
     * Returns the list of {@link TableRow} objects representing the resource
     * tree.
     *
     * @return a list of tree nodes.
     */
    public List getRows()
    {
        return Arrays.asList(rowSet.getRows());
    }

    /**
     * Returns the list of ancestors for the row, it does include root row,
     * but does not include current row.
     * This method is used to build tree with lines.
     *
     * @param row the examinated row
     * @return the list of the ancestors
     */
    public List getAncestors(TableRow row)
    {
        List ancestors = new ArrayList();
        TableRow rootRow = rowSet.getRootRow();

        TableRow parentRow = row;
        while(parentRow != rootRow)
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
    public List getReverseAncestors(TableRow row)
    {
        // a little bit slower, but less code
        List list = getAncestors(row);
        Collections.reverse(list);
        return list;
    }

    /**
     * Return the parent of the row - delegate this method to model.
     * 
     * @param ancestor the ancestor row
     * @param descendant the descendant row
     * @return <code>true</code> if ancestor has more children.
     */
    public boolean hasMoreChildren(TableRow ancestor, TableRow descendant)
    {
        return rowSet.hasMoreChildren(ancestor, descendant);
    }

    /**
     * Return the parent row of a given row.
     *
     * @param row the child row
     * @return the parent row, or null for root node.
     */
    public TableRow getParent(TableRow row)
    {
        return rowSet.getParentRow(row);
    }

    /**
     * Checks if a row is expanded.
     *
     * @param row the row
     * @return <code>true</code> if row is expanded
     */
    public boolean isExpanded(TableRow row)
    {
        return state.isExpanded(row.getId());
    }
    
    /**
     * Returns a list of {@link LinesAndFoldersBox} objects for a given row to help building tree 
     * like structures. 
     * @param row the row for which the lines and folders data will be generated
     * @return list of {@link LinesAndFoldersBox} 
     */
    public List linesAndFolders(TableRow row)
    {
		List linesAndFolders = new ArrayList();
		if( ! getViewAsTree() )
		{
			// -- list view
			if (row.getChildCount() == 0) 
			{
				linesAndFolders.add(laFfile);
			}
			else if(row.getVisibleChildCount() > 0)
			{
				linesAndFolders.add(laFfolderopen);
			}
			else
			{
				linesAndFolders.add(laFfolder);
			}
			return linesAndFolders;
		}

		// -- tree view
		
		// only root
		if(getRootRow() == row)
		{
			linesAndFolders.add(laFfolderopen);
			return linesAndFolders;
		}

		// other rows
		TableRow parent = getParent(row);
		// -- lines generation START
		for (Iterator iter = getAncestors(row).iterator(); iter.hasNext();)
        {
            TableRow ancestor = (TableRow) iter.next();
			if(ancestor != parent)
			{
				if(hasMoreChildren(ancestor, row))
				{
					linesAndFolders.add(laFI);
				}
				else
				{
					linesAndFolders.add(laFblank);
				}
			}
        }
		// -- lines generation END
		// -- plus minus and file/folder generation START
		if (row.getChildCount() == 0)
		{
            if (hasMoreChildren(parent, row))
            {
                linesAndFolders.add(laFT);
            }
            else
            {
                linesAndFolders.add(laFL);
            }
            linesAndFolders.add(laFfile);
        }
        else // getChildCount() > 0
        {
            // allow toggle expand links attachment
            if (hasMoreChildren(parent, row))
            {
                if (row.getVisibleChildCount() > 0)
                {
                    linesAndFolders.add(laFTminus); // link on this element
                    linesAndFolders.add(laFfolderopen);
                }
                else
                {
                    linesAndFolders.add(laFTplus); // link on this element
                    linesAndFolders.add(laFfolder);
                }
            }
            else
            {
                if (row.getVisibleChildCount() > 0)
                {
                    linesAndFolders.add(laFLminus); // link on this element
                    linesAndFolders.add(laFfolderopen);
                }
                else
                {
                    linesAndFolders.add(laFLplus); // link on this element
                    linesAndFolders.add(laFfolder);
                }
            }
        }
        // -- plus minus and file/folder generation END
		return linesAndFolders;
    }

	private static LinesAndFoldersBox laFI = new LinesAndFoldersBox("I");
	private static LinesAndFoldersBox laFL = new LinesAndFoldersBox("L");
	private static LinesAndFoldersBox laFT = new LinesAndFoldersBox("T");
	private static LinesAndFoldersBox laFblank = new LinesAndFoldersBox("blank");
	private static LinesAndFoldersBox laFLplus = new LinesAndFoldersBox("Lplus", "toggle-expand");
	private static LinesAndFoldersBox laFLminus = new LinesAndFoldersBox("Lminus", "toggle-expand");
	private static LinesAndFoldersBox laFTplus = new LinesAndFoldersBox("Tplus", "toggle-expand");
	private static LinesAndFoldersBox laFTminus = new LinesAndFoldersBox("Tminus", "toggle-expand");
	private static LinesAndFoldersBox laFfolder = new LinesAndFoldersBox("folder");
	private static LinesAndFoldersBox laFfile = new LinesAndFoldersBox("file");
	private static LinesAndFoldersBox laFfolderopen = new LinesAndFoldersBox("folderopen");

	/** 
	 * Represents an element of "lines and folders" line generated for a tree row.
	 */  
	public static class LinesAndFoldersBox
	{
		private String type;
		private String linkType = "none";

		/**
		 * Creates a box of a given type with undefined link type.
		 * @param type type of the box
		 */
		public LinesAndFoldersBox(String type)
		{
			this.type = type;
		}
		
		/**
		 * Creates a box of a given type and link type.
		 * @param type type of the box
		 * @param linkType type of the link
		 */
		public LinesAndFoldersBox(String type, String linkType)
		{
			this(type);
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
		 * This boxes type, one of:
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
	}

	/*
	 * Sanitizes the number of the current page of the table - set a proper number in the state.
	 * TODO: Add pageNumber sanity check either here or in RowSet.
	 *
	 * @return the sanitized page number 
	 */
/*	public int sanitizeCurrentPage(TableState state, int numberOfPages)
	{
		int perPage = state.getPageSize();
		if(perPage == 0)
		{
			// automatic page number reset
			state.setCurrentPage(0);
		}
		else
		{
			int maxPage = numberOfPages;
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
	}*/
}
