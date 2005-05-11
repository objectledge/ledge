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
import java.util.List;

import org.jcontainer.dna.Configuration;

/**
 * A component that gathers systemwide statistics. 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Statistics.java,v 1.2 2005-05-11 05:25:08 rafal Exp $
 */
public class Statistics
{
    private Configuration config;
    
    private final List<StatisticsProvider> providers = new ArrayList<StatisticsProvider>();
    
    /**
     * Creates new Statistics instance.
     *
     * @param config the component configuration.
     */
    public Statistics(Configuration config)
    {
        this.config = config;
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
        providers.add(provider);
    }
    
    /**
     * Returns the registered graphs.
     * 
     * @return the registered graphs.
     */
    public String[] getGraphNames()
    {
        return new String[0];
    }
    
    /**
     * Returns graph configuration for the specified graph
     * 
     * @param name the graph name.
     * @return graph configuration.
     */
    public Graph getGraph(String name)
    {
        return null;
    }
    
    /**
     * Returns graph's data samples.
     * 
     * @param name the graph name.
     * @return the graph's data samples.
     */
    public Number[] getValues(String name)
    {
        return null;
    }
}
