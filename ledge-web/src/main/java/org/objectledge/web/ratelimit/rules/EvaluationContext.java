package org.objectledge.web.ratelimit.rules;

import java.net.InetAddress;

public interface EvaluationContext
{
    InetAddress getAddress();
    
    String getHost();
    
    String getHeader(String headerName);
    
    int getHits();
    
    boolean inList(String listName);
}
