// 
// Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//
package org.objectledge.table;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableToolTest.java,v 1.4 2006-02-07 13:15:03 zwierzem Exp $
 */
public class TableToolTest extends TestCase
{
	private TableState state;

    /**
     * Constructor for TableToolTest.
     * @param arg0
     */
    public TableToolTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TableToolTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        state = new TableState(1);
		state.setTreeView(true);
		state.setExpanded( new String[] { "0", "1", "2", "5" } );
    }
    
	public void testGetId() throws TableException
    {
		TableTool table = new TableTool(state, null, new MockTableModel());
    	assertEquals(table.getId(), state.getId());
    }
    
	public void testGetAscSort() throws TableException
    {
		TableTool table = new TableTool(state, null, new MockTableModel());
    	assertEquals(table.getAscSort(), state.getAscSort());
    }
    
	public void testGetColumn() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		
    	try
        {
            TableColumn column = table.getColumn("column1");
            assertNotNull(column.getComparator());
			column = table.getColumn("column2");
			assertNull(column.getComparator());
			column = table.getColumn("column3");
			assertNull(column.getComparator());
        }
        catch (TableException e)
        {
        	fail("could not get column objects");
        }
	}
	
	public void testGetCurrentPage() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getCurrentPage(), 1);
	}
	
	public void testGetNumPages() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getNumPages(), 1);
	}
	
	public void testGetPageNumber() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getPageNumber(10), 1);
		assertEquals(table.getPageNumber(1), 1);
		assertEquals(table.getPageNumber(-10), 1);
	}

	public void testGetPageRowCount() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getPageRowCount(), 8);
	}

	public void testGetPageSize() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getPageSize(), 0);
	}

	public void testGetParent() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		List rows = table.getRows();
		assertEquals(table.getParent((TableRow) rows.get(1)), rows.get(0));
		assertEquals(table.getParent((TableRow) rows.get(4)), rows.get(2));
		assertNull(table.getParent((TableRow) rows.get(0)));
	}

	public void testGetRelativePageNumber() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getRelativePageNumber(-2), 1);
		assertEquals(table.getRelativePageNumber(2), 1);
	}

	public void testGetReverseAncestors() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		List rows = table.getRows();
		List revAncestors = table.getReverseAncestors((TableRow) rows.get(4));
		assertEquals(revAncestors.get(0), rows.get(2));
		assertEquals(revAncestors.get(1), rows.get(1));
		assertEquals(revAncestors.get(2), rows.get(0));
	}

	public void testGetRootRow() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		List rows = table.getRows();
		assertEquals(table.getRootRow(), rows.get(0));
	}

	public void testGetRows() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		List rows = table.getRows();
		assertEquals(rows.size(), 8);
	}

	public void testGetShowRoot() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getShowRoot(), state.getShowRoot());
    }

	public void testGetSortColumn() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertNull(table.getSortColumn());
	}

	public void testGetTotalRowCount() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getTotalRowCount(), 8);
	}

	public void testGetViewAsTree() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		assertEquals(table.getViewAsTree(), state.getTreeView());
	}

	public void testHasMoreChildren() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		List rows = table.getRows();
		try
		{
			table.hasMoreChildren((TableRow) rows.get(4), (TableRow) rows.get(4));
			fail("should throw an exception");
		}
		catch (Exception e)
		{
			// ok
		}

		try
		{
			table.hasMoreChildren((TableRow) rows.get(1), (TableRow) rows.get(0));
			fail("should throw an exception");
		}
		catch (Exception e)
		{
			// ok
		}
		
		assertTrue(table.hasMoreChildren((TableRow) rows.get(0), (TableRow) rows.get(1)));
		assertTrue(table.hasMoreChildren((TableRow) rows.get(0), (TableRow) rows.get(2)));
		assertTrue(table.hasMoreChildren((TableRow) rows.get(0), (TableRow) rows.get(3)));
		assertTrue(table.hasMoreChildren((TableRow) rows.get(0), (TableRow) rows.get(4)));
		assertFalse(table.hasMoreChildren((TableRow) rows.get(0), (TableRow) rows.get(5)));
		assertFalse(table.hasMoreChildren((TableRow) rows.get(0), (TableRow) rows.get(6)));
		assertFalse(table.hasMoreChildren((TableRow) rows.get(0), (TableRow) rows.get(7)));

		assertFalse(table.hasMoreChildren((TableRow) rows.get(1), (TableRow) rows.get(2)));
		assertFalse(table.hasMoreChildren((TableRow) rows.get(1), (TableRow) rows.get(3)));
		assertFalse(table.hasMoreChildren((TableRow) rows.get(1), (TableRow) rows.get(4)));

		assertTrue(table.hasMoreChildren((TableRow) rows.get(2), (TableRow) rows.get(3)));
		assertFalse(table.hasMoreChildren((TableRow) rows.get(2), (TableRow) rows.get(4)));

		assertTrue(table.hasMoreChildren((TableRow) rows.get(5), (TableRow) rows.get(6)));
		assertFalse(table.hasMoreChildren((TableRow) rows.get(5), (TableRow) rows.get(7)));
	}

	public void testIsExpanded() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		List rows = table.getRows();
		for (Iterator iter = rows.iterator(); iter.hasNext();)
        {
            TableRow row = (TableRow) iter.next();
			assertEquals(table.isExpanded(row), state.isExpanded(row.getId()));
        }
	}

	public void testLinesAndFolders() throws TableException
	{
		TableTool table = new TableTool(state, null, new MockTableModel());
		List rows = table.getRows();
		
		List laf = table.linesAndFolders((TableRow) rows.get(0));
		assertEquals(laf.size(), 1);
		TableTool.LinesAndFoldersBox box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(1));
		assertEquals(laf.size(), 2);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Tminus");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(2));
		assertEquals(laf.size(), 3);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "I");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(1); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Lminus");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(3));
		assertEquals(laf.size(), 4);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "I");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "T");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(3); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(4));
		assertEquals(laf.size(), 4);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "I");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "L");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(3); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(5));
		assertEquals(laf.size(), 2);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Lminus");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(6));
		assertEquals(laf.size(), 3);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(1); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Tplus");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(7));
		assertEquals(laf.size(), 3);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(1); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Lplus");
        assertFalse(box.isIcon());
		box = (TableTool.LinesAndFoldersBox) laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());
		
		// list view
		state.setTreeView(false); 
		table = new TableTool(state, null, new MockTableModel());
		rows = table.getRows();
		
		laf = table.linesAndFolders((TableRow) rows.get(0));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(1));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(2));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(3));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(4));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(5));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(6));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders((TableRow) rows.get(7));
		assertEquals(laf.size(), 1);
		box = (TableTool.LinesAndFoldersBox) laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());
	}
    

    // implementation -----------------------------------------------------------------------------

    private class MockTableModel implements TableModel
    {
		private TableColumn[] columns;

		public MockTableModel() throws TableException
		{
			columns = new TableColumn[] {
				new TableColumn("column1", new IntegerComparator()),
				new TableColumn("column2", null)
			};
		}

	    /**
		 * {@inheritDoc}
		 */
		public TableColumn[] getColumns()
		{
			return columns;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public TableRowSet getRowSet(TableState state, TableFilter[] filters)
		{
			return new MockRowSet(state, filters);
		}
	}

    private class IntegerComparator implements Comparator
    {
        /**
         * {@inheritDoc}
         */
        public int compare(Object o1, Object o2)
        {
            return ((Integer)o1).intValue() - ((Integer)o2).intValue();
        }
    }

	private class MockRowSet implements TableRowSet
	{
		private TableState state;
		private TableRow[] pagedRows;
		private TableRow[] rows;
		private Map<TableRow, TableRow> rowsByChild = new HashMap<TableRow, TableRow>();
		
		public MockRowSet (TableState state, TableFilter[] filters)
		{
			this.state = state;
			
			rows = new TableRow[] {
				new TableRow("0", null, 0, 2, 2),
				new TableRow("1", null, 1, 1, 1),
				new TableRow("2", null, 2, 2, 2),
				new TableRow("3", null, 3, 0, 0),
				new TableRow("4", null, 3, 0, 0),
				new TableRow("5", null, 1, 2, 2),
				new TableRow("6", null, 2, 1, 0),
				new TableRow("7", null, 2, 1, 0)
			};
			
			pagedRows = rows;
			
			// child -> parent
			rowsByChild.put(rows[5], rows[0]);
			rowsByChild.put(rows[1], rows[0]);
			rowsByChild.put(rows[6], rows[5]);
			rowsByChild.put(rows[7], rows[5]);
			rowsByChild.put(rows[2], rows[1]);
			rowsByChild.put(rows[3], rows[2]);
			rowsByChild.put(rows[4], rows[2]);
		}

        /**
         * {@inheritDoc}
         */
        public TableState getState()
        {
            return state;
        }

        /**
         * {@inheritDoc}
         */
        public TableRow[] getRows()
        {
            return rows;
        }

        /**
         * {@inheritDoc}
         */
        public TableRow getRootRow()
        {
            return rows[0];
        }

        /**
         * {@inheritDoc}
         */
        public TableRow getParentRow(TableRow childRow)
        {
            return (TableRow) rowsByChild.get(childRow);
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasMoreChildren(TableRow ancestorRow, TableRow descendantRow)
        {
        	TableRow ancestor = (TableRow) rowsByChild.get(descendantRow);
        	while(ancestor != null && ancestor != ancestorRow)
        	{
				ancestor = (TableRow) rowsByChild.get(ancestor);
        	}
        	if(ancestor != null)
        	{
				// if pair of descendant -> ancestor exists - than ancestor has more children 
				return (descendantRow == rows[1] && ancestorRow == rows[0])
					|| (descendantRow == rows[2] && ancestorRow == rows[0])
					|| (descendantRow == rows[3] && ancestorRow == rows[0])
					|| (descendantRow == rows[4] && ancestorRow == rows[0])
					|| (descendantRow == rows[3] && ancestorRow == rows[2])
					|| (descendantRow == rows[6] && ancestorRow == rows[5]);
        	}
			throw new IllegalStateException("Ancestor is not a real ancestor of a given row");
        }

        /**
         * {@inheritDoc}
         */
        public int getPageRowCount()
        {
            return pagedRows.length;
        }

        /**
         * {@inheritDoc}
         */
        public int getTotalRowCount()
        {
            return rows.length;
        }
	}
}

