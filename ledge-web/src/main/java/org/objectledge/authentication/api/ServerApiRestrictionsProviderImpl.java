package org.objectledge.authentication.api;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;

public class ServerApiRestrictionsProviderImpl
    implements ServerApiRestrictionProvider
{
    private final ServerApiRestrictions serverApiRestrictions;

    public ServerApiRestrictionsProviderImpl(Configuration config, Logger logger)
        throws ConfigurationException
    {
        this.serverApiRestrictions = new ServerApiRestrictions(config, logger);
    }

    public ServerApiRestrictions getServerApiRestrictions()
    {
        return serverApiRestrictions;
    }
}
