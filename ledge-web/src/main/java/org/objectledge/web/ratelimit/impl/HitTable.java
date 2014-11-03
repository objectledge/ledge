package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HitTable
{
    private Map<String, Hit> table = new ConcurrentHashMap<>();

    public int getHits(InetAddress adddress)
    {
        Hit hit = table.get(adddress.toString());
        return hit == null ? 0 : hit.getHits();
    }

    public Date getLastHit(InetAddress adddress)
    {
        Hit hit = table.get(adddress.toString());
        return hit == null ? null : hit.getLastHit();
    }

    public int getMatches(InetAddress adddress)
    {
        Hit hit = table.get(adddress.toString());
        return hit == null ? 0 : hit.getMatches();
    }

    public Date getLastMatch(InetAddress adddress)
    {
        Hit hit = table.get(adddress.toString());
        return hit == null ? null : hit.getLastMatch();
    }

    public long getLastMatchingRuleId(InetAddress adddress)
    {
        Hit hit = table.get(adddress.toString());
        return hit == null ? -1 : hit.getLastMatchingRuleId();
    }

    public void hit(InetAddress adddress)
    {
        String key = adddress.toString();
        Hit hit = table.get(key);
        if(hit == null)
        {
            table.put(key, new Hit());
        }
        else
        {
            hit.incHits();
        }
    }

    public void match(Rule rule, InetAddress adddress)
    {
        String key = adddress.toString();
        Hit hit = table.get(key);
        if(hit == null)
        {
            hit = new Hit();
            table.put(key, hit);
        }
        hit.incMatches(rule.getRuleId());
    }

    private static class Hit
    {
        private final AtomicInteger hits;

        private final AtomicInteger matches;

        private final AtomicLong lastMatchingRuleId;

        private final AtomicLong lastHit;

        private final AtomicLong lastMatch;

        public Hit(int hits, long lastHit, int matches, long lastMatchingRuleId, long lastMatch)
        {
            this.hits = new AtomicInteger(hits);
            this.lastHit = new AtomicLong(lastHit);
            this.matches = new AtomicInteger(matches);
            this.lastMatchingRuleId = new AtomicLong(lastMatchingRuleId);
            this.lastMatch = new AtomicLong(lastMatch);
        }

        public Hit()
        {
            this(1, System.currentTimeMillis(), 0, -1, 0l);
        }

        public int getHits()
        {
            return hits.get();
        }

        public Date getLastHit()
        {
            return new Date(lastHit.get());
        }

        public int getMatches()
        {
            return matches.get();
        }

        public long getLastMatchingRuleId()
        {
            return lastMatchingRuleId.get();
        }

        public Date getLastMatch()
        {
            return new Date(lastMatch.get());
        }

        public void incHits()
        {
            hits.incrementAndGet();
            lastHit.set(System.currentTimeMillis());
        }

        public void incMatches(long ruleId)
        {
            matches.incrementAndGet();
            lastMatchingRuleId.set(ruleId);
            lastMatch.set(System.currentTimeMillis());
        }
    }

}
