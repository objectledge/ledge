package org.objectledge.statistics;

import java.util.Map;

public interface MuninGraph
{
    public String getId();
    
    public String getConfig();
    
    public String getTitle();
    
    public String[] getVariables();
    
    public String getLabel(String variable);

    public Map<String, Number> getValues();
}
