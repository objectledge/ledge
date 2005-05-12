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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;
import static org.objectledge.statistics.DataSource.Type.COUNTER;
import static org.objectledge.statistics.DataSource.Type.GAUGE;
import static org.objectledge.statistics.DataSource.Graph.LINE1;

import org.picocontainer.Startable;

/**
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: VMStatisticsProvider.java,v 1.3 2005-05-12 04:46:08 rafal Exp $
 */
public class VMStatisticsProvider
    extends ReflectiveStatisticsProvider
    implements Startable
{
    private static final DataSource[] DATA_SOURCES = { 
        new DataSource("vm_memory_heap_used", "Heap used", null, GAUGE, LINE1, null),
        new DataSource("vm_memory_heap_max", "Heap max", null, GAUGE, LINE1, null),
        new DataSource("vm_memory_nonheap_used", "Non-heap used", null, GAUGE, LINE1, null),
        new DataSource("vm_memory_nonheap_max", "Non-heap max", null, GAUGE, LINE1, null),
        new DataSource("vm_gc_count", "GC count", null, COUNTER, LINE1, null),
        new DataSource("vm_gc_time", "Total GC time", null, COUNTER, LINE1, null)
    };
    
    private static final Graph[] GRAPHS = {
        new Graph("vm", "Virtual Machine statisitcs", null, DATA_SOURCES, null)
    };
    
    /**
     * Creates new VMStatisticsProvider instance.
     * 
     * @param statistics the statistics component.
     */
    public VMStatisticsProvider(Statistics statistics)
    {
        statistics.registerProvider(this);
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
        // Startable should be a marker interface
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // and stop() should live in a Stoppable interface
    }

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
    public Number getVmMemoryHeapUsedValue()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getHeapMemoryUsage().getUsed());
    }
    
    /**
     * Returns the maximum size of heap memory.
     * 
     * @return the maximum size of heap memory.
     */
    public Number getVmMemoryHeapMaxValue()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getHeapMemoryUsage().getMax());
    }

    /**
     * Returns the size of used non-heap memory.
     * 
     * @return the size of used non-heap memory.
     */
    public Number getVmMemoryNonheapUsedValue()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getNonHeapMemoryUsage().getUsed());
    }
    
    /**
     * Returns the maximum size of non-heap memory.
     * 
     * @return the maximum size of non-heap memory.
     */
    public Number getVmMemoryNonheapMaxValue()
    {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return new Long(memory.getNonHeapMemoryUsage().getMax());
    }

    /**
     * Returns the total number of garbage collections.
     * 
     * @return the total number of garbage collections.
     */
    public Number getVmGcCountValue()
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
    public Number getVmGcTimeValue()
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
