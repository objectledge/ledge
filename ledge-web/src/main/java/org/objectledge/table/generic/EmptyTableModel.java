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

import org.objectledge.table.TableColumn;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;

/**
 * An empty table model.
 *
 * <p>Useful when you need need to display a table but the actual data source
 * is not available.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EmptyTableModel.java,v 1.1 2004-02-10 17:17:46 zwierzem Exp $
 */
public class EmptyTableModel implements TableModel
{
	/** 
	 * {@inheritDoc}
	 */
    public TableRowSet getRowSet(TableState state)
    {
        return new EmptyRowSet(state);
    }

	/** 
	 * {@inheritDoc}
	 */
    public TableColumn[] getColumns()
    {
        return new TableColumn[0];
    }

    // row set implementation

	/** An empty implementation of a rowset. */
    private static class EmptyRowSet implements TableRowSet
    {
        private TableState state;

        public EmptyRowSet(TableState state)
        {
            this.state = state;
        }

        public int getPageRowCount()
        {
            return 0;
        }

        public TableRow getParentRow(TableRow childRow)
        {
            return null;
        }

        public TableRow getRootRow()
        {
            return null;
        }

        public TableRow[] getRows()
        {
            return new TableRow[0];
        }

        public TableState getState()
        {
            return state;
        }

        public int getTotalRowCount()
        {
            return 0;
        }

        public boolean hasMoreChildren(TableRow ancestorRow, TableRow descendantRow)
        {
            return false;
        }
    }
}
