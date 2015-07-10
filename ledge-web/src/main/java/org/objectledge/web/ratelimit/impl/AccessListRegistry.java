package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;

public interface AccessListRegistry
{
    boolean contains(String listName, InetAddress address);
    
    boolean anyContains(InetAddress address);
}
