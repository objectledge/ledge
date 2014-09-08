package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Hit
{
    private InetAddress address;

    private final AtomicInteger hits;

    private final AtomicLong lastHit;

    public Hit(InetAddress address, int hits, long lastHit)
    {
        this.address = address;
        this.hits = new AtomicInteger(hits);
        this.lastHit = new AtomicLong(lastHit);
    }

    public Hit(InetAddress address)
    {
        this(address, 1, System.currentTimeMillis());
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

    public void incHits()
    {
        hits.incrementAndGet();
        lastHit.set(System.currentTimeMillis());
    }
}
