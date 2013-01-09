//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, 
//are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//nor the names of its contributors may be used to endorse or promote products 
//derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
//POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.parameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.objectledge.database.DatabaseUtils;

/**
 * A simple implementation of parameters container.
 *
 * @author <a href="mailto:pablo@caltha.com">Pawel Potempski</a>
 * @version $Id: DefaultParameters.java,v 1.24 2005-12-20 09:09:32 pablo Exp $
 */
public class DefaultParameters implements Parameters
{
    /** string representation for boolean <code>true</code> value. */
    public static final String TRUE = "true";

    /** 
     * Create a string represenation of value array.
     * 
     * <p>Values will be emmited comma separated, with any contained commas backslash-escaped.</p>
     *  
     * @param values value arrary.
     * @return the string representation of the values. 
     */
    public static String toString(String[] values)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++)
        {
            String value = values[i];
            int index = value.indexOf(',');
            int start = 0;
            while (index >= 0)
            {
                sb.append(value.substring(start, index));
                sb.append("\\,");
                start = index + 1;
                index = value.indexOf(',', start);
            }
            if (start < value.length())
            {
                sb.append(value.substring(start));
            }
            if (i < (values.length - 1))
            {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    /** The main parameters map. */
    protected Map<String, String[]> map;

	/**
     * Method used in constructors to choose backing <code>Map</code> implementation. 
     * By default a <code>HashMap</code> is used.
     */
    protected void setupMap()
    {
        map = new HashMap<String, String[]>();
    }
    
    /**
     * Create the empty container.
     */
    public DefaultParameters()
    {
        setupMap();
    }

    /**
     * Create the container and feed it with configuration given as string. 
     * 
     * @param configuration the string representation of the container. 
     */
    public DefaultParameters(String configuration)
    {
        setupMap();
        try
        {
            LineNumberReader reader = new LineNumberReader(new StringReader(configuration));
            loadParameters(reader);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Exception occurred" + e.getMessage());
        }
    }

    /**
     * Create the container and feed it with configuration given as string. 
     * 
     * @param is the stream with byte representation of the container.
     * @param encoding the encoding of the source.
     * @throws UnsupportedEncodingException if the specified encoding is not supported by the JVM.
     * @throws IOException if there is an error reading data from the stream.
     */
    public DefaultParameters(InputStream is, String encoding)
        throws IOException, UnsupportedEncodingException
    {
        setupMap();
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is, encoding));
        loadParameters(reader);
    }

    /**
     * Create the container as a copy of source container. 
     * 
     * @param source the source container.
     */
    public DefaultParameters(Parameters source)
    {
        setupMap();
        add(source, true);
    }

    /**
     * {@inheritDoc}
     */
    public String get(String name)
    {
        String[] values = map.get(name);
        if (values == null || values.length == 0)
        {
            throw new UndefinedParameterException("Parameter '" + name + "' is undefined");
        }
        if (values.length > 1)
        {
            throw new AmbiguousParameterException("Parameter '" + name + "' has multiple values");
        }
        return values[0];
    }

    /**
     * Returns the value of the parameter, or null if it is undefined.
     * 
     * <p>This method will return <code>null</code> in any of the following situations:
     * <ul>
     * <li>the internal map contains no value array for the specified name</li>
     * <li>the internal map contains an empty value array for the specified name</li>
     * <li>the internal map contains a single value array for the specified name, and the 
     * value in the array is an empty string</li>
     * </ul>
     * @param name the name of the parameter.
     * @return value of the parameter if undefined (see above).
     * @throws AmbiguousParameterException if the parameter has more than one value.
     */
    protected String getSingleValue(String name)
        throws AmbiguousParameterException
    {
        String[] values = map.get(name);
        if (values == null || values.length == 0)
        {
            return null;
        }
        if (values.length > 1)
        {
            throw new AmbiguousParameterException("Parameter '" + name + "'has multiple values");
        }
        String value = values[0];
        if(value.equals(""))
        {
            return null;
        }
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    public String get(String name, String defaultValue)
    {
        String[] values = map.get(name);
        if (values == null || values.length == 0)
        {
            return defaultValue;
        }
        if (values.length > 1)
        {
            throw new AmbiguousParameterException("Parameter '" + name + "'has multiple values");
        }
        return values[0];
    }

    /**
     * {@inheritDoc}
     */
    public String[] getStrings(String name)
    {
        String[] values = map.get(name);
        if (values == null)
        {
            return new String[0];
        }
        String[] target = new String[values.length];
        System.arraycopy(values, 0, target, 0, values.length);
        return target;
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
        String value = getSingleValue(name);
        return value != null ? value.equals(TRUE) : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public boolean[] getBooleans(String name)
    {
        String[] values = getStrings(name);
        boolean[] target = new boolean[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = values[i].equals(TRUE);
        }
        return target;
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String name)
    {
        String value = get(name);
        return new Date(Long.parseLong(value));
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String name, Date defaultValue)
    {
        String value = getSingleValue(name);
        return value != null ? new Date(Long.parseLong(value)) : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public Date[] getDates(String name)
    {
        String[] values = getStrings(name);
        Date[] target = new Date[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = new Date(Long.parseLong(values[i]));
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
        String value = getSingleValue(name);
        return value != null ? Float.parseFloat(value) : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public float[] getFloats(String name) throws NumberFormatException
    {
        String[] values = getStrings(name);
        float[] target = new float[values.length];
        for (int i = 0; i < values.length; i++)
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
        String value = getSingleValue(name);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getInts(String name)
    {
        String[] values = getStrings(name);
        int[] target = new int[values.length];
        for (int i = 0; i < values.length; i++)
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
        String value = getSingleValue(name);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public long[] getLongs(String name) throws NumberFormatException
    {
        String[] values = getStrings(name);
        long[] target = new long[values.length];
        for (int i = 0; i < values.length; i++)
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
    public boolean isDefined(String name)
    {
        String[] values = map.get(name);
        if (values != null && values.length > 0)
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
        String[] values = map.get(name);
        if (values == null || values.length == 0)
        {
            return;
        }
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; i++)
        {
            if (!((values[i] == null && value == null) || 
                (value != null && value.equals(values[i]))))
            {
                list.add(values[i]);
            }
        }
        values = new String[list.size()];
        list.toArray(values);
        map.put(name, values);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, Date value)
    {
        remove(name, Long.toString(value.getTime()));
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, float value)
    {
        remove(name, Float.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, int value)
    {
        remove(name, Integer.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, long value)
    {
        remove(name, Long.toString(value));
    }

    /**
     * Remove all parameters with a name contained in given set.
     *
     * @param keys the set of keys.
     */
    public void remove(Set<String> keys)
    {
        Iterator<String> it = keys.iterator();
        while (it.hasNext())
        {
            map.remove(it.next());
        }
    }

    /**
     * Remove all except those with a keys specified in the set.
     *
     * @param keys the set of names.
     */
    public void removeExcept(Set<String> keys)
    {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext())
        {
            String key = it.next();
            if (!keys.contains(key))
            {
                it.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String value)
    {
        if(value != null)
        {
            String[] values = new String[1];
            values[0] = value;
            map.put(name, values);
        }
        else
        {
            map.remove(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values)
    {
        ArrayList<String> list = new ArrayList<String>(values.length);
        for (String val : values)
        {
            if(val != null)
            {
                list.add(val);
            }
        }
        if(list.size() > 0)
        {
            map.put(name, list.toArray(new String[list.size()]));
        }
        else
        {
            map.remove(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean value)
    {
        set(name, Boolean.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Boolean.toString(values[i]);
        }
        map.put(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date value)
    {
        if(value != null)
        {
            set(name, Long.toString(value.getTime()));
        }
        else
        {
            remove(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date[] values)
    {
        ArrayList<Date> list = new ArrayList<Date>(values.length);
        for (Date val : values)
        {
            if(val != null)
            {
                list.add(val);
            }
        }
        if(list.size() > 0)
        {
            String[] target = new String[list.size()];
            for (int i = 0; i < target.length; i++)
            {
                target[i] = Long.toString(list.get(i).getTime());
            }
            map.put(name, target);
        }
        else
        {
            map.remove(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float value)
    {
        set(name, Float.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Float.toString(values[i]);
        }
        map.put(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, int value)
    {
        set(name, Integer.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, int[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Integer.toString(values[i]);
        }
        map.put(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, long value)
    {
        set(name, Long.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, long[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Long.toString(values[i]);
        }
        map.put(name, target);
    }

    /**
     * {@inheritDoc}
     */
	public void set(Parameters parameters)
	{
		remove();
		add(parameters, true);
	}
	
    /**
     * {@inheritDoc}
     */
    public void add(String name, String value)
    {
        String[] values = map.get(name);
        if (values == null || values.length == 0)
        {
            String[] target = new String[] { value };
            map.put(name, target);
        }
        else
        {
            String[] target = new String[values.length + 1];
            System.arraycopy(values, 0, target, 0, values.length);
            target[values.length] = value;
            map.put(name, target);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, String[] values)
    {
        String[] prevValues = map.get(name);
        if (prevValues == null || prevValues.length == 0)
        {
            set(name, values);
        }
        else
        {
            ArrayList<String> list = new ArrayList<String>(values.length + prevValues.length);
            list.addAll(Arrays.asList(prevValues));
            for (String val : values)
            {
                if(val != null)
                {
                    list.add(val);
                }
            }
            if(list.size() > 0)
            {
                map.put(name, list.toArray(new String[list.size()]));
            }
            else
            {
                map.remove(name);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, boolean value)
    {
        add(name, Boolean.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, boolean[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Boolean.toString(values[i]);
        }
        add(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date value)
    {
        if(value != null)
        {
            add(name, Long.toString(value.getTime()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date[] values)
    {
        ArrayList<Date> list = new ArrayList<Date>(values.length);
        for (Date val : values)
        {
            if(val != null)
            {
                list.add(val);
            }
        }
        if(list.size() > 0)
        {
            String[] target = new String[list.size()];
            for (int i = 0; i < target.length; i++)
            {
                target[i] = Long.toString(list.get(i).getTime());
            }
            add(name, target);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, float value)
    {
        add(name, Float.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, float[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Float.toString(values[i]);
        }
        add(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, int value)
    {
        add(name, Integer.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, int[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Integer.toString(values[i]);
        }
        add(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, long value)
    {
        add(name, Long.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, long[] values)
    {
        String[] target = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            target[i] = Long.toString(values[i]);
        }
        add(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void add(Parameters parameters, boolean overwrite)
    {
        String[] names = parameters.getParameterNames();
        for (int i = 0; i < names.length; i++)
        {
            String[] values = parameters.getStrings(names[i]);
            if (values != null)
            {
                if (overwrite)
                {
                    set(names[i], values);
                }
                else
                {
                    add(names[i], values);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        for(String key : keys)
        {
            sb.append(key);
            sb.append('=');
            String[] values = getStrings(key);
            sb.append(toString(values));
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Parameters getChild(String prefix)
    {
        return new ScopedParameters(this, prefix);
    }

    private void loadParameters(LineNumberReader reader) throws IOException
    {
        if (reader == null || !reader.ready())
        {
            return;
        }
        String line = null;
        do
        {
            // merge broken lines
            StringBuilder sb = new StringBuilder();
            boolean breakAtEnd = false;
            int lastBrokenLine = 0;
            do
            {
                line = reader.readLine();
                if (line != null)
                {
                    line = line.trim();
                    if (line.length() > 0 && !line.startsWith("#"))
                    {
                        if (line.endsWith("\\"))
                        {
                            breakAtEnd = true;
                            lastBrokenLine = reader.getLineNumber();
                            sb.append(line.substring(0, line.length() - 1));
                        }
                        else
                        {
                            breakAtEnd = false;
                            sb.append(line);
                        }
                    }
                }
            }
            while (reader.ready() && line != null && (line.endsWith("\\")
                   || line.length() == 0 || line.startsWith("#")));
            if (breakAtEnd)
            {
                throw new IllegalArgumentException("The " + lastBrokenLine + " line is not ended");
            }
            // process the line			
            String line2 = sb.toString();
            if (line2.length() == 0)
            {
                continue;
            }
            int delim = line2.indexOf('=');
            if (delim == -1)
            {
                throw new IllegalArgumentException("The property '" + line2 + 
                    "' has no '=' delimiter");
            }
            String name = line2.substring(0, delim).trim();
            String value = line2.substring(delim + 1).trim();
            // process the value
            if (value.indexOf(',') == -1)
            {
                add(DatabaseUtils.unescapeSqlString(name), 
                    DatabaseUtils.unescapeSqlString(value));
                continue;
            }
            StringTokenizer st = new StringTokenizer(value, ",");
            ArrayList<String> values = new ArrayList<String>();
            while (st.hasMoreTokens())
            {
                String v = st.nextToken();
                while (v.endsWith("\\") && st.hasMoreTokens())
                {
                    v = v.substring(0,v.length()-1) + "," + st.nextToken();
                }
                values.add(DatabaseUtils.unescapeSqlString(v));
            }
            String[] target = new String[values.size()];
            values.toArray(target);
            add(name, target);
        }
        while (reader.ready() && line != null);
    }
}
