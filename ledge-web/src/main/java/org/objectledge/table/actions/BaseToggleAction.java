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

package org.objectledge.table.actions;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableConstants;
import org.objectledge.table.TableStateManager;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;

/**
 * Base action class for row state toggling actions.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseToggleAction.java,v 1.1 2004-02-10 17:17:47 zwierzem Exp $
 */
public abstract class BaseToggleAction
    extends BaseTableAction
{
	/** 
	 * {@inheritDoc}
	 */
    public BaseToggleAction(TableStateManager tableStateManager)
    {
        super(tableStateManager);
    }

    /**
     * Returns id of a row for which it's state is toggled.
	 * @param context used to access request parameters and http context.
	 * @return row id selected using current request parameters
	 * @throws ProcessingException if row identification parameter is not defined for
	 * 		this request. 
     */
    public String getRowId(Context context)
        throws ProcessingException
    {
		Parameters requestParameters = RequestParameters.getRequestParameters(context);
        String rowId = requestParameters.get(TableConstants.ROW_ID_PARAM_KEY, "");
        if(rowId.length() == 0)
        {
            throw new ProcessingException("'"+TableConstants.ROW_ID_PARAM_KEY+
				"' parameter not found");
        }
        return rowId;
    }
}
