package org.objectledge.web.captcha;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.HttpContext;

/**
 * A service that provides CAPTCHA functionality for web applications.
 * 
 * @author rafal 
 */
public class CaptchaCacheKey
{
    private String remoteAddr;
    
    private String challenge;

    private String response;
    
    public CaptchaCacheKey(String remoteAddr, String challenge, String response)
    {
        this.remoteAddr = remoteAddr;
        this.challenge = challenge;
        this.response = response;
    }

    public String getRemoteAddr()
    {
        return remoteAddr;
    }

    public String getChallenge()
    {
        return challenge;
    }

    public String getResponse()
    {
        return response;
    }
    
    public int hashCode()
    {
        return response.hashCode() ^ challenge.hashCode() ^ remoteAddr.hashCode();
    }

    public boolean equals(Object object)
    {
        if(object != null && object instanceof CaptchaCacheKey)
        {
            CaptchaCacheKey other = (CaptchaCacheKey)object;
            return this.challenge.equals(other.challenge) && this.response.equals(other.response)
                && this.remoteAddr.equals(other.remoteAddr);
        }
        else
        {
            return false;
        }
    }
    
}