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

import org.objectledge.filesystem.FileSystem;

/**
 * A Statistics provider for the VM using JDK 5 java.lang.management interface.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: VMStatisticsProvider.java,v 1.12 2008-01-20 15:17:31 rafal Exp $
 */
public class VMStatisticsProvider
    extends ReflectiveStatisticsProvider
{
    private final MuninGraph[] graphs;
    
    public VMStatisticsProvider(FileSystem fs)
    {
        graphs = new MuninGraph[] { new HeapMemory(fs), new NonHeapMemory(fs), new GcCount(fs),
                        new GcTime(fs) }; 
    }
    
    /**
     * {@inheritDoc}
     */
    public MuninGraph[] getGraphs()
    {
        return graphs;
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "vm";
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////

    public class HeapMemory
        extends AbstractMuninGraph
    {
        public HeapMemory(FileSystem fs)
        {
            super(fs);
        }

        public String getId()
        {
            return "heapMemory";
        }
        
        /**
         * Returns the size of used heap memory.
         * 
         * @return the size of used heap memory.
         */
        public Number getUsed()
        {
            MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
            return new Long(memory.getHeapMemoryUsage().getUsed());
        }
        
        /**
         * Returns the size of free heap memory.
         * 
         * @return the size of free heap memory.
         */
        public Number getFree()
        {
            MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
            return new Long(memory.getHeapMemoryUsage().getMax()
                - memory.getHeapMemoryUsage().getUsed());
        }
    }
    
    public class NonHeapMemory
        extends AbstractMuninGraph
    {
        public NonHeapMemory(FileSystem fs)
        {
            super(fs);
        }
        
        public String getId()
        {
            return "nonHeapmMemory";
        }
        
        /**
         * Returns the size of used non-heap memory.
         * 
         * @return the size of used non-heap memory.
         */
        public Number getUsed()
        {
            MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
            return new Long(memory.getNonHeapMemoryUsage().getUsed());
        }
        
        /**
         * Returns the size of free non-heap memory.
         * 
         * @return the size of free non-heap memory.
         */
        public Number getFree()
        {
            MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
            return new Long(memory.getNonHeapMemoryUsage().getMax() - memory.getNonHeapMemoryUsage().getUsed());
        }
    }

    public class GcCount
        extends AbstractMuninGraph
    {
        public GcCount(FileSystem fs)
        {
            super(fs);
        }
        
        public String getId()
        {
            return "gcCount";
        }
        
        /**
         * Returns the total number of garbage collections.
         * 
         * @return the total number of garbage collections.
         */
        public Number getCount()
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
    }

    public class GcTime
        extends AbstractMuninGraph
    {
        public GcTime(FileSystem fs)
        {
            super(fs);
        }
        
        public String getId()
        {
            return "gcTime";
        }
    
        /**
         * Returns the approximate accumulated garbage collection elapsed time in milliseconds.
         * 
         * @return the approximate accumulated garbage collection elapsed time in milliseconds.
         */
        public Number getTime()
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
}