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

package org.objectledge.cache.spi;

import java.util.Map;

import org.jcontainer.dna.ConfigurationException;
import org.objectledge.cache.DelayedUpdate;
import org.objectledge.cache.ValueFactory;
import org.objectledge.notification.Notification;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CachingSPI.java,v 1.1 2004-02-12 11:41:26 pablo Exp $
 */
public interface CachingSPI
{
    /**
     * Returns an implementation of a requested map type.
     *
     * @param type the map type
     * @return the map.
     */
    public Map getMap(String type);

    /**
     * Returns an instance of a map defined in the configuration file.
     *
     * <p>Subsequent calls of this method with a given <code>name</code>
     * argument will yeield reference to a single object that is constructed
     * and initialized on the first call. The application is exclusively
     * responsible for synchronization of access to this object by different
     * threads.</p>
     *
     * @param name the name of the map instance.
     * @return the map.
     */
    public Map getInstance(String name);
    
    /**
     * Returns an instance of a map. 
     * 
     * <p>If the instance is not configured in the configuration file, a shared
     * configuration referenced by <code>configAlias</code> is used.</p>
     *
     * <p>Subsequent calls of this method with a given <code>name</code>
     * argument will yeield reference to a single object that is constructed
     * and initialized on the first call. The application is exclusively
     * responsible for synchronization of access to this object by different
     * threads.</p>
     *
     * @param name the name of the map instance.
     * @param configAlias the configuration alias.
     * @return the map.
     * @throws ConfigurationException if config is wrong.
     */    
    public Map getInstance(String name, String configAlias)
        throws ConfigurationException;


    /**
     * Returns an instance of value factory defined in the configuration file.
     *
     * <p>Each call of this method will result in constructing and configuring
     * a new instance of the factory.</p>
     *
     * @param factory the configuration file alias of the factory.
     * @param map the name of the map the factory is being attached to.
     * @return the value factory.
     */
    public ValueFactory getValueFactory(String factory, String map);

    /**
     * Creates an instance of <code>java.util.HashMap</code>
     * 
     * @return the hash map.
     */
    public Map getHashMap();

    /**
     * Creates a Map that discards it's entries that have not been accessed for
     * a specified period of time.
     *
     * @param timeoutMillis the time to retain unused entries in milliseconds.
     * @return the timeout map.
     */
    public Map getTimeoutMap(long timeoutMillis);
    
    /**
     * Creates a Map that holds a limited number of entries, and discards the
     * least recently used ones.
     *
     * @param capacity the Map capacity.
     * @return the lru map.
     */
    public Map getLRUMap(int capacity);
    
    /**
     * Creates a Map that holds the mapping values on soft references (see
     * <code>java.lang.ref.SoftReference</code>), and protects a specified
     * number of most recently used entires with hard references.
     *
     * @param protect the number of entries to protect from being GCed (0 to
     *        disable).
     * @return the soft map.
     */
    public Map getSoftMap(int protect);
    
    /**
     * Creates a distributed map.
     *
     * <p>A distributed map is shared among the nodes in a clustered 
     * installation. This requires Notification to be properly
     * configured. Each of the maps to be shared is indetified by an unique
     * name. The delegate parameter of this method allows you to specify the
     * policy for local storage of the shared objects. You can use either
     * <code>java.util.HashMap</code> or one of the Map implementations
     * provided by the caches service that suits your application.
     *
     * @param name the name of the map.
     * @param delegate the Map implemention that provides local storage.
     * @return the distibuted map.
     */
    public org.objectledge.cache.DistributedMap getDistributedMap(String name, Map delegate);

    /**
     * Creates a factory map.
     *
     * <p>Factory map calls {@link ValueFactory#getValue(Object)} method
     * whenever the <code>Map.get(Object)</code> method is called with a key
     * does not map to any value. The returned value will be stored in the map
     * and returned.</p>
     *
     * @param factory the value factory to use.
     * @param delegate the Map object that provides data storage.
     * @return the factory map.
     */
    public Map getFactoryMap(ValueFactory factory, Map delegate);

    /**
     * Creates a map that performs performance statistics.
     *
     * <p>The item count, number or requests, hits and misses, along with
     * computed hit ration will be shown in the Caching status repors.
     * The name is used to identify the cache instance in the report.</p>
     *
     * @param name the name of the map to be used in the reports.
     * @param delegate the map to provide statistics for.
     * @return the statistics map.
     */
    public Map getStatisticsMap(String name, Map delegate);


    /**
     * Creates a ValueFactory that uses Persistence service to load values
     * missing from the map.
     *
     * <p>The value class should implement the <code>Persistent</code>
     * interface. The key class should extend
     * <code>java.lang.Number</code>.</p>
     *
     * @param valueClass the class of the map values.
     * @return the value factory.
     */
    public ValueFactory getPersitenceValueFactory(Class valueClass);

    // delayed update ////////////////////////////////////////////////////////
    
    /**
     * Registers an object for a delayed update.
     *
     * @param object the object to register.
     */
    public void register(DelayedUpdate object);
    
    /**
     * Adds an statistics map the the service's registry.
     *
     * @param map the map
     */
    public void addStatisticsMap(StatisticsMap map);
    
    /**
     * Get the notification system.
     * 
     * @return the notification system.
     */
    public Notification getNotification();
}
