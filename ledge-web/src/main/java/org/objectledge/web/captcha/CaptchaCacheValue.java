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
public class CaptchaCacheValue
{
    private boolean value;

    private long timestamp;
    
    private long counter;

    public CaptchaCacheValue(boolean value)
    {
        this.value = value;
        this.counter = 0;
        this.timestamp = (new Date()).getTime();
    }

    public boolean getValue()
    {
        this.counter++;
        return value;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
    
    public long getCounter()
    {
        return counter;
    }
}
