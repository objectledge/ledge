package org.objectledge.parameters.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.UndefinedParameterException;

/**
 * A simple implementation of parameters container.
 *
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: ParametersImpl.java,v 1.1 2003-11-27 17:02:48 pablo Exp $
 */
public class ParametersImpl implements Parameters
{
	/** string representation for boolean <code>true</code> value. */
	public static final String TRUE = "true";
	
	/** The main parameters map */
	protected HashMap map = new HashMap();

    /**
     * Create the empty container.
     */
    public ParametersImpl()
    {
    }

    /**
     * Create the container and feed it with configuration given as string. 
     * 
     * @param configuration the string representation of the container. 
     */
    public ParametersImpl(String configuration)
    {
        LineNumberReader reader = new LineNumberReader(new StringReader(configuration));
        try
        {
            loadParameters(reader);
        }
		catch(IOException e)
		{
			throw new IllegalArgumentException("IOException occurred"+e.getMessage());
		}
    }

    /**
     * Create the container and feed it with configuration given as string. 
     * 
     * @param is the stream with byte representation of the container.
     * @param encoding the encoding of the source.
     */
    public ParametersImpl(InputStream is, String encoding)
    {
    	try
    	{
    		LineNumberReader reader = new LineNumberReader(new InputStreamReader(is, encoding));
    		loadParameters(reader);
    	}
    	catch(UnsupportedEncodingException e)
    	{
    		throw new IllegalArgumentException("Unknown Character Exception");
    	}
    	catch(IOException e)
    	{
			throw new IllegalArgumentException("IOException occurred"+e.getMessage());
    	}
    }

