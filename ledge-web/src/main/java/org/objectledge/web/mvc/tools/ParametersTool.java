//
//Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

package org.objectledge.web.mvc.tools;

import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.mvc.MVCContext;

/**
 * Give a read only access to request parameters including MVC parameters.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ParametersTool.java,v 1.8 2005-08-22 14:13:37 zwierzem Exp $
 */
public class ParametersTool
extends AnyParametersTool
{
    private MVCContext mvcContext;

    /**
     * Creates the parameters tool for a given set of parameters and mvc context.
     * 
     * @param mvcContext the MVC context providing info about currenlty selected action and view.
     * @param parameters the parameters to be represented by the tool
     */
    public ParametersTool(MVCContext mvcContext, RequestParameters parameters)
    {
       super(parameters);
       this.mvcContext = mvcContext;
    }

    /**
     * Returns the action paremeter.
     * 
     * @return the value of action parameter.
     */
    public String getAction()
    {
        return mvcContext.getAction();
    }
    
    /**
     * Returns the view paremeter.
     *
     * @return the value of view parameter.
     */
    public String getView()
    {
        return mvcContext.getView();
    }
    
    /**
     * Checks if the parameter was passed in through request path info.
     * 
     * @param name name of the parameter.
     * @return <code>true</code> if the parameter was passed in through path info.
     */
    public boolean isPathInfoParameter(String name)
    {
        return ((RequestParameters)parameters).isPathInfoParameter(name);
    }

    /**
     * Checks if the parameter was passed in through request query string.
     * 
     * @param name name of the parameter.
     * @return <code>true</code> if the parameter was passed in through request query string.
     */
    public boolean isQueryStringParameter(String name)
    {
        return ((RequestParameters)parameters).isQueryStringParameter(name);
    }
    
    /**
     * Checks if the parameter was passed in through POST request body.
     * 
     * @param name name of the parameter.
     * @return <code>true</code> if the parameter was passed through POST request body.
     */
    public boolean isPOSTParameter(String name)
    {
        return ((RequestParameters)parameters).isPOSTParameter(name);
    }    
}
