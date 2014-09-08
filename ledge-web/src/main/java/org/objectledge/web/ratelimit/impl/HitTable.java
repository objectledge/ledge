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

    public int getMatches(RequestInfo requestInfo)
    {
        Hit hit = table.get(requestInfo.getAddress().toString());
        return hit == null ? 0 : hit.getMatches();
    }

    public long getLastMatchingRuleId(RequestInfo requestInfo)
    {
        Hit hit = table.get(requestInfo.getAddress().toString());
        return hit == null ? -1 : hit.getLastMatchingRuleId();
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

    public void match(Rule rule, RequestInfo requestInfo)
    {
        String key = requestInfo.getAddress().toString();
        Hit hit = table.get(key);
        if(hit == null)
        {
            hit = new Hit(requestInfo.getAddress());
            table.put(key, hit);
        }
        hit.incMatches(rule.getRuleId());
    }
}
