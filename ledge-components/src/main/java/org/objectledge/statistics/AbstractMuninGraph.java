package org.objectledge.statistics;

import static java.util.regex.Pattern.MULTILINE;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;

public abstract class AbstractMuninGraph
    implements MuninGraph
{
    private final FileSystem fs;

    private String config;

    public AbstractMuninGraph(FileSystem fs)
    {
        this.fs = fs;
    }

    public String getConfig()
    {
        if(config == null)
        {
            String path = getConfigPath();
            try
            {
                config = fs.read(path, "UTF-8");
            }
            catch(IOException e)
            {
                throw new ComponentInitializationError("Failed to load configuration", e);
            }
        }
        return config;
    }

    protected Number getValue(String variable)
    {
        String accessorName = getAccessorName(variable);
        Method m;
        try
        {
            m = getClass().getMethod(accessorName);
            return (Number)m.invoke(this);
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException("unknown variable " + variable, e);
        }
        catch(InvocationTargetException e)
        {
            throw new RuntimeException("failed to retrieve value", e.getTargetException());
        }
        catch(Exception e)
        {
            throw new RuntimeException("introspection problem", e);
        }
    }

    public String[] getVariables()
    {
        Matcher m = Pattern.compile("^graph_order\\s+(.*)$", MULTILINE).matcher(getConfig());
        if(!m.find())
        {
            throw new ComponentInitializationError(
                "graph_order entry is missing from configuration file " + getConfigPath());
        }
        return m.group(1).split("\\s+");
    }
    
    public String getTitle()
    {
        Matcher m = Pattern.compile("^graph_title\\s+(.*)$", MULTILINE).matcher(getConfig());
        if(!m.find())
        {
            throw new ComponentInitializationError(
                "graph_title entry is missing from configuration file " + getConfigPath());
        }
        return m.group(1);
    }
    
    public String getLabel(String variable)
    {
        Matcher m = Pattern.compile("^"+variable+".label\\s+(.*)$", MULTILINE).matcher(getConfig());
        if(!m.find())
        {
            throw new ComponentInitializationError(
                variable+".label entry is missing from configuration file " + getConfigPath());
        }
        return m.group(1);
    }

    @Override
    public Map<String, Number> getValues()
    {
        Map<String, Number> values = new HashMap<String, Number>();
        for(String variable : getVariables())
        {
            values.put(variable, getValue(variable));
        }
        return values;
    }

    protected String getConfigPath()
    {
        String overridePath = "config/" + getClass().getName() + ".munin";
        String basePath = getClass().getName().replace('.', '/') + ".munin";
        if(fs.exists(overridePath)) 
        {
            return overridePath;
        }
        else
        {
            return basePath;
        }
    }

    protected String getAccessorName(String variable)
    {
        return "get" + Character.toUpperCase(variable.charAt(0)) + variable.substring(1);
    }
}
