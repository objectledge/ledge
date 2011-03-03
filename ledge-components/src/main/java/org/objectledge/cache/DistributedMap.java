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

import java.util.Map;

/**
 * A map that is shared among the nodes of a clustered installation.
 *
 * <p>Remote nodes will respond with dropping the mapping on explicit
 * puts and removes on the map. When you modify the internal state of a value of
 * a mapping, you need to notify the remote nodes of your action by calling the 
 * {@link #updated(Object)} method.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>*
 * @version $Id: DistributedMap.java,v 1.1 2004-02-12 11:41:27 pablo Exp $
 */
public interface DistributedMap<K, V>
    extends Map<K, V>
{
    /**
     * Notifies peer nodes that the mappig value was modified.
     *
     * <p>The peer nodes will check if the mapping value they have is an
     * instance of {@link Refreshable} interface and call {@link
     * Refreshable#refresh()} method on it, otherwise they will discard the
     * mapping entry.</p>
     *
     * <p>The key object will be serialized and passed to the other nodes,
     * therefore the objects should be small and simple. Use of anything except
     * Strings, Integers and Longs is discouraged.</p>
     * 
     * @param key the object key.
     */
    public void updated(K key);
}
