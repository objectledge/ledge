package org.objectledge.authentication.sso;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.net.CIDRBlock;
import org.objectledge.net.IPAddressUtil;

public class ServerApiRestrictions
{
    private final boolean enabled;

    private final boolean requireSsl;

    private final String secret;

    private final Logger log;

    private final Set<CIDRBlock> addressRanges;

    public ServerApiRestrictions(Configuration config, Logger log)
        throws ConfigurationException
    {
        this.log = log;
        this.enabled = config.getAttributeAsBoolean("enabled", false);
        this.requireSsl = config.getAttributeAsBoolean("requireSsl", true);
        this.secret = config.getChild("secret").getValue(null);
        Configuration authorizedClientsConfig = config.getChild("authorizedClients", false);
        if(authorizedClientsConfig == null)
        {
            addressRanges = null;
        }
        else
        {
            addressRanges = new HashSet<CIDRBlock>();
            for(Configuration addressRangeConfig : authorizedClientsConfig.getChildren())
            {
                String[] rangeDef = addressRangeConfig.getValue().split("/");
                if(rangeDef.length != 2)
                {
                    throw new ConfigurationException("invalid CIDR block specification "
                        + addressRangeConfig.getValue(), addressRangeConfig.getPath(),
                        addressRangeConfig.getLocation());
                }
                InetAddress networkPrefix;
                try
                {
                    networkPrefix = IPAddressUtil.byAddress(rangeDef[0]);
                }
                catch(IllegalArgumentException e)
                {
                    throw new ConfigurationException("invalid IP address " + rangeDef[0],
                        addressRangeConfig.getPath(), addressRangeConfig.getLocation(), e);
                }
                catch(UnknownHostException e)
                {
                    throw new ConfigurationException("invalid IP address " + rangeDef[0],
                        addressRangeConfig.getPath(), addressRangeConfig.getLocation(), e);
                }
                int prefixLength;
                try
                {
                    prefixLength = Integer.parseInt(rangeDef[1]);
                }
                catch(NumberFormatException e)
                {
                    throw new ConfigurationException("invalid prefix length",
                        addressRangeConfig.getPath(), addressRangeConfig.getLocation(), e);
                }
                try
                {
                    addressRanges.add(new CIDRBlock(networkPrefix, prefixLength));
                }
                catch(IllegalArgumentException e)
                {
                    throw new ConfigurationException("invalid CIDR block specification "
                                    + addressRangeConfig.getValue(), addressRangeConfig.getPath(),
                                    addressRangeConfig.getLocation(), e);
                }
            }
        }
    }

    public boolean validateApiRequest(String secret, String remoteAddr, boolean secure)
    {
        String declineReason;
        if(enabled)
        {
            if(secure || !requireSsl)
            {
                if(this.secret == null || this.secret.equals(secret))
                {
                    if(this.addressRanges != null)
                    {
                        try
                        {
                            InetAddress remote = IPAddressUtil.byAddress(remoteAddr);
                            for(CIDRBlock addressRange : addressRanges)
                            {
                                try
                                {
                                    if(addressRange.contains(remote))
                                    {
                                        log.debug("ACCEPTED API call from " + remoteAddr);
                                        return true;
                                    }
                                }
                                catch(IllegalArgumentException e)
                                {
                                    declineReason = "failed to match IP address to CIDR block " + addressRange;
                                    log.error(declineReason, e);
                                }
                            }
                            declineReason = "IP does not match any of the authorized ranges";
                        }
                        catch(IllegalArgumentException e)
                        {
                            declineReason = "failed to parse IP address";
                            log.error(declineReason, e);
                        }
                        catch(UnknownHostException e)
                        {
                            declineReason = "failed to parse IP address";
                            log.error(declineReason, e);
                        }
                    }     
                    else
                    {
                        log.debug("ACCEPTED API call from " + remoteAddr);
                        return true;                        
                    }
                }
                else if(secret == null)
                {
                    declineReason = "no secret provided";
                }
                else
                {
                    declineReason = "invalid secret provided";
                }
            }
            else
            {
                declineReason = "not using https";
            }
        }
        else
        {
            declineReason = "API access disabled";
        }
        log.warn("DECLINED API call from " + remoteAddr + " " + declineReason);
        return false;
    }
}
