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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.objectledge.web.HttpContext;

/**
 * Implementation of table state manager.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableStateManagerImpl.java,v 1.4 2004-03-16 15:36:00 zwierzem Exp $
 */
public class TableStateManagerImpl
    implements TableStateManager
{
	/** Table data session context key - <code>table_data</code>. */
	public static final String TABLE_DATA_KEY = "table_data";

	/** nextId counter */
    private int nextId = 1;

    /** id/name mapping */
    private Map byNameMapping = new HashMap();

    /** name/id mapping */
    private Map byIdMapping = new HashMap();

    /**
     * Component constructor.
     */
    public TableStateManagerImpl()
    {
    }

    // user access methods /////////////////////////////////////////////////////////////////////////

	/** 
	 * {@inheritDoc}
	 */
    public TableState getState(HttpContext httpContext, String name)
    {
        Integer id = getId(name);
        TableData tableData = getTableData(httpContext);
        TableState state = getTableState(tableData, id);
        if(state == null)
        {
            state = new TableState(id.intValue());
            tableData.put(id, state);
        }
        return state;
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
        Integer id = (Integer)byNameMapping.get(name);
        if(id == null)
        {
            //create id
            id = new Integer(nextId);
            nextId++;
            //create mappings
            byNameMapping.put(name, id);
            byIdMapping.put(id, name);
        }
        return id;
    }

    // table service companion access methods //////////////////////////////////////////////////////

	/** 
	 * {@inheritDoc}
	 */
    public TableState getState(HttpContext httpContext, Integer id)
    {
        if(byIdMapping.get(id) == null)
        {
            return null;
        }

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
    public class TableData
    {
        private Map map = new HashMap();

        TableData()
        {
        }

        Object get(Object key)
        {
            return map.get(key);
        }

        void put(Object key, Object value)
        {
            map.put(key,value);
        }
    }
}
