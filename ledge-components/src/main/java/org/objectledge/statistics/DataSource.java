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

/**
 * Describes a data source used for statistics computation.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DataSource.java,v 1.1 2005-05-10 11:02:48 rafal Exp $
 */
public class DataSource
{
    private final String label;
    
    private final String cdef;
    
    private final Type type;
    
    private final Graph graph;
    
    private final Number min;
    
    private final Number max;
    
    private final Number minWarning;
    
    private final Number maxWarning;
    
    private final Number minCritical;
    
    private final Number maxCritical;
    
    private final String info;
    
    /**
     * Creates new ValueDescription instance.
     * 
     * @param label the data source label.
     * @param cdef the data transformation RPN expression.
     * @param type the data source type.
     * @param graph the graph type for the data source.
     * @param min minimal cutoff value (lesser values will be discarded), null to disable.
     * @param max maximal cutoff value (greater values will be discarded), null to disable.
     * @param minWarning the minimal warning value, null to disable.
     * @param maxWarning the maximal warning value, null to disable.
     * @param minCritical the minimal critical warning value, null to disable.
     * @param maxCritical the maximal critical warning value, null to disable.
     * @param info the additional description string.
     */
    public DataSource(String label, String cdef, Type type, Graph graph,
        Number min, Number max, Number minWarning, Number maxWarning, 
        Number minCritical, Number maxCritical, String info)
    {
        this.label = label;
        this.cdef = cdef;
        this.type = type;
        this.graph = graph;
        this.min = min;
        this.max = max;
        this.minWarning = minWarning;
        this.maxWarning = maxWarning;
        this.minCritical = minCritical;
        this.maxCritical = maxCritical;
        this.info = info;
    }

    /**
     * Creates new DataSource instance.
     * 
     * @param label the data source label.
     * @param cdef the data transformation RPN expression.
     * @param type the data source type.
     * @param graph the graph type for the data source.
     * @param info the additional description string.
     */
    public DataSource(String label, String cdef, Type type, Graph graph, String info)
    {
        this(label, cdef, type, graph, null, null, null, null, null, null, info);
    }

    /**
     * Returns the label.
     *
     * @return the label.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Returns the cdef.
     *
     * @return the cdef.
     */
    public String getCdef()
    {
        return cdef;
    }

    /**
     * Returns the type.
     *
     * @return the type.
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Returns the graph.
     *
     * @return the graph.
     */
    public Graph getGraph()
    {
        return graph;
    }

    /**
     * Returns the min.
     *
     * @return the min.
     */
    public Number getMin()
    {
        return min;
    }

    /**
     * Returns the max.
     *
     * @return the max.
     */
    public Number getMax()
    {
        return max;
    }

    /**
     * Returns the minWarning.
     *
     * @return the minWarning.
     */
    public Number getMinWarning()
    {
        return minWarning;
    }

    /**
     * Returns the maxWarning.
     *
     * @return the maxWarning.
     */
    public Number getMaxWarning()
    {
        return maxWarning;
    }

    /**
     * Returns the minCritical.
     *
     * @return the minCritical.
     */
    public Number getMinCritical()
    {
        return minCritical;
    }

    /**
     * Returns the maxCritical.
     *
     * @return the maxCritical.
     */
    public Number getMaxCritical()
    {
        return maxCritical;
    }

    /**
     * Returns the info.
     *
     * @return the info.
     */
    public String getInfo()
    {
        return info;
    }
    

    /**
     * Types of data sources modeled after RRDTool.
     */
    public enum Type
    {
        /** A nondecreasing conter value, suitable for computing rate over time. */
        COUNTER,
    
        /** A a floating value, suitable for computing average over time. */
        GAUGE,
    
        /** Like COUNTER, but value is reset to 0 after each read. */
        ABSOLUTE,
    
        /** Like COUNTER, but may increase and decrease - thus no wraparound protection 
         * is available.*/
        DERIVE
    }

    /**
     * Graph types modeled after RRDTool.
     */
    public enum Graph
    {
        /** Hidden graph. */
        HIDDEN,
        
        /** Line graph 1. */
        LINE1,
        
        /** Line graph 2. */
        LINE2,
        
        /** Line graph 3. */
        LINE3,
        
        /** Area graph. */
        AREA
    }
}
