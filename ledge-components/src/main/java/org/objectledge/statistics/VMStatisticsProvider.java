// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.statistics;

import static org.objectledge.statistics.DataSource.Graph.LINE1;
import static org.objectledge.statistics.DataSource.Type.COUNTER;
import static org.objectledge.statistics.DataSource.Type.GAUGE;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;

/**
 * A Statistics provider for the VM using JDK 5 java.lang.management interface.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: VMStatisticsProvider.java,v 1.11 2005-05-16 09:55:05 rafal Exp $
 */
public class VMStatisticsProvider
    extends ReflectiveStatisticsProvider
{
    private static final DataSource MEMORY_HEAP_USED_DS =
        new DataSource("memory_heap_used", "Heap used", GAUGE, LINE1);
    
    private static final DataSource MEMORY_HEAP_MAX_DS =
        new DataSource("memory_heap_max", "Heap max", GAUGE, LINE1);
    
    private static final DataSource MEMORY_NONHEAP_USED_DS =
        new DataSource("memory_nonheap_used", "Non-heap used", GAUGE, LINE1);
    
    private static final DataSource MEMORY_NONHEAP_MAX_DS =
        new DataSource("memory_nonheap_max", "Non-heap max", GAUGE, LINE1);

    private static final Graph MEMORY_GRAPH = new Graph("memory", "Memory", null, new DataSource[] {
                    MEMORY_HEAP_USED_DS, MEMORY_HEAP_MAX_DS, MEMORY_NONHEAP_USED_DS,
                    MEMORY_NONHEAP_MAX_DS }, "bytes");        
    
    private static final DataSource GC_COUNT_VALUE_DC =
        new DataSource("gc_count_value", "GC run count", COUNTER, LINE1);
    
    private static final DataSource GC_TIME_VALUE_DC = 
        new DataSource("gc_time_value", "Total GC time", COUNTER, LINE1);

    private static final Graph GC_COUNT_GRAPH = new Graph("gc_count", "Garbage collection runns",
        null, new DataSource[] { GC_COUNT_VALUE_DC }, "runns");

    private static final Graph GC_TIME_GRAPH = new Graph("gc_time", "Garbage collection time",
        null, new DataSource[] { GC_TIME_VALUE_DC }, "milliseconds");
    
    private static final DataSource[] DATA_SOURCES = {
        MEMORY_HEAP_USED_DS, MEMORY_HEAP_MAX_DS, MEMORY_NONHEAP_USED_DS,
        MEMORY_NONHEAP_MAX_DS, GC_COUNT_VALUE_DC, GC_TIME_VALUE_DC          
    };
    
    private static final Graph[] GRAPHS = {
        MEMORY_GRAPH, GC_COUNT_GRAPH, GC_TIME_GRAPH
    };
    
    /**
     * {@inheritDoc}
     */
    public DataSource[] getDataSources()
    {
        return DATA_SOURCES;
    }

    /**
     * {@inheritDoc}
     */
    public Graph[] getGraphs()
    {
        return GRAPHS;
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "vm";
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the size of used heap memory.
     * 
     * @return the size of used heap memory.
     */
    public Number getMemoryHeapUsed()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getHeapMemoryUsage().getUsed());
    }
    
    /**
     * Returns the maximum size of heap memory.
     * 
     * @return the maximum size of heap memory.
     */
    public Number getMemoryHeapMax()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getHeapMemoryUsage().getMax());
    }

    /**
     * Returns the size of used non-heap memory.
     * 
     * @return the size of used non-heap memory.
     */
    public Number getMemoryNonheapUsed()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getNonHeapMemoryUsage().getUsed());
    }
    
    /**
     * Returns the maximum size of non-heap memory.
     * 
     * @return the maximum size of non-heap memory.
     */
    public Number getMemoryNonheapMax()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getNonHeapMemoryUsage().getMax());
    }

    /**
     * Returns the total number of garbage collections.
     * 
     * @return the total number of garbage collections.
     */
    public Number getGcCountValue()
    {
        List<GarbageCollectorMXBean> garbageCollectors = 
            ManagementFactory.getGarbageCollectorMXBeans();
        long sum = 0;
        for(GarbageCollectorMXBean garbageCollector : garbageCollectors)
        {
            sum += garbageCollector.getCollectionCount();
        }
        return new Long(sum);
    }

    /**
     * Returns the approximate accumulated garbage collection elapsed time in milliseconds.
     * 
     * @return the approximate accumulated garbage collection elapsed time in milliseconds.
     */
    public Number getGcTimeValue()
    {
        List<GarbageCollectorMXBean> garbageCollectors = 
            ManagementFactory.getGarbageCollectorMXBeans();
        long sum = 0;
        for(GarbageCollectorMXBean garbageCollector : garbageCollectors)
        {
            sum += garbageCollector.getCollectionTime();
        }
        return new Long(sum);
    }
}
