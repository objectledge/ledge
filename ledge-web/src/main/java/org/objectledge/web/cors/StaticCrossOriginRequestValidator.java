package org.objectledge.web.cors;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

public class StaticCrossOriginRequestValidator
    implements CrossOriginRequestValidator
{
    private final Set<String> allowed = new HashSet<String>();

    public StaticCrossOriginRequestValidator(final Configuration config)
        throws ConfigurationException
    {
        for(final Configuration allowedConfig : config.getChildren("allowed"))
        {
            final String host = allowedConfig.getAttribute("host");
            final String protocols[] = allowedConfig.getAttribute("protocol", "http").trim()
                .split(" ");
            for(String protocol : protocols)
            {
                try
                {
                    switch(protocol)
                    {
                    case "http":
                        allowed.add("http://" + host);
                        break;
                    case "https":
                        allowed.add("https://" + host);
                        break;
                    }
                }
                catch(IllegalArgumentException e)
                {
                    throw new ConfigurationException("invalid ssl flag " + protocol,
                        allowedConfig.getPath(), allowedConfig.getLocation());
                }
            }
        }
    }

    @Override
    public boolean isAllowed(String originUri)
    {
        return allowed.contains(originUri);
    }
}
