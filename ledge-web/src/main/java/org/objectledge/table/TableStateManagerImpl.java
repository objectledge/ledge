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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;

/**
 * Implementation of table state manager.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableStateManagerImpl.java,v 1.10 2006-04-21 16:04:28 zwierzem Exp $
 */
public class TableStateManagerImpl
    implements TableStateManager
{
	/** Table data session context key - <code>table_data</code>. */
	public static final String TABLE_DATA_KEY = "table_data";

	/** nextId counter */
    private int nextId = 1;

    /** id/name mapping */
    private Map<String, Integer> byNameMapping = new HashMap<String, Integer>();

    /** name/id mapping */
    private Map<Integer, String> byIdMapping = new HashMap<Integer, String>();

    // user access methods /////////////////////////////////////////////////////////////////////////

	/** 
	 * {@inheritDoc}
	 */
    public TableState getState(Context context, String name)
    {
        Integer id = getId(name);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        TableData tableData = getTableData(httpContext);
        TableState state = getTableState(tableData, id);
        if(state == null)
        {
            name = name.intern(); // save space for names in the 
            state = createTableState(name, id);
            tableData.put(id, state);
        }
        return state;
    }

    /** 
     * {@inheritDoc}
     */
    public void clearState(Context context, String name)
    {
        Integer id = getId(name);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        TableData tableData = getTableData(httpContext);
        tableData.clear(id);
    }

    /**
     * The factory method for {@link TableState} objects. Allows overriding of
     * <code>TableState</code> class.
     * 
     * @param name the name of the state
     * @param id
     * @return the newly created table state object.
     */
    protected TableState createTableState(String name, int id)
    {
        return new TableState(name, id);
    }

    /**
     * Gets the id of the table instance.
     * If id is not defined yet, it is created together with name-id
     * and id-name mappings.
     *
     * @param name the name of the instance
     * @return the id of the instance
     */
    private synchronized Integer getId(String name)
    {
        Integer id = byNameMapping.get(name);
        if(id == null)
        {
            //create id
            id = new Integer(nextId);
            nextId++;
            //create mappings
            name = name.intern();
            byNameMapping.put(name, id);
            byIdMapping.put(id, name);
        }
        return id;
    }

    // table service companion access methods //////////////////////////////////////////////////////

	/** 
	 * {@inheritDoc}
	 */
    public TableState getState(Context context, Integer id)
    {
        if(byIdMapping.get(id) == null)
        {
            return null;
        }
        HttpContext httpContext = HttpContext.getHttpContext(context);
        TableData tableData = getTableData(httpContext);
        TableState state = getTableState(tableData, id);
        return state;
    }

    // utility methods /////////////////////////////////////////////////////////////////////////////

    private final TableData getTableData(HttpContext httpContext)
    {
    	HttpSession session = httpContext.getRequest().getSession();
        TableData tableData = (TableData) session.getAttribute(TABLE_DATA_KEY);
        if(tableData == null)
        {
            tableData = new TableData();
			session.setAttribute(TABLE_DATA_KEY, tableData);
        }
        return tableData;
    }

    private final TableState getTableState(TableData tableData, Integer id)
    {
        TableState state = (TableState)tableData.get(id);
        if(state != null)
        {
            state.setOld();
        }
        return state;
    }

    /**
     * A container class for {@link TableState} instances kept in user's session.
     */
    private class TableData
    {
        private Map<Integer, TableState> map = Collections
            .synchronizedMap(new HashMap<Integer, TableState>());

        TableState get(Integer key)
        {
            return map.get(key);
        }

        void put(Integer id, TableState state)
        {
            map.put(id, state);
        }

        void clear(Integer id)
        {
            map.remove(id);
        }
    }
}
