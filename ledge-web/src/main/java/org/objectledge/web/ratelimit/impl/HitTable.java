package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class HitTable
{
    protected Map<String, Hit> table = new ConcurrentHashMap<>();

    public int getHits(InetAddress address)
    {
        Hit hit = table.get(address.toString());
        return hit == null ? 0 : hit.getHits();
    }

    public Date getLastHit(InetAddress address)
    {
        Hit hit = table.get(address.toString());
        return hit == null ? null : hit.getLastHit();
    }

    public int getMatches(InetAddress address)
    {
        Hit hit = table.get(address.toString());
        return hit == null ? 0 : hit.getMatches();
    }

    public Date getLastMatch(InetAddress address)
    {
        Hit hit = table.get(address.toString());
        return hit == null ? null : hit.getLastMatch();
    }

    public long getLastMatchingRuleId(InetAddress address)
    {
        Hit hit = table.get(address.toString());
        return hit == null ? -1 : hit.getLastMatchingRuleId();
    }

    public boolean isThresholdExceeded(InetAddress address)
    {
        Hit hit = table.get(address.toString());
        return hit == null ? false : hit.isThresholdExceeded();
    }

    public Hit hit(InetAddress address)
    {
        String key = address.toString();
        Hit hit = table.get(key);
        if(hit == null)
        {
            hit = new Hit();
            table.put(key, hit);
        }
        else
        {
            hit.incHits();
        }
        return hit;
    }

    public Hit match(Rule rule, InetAddress address)
    {
        String key = address.toString();
        Hit hit = table.get(key);
        if(hit == null)
        {
            hit = new Hit();
            table.put(key, hit);
        }
        hit.incMatches(rule.getRuleId());
        return hit;
    }

    public static class Hit
    {
        private final AtomicInteger hits;

        private final AtomicInteger matches;

        private final AtomicLong lastMatchingRuleId;

        private final AtomicLong lastHit;

        private final AtomicLong lastMatch;

        private final AtomicBoolean thresholdExceeded;

        public Hit(int hits, long lastHit, int matches, long lastMatchingRuleId, long lastMatch)
        {
            this.hits = new AtomicInteger(hits);
            this.lastHit = new AtomicLong(lastHit);
            this.matches = new AtomicInteger(matches);
            this.lastMatchingRuleId = new AtomicLong(lastMatchingRuleId);
            this.lastMatch = new AtomicLong(lastMatch);
            this.thresholdExceeded = new AtomicBoolean(false);
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

        public boolean isThresholdExceeded()
        {
            return thresholdExceeded.get();
        }

        public void setThresholdExeeded()
        {
            thresholdExceeded.set(true);
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
