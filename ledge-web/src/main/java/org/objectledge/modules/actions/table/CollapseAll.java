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

package org.objectledge.modules.actions.table;

import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableConstants;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;

/**
 * Toggles expanded-all table state flag.
 * Uses following parameters:
 * <ul>
 * <li>{@link org.objectledge.table.TableConstants#TABLE_ID_PARAM_KEY} - to retreive the table state</li>
 * </ul>
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CollapseAll.java,v 1.1 2008-03-06 16:31:53 rafal Exp $
 */
public class CollapseAll
    extends BaseToggleAction
{
    /**
     * Constructs the table action.
     * @param tableStateManager used to get currently modified table state.
     */
	public CollapseAll(TableStateManager tableStateManager)
	{
		super(tableStateManager);
	}

	/** 
	 * {@inheritDoc}
	 */
	public void process(Context context)
		throws ProcessingException
	{
        Parameters parameters = RequestParameters.getRequestParameters(context); 
        String[] ids = parameters.getStrings(TableConstants.TABLE_ID_PARAM_KEY);
        for(int i = 0; i<ids.length; i++)
        {
            TableState state = tableStateManager.getState(context, new Integer(ids[i]));
            // null pointer exception protection
            if(state == null)
            {
                continue;
            }
            state.clearExpanded();
        }
    }
}