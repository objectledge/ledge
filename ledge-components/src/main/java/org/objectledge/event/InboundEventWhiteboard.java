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

package org.objectledge.event;

import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * Allows only listener registration / deregistration on an uderlying {@link
 * EventWhiteboard}. 
 *
 * <p>You can use this class to safeguard against an incorrect usage of an
 * unidirectional event forwarder. First, create a private forwarder ({@link
 * EventWhiteboardFactory#getForwarder()}, then bind it to the event generator object
 * (usually a NotificationReceiver), create {@link InboundEventWhiteboard}
 * proxy object upon your private forwarder, and pass the reference to the
 * proxy to the downstream code. Any attempt to fire an event on the forwarder
 * made by the downstream code will end up with an exception, and thus
 * unwanted local-echoing of events will be avoided.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: InboundEventWhiteboard.java,v 1.2 2004-12-23 07:16:39 rafal Exp $
 */
public class InboundEventWhiteboard implements EventWhiteboard
{
    /** The underlying EventWhiteboard */
    private EventWhiteboard delegate;

    /**
     * Contstructs an <code>InboundEventWhiteboard</code>.
     *
     * @param delegate the underlying <code>EventWhiteboard</code>
     */
    public InboundEventWhiteboard(EventWhiteboard delegate)
    {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(Class iface, Object listener, Object object)
        throws IllegalArgumentException
    {
        delegate.addListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(Class iface, Object listener, Object object)
    {
        delegate.removeListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void addRemoteListener(Class iface, Remote listener, Object object)
        throws IllegalArgumentException
    {
        delegate.addRemoteListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRemoteListener(Class iface, Remote listener, Object object)
    {
        delegate.removeRemoteListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void fireEvent(Method method, Object[] args, Object object)
    {
        throw new IllegalStateException("can't fire events on an inbound forwarder");
    }
}
