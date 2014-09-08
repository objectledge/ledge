package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestInfo
{
    private final InetAddress address;

    private final String host;

    private final Map<String, String> headers;
    
    public RequestInfo(InetAddress address, String host, Map<String, String> headers)
    {
        this.address = address;
        this.host = host;
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));        
    }

    public InetAddress getAddress()
    {
        return address;
    }

    public String getHost()
    {
        return host;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }
}
