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

package org.objectledge.modules.views.system;

import static java.util.Collections.sort;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectledge.cache.CacheFactory;
import org.objectledge.cache.spi.StatisticsMap;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id$
 */
public class Cache
    extends PolicyProtectedBuilder
{

    private final CacheFactory cacheFactory;
    
    /**
     * Creates a new Cache instance.
     * 
     * @param context request context.
     * @param policySystemArg PolicySystem component.
     * @param cacheFactoryArg CacheFactoryComponent.
     */
    public Cache(Context context, PolicySystem policySystemArg, CacheFactory cacheFactoryArg)
    {
        super(context, policySystemArg);
        this.cacheFactory = cacheFactoryArg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        List<String> names = new ArrayList<String>(cacheFactory.getInstanceNames());
        sort(names);
        templatingContext.put("cacheNames", names);
        Map<String, Info> info = new HashMap<String, Info>(names.size());
        for(String name : names)
        {
            info.put(name, new Info(name, cacheFactory.getInstance(name)));
        }
        templatingContext.put("cacheInfo", info);
    }

    public static class Info
    {
        private final String name;

        private final int size;

        private boolean statistics;

        private final int requests;

        private final int hits;

        private final int misses;

        private final String hitRatio;

        public Info(String name, Map map)
        {
            this.name = name;
            this.size = map.size();
            if(map instanceof StatisticsMap)
            {
                StatisticsMap sMap = (StatisticsMap)map;
                statistics = true;
                requests = sMap.getRequestCount();
                hits = sMap.getHitCount();
                misses = requests - hits;
                if(requests > 0)
                {
                    NumberFormat percent = NumberFormat.getPercentInstance();
                    hitRatio = percent.format((double)hits/requests);                    
                }
                else
                {
                    hitRatio = "";                    
                }
            }
            else
            {
                statistics = false;
                requests = 0;
                hits = 0;
                misses = 0;
                hitRatio = "";
            }
        }

        /**
         * Returns the hitRatio value.
         *
         * @return the hitRatio.
         */
        public String getHitRatio()
        {
            return hitRatio;
        }

        /**
         * Returns the hits value.
         *
         * @return the hits.
         */
        public int getHits()
        {
            return hits;
        }

        /**
         * Returns the misses value.
         *
         * @return the misses.
         */
        public int getMisses()
        {
            return misses;
        }

        /**
         * Returns the name value.
         *
         * @return the name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * Returns the requests value.
         *
         * @return the requests.
         */
        public int getRequests()
        {
            return requests;
        }

        /**
         * Returns the size value.
         *
         * @return the size.
         */
        public int getSize()
        {
            return size;
        }

        /**
         * Returns the statistics value.
         *
         * @return the statistics.
         */
        public boolean isStatistics()
        {
            return statistics;
        }
    }
}
