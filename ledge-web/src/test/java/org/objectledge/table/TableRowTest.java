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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableRowTest.java,v 1.1 2004-02-12 10:25:25 zwierzem Exp $
 */
public class TableRowTest extends TestCase
{

    /**
     * Constructor for TableRowTest.
     * @param arg0
     */
    public TableRowTest(String arg0)
    {
        super(arg0);
    }

    public void testTableRow()
    {
    	String id = "id";
    	Integer o = new Integer(12345);
    	int depth = 2;
    	int childCount = 3;
    	int visibleChildCount = 2;
    	TableRow row = new TableRow(id, o, depth, childCount, visibleChildCount);
    	
    	assertEquals(row.getId(), id);
    	assertEquals(row.getObject(), o);
    	assertEquals(row.getDepth(), depth);
    	assertEquals(row.getChildCount(), childCount);
    	assertEquals(row.getVisibleChildCount(), visibleChildCount);
    	
		assertFalse(row.equals(o));

		TableRow row2 = new TableRow(id, null, 1, 1, 1);
		assertTrue(row.equals(row2));

		TableRow row3 = new TableRow("other-id", null, 1, 1, 1);
		assertFalse(row.equals(row3));

		assertEquals(row.hashCode(), row.hashCode());
		assertEquals(row.hashCode(), row2.hashCode());
		assertFalse(row.hashCode() == row3.hashCode());
    }
}
