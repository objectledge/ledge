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
package org.objectledge.event;

import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * A whiteboard that deletates all it's functionality to another whiteboard.
 * 
 * <p>The delegate can be changed after the object is created. If <code>null</code> is passed as 
 * the delegate argument to the constructor or the {@link #swap(EventWhiteboard)} mehtod, 
 * IllegalStateException is thrown if any of the add/remove/fire methods are thrown.</p> 
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DelegatingEventWhiteboard.java,v 1.1 2004-03-01 15:34:30 fil Exp $
 */
public class DelegatingEventWhiteboard implements EventWhiteboard
{
    private EventWhiteboard delegate;

    /**
     * Creates a whiteboard instance.
     * 
     * @param delegate the initial delegate (may be null).
     */
    public DelegatingEventWhiteboard(EventWhiteboard delegate)
    {
        this.delegate = delegate;
    }

    /**
     * Attaches a different delegate.
     * 
     * @param delegate the new delegate (may be null).
     */
    public void swap(EventWhiteboard delegate)
    {
        this.delegate = delegate;
    }

    /**
     * Checks if a delegate is currently attached.
     * 
     * @throws IllegalStateException if no delegate is attached.
     */
    private void checkDelegate()
        throws IllegalStateException
    {
        if(delegate == null)
        {
            throw new IllegalStateException("no delegate attached");
        }
    }

    /** 
     * {@inheritDoc}
     */
    public void addListener(Class iface, Object listener, Object object)
        throws IllegalArgumentException
    {
        checkDelegate();
        delegate.addListener(iface, listener, object);
    }

    /** 
     * {@inheritDoc}
     */
    public void removeListener(Class iface, Object listener, Object object)
    {
        checkDelegate();
        delegate.removeListener(iface, listener, object);
    }

    /** 
     * {@inheritDoc}
     */
    public void addRemoteListener(Class iface, Remote listener, Object object)
        throws IllegalArgumentException
    {
        checkDelegate();
        delegate.addRemoteListener(iface, listener, object);
    }

    /** 
     * {@inheritDoc}
     */
    public void removeRemoteListener(Class iface, Remote listener, Object object)
    {
        checkDelegate();
        delegate.removeRemoteListener(iface, listener, object);        
    }

    /** 
     * {@inheritDoc}
     */
    public void fireEvent(Method method, Object[] args, Object object)
    {
        checkDelegate();
        delegate.fireEvent(method, args, object);        
    }
}
