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

package org.objectledge.table.mvc.actions;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableConstants;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;

/**
 * Sets a new sorting for a given table.
 * If the same column is set, sorting direction is changed.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SetSortColumn.java,v 1.2 2004-03-05 12:14:15 zwierzem Exp $
 */
public class SetSortColumn extends BaseTableAction
{
	/** 
	 * {@inheritDoc}
	 */
	public SetSortColumn(TableStateManager tableStateManager)
	{
		super(tableStateManager);
	}

	/** 
	 * {@inheritDoc}
	 */
	public void process(Context context)
		throws ProcessingException
	{
        TableState state = getTableState(context);

        // null pointer exception protection
        if(state == null)
        {
            return;
        }

		Parameters requestParameters = RequestParameters.getRequestParameters(context);
        String sortColumnName = requestParameters.get(TableConstants.SORT_COLUMN_PARAM_KEY, "");
        if(sortColumnName.length() == 0)
        {
            throw new ProcessingException("'"+TableConstants.SORT_COLUMN_PARAM_KEY+
				"' parameter, not found");
        }

        String originalSortColumnName = state.getSortColumnName();
        if(sortColumnName.equals(originalSortColumnName))
        {
            state.setAscSort(!state.getAscSort());
        }
        else
        {
            state.setSortColumnName(sortColumnName);
        }
    }
}
