// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableColumnTest.java,v 1.1 2004-02-12 10:10:11 zwierzem Exp $
 */
public class TableColumnTest extends TestCase
{

    /**
     * Constructor for TableColumnTest.
     * @param arg0
     */
    public TableColumnTest(String arg0)
    {
        super(arg0);
    }

    /*
     * Test for void TableColumn(String)
     */
    public void testTableColumnString()
    {
		try
		{
			TableColumn tableColumn = new TableColumn(null);
    		fail("Should raise a TableException");
		}
    	catch (TableException success) {}

		try
		{
			TableColumn tableColumn = new TableColumn("");
			fail("Should raise a TableException");
		}
		catch (TableException success) {}

		TableColumn tableColumn = null;
    	try
        {
            tableColumn = new TableColumn("name");
        }
        catch (TableException e)
        {
			fail("There should be no exceptions");
        }
        
		assertEquals(tableColumn.getName(), "name");
        assertSame(tableColumn.set("style", "width:10%;"), tableColumn);
        assertEquals(tableColumn.get("style"), "width:10%;");
        assertNull(tableColumn.getComparator());
		assertNull(tableColumn.getReverseComparator());
		assertEquals(tableColumn.isSortable(), false);
		assertEquals(tableColumn.toString(), "");
    }

    /*
     * Test for void TableColumn(String, Comparator)
     */
    public void testTableColumnStringComparator()
    throws TableException
    {
    	Comparator comparator = new Comparator()
    	{
	        public int compare(Object o1, Object o2)
	        {
	            return -1;
	        }
    	};
    	
		TableColumn tableColumn = new TableColumn("name", comparator);
		assertSame(tableColumn.getComparator(), comparator);
		assertNotNull(tableColumn.getReverseComparator());
		assertNotSame(tableColumn.getReverseComparator(), tableColumn.getComparator());
		assertEquals(tableColumn.isSortable(), true);
		assertEquals(tableColumn.getReverseComparator().compare(null, null),
					(- tableColumn.getComparator().compare(null, null)));
    }

    /*
     * Test for void TableColumn(String, Comparator, Comparator)
     */
    public void testTableColumnStringComparatorComparator()
    	throws TableException
    {
		Comparator comparator = new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return -1;
			}
		};

		Comparator reverseComparator = new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return 1;
			}
		};
    	
		TableColumn tableColumn = new TableColumn("name", comparator, reverseComparator);
		assertSame(tableColumn.getComparator(), comparator);
		assertSame(tableColumn.getReverseComparator(), reverseComparator);
		assertNotSame(tableColumn.getReverseComparator(), comparator);
		assertEquals(tableColumn.isSortable(), true);
		assertEquals(tableColumn.getReverseComparator().compare(null, null),
					(- tableColumn.getComparator().compare(null, null)));

		tableColumn = new TableColumn("name", null, reverseComparator);
		assertNotNull(tableColumn.getComparator());
		assertNotSame(tableColumn.getComparator(), reverseComparator);
		assertSame(tableColumn.getReverseComparator(), reverseComparator);
		assertEquals(tableColumn.isSortable(), true);
		assertEquals(tableColumn.getReverseComparator().compare(null, null),
					(- tableColumn.getComparator().compare(null, null)));
    }
}
