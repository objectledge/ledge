package org.objectledge.authentication.sso;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;

public class ServerApiRestrictions
{
    private final boolean enabled;
    
    private final boolean requireSsl;
    
    private final String secret;

    private final Logger log;
    
    public ServerApiRestrictions(Configuration config, Logger log)
    {
        this.log = log;
        this.enabled = config.getAttributeAsBoolean("enabled", false);
        this.requireSsl = config.getAttributeAsBoolean("requireSsl", true);
        this.secret = config.getChild("secret").getValue(null);
    }
    
    public boolean validateApiRequest(String secret, String remoteAddr, boolean secure)
    {
        String declineReson;
        if(enabled)
        {
            if(secure || !requireSsl)
            {
                if(this.secret == null || this.secret.equals(secret))
                {
                    log.debug("ACCEPTED API call from " + remoteAddr);
                    return true;
                }
                else if(secret == null)
                {
                    declineReson = "no secret provided";
                }                    
                else
                {
                    declineReson = "invalid secret provided";
                }
            }
            else
            {
                declineReson = "not using https";
            }
        }
        else
        {
            declineReson = "API access disabled";
        }
        log.warn("DECLINED API call from " + remoteAddr + " " + declineReson);
        return false;
    }
}
