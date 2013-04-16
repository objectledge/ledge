package org.objectledge.web.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class RegisterAuthenticationFilter
    implements JerseyConfigurationHook
{

    @Override
    public void configure(ResourceConfig config)
    {
        config.register(JerseyRestAuthenticationFilter.class);
    }

}
