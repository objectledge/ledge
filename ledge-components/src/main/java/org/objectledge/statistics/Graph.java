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

import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 * Describes a statistics graph, modeled after Munin tool.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Graph.java,v 1.3 2005-05-11 07:17:02 rafal Exp $
 */
public class Graph
{
    private final String name;

    private final String title;

    private final String createArgs;

    private final String graphArgs;

    private final DataSource[] order;

    private final String vLabel;

    private final String totalLabel;

    private final boolean notScaled;

    private final boolean notDrawn;

    private final boolean notUpdated;

    /**
     * Creates new Graph instance.
     * 
     * @param name the graph name.
     * @param title graph title
     * @param createArgs additional args passed to rrdcreate.
     * @param graphArgs additional args passed to rrdgraph.
     * @param order datasources in this graph.
     * @param vLabel the label for graph's virtual axis.
     * @param totalLabel if non-null, data source values will be sumarized, using this label.
     * @param notScaled <code>true</code> to disable min/max/cur value scaling.
     * @param notDrawn <code>true</code> to disable drawing the graph.
     * @param notUpdated <code>true</code> to disable updating graph data values.
     */
    public Graph(String name, String title, String createArgs, String graphArgs,
        DataSource[] order, String vLabel, String totalLabel, boolean notScaled, boolean notDrawn,
        boolean notUpdated)
    {
        this.name = name;
        this.title = title;
        this.createArgs = createArgs;
        this.graphArgs = graphArgs;
        this.order = order;
        this.vLabel = vLabel;
        this.totalLabel = totalLabel;
        this.notScaled = notScaled;
        this.notDrawn = notDrawn;
        this.notUpdated = notUpdated;
    }

    /**
     * Creates new Graph instance.
     * 
     * @param name the graph name.
     * @param title graph title
     * @param graphArgs additional args passed to rrdgraph.
     * @param order datasources in this graph.
     * @param vLabel the label for graph's virtual axis.
     */
    public Graph(String name, String title, String graphArgs, DataSource[] order, String vLabel)
    {
        this(name, title, null, graphArgs, order, vLabel, null, false, false, false);
    }

    /**
     * Creates new Graph instance.
     * 
     * @param config DNA congfiuration object
     * @param dataSources the registered dataSources.
     * @throws ConfigurationException if the configuration contains invalid data.
     */
    public Graph(Configuration config, Map<String, DataSource> dataSources)
        throws ConfigurationException
    {
        this(config.getChild("name").getValue(), config.getChild("title").getValue(null), config
            .getChild("createArgs").getValue(null), config.getChild("graphArgs").getValue(null),
                        getOrder(config, dataSources), config.getChild("vLabel").getValue(null),
                        config.getChild("totalLabel").getValue(null), config
                            .getChildren("notScaled").length > 0,
                        config.getChildren("notDrawn").length > 0,
                        config.getChildren("notUpdated").length > 0);
    }

    /**
     * Creates new Graph instance.
     * 
     * @param base base graph configuration.
     * @param override override graph configuration.
     */
    public Graph(Graph base, Graph override)
    {
        this(base.getName(),
            override.getTitle() != null ? override.getTitle() : base.getTitle(),
            override.getCreateArgs() != null ? override.getCreateArgs() : base.getCreateArgs(),
            override.getGraphArgs() != null ? override.getGraphArgs() : base.getGraphArgs(),
            override.getOrder() != null ? override.getOrder() : base.getOrder(),
            override.getVLabel() != null ? override.getVLabel() : base.getVLabel(),
            override.getTotalLabel() != null ? override.getTotalLabel() : base.getTotalLabel(),
            override.isNotScaled() ? true : base.isNotScaled(),
            override.isNotDrawn() ? true : base.isNotDrawn(),
            override.isNotUpdated() ? true : base.isNotUpdated()
            );
    }
    
    /**
     * Returns the graph name.
     * 
     * @return the graph name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the title.
     * 
     * @return the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Returns the createArgs.
     * 
     * @return the createArgs.
     */
    public String getCreateArgs()
    {
        return createArgs;
    }

    /**
     * Returns the graphArgs.
     * 
     * @return the graphArgs.
     */
    public String getGraphArgs()
    {
        return graphArgs;
    }

    /**
     * Returns the data source order.
     * 
     * @return the data source order.
     */
    public DataSource[] getOrder()
    {
        return order;
    }

    /**
     * Returns the vLabel.
     * 
     * @return the vLabel.
     */
    public String getVLabel()
    {
        return vLabel;
    }

    /**
     * Returns the total label.
     * 
     * @return the total label.
     */
    public String getTotalLabel()
    {
        return totalLabel;
    }

    /**
     * Returns the notScaled flag.
     * 
     * @return the notScaled flag.
     */
    public boolean isNotScaled()
    {
        return notScaled;
    }

    /**
     * Returns the notDrawn flag.
     * 
     * @return the notDrawn flag.
     */
    public boolean isNotDrawn()
    {
        return notDrawn;
    }

    /**
     * Returns the notUpdated flag.
     * 
     * @return the notUpdated flag.
     */
    public boolean isNotUpdated()
    {
        return notUpdated;
    }

    private static DataSource[] getOrder(Configuration config, Map<String, DataSource> dataSources)
        throws ConfigurationException
    {
        String graphName = config.getChild("name").getValue();
        Configuration orderCfg = config.getChild("order");
        String order = orderCfg.getValue(null);
        if(order == null)
        {
            return null;
        }
        String[] names = order.split("\\s*,\\s");
        DataSource[] result = new DataSource[names.length];
        int i = 0;
        for(String name : names)
        {
            DataSource ds = dataSources.get(name);
            if(ds == null)
            {
                throw new ConfigurationException("unknown dataSource " + name
                    + " referenced in graph " + graphName, orderCfg.getPath(), orderCfg
                    .getLocation());
            }
            result[i++] = ds;
        }
        return result;
    }
}
