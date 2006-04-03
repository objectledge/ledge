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

/**
 * TableModel interface defines a tree/list data source. It must be implemented to provide objects
 * the chosen implementation of <code>{@link TableRowSet}</code> interface. A different approach is
 * to implement an <code>{@link ExtendedTableModel}</code> and use
 * default implementations of <code>{@link TableRowSet}</code> -
 * <code>{@link org.objectledge.table.generic.GenericListRowSet}</code> or 
 * <code>{@link org.objectledge.table.generic.GenericTreeRowSet}</code>.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableModel.java,v 1.6 2006-04-03 18:38:51 zwierzem Exp $
 */
public interface TableModel<T>
{
    /**
     * Returns a {@link TableRowSet} object initialised by this model, a given {@link TableState}
     * and set of filters defined for the view.
     *
     * @param state the parent
     * @param filters a list of filters to be used while creating the rows set
     * @return the row set object which allows access to tree/list objects.
     */
    public TableRowSet<T> getRowSet(TableState state, TableFilter<T>[] filters);

    /**
     * Returns array of column definitions. They defitinions should be created on every call,
     * because they may be modified during it's lifecycle 
     * (see {@link TableColumn#set(String, Object)}).
     *
     * @return array of <code>TableColumn</code> objects
     */
    public TableColumn<T>[] getColumns();
}
