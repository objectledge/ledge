// 
// Copyright (c) 2003, 2004 Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableStateTest.java,v 1.2 2004-03-05 12:14:26 zwierzem Exp $
 */
public class TableStateTest extends TestCase
{

    /**
     * Constructor for TableStateTest.
     * @param arg0
     */
    public TableStateTest(String arg0)
    {
        super(arg0);
    }

	private int id = 1;

    public void testTableState()
    {
    	TableState state = new TableState(id);
    	// check defaults
    	assertTrue(state.isNew());
    	assertFalse(state.getAllExpanded());
    	assertEquals(state.getCurrentPage(), 1);
    	assertEquals(state.getFilters().length, 0);
    	assertEquals(state.getId(), id);
    	assertEquals(state.getMaxVisibleDepth(), 0);
    	assertEquals(state.getPageSize(), 0);
    	assertEquals(state.getRootId(), "");
    	assertFalse(state.getShowRoot());
    	assertNull(state.getSortColumnName());
    	assertEquals(state.getAscSort(), true);
    	assertEquals(state.getTreeView(), false);
    	
		// check page sizes
		state.setPageSize(4);
		assertEquals(state.getPageSize(), 4);
		
		// check current page 
    	state.setCurrentPage(2);
		assertEquals(state.getCurrentPage(), 2);
		
		// check filters
		TableFilter filter = new TableFilter()
		{
			public boolean accept(Object object)
			{
				return false;
			}
		}; 
		state.addFilter(filter);
		assertEquals(state.getFilters()[0], filter);
    	state.clearFilters();
		assertEquals(state.getFilters().length, 0);
		state.addFilter(filter);
		assertEquals(state.getFilters()[0], filter);
		state.addFilter(filter);
		assertEquals(state.getFilters()[0], filter);
		assertEquals(state.getFilters().length, 1);
    	state.removeFilter(filter);
		assertEquals(state.getFilters().length, 0);
		state.removeFilter(filter);
		assertEquals(state.getFilters().length, 0);
    	
    	// check depth
    	state.setMaxVisibleDepth(2);
		assertEquals(state.getMaxVisibleDepth(), 2);
    	
    	// check old-new
    	state.setOld();
		assertFalse(state.isNew());
    	
    	// check root id
    	state.setRootId("root-id");
    	assertEquals(state.getRootId(), "root-id");
    	
    	// check show root 
    	state.setShowRoot(true);
    	assertTrue(state.getShowRoot());
    	
    	// check sort column
    	state.setSortColumnName("sort-column");
		assertEquals(state.getSortColumnName(), "sort-column");
    	
    	// check sort dir
    	state.setAscSort(false);
		assertEquals(state.getAscSort(), false);
		state.setAscSort(true);
		assertEquals(state.getAscSort(), true);
    	
    	//check view type
    	state.setTreeView(true);
		assertEquals(state.getTreeView(), true);
		state.setTreeView(false);
		assertEquals(state.getTreeView(), false);
    }
    

	public void testExpanded()
	{
		TableState state = new TableState(id);

		assertFalse(state.isExpanded("expanded-id"));
		state.setExpanded("expanded-id");
		assertTrue(state.isExpanded("expanded-id"));
		state.toggleExpanded("expanded-id");
		assertFalse(state.isExpanded("expanded-id"));
		state.toggleExpanded("expanded-id");
		assertTrue(state.isExpanded("expanded-id"));
		state.toggleExpanded("expanded-id");

		state.setAllExpanded(true);
		assertTrue(state.isExpanded("expanded-id"));
		assertTrue(state.isExpanded("expanded-id1"));
		assertTrue(state.isExpanded("expanded-id2"));

		state.setAllExpanded(false);
		assertFalse(state.isExpanded("expanded-id"));
		assertFalse(state.isExpanded("expanded-id1"));
		assertFalse(state.isExpanded("expanded-id2"));

		state.setExpanded("expanded-id");
		state.setExpanded("expanded-id1");
		state.setExpanded("expanded-id2");
		assertTrue(state.isExpanded("expanded-id"));
		assertTrue(state.isExpanded("expanded-id1"));
		assertTrue(state.isExpanded("expanded-id2"));

		state.clearExpanded();
		assertFalse(state.isExpanded("expanded-id"));
		assertFalse(state.isExpanded("expanded-id1"));
		assertFalse(state.isExpanded("expanded-id2"));

		state.setExpanded("expanded-id");
		assertTrue(state.isExpanded("expanded-id"));

		state.setCollapsed("expanded-id");
		assertFalse(state.isExpanded("expanded-id"));

		assertFalse(state.isExpanded("expanded-id1"));
		assertFalse(state.isExpanded("expanded-id2"));
		state.setExpanded(new String[] {"expanded-id1", "expanded-id2"});
		assertTrue(state.isExpanded("expanded-id1"));
		assertTrue(state.isExpanded("expanded-id2"));
	}
}
