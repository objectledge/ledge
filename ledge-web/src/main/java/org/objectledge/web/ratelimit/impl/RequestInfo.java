package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.objectledge.net.IPAddressUtil;

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
        this.headers = new HashMap<>();
        for(Map.Entry<String, String> e : headers.entrySet())
        {
            this.headers.put(e.getKey().toLowerCase(), e.getValue());
        }
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

    public String getHeader(String name)
    {
        return headers.get(name.toLowerCase());
    }

    public String getPath()
    {
        return path;
    }

    public static RequestInfo of(HttpServletRequest req)
    {
        try
        {
            Map<String, String> headers = new HashMap<>();
            @SuppressWarnings("unchecked")
            Enumeration<String> he = req.getHeaderNames();
            while(he.hasMoreElements())
            {
                String header = he.nextElement();
                headers.put(header, req.getHeader(header));
            }
            StringBuilder b = new StringBuilder();
            b.append(req.getContextPath());
            b.append(req.getServletPath());
            if(req.getPathInfo() != null)
            {
                b.append(req.getPathInfo());
            }
            return new RequestInfo(IPAddressUtil.byAddress(req.getRemoteAddr()),
                req.getRemoteHost(), headers, b.toString());
        }
        catch(UnknownHostException | IllegalArgumentException e)
        {
            throw new IllegalArgumentException(
                "Invalid address retured by HttpServletRequest.getRemoteAddr()", e);
        }
    }
}
