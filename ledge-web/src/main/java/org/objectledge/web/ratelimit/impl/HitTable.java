package org.objectledge.web.ratelimit.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HitTable
{
    private Map<String, Hit> table = new ConcurrentHashMap<>();

    public int getHits(RequestInfo requestInfo)
    {
        Hit hit = table.get(requestInfo.getAddress().toString());
        return hit == null ? 0 : hit.getHits();
    }

    public void hit(RequestInfo requestInfo)
    {
        String key = requestInfo.getAddress().toString();
        Hit hit = table.get(key);
        if(hit == null)
        {
            table.put(key, new Hit(requestInfo.getAddress()));
        }
        else
        {
            hit.incHits();
        }
    }
}
