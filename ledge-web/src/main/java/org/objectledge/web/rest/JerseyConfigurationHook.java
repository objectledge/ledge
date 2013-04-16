package org.objectledge.web.rest;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Use this hook for registering filters
 * 
 * @author Marek Lewandowski
 */
public interface JerseyConfigurationHook
{
    /**
     * Configure ResourceConfig
     * 
     * @param config
     */
    void configure(ResourceConfig config);
}
