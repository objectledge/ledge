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
 * This interface is implemented by both public (<code>EventWhiteboardFactory</code>)
 * and private event forwarding objects.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EventWhiteboard.java,v 1.1 2004-03-01 13:33:45 fil Exp $
 */
public interface EventWhiteboard
{
    /**
     * Adds an event listener.
     *
     * <p>This method is idempotent - calling it more than once with the same
     * set of arguments has the same effect as calling it once (more
     * specifically, the event handlig method will be called exactly once for
     * each matching {@link #fireEvent(Method,Object[],Object)} call.</p>
     *
     * @param iface the listener interface.
     * @param listener the listener object.
     * @param object the object the listener want's to recive notifications
     *        on, or <code>null</code> for all objects.
     * @throws IllegalArgumentException if the <code>iface</code> is not an
     *         interaface, or <code>handler</code> does not implement
     *         <code>iface</code> .
     */
    public void addListener(Class iface, Object listener, Object object)
        throws IllegalArgumentException;

    /**
     * Removes an event listener.
     *
     * <p>If the listener is not currently registered for the specifc
     * interface and object, this call has no effect.</p>
     *
     * @param iface the listener interface.
     * @param listener the listener object.
     * @param object the object the listener is recieving notifications on, or
     *        <code>null</code> for all objects.
     */
    public void removeListener(Class iface, Object listener, Object object);

    /**
     * Adds an event listener.
     *
     * <p><code>Remote</code> objects are treated in a special way. They don't
     * need to implement the interface <code>iface</code>, but they need to
     * implementa a <i>similar</i> interface, that has methods of the same
     * names, same return values, same argument count and types, and same
     * thrown exceptions - plus the methods in the </code>Remote</code>
     * derived may (and per RMI contract - must) throw
     * <code>RemoteExceptions</code>. A special proxy object will be
     * registered as the listener, that will forward method calls to the
     * remote object.</p>
     *
     * @param iface the listener interface.
     * @param listener the remote listener object.
     * @param object the object the listener want's to recive notifications
     *        on. <code>null</code> for all objects.
     * @throws IllegalArgumentException if the <code>iface</code> is an
     *         unknown interaface.
     */
    public void addRemoteListener(Class iface, Remote listener, Object object)
        throws IllegalArgumentException;
    
    /**
     * Removes an events listener.
     *
     * <p><code>Remote</code> obejcts are treated in a special way. This
     * method locates the proxy object created by the {@link
     * #addRemoteListener(Class,Remote,Object)} method and unregisters it.</p>
     *
     * @param iface the listener interface.
     * @param listener the listener object.
     * @param object the object the listener is recieving notifications on.
     */
    public void removeRemoteListener(Class iface, Remote listener, Object object);

    /**
     * Calls a method on registered handlers.
     *
     * <p>If there are no mathching listeneres, this call has no effect.
     * Otherwise, the <code>method</code> will be called once on each of the
     * registered listeners. Any exceptions or errors (except for
     * <code>VirtualMachineError</code> subclasses, and the
     * <code>ThreadDeath</code> error) thrown will be caught, logged and then
     * discarded.</p>
     * 
     * <p>If there are both listeners on a specific and all objects for a
     * given interface, it's up to the application to call this method with
     * the <code>null</code> as the <code>object</code> argument.
     * 
     * @param method the method to call.
     * @param args the method arguments.
     * @param object the object the listeners request notifications on, or
     *        <code>null</code> for 'all objects' notification.
     */
    public void fireEvent(Method method, Object[] args, Object object);
}
