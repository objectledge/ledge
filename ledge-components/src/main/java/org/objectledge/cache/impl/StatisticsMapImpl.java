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

package org.objectledge.cache.impl;

import java.text.NumberFormat;
import java.util.Map;

import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.StatisticsMap;

/**
 * An implementation of performance statistics performing cache.
 *
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: StatisticsMapImpl.java,v 1.3 2004-12-23 07:16:50 rafal Exp $
 */
public class StatisticsMapImpl
    extends DelegateMap
    implements StatisticsMap, ConfigurableMap
{
    // constants /////////////////////////////////////////////////////////////

    /** The default name of the map. (map) */
    public static final String NAME_DEFAULT = "map";

    // instance variables ////////////////////////////////////////////////////

    /** Name the name of the facility. */
    private String name = NAME_DEFAULT;

    /** The number of requests. (get method calls). */
    private int requests;
    
    /** The number of hits (non-null get results). */
    private int hits;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Constructs a StatisticsMapImpl.
     */
    public StatisticsMapImpl()
    {
        super();
    }

    /**
     * Constructs a statistics cache.
     * 
     * @param name the name.
     * @param delegate the delegate map.
     */
    public StatisticsMapImpl(String name, Map delegate)
    {
        super(delegate);
        this.name = name;
    }

    // ConfigurableMap interface /////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void configure(CacheFactorySPI caching, String name, String config)
    {
        setName(name);
        caching.addStatisticsMap(this);
    }    

    // StatisticsMapSPI interface ////////////////////////////////////////////

    /**
     * Returns the number of requests.
     *
     * @return the number of requests.
     */
    public int getRequestCount()
    {
        return requests;
    }

    /**
     * Returns the number of hits.
     *
     * @return the numer of hists.
     */
    public int getHitCount()
    {
        return hits;
    }
    
    /**
     * Resets the counters.
     */
    public void resetCount()
    {
        requests = 0;
        hits = 0;
    }

    /**
     * Sets the map name.
     * 
     * @param name the name.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Returns the map name.
     *
     * @return get name.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Returns the statistics for the cache.
     * 
     * @return the statistics.
     */
    public String getStatistics()
    {
        StringBuffer buff = new StringBuffer();
        buff.append(name).append(": ");
        buff.append(delegate.size()).append(" items, ");
        buff.append(requests).append(" requests");
        if(requests != 0)
        {
            buff.append(", ").append(hits).append(" hits, ");
            buff.append(requests-hits).append(" misses, ");
            NumberFormat percent = NumberFormat.getPercentInstance();
            buff.append(percent.format(((float)hits)/requests)).append(" hit ratio");
        }
        buff.append("\n");
        return buff.toString();
    }

    // Map interface /////////////////////////////////////////////////////////

    /**
     * Returns the value to which this map maps the specified key.
     *
     * @param key the key.
     * @return the value
     */
    public Object get(Object key)
    {
        Object result = delegate.get(key);
        requests++;
        if(result != null)
        {
            hits++;
        }
        return result;
    }
}

