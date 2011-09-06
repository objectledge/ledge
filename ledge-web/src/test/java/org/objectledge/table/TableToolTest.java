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
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableToolTest.java,v 1.5 2006-04-21 16:04:27 zwierzem Exp $
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
        
        state = new TableState("name", 1);
		state.setTreeView(true);
		state.setExpanded( new String[] { "0", "1", "2", "5" } );
    }
    
	public void testGetId() throws TableException
    {
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
    	assertEquals(table.getId(), state.getId());
    }
    
	public void testGetAscSort() throws TableException
    {
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
    	assertEquals(table.getAscSort(), state.getAscSort());
    }
    
	public void testGetColumn() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		
    	try
        {
            TableColumn<Object> column = table.getColumn("column1");
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
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getCurrentPage(), 1);
	}
	
	public void testGetNumPages() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getNumPages(), 1);
	}
	
	public void testGetPageNumber() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getPageNumber(10), 1);
		assertEquals(table.getPageNumber(1), 1);
		assertEquals(table.getPageNumber(-10), 1);
	}

	public void testGetPageRowCount() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getPageRowCount(), 8);
	}

	public void testGetPageSize() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getPageSize(), 0);
	}

	public void testGetParent() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		List<TableRow<Object>> rows = table.getRows();
		assertEquals(table.getParent(rows.get(1)), rows.get(0));
		assertEquals(table.getParent(rows.get(4)), rows.get(2));
		assertNull(table.getParent(rows.get(0)));
	}

	public void testGetRelativePageNumber() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getRelativePageNumber(-2), 1);
		assertEquals(table.getRelativePageNumber(2), 1);
	}

	public void testGetReverseAncestors() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		List<TableRow<Object>> rows = table.getRows();
		List<TableRow<Object>> revAncestors = table.getReverseAncestors(rows.get(4));
		assertEquals(revAncestors.get(0), rows.get(2));
		assertEquals(revAncestors.get(1), rows.get(1));
		assertEquals(revAncestors.get(2), rows.get(0));
	}

	public void testGetRootRow() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		List<TableRow<Object>> rows = table.getRows();
		assertEquals(table.getRootRow(), rows.get(0));
	}

	public void testGetRows() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		List<TableRow<Object>> rows = table.getRows();
		assertEquals(rows.size(), 8);
	}

	public void testGetShowRoot() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getShowRoot(), state.getShowRoot());
    }

	public void testGetSortColumn() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertNull(table.getSortColumn());
	}

	public void testGetTotalRowCount() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getTotalRowCount(), 8);
	}

	public void testGetViewAsTree() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		assertEquals(table.getViewAsTree(), state.getTreeView());
	}

	public void testHasMoreChildren() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		List<TableRow<Object>> rows = table.getRows();
		try
		{
			table.hasMoreChildren(rows.get(4), rows.get(4));
			fail("should throw an exception");
		}
		catch (Exception e)
		{
			// ok
		}

		try
		{
			table.hasMoreChildren(rows.get(1), rows.get(0));
			fail("should throw an exception");
		}
		catch (Exception e)
		{
			// ok
		}
		
		assertTrue(table.hasMoreChildren(rows.get(0), rows.get(1)));
		assertTrue(table.hasMoreChildren(rows.get(0), rows.get(2)));
		assertTrue(table.hasMoreChildren(rows.get(0), rows.get(3)));
		assertTrue(table.hasMoreChildren(rows.get(0), rows.get(4)));
		assertFalse(table.hasMoreChildren(rows.get(0), rows.get(5)));
		assertFalse(table.hasMoreChildren(rows.get(0), rows.get(6)));
		assertFalse(table.hasMoreChildren(rows.get(0), rows.get(7)));

		assertFalse(table.hasMoreChildren(rows.get(1), rows.get(2)));
		assertFalse(table.hasMoreChildren(rows.get(1), rows.get(3)));
		assertFalse(table.hasMoreChildren(rows.get(1), rows.get(4)));

		assertTrue(table.hasMoreChildren(rows.get(2), rows.get(3)));
		assertFalse(table.hasMoreChildren(rows.get(2), rows.get(4)));

		assertTrue(table.hasMoreChildren(rows.get(5), rows.get(6)));
		assertFalse(table.hasMoreChildren(rows.get(5), rows.get(7)));
	}

	public void testIsExpanded() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		List<TableRow<Object>> rows = table.getRows();
		for (TableRow<Object> row : rows)
        {
            
			assertEquals(table.isExpanded(row), state.isExpanded(row.getId()));
        }
	}

	public void testLinesAndFolders() throws TableException
	{
		TableTool<Object> table = new TableTool<Object>(state, null, new MockTableModel());
		List<TableRow<Object>> rows = table.getRows();
		
		List<TableTool.LinesAndFoldersBox> laf = table.linesAndFolders(rows.get(0));
		assertEquals(laf.size(), 1);
		TableTool.LinesAndFoldersBox box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(1));
		assertEquals(laf.size(), 2);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Tminus");
        assertFalse(box.isIcon());
		box = laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(2));
		assertEquals(laf.size(), 3);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "I");
        assertFalse(box.isIcon());
		box = laf.get(1); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Lminus");
        assertFalse(box.isIcon());
		box = laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(3));
		assertEquals(laf.size(), 4);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "I");
        assertFalse(box.isIcon());
		box = laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "T");
        assertFalse(box.isIcon());
		box = laf.get(3); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(4));
		assertEquals(laf.size(), 4);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "I");
        assertFalse(box.isIcon());
		box = laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "L");
        assertFalse(box.isIcon());
		box = laf.get(3); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(5));
		assertEquals(laf.size(), 2);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Lminus");
        assertFalse(box.isIcon());
		box = laf.get(1); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(6));
		assertEquals(laf.size(), 3);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = laf.get(1); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Tplus");
        assertFalse(box.isIcon());
		box = laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(7));
		assertEquals(laf.size(), 3);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "blank");
        assertFalse(box.isIcon());
		box = laf.get(1); 
		assertEquals(box.getLinkType(), "toggle-expand");
		assertEquals(box.getType(), "Lplus");
        assertFalse(box.isIcon());
		box = laf.get(2); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());
		
		// list view
		state.setTreeView(false); 
		table = new TableTool<Object>(state, null, new MockTableModel());
		rows = table.getRows();
		
		laf = table.linesAndFolders(rows.get(0));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(1));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(2));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(3));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(4));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "file");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(5));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folderopen");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(6));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());

		laf = table.linesAndFolders(rows.get(7));
		assertEquals(laf.size(), 1);
		box = laf.get(0); 
		assertEquals(box.getLinkType(), "none");
		assertEquals(box.getType(), "folder");
        assertTrue(box.isIcon());
	}
    

    // implementation -----------------------------------------------------------------------------

    private class MockTableModel implements TableModel<Object>
    {
		private TableColumn<Object>[] columns;

		public MockTableModel() throws TableException
		{
			columns = new TableColumn[] {
				new TableColumn<Object>("column1", new MockComparator()),
				new TableColumn<Object>("column2", null)
			};
		}

	    /**
		 * {@inheritDoc}
		 */
		public TableColumn<Object>[] getColumns()
		{
			return columns;
		}
		
		public TableColumn<Object> getColumn(String name)
		{
		    for(TableColumn<Object> column : columns)
	        {
	            if(column.getName().equals(name))
	            {
	                return column;
	            }
	        }
	        return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public TableRowSet<Object> getRowSet(TableState state, TableFilter<Object>[] filters)
		{
			return new MockRowSet(state, filters);
		}
	}

    private class MockComparator implements Comparator<Object>
    {
        /**
         * {@inheritDoc}
         */
        public int compare(Object o1, Object o2)
        {
            return 0;
        }
    }

	private class MockRowSet implements TableRowSet<Object>
	{
		private TableRow<Object>[] pagedRows;
		private TableRow<Object>[] rows;
		private Map<TableRow<Object>, TableRow<Object>> rowsByChild = new HashMap<TableRow<Object>, TableRow<Object>>();
		
		public MockRowSet (TableState state, TableFilter<Object>[] filters)
		{		
			rows = new TableRow[] {
				new TableRow<Object>("0", null, 0, 2, 2),
				new TableRow<Object>("1", null, 1, 1, 1),
				new TableRow<Object>("2", null, 2, 2, 2),
				new TableRow<Object>("3", null, 3, 0, 0),
				new TableRow<Object>("4", null, 3, 0, 0),
				new TableRow<Object>("5", null, 1, 2, 2),
				new TableRow<Object>("6", null, 2, 1, 0),
				new TableRow<Object>("7", null, 2, 1, 0)
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
        public TableRow<Object>[] getRows()
        {
            return rows;
        }

        /**
         * {@inheritDoc}
         */
        public TableRow<Object> getRootRow()
        {
            return rows[0];
        }

        /**
         * {@inheritDoc}
         */
        public TableRow<Object> getParentRow(TableRow<Object> childRow)
        {
            return rowsByChild.get(childRow);
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasMoreChildren(TableRow<Object> ancestorRow, TableRow<Object> descendantRow)
        {
        	TableRow<Object> ancestor = rowsByChild.get(descendantRow);
        	while(ancestor != null && ancestor != ancestorRow)
        	{
				ancestor = rowsByChild.get(ancestor);
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

