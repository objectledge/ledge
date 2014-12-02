package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestInfo
{
    private final String path;

    private final InetAddress address;

    private final String host;

    private final Map<String, String> headers;

    public RequestInfo(InetAddress address, String host, Map<String, String> headers, String path)
    {
        this.address = address;
        this.host = host;
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
        this.path = path;
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
    
    public String getPath()
    {
        return path;
    }
}
