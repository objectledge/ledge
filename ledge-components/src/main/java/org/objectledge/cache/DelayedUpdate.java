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

package org.objectledge.cache;

/**
 * Implemented by objects that tolerate having usaved state, or stale data for a
 * limited period of time.
 *
 * <p>The usage pattern for <code>DelayedUpdate</code> supporting objects is:
 * <ol>
 *    <li> {@link Caching#register(DelayedUpdate)} is called on the
 *    object.</li>
 *    <li> The <code>Caching</code> queries the requested delay time with
 *    {@link #getUpdateLatency()} call, and puts the object into the waiting queue.</li>
 *    <li> After the requested delay time passes, {@link #update()} is called
 *    and the object is removed from the queue.</li>
 * </ol>
 * <p> Subsequent calls of <code>register()</code> method on an object that is
 * already in the queue will change the target update time, and possibly move
 * the object towards the end of the queue, and will not cause multiple
 * <code>update()</code> calls.</p>
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DelayedUpdate.java,v 1.1 2004-02-12 11:41:27 pablo Exp $
 */
public interface DelayedUpdate
{
    /**
     * Returns the requested maximum time in milliseconds that passes between
     * registration for update, and the actual {@link #update()} call.
     *
     * @return update latency in milliseconds.
     */
    public long getUpdateLatency();
    
    /**
     * Updates the object's state.
     *
     * <p>Depending on the application this may be a read (refresh data) or
     * write (flush changes) update.</p> 
     */
    public void update();
}