	/**
	 * Create the container as a copy of source container. 
	 * 
	 * @param source the source container.
	 */
	public ParametersImpl(Parameters source)
	{
		this();
		String[] names = source.getParameterNames();
		for(int i=0; i<names.length; i++)
		{
			String[] values = source.getStrings(names[i]);
			if(values != null)
			{
				String[] target = new String[values.length];
				System.arraycopy(values,0,target,0,values.length); 
				map.put(names[i], target);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
    public String get(String name)
    {
		String[] values = (String[])map.get(name);
		if(values == null || values.length == 0)
		{
			throw new UndefinedParameterException("Parameter '"+name+"'is undefined");
		}
		if(values.length > 1)
		{
			throw new AmbiguousParameterException("Parameter '"+name+"'is not unique");
		}
		return values[0];
    }

	/**
	 * {@inheritDoc}
	 */
    public String get(String name, String defaultValue)
    {
		String[] values = (String[])map.get(name);
		if(values == null || values.length == 0)
		{
			return defaultValue;
		}
		if(values.length > 1)
		{
			throw new AmbiguousParameterException("Parameter '"+name+"'is not unique");
		}
		return values[0];
    }

	/**
	 * {@inheritDoc}
	 */
    public String[] getStrings(String name)
    {
		String[] values = (String[])map.get(name);
		if(values == null)
		{
			values = new String[0];
		}
		return values;
    }

	/**
	 * {@inheritDoc}
	 */
    public boolean getBoolean(String name)
    {
    	String value = get(name);
    	return value.equals(TRUE);
    }

	/**
	 * {@inheritDoc}
	 */
    public boolean getBoolean(String name, boolean defaultValue)
    {
    	String value = get(name, ""+defaultValue);
    	return value.equals(TRUE);
    }

    /**
     * {@inheritDoc}
     */
    public boolean[] getBooleans(String name)
    {
    	String[] values = getStrings(name);
    	boolean[] target = new boolean[values.length];
    	for(int i = 0; i < values.length; i++)
    	{
    	    target[i] = values[i].equals(TRUE);
    	}
    	return target;
    }

	/**
	 * {@inheritDoc}
	 */
    public float getFloat(String name) throws NumberFormatException
    {
		return Float.parseFloat(get(name));
    }

	/**
	 * {@inheritDoc}
	 */
    public float getFloat(String name, float defaultValue)
    {
    	return Float.parseFloat(get(name,""+defaultValue));
    }

    /**
     * {@inheritDoc}
     */
    public float[] getFloats(String name) throws NumberFormatException
    {
		String[] values = getStrings(name);
		float[] target = new float[values.length];
		for(int i = 0; i < values.length; i++)
		{
			target[i] = Float.parseFloat(values[i]);
		}
		return target;
    }

	/**
	 * {@inheritDoc}
	 */
    public int getInt(String name) throws NumberFormatException
    {
    	return Integer.parseInt(get(name));
    }

	/**
	 * {@inheritDoc}
	 */
    public int getInt(String name, int defaultValue)
    {
		return Integer.parseInt(get(name, ""+defaultValue));
    }

    /**
     * {@inheritDoc}
     */
    public int[] getInts(String name)
    {
		String[] values = getStrings(name);
		int[] target = new int[values.length];
		for(int i = 0; i < values.length; i++)
		{
			target[i] = Integer.parseInt(values[i]);
		}
		return target;
    }

	/**
	 * {@inheritDoc}
	 */
    public long getLong(String name) throws NumberFormatException
    {
		return Long.parseLong(get(name));
    }

	/**
	 * {@inheritDoc}
	 */
    public long getLong(String name, long defaultValue)
    {
        return Long.parseLong(get(name,""+defaultValue));
    }
    
	/**
	 * {@inheritDoc}
	 */
	public long[] getLongs(String name) throws NumberFormatException
	{
		String[] values = getStrings(name);
		long[] target = new long[values.length];
		for(int i = 0; i < values.length; i++)
		{
			target[i] = Long.parseLong(values[i]);
		}
		return target;		
	}

	/**
	 * {@inheritDoc}
	 */
    public String[] getParameterNames()
    {
    	String[] names = new String[map.size()];
    	map.keySet().toArray(names);
    	return names;
    }

	/**
	 * {@inheritDoc}
	 */
    public boolean isDefinied(String name)
    {
        String[] values = (String[])map.get(name);
        if(values != null && values.length > 0)
        {
        	return true;
        }
        return false;
    }

	/**
	 * {@inheritDoc}
	 */
    public void remove()
    {
    	map.clear();
    }

	/**
	 * {@inheritDoc}
	 */
    public void remove(String name)
    {
		map.remove(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public void remove(String name, String value)
    {
    	//TODO
    	String[] values = (String[])map.get(name);
    	//...
    	//...  
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, float value)
    {
    	throw new UnsupportedOperationException("not implemented yet");    
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, int value)
    {
    	throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, long value)
    {
    	throw new UnsupportedOperationException("not implemented yet");
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, String value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, boolean value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, float value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, int value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, int[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, long value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, long[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, String value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
	/**
	 * {@inheritDoc}
	 */
	public void add(String name, String[] values)
	{
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, boolean value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, boolean[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, float value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, float[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, int value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, int[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, long value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, long[] values)
    {
        // TODO Auto-generated method stub
    
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(Parameters parameters, boolean override)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

	/**
	 * {@inheritDoc}
	 */
    public String getString()
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

	/**
	 * {@inheritDoc}
	 */
    public Parameters getChild(String prefix)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    private void loadParameters(LineNumberReader reader)
    	throws IOException
    {
		if(reader==null || !reader.ready())
		{
			return;
		}
		while(reader.ready())
		{
			// merge broken lines
			String line = reader.readLine().trim();
			if(line.startsWith("#") || line.length()==0)
			{
				continue;
			}
			int startingLine = reader.getLineNumber();
			StringBuffer sb = new StringBuffer();
			while(line.endsWith("\\") && reader.ready())
			{
				if(line.endsWith("\\"))
				{
					sb.append(line.substring(0, line.length()-1));
				}
				else
				{
					sb.append(line);
				}
				line = reader.readLine().trim();
			}
						
			while(line == null)
			{
				line = reader.readLine();
				sb.append(line);
			}							
			if(line.endsWith("\\"))
			{
				throw new IllegalArgumentException("The "+startingLine+" line is not ended");
			}
			// process the line			


			// ...
		}
    }
    
}
