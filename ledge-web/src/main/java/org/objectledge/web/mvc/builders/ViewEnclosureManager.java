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
package org.objectledge.web.mvc.builders;

import org.objectledge.context.Context;
import org.objectledge.templating.tools.ContextToolFactory;

/**
 * Template enclosure manager together with ViewEnclosureTool allows setting and retrieval of
 * enclosing view from templates.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ViewEnclosureManager.java,v 1.2 2005-02-21 10:10:13 rafal Exp $
 */
public class ViewEnclosureManager implements ContextToolFactory
{
    private Context context;
    
    /**
     * Creates a template enclosure manager.
     * 
     * @param context request context.
     */
	public ViewEnclosureManager(Context context)
	{
		this.context = context;
	}

    // enclosure management

    /**
     * Sets the enclosing view to {@link EnclosingView#TOP}.   
     */
    public void setTopEnclosingView()
    {
        context.setAttribute(EnclosingView.class, EnclosingView.TOP);
    }

    /**
     * Sets the enclosing view to a view with a given name.
     * 
     * b@param enclosingViewName the chosen enclosing view name.
     */
    public void setEnclosingView(String enclosingViewName)
    {
        context.setAttribute(EnclosingView.class, new EnclosingView(enclosingViewName));
    }    
    
    /**
     * Get and clear the enclosing view set from the template.
     * 
     * @param enclosingView default value if no enclosing view has not been set.
     * 
     * @return enclosing view set from the template or the given default value.
     */
    public EnclosingView getEnclosingView(EnclosingView enclosingView)
    {
        EnclosingView templateEnclosingView =
            (EnclosingView) context.getAttribute(EnclosingView.class);
        context.removeAttribute(EnclosingView.class);
        if(templateEnclosingView == null)
        {
            templateEnclosingView = enclosingView;
        }
        return templateEnclosingView;
    }

    // ContextToolFactory
    
    /**
     * {@inheritDoc}
     */
    public Object getTool()
    {
        return new ViewEnclosureTool(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void recycleTool(Object tool)
    {
        //do nothing ViewEnclosureTool is too simple object to be pooled
    }

    /**
     * {@inheritDoc}
     */
    public String getKey()
    {
        return "viewEnclosureTool";
    }
}
