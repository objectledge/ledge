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
package org.objectledge.cache;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.jcontainer.dna.ConfigurationException;

/**
 * A factory of cache objects, which are variants of java.util.Map with additional functionality.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CacheFactory.java,v 1.5 2008-01-01 22:28:32 rafal Exp $
 */
public interface CacheFactory
{
    // CacheFactorySPI interface ////////////////////////////////////////////////
    
    /**
     * Get the map.
     * 
     * @param type the type.
     * @return the map. 
     */
    public abstract Map getMap(String type);

    /**
     * {@inheritDoc}
     */
    public abstract Map getInstance(String name);

    /**
     * {@inheritDoc}
     */
    public abstract Map getInstance(String name, String configAlias) throws ConfigurationException;

    /**
     * Returns the names of all currently active map instances.
     *  
     * @return the names of all currently active map instances.
     */    
    public abstract Set<String> getInstanceNames();
    
    /**
     * {@inheritDoc}
     */
    public abstract ValueFactory getValueFactory(String factory, String map);

    /**
     * {@inheritDoc}
     */
    public abstract Map getHashMap();

    /**
     * {@inheritDoc}
     */
    public abstract Map getTimeoutMap(long timeoutMillis);

    /**
     * {@inheritDoc}
     */
    public abstract Map getLRUMap(int capacity);

    /**
     * {@inheritDoc}
     */
    public abstract Map getSoftMap(int protect);

    /**
     * {@inheritDoc}
     */
    public abstract org.objectledge.cache.DistributedMap getDistributedMap(
        String name,
        Map delegate);

    /**
     * {@inheritDoc}
     */
    public abstract Map getFactoryMap(ValueFactory factory, Map delegate);

    /**
     * {@inheritDoc}
     */
    public abstract Map getStatisticsMap(String name, Map delegate);

    /**
     * {@inheritDoc}
     */
    public abstract ValueFactory getPersitenceValueFactory(Class valueClass);

    /**
     * {@inheritDoc}
     */
    public abstract void register(DelayedUpdate object);
    
    /**
     * Ensure a WeakHashMap object will expunge it's stale entries periodically.
     * <p>
     * A WeakHashMap object passed to this method will have it's size() method called roughly once
     * each minute. This will trigger expunge of keys that have their referent objects collected.
     * </p>
     * 
     * @param map the map to register for periodic expunge.
     */
    public void registerForPeriodicExpunge(WeakHashMap<?, ?> map);

    /**
     * Prints the report on the specified PrintWriter.
     *
     * @param out the PrintWriter to print report into.
     */
    public abstract void getStatus(PrintWriter out);
}