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

import java.math.BigDecimal;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 * Describes a data source used for statistics computation, modeled after Munin tool.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DataSource.java,v 1.6 2005-05-13 06:44:35 rafal Exp $
 */
public class DataSource
{
    private final String name;
    
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
    
    /**
     * Creates new ValueDescription instance.
     * 
     * @param name the name of the data source. 
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
     */
    public DataSource(String name, String label, String cdef, Type type, Graph graph,
        Number min, Number max, Number minWarning, Number maxWarning, 
        Number minCritical, Number maxCritical)
    {
        this.name = name;
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
    }

    /**
     * Creates new DataSource instance.
     * 
     * @param name the name of the data source. 
     * @param label the data source label.
     * @param cdef the data transformation RPN expression.
     * @param type the data source type.
     * @param graph the graph type for the data source.
     */
    public DataSource(String name, String label, String cdef, Type type, Graph graph)
    {
        this(name, label, cdef, type, graph, null, null, null, null, null, null);
    }
    
    /**
     * Creates new DataSource instance.
     * 
     * @param config DNA configuration object.
     * @throws ConfigurationException if the configuraiton object contains invalid data.
     */
    public DataSource(Configuration config)
        throws ConfigurationException
    {
        this(
            config.getChild("name").getValue(),
            config.getChild("label").getValue(null),
            config.getChild("cdef").getValue(null),
            getType(config.getChild("type").getValue(null)),
            getGraph(config.getChild("graph").getValue(null)),
            getNumber(config.getChild("min").getValue(null)),
            getNumber(config.getChild("max").getValue(null)),
            getNumber(config.getChild("minWarning").getValue(null)),
            getNumber(config.getChild("maxWarning").getValue(null)),
            getNumber(config.getChild("minCritical").getValue(null)),
            getNumber(config.getChild("maxCritical").getValue(null))
            );
    }
    
    /**
     * Creates new DataSource instance.
     * 
     * @param base base data source configuraiton.
     * @param override overriding data source configuration.
     */
    public DataSource(DataSource base, DataSource override)
    {
        this(
            base.getName(),
            override.getLabel() != null ? override.getLabel() : base.getLabel(),
            override.getCdef() != null ? override.getCdef() : base.getCdef(),
            override.getType() != null ? override.getType() : base.getType(),
            override.getGraph() != null ? override.getGraph() : base.getGraph(),
            override.getMin() != null ? override.getMin() : base.getMin(),
            override.getMax() != null ? override.getMax() : base.getMax(),
            override.getMinWarning() != null ? override.getMinWarning() : base.getMinWarning(),
            override.getMaxWarning() != null ? override.getMaxWarning() : base.getMaxWarning(),
            override.getMinCritical() != null ? override.getMinCritical() : base.getMinCritical(),
            override.getMaxCritical() != null ? override.getMaxCritical() : base.getMaxCritical()
            );
    }
    
    /**
     * Returns the name.
     * 
     * @return the name.
     */
    public String getName()
    {
        return name;
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

    private static Type getType(String type)
    {
        if(type == null)
        {
            return null;
        }
        else
        {
            return Type.valueOf(type);
        }
    }

    private static Graph getGraph(String graph)
    {
        if(graph == null)
        {
            return null;
        }
        else
        {
            return Graph.valueOf(graph);
        }
    }

    private static Number getNumber(String number)
    {
        if(number == null)
        {
            return null;
        }
        else
        {
            return new BigDecimal(number);
        }
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
