package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Hit
{
    private InetAddress address;

    private final AtomicInteger hits;

    private final AtomicInteger matches;

    private final AtomicLong lastMatchingRuleId;

    private final AtomicLong lastHit;

    private final AtomicLong lastMatch;

    public Hit(InetAddress address, int hits, long lastHit, int matches, long lastMatchingRuleId,
        long lastMatch)
    {
        this.address = address;
        this.hits = new AtomicInteger(hits);
        this.lastHit = new AtomicLong(lastHit);
        this.matches = new AtomicInteger(matches);
        this.lastMatchingRuleId = new AtomicLong(lastMatchingRuleId);
        this.lastMatch = new AtomicLong(lastMatch);
    }

    public Hit(InetAddress address)
    {
        this(address, 1, System.currentTimeMillis(), 0, -1, 0l);
    }

    public InetAddress getAddress()
    {
        return address;
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
