// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
 * Contains information about the enclosing view.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EnclosingView.java,v 1.1 2005-02-17 17:04:20 zwierzem Exp $
 */
public class EnclosingView
{
    public static final EnclosingView TOP = new EnclosingView(true);
    public static final EnclosingView DEFAULT = new EnclosingView(false);
    
    private boolean top = false;
    private String view;

    /**
     * Constructs the top or default enclosing view.
     * @param top <code>true</code> if the constructed enclosing view is the top view.
     */
    private EnclosingView(boolean top)
    {
        this.view = null;
        this.top = top;
    }

    /**
     * Constructs the overriding enclosing view.
     * @param viewName Name of the enclosing view, cannot be <code>null</code>.
     */
    public EnclosingView(String viewName)
    {
        if(viewName == null)
        {
            throw new IllegalArgumentException("Override view name cannot be null");
        }
        this.view = viewName;
    }

    /**
     * @return Returns the enclosing view name, <code>null</code> if not specified.
     */
    public String getView()
    {
        return view;
    }

    /**
     * @return
     */
    public boolean override()
    {
        return view != null && !top;
    }

    /**
     * @return
     */
    public boolean top()
    {
        return view == null && top;
    }

    /**
     * @return
     */
    public boolean defaultBehaviour()
    {
        return view == null && !top;
    }
}
