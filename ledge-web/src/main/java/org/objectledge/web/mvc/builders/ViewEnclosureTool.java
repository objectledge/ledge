// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

/**
 * Allows template designers to control view enclosures from template level.
 *  
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ViewEnclosureTool.java,v 1.2 2005-02-21 10:10:13 rafal Exp $
 */
public class ViewEnclosureTool
{
    private ViewEnclosureManager viewEnclosureManager;
    
    /**
     * @param viewEnclosureManager the ViewEnclosureManager component. 
     */
    public ViewEnclosureTool(ViewEnclosureManager viewEnclosureManager)
    {
        this.viewEnclosureManager = viewEnclosureManager;
    }
    
    /**
     * Tell builder valve to terminate enclosure loop.
     * 
     * @return an empty string to avoid spoiling template output and warnings.
     */
    public String top()
    {
        viewEnclosureManager.setTopEnclosingView();
        return "";
    }
    
    
    /**
     * Tell builder valve to override the enclosing view.
     *
     * @param enclosingViewName the requested enclosing view name.
     * @return an empty string to avoid spoiling template output and warnings.
     */
    public String override(String enclosingViewName)
    {
        viewEnclosureManager.setEnclosingView(enclosingViewName);
        return "";
    }
}
