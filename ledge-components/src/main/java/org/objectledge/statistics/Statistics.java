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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;

/**
 * A component that gathers systemwide statistics. 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Statistics.java,v 1.6 2005-05-12 05:55:33 rafal Exp $
 */
public class Statistics
{
    private Configuration config;
    
    private final Logger log;
    
    private final List<StatisticsProvider> providers = new ArrayList<StatisticsProvider>();
    
    private long lastConfigTime = -1L;
    
    private long lastProvidersCount = 0;
    
    /**
     * Creates new Statistics instance.
     *
     * @param config the component configuration.
     * @param log the logger.
     */
    public Statistics(Configuration config, Logger log)
    {
        this.config = config;
        this.log = log;
    }

    /**
     * Register a statistics provider instance.
     *
     * <p>It's a good idea to make components that implement that interface (directly or through a 
     * helper class) Startable, so that full set of providers is register upon system startup.</p>
     * 
     * @param provider the statistics provider.
     */
    public void registerProvider(StatisticsProvider provider)
    {
        log.info("registering provider " + provider.getName());
        synchronized(providers)
        {
            providers.add(provider);
        }
    }
    
    /**
     * Returns the registered graphs.
     * 
     * @return the registered graphs.
     */
    public String[] getGraphNames()
    {
        synchronized(providers)
        {
            reconfigureIfNeeded();
            List<String> l = new ArrayList<String>(graphs.size());
            l.addAll(graphs.keySet());
            Collections.sort(l);
            return l.toArray(new String[l.size()]);
        }
    }
    
    /**
     * Returns graph configuration for the specified graph
     * 
     * @param name the graph name.
     * @return graph configuration.
     */
    public Graph getGraph(String name)
    {
        synchronized(providers)
        {
            reconfigureIfNeeded();
            Graph g = graphs.get(name);
            if(g == null)
            {
                throw new IllegalArgumentException("unknown graph "+name);
            }
            return g;
        }
    }
    
    /**
     * Returns graph's data samples.
     * 
     * @param ds the DataSource.
     * @return the graph's data samples.
     */
    public Number getDataValue(DataSource ds)
    {
        synchronized(providers)
        {
            StatisticsProvider provider = dataSourceOwners.get(ds);
            return provider.getDataValue(ds.getName());
        }
    }
    
    private Map<String,DataSource> dataSources = new HashMap<String,DataSource>();
    
    private Map<DataSource, StatisticsProvider> dataSourceOwners = 
        new HashMap<DataSource,StatisticsProvider>();
    
    private Map<String,Graph> graphs = new HashMap<String,Graph>();
    
    private void configure(Configuration config)
        throws ConfigurationException
    {
        // declared data sources
        for(StatisticsProvider provider : providers)
        {
            DataSource[] providerDataSources = provider.getDataSources();
            for(DataSource dataSource : providerDataSources)
            {
                DataSource registered = dataSources.get(dataSource.getName());
                if(registered != null)
                {
                    throw new ComponentInitializationError("statistics providers " + 
                        provider.getName() + " and " + dataSourceOwners.get(registered) + 
                        " declare data source with name " + dataSource.getName());
                }
                dataSources.put(dataSource.getName(), dataSource);
                dataSourceOwners.put(dataSource, provider);
            }
        }
        // data source overrides
        Configuration[] overrideCfgs = config.getChildren("dataSource");
        for(Configuration overrideCfg : overrideCfgs)
        {
            DataSource override = new DataSource(overrideCfg);
            DataSource registered = dataSources.get(override.getName());
            if(registered == null)
            {
                throw new ConfigurationException("found override for not registered data source "+
                    override.getName(), overrideCfg.getPath(), overrideCfg.getLocation());
            }
            dataSources.put(override.getName(), new DataSource(registered, override));
        }
        // declared graphs
        Map<Graph, StatisticsProvider> graphOwner = new HashMap<Graph, StatisticsProvider>();
        for(StatisticsProvider provider : providers)
        {
            Graph[] providerGraphs = provider.getGraphs();
            for(Graph graph : providerGraphs)
            {
                Graph registered = graphs.get(graph.getName());
                if(registered != null)
                {
                    throw new ComponentInitializationError("statistics providers " + 
                        provider.getName() + " and " + dataSourceOwners.get(registered) + 
                        " declare graph with name " + graph.getName());
                }
                graphs.put(graph.getName(), graph);
            }
        }
        // graph overrides
        overrideCfgs = config.getChildren("graph");
        for(Configuration overrideCfg : overrideCfgs)
        {
            Graph override = new Graph(overrideCfg, dataSources);
            Graph registered = graphs.get(override.getName());
            if(registered == null)
            {
                throw new ConfigurationException("found override for not registered graph "+
                    override.getName(), overrideCfg.getPath(), overrideCfg.getLocation());
            }
            graphs.put(override.getName(), new Graph(registered, override));
        }
        // update DataSource references inside Graph objects
        for(Graph graph : graphs.values())
        {
            graph.updateDataSources(dataSources);
        }
        lastConfigTime = System.currentTimeMillis();
        lastProvidersCount = providers.size();
    }
    
    private void reconfigureIfNeeded()
    {
        synchronized(providers)
        {
            if(lastConfigTime == -1L || providers.size() != lastProvidersCount)
            {
                if(lastConfigTime != -1L)
                {
                   log.warn("statistics providers registered after system startup: reconfiguring"); 
                }
                try
                {
                    configure(config);
                }
                catch(ConfigurationException e)
                {
                    throw new ComponentInitializationError("failed to configure " +
                            "Statistics component", e);
                }
            }
        }
    }
}
