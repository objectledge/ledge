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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.objectledge.utils.StringUtils;

/**
 * A simple implementation of parameters container.
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: DefaultParameters.java,v 1.7 2004-02-20 13:51:31 pablo Exp $
 */
public class DefaultParameters implements Parameters
{
    /** string representation for boolean <code>true</code> value. */
    public static final String TRUE = "true";

    /** The main parameters map */
    protected HashMap map = new HashMap();

    /**
     * Create the empty container.
     */
    public DefaultParameters()
    {
    }

    /**
     * Create the container and feed it with configuration given as string. 
     * 
     * @param configuration the string representation of the container. 
     */
    public DefaultParameters(String configuration)
    {
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
        String[] names = source.getParameterNames();
        for (int i = 0; i < names.length; i++)
        {
            String[] values = source.getStrings(names[i]);
            if (values != null)
            {
                String[] target = new String[values.length];
                System.arraycopy(values, 0, target, 0, values.length);
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
        if (values == null || values.length == 0)
        {
            throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
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
    public String get(String name, String defaultValue)
    {
        String[] values = (String[])map.get(name);
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
        String[] values = (String[])map.get(name);
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
        String value = get(name, Boolean.toString(defaultValue));
        return value.equals(TRUE);
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
    public float getFloat(String name) throws NumberFormatException
    {
        return Float.parseFloat(get(name));
    }

    /**
     * {@inheritDoc}
     */
    public float getFloat(String name, float defaultValue)
    {
        return Float.parseFloat(get(name, Float.toString(defaultValue)));
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
        return Integer.parseInt(get(name, Integer.toString(defaultValue)));
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
        return Long.parseLong(get(name, Long.toString(defaultValue)));
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
        String[] values = (String[])map.get(name);
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
        String[] values = (String[])map.get(name);
        if (values == null || values.length == 0)
        {
            return;
        }
        ArrayList list = new ArrayList();
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
    public void remove(Set keys)
    {
        Iterator it = keys.iterator();
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
    public void removeExcept(Set keys)
    {
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String)it.next();
            if (!keys.contains(key))
            {
                map.remove(key);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String value)
    {
        String[] values = new String[1];
        values[0] = value;
        map.put(name, values);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values)
    {
        String[] target = new String[values.length];
        System.arraycopy(values, 0, target, 0, values.length);
        map.put(name, target);
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
    public void add(String name, String value)
    {
        String[] values = (String[])map.get(name);
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
        String[] prevValues = (String[])map.get(name);
        if (prevValues == null || prevValues.length == 0)
        {
            String[] target = new String[values.length];
            System.arraycopy(values, 0, target, 0, values.length);
            map.put(name, target);
        }
        else
        {
            String[] target = new String[prevValues.length + values.length];
            System.arraycopy(prevValues, 0, target, 0, prevValues.length);
            System.arraycopy(values, 0, target, prevValues.length, values.length);
            map.put(name, target);
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
        StringBuffer sb = new StringBuffer();
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            String name = (String)it.next();
            sb.append(name);
            sb.append('=');
            sb.append(toString(name));
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Parameters getChild(String prefix)
    {
        Parameters target = new DefaultParameters();
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            String name = (String)it.next();
            if (name.startsWith(prefix) && name.length() > prefix.length())
            {
                target.set(name.substring(prefix.length()), getStrings(name));
            }
        }
        return target;
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
            StringBuffer sb = new StringBuffer();
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
                add(StringUtils.expandUnicodeEscapes(name), 
                    StringUtils.expandUnicodeEscapes(value));
                continue;
            }
            StringTokenizer st = new StringTokenizer(value, ",");
            ArrayList values = new ArrayList();
            while (st.hasMoreTokens())
            {
                String v = st.nextToken();
                while (v.endsWith("\\") && st.hasMoreTokens())
                {
                    v = v.substring(0,v.length()-1) + "," + st.nextToken();
                }
                values.add(StringUtils.expandUnicodeEscapes(v));
            }
            String[] target = new String[values.size()];
            values.toArray(target);
            add(name, target);
        }
        while (reader.ready() && line != null);
    }

    /** 
     * Get string representation of parameter values
     *  
     * @param name the name of the parameters.
     * @return the string representation of the parameter value(s). 
     */
    protected String toString(String name)
    {
        StringBuffer sb = new StringBuffer();
        String[] values = getStrings(name);
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

}
