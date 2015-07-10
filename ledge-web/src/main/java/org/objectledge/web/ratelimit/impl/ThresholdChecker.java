package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;

import org.objectledge.web.ratelimit.impl.HitTable.Hit;

public interface ThresholdChecker
{
    boolean isThresholdExceeded(InetAddress address, Hit hit);
}
