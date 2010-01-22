package org.objectledge.statistics;

public interface MuninGraph
{
    public String getId();
    
    public String getConfig();
    
    public String getTitle();
    
    public String[] getVariables();
    
    public String getLabel(String variable);
    
    public Number getValue(String variable);
}
