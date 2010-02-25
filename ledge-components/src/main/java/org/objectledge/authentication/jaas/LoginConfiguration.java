package org.objectledge.authentication.jaas;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.ConfigurationSpi;

public class LoginConfiguration
    extends ConfigurationSpi
{
    private final LoginConfigurationParameters params;

    public LoginConfiguration(Configuration.Parameters params)
    {
        if(params instanceof LoginConfigurationParameters)
        {
            this.params = (LoginConfigurationParameters)params;
        }
        else
        {
            throw new IllegalArgumentException("invalid Parameters class "
                + params.getClass().getName());
        }
    }

    @Override
    protected AppConfigurationEntry[] engineGetAppConfigurationEntry(String name)
    {
        return params.engineGetAppConfigurationEntry(name);
    }
}
