//
//Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of parameters decorator class to scope parameters key names.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ScopedParameters.java,v 1.7 2005-12-16 11:42:16 pablo Exp $
 */
public class ScopedParameters implements Parameters
{
    /** the base parameters */ 
    private Parameters parameters;
    
    /** prefix */
    private String prefix;
    
    /**
     * Create the container scoped decorator. 
     * 
     * @param parameters the container to decorate.
     * @param prefix the scope prefix.
     */
    public ScopedParameters(Parameters parameters, String prefix)
    {
        this.parameters = parameters;
        this.prefix = prefix != null ? prefix : "";
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
    public void add(String name, boolean value)
    {
        parameters.add(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, boolean[] values)
    {
        parameters.add(getPrefix()+name, values);
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date value)
    {
        parameters.add(getPrefix()+name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date[] values)
    {
        parameters.add(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, float value)
    {
        parameters.add(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, float[] values)
    {
        parameters.add(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, int value)
    {
        parameters.add(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, int[] values)
    {
        parameters.add(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, long value)
    {
        parameters.add(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, long[] values)
    {
        parameters.add(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, String value)
    {
        parameters.add(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, String[] values)
    {
        parameters.add(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public String get(String name, String defaultValue)
    {
        return parameters.get(getPrefix()+name, defaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public String get(String name)
    {
        return parameters.get(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String name, boolean defaultValue)
    {
        return parameters.getBoolean(getPrefix()+name, defaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String name)
    {
        return parameters.getBoolean(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean[] getBooleans(String name)
    {
        return parameters.getBooleans(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public Parameters getChild(String prefix)
    {
        return new ScopedParameters(this, prefix);
    }
    
    /**
     * {@inheritDoc}
     */
    public Date getDate(String name)
    {
        return parameters.getDate(getPrefix()+name);
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String name, Date defaultValue)
    {
        return parameters.getDate(getPrefix()+name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public Date[] getDates(String name)
    {
        return parameters.getDates(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public float getFloat(String name, float defaultValue)
    {
        return parameters.getFloat(getPrefix()+name, defaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public float getFloat(String name) throws NumberFormatException
    {
        return parameters.getFloat(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public float[] getFloats(String name) throws NumberFormatException
    {
        return parameters.getFloats(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getInt(String name, int defaultValue)
    {
        return parameters.getInt(getPrefix()+name, defaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getInt(String name) throws NumberFormatException
    {
        return parameters.getInt(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public int[] getInts(String name) throws NumberFormatException
    {
        return parameters.getInts(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public long getLong(String name, long defaultValue)
    {
        return parameters.getLong(getPrefix()+name, defaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public long getLong(String name) throws NumberFormatException
    {
        return parameters.getLong(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public long[] getLongs(String name) throws NumberFormatException
    {
        return parameters.getLongs(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getParameterNames()
    {
        if(getPrefix().length() == 0)
        {
            return parameters.getParameterNames();
        }
        String[] keys = parameters.getParameterNames();
        ArrayList<String> list = new ArrayList<String>();
        for(int i=0; i<keys.length; i++)
        {
            if(keys[i].startsWith(getPrefix()))
            {
                list.add(keys[i].substring(getPrefix().length()));
            }
        }
        String[] result = new String[list.size()];
        return list.toArray(result);
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getStrings(String name)
    {
        return parameters.getStrings(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        return parameters.isDefined(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        if(getPrefix().length() == 0)
        {
            parameters.remove();
            return;
        }
        String[] keys = parameters.getParameterNames();
        for(int i=0; i<keys.length; i++)
        {
            if(keys[i].startsWith(getPrefix()))
            {
                parameters.remove(keys[i]);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(Set<String> keys)
    {
        if(getPrefix().length() == 0)
        {
            parameters.remove(keys);
            return;
        }
        HashSet<String> temp = new HashSet<String>(keys.size());
        for(String key: keys)
        {
            temp.add(getPrefix()+key);
        }
        parameters.remove(temp);
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(String name, Date value)
    {
        parameters.remove(getPrefix()+name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, float value)
    {
        parameters.remove(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(String name, int value)
    {
        parameters.remove(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(String name, long value)
    {
        parameters.remove(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(String name, String value)
    {
        parameters.remove(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(String name)
    {
        parameters.remove(getPrefix()+name);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeExcept(Set<String> keys)
    {
        if(getPrefix().length() == 0)
        {
            parameters.removeExcept(keys);
            return;
        }
        String[] names = getParameterNames();
        HashSet<String> temp = new HashSet<String>(keys.size());
        for(String key:names)
        {
            if (!keys.contains(key))
            {
                temp.add(getPrefix()+key);
            }
        }
        parameters.remove(temp);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean value)
    {
        parameters.set(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean[] values)
    {
        parameters.set(getPrefix()+name, values);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date value)
    {
        parameters.set(getPrefix()+name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date[] values)
    {
        parameters.set(getPrefix()+name, values);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float value)
    {
        parameters.set(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, float[] values)
    {
        parameters.set(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, int value)
    {
        parameters.set(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, int[] values)
    {
        parameters.set(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, long value)
    {
        parameters.set(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, long[] values)
    {
        parameters.set(getPrefix()+name, values);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, String value)
    {
        parameters.set(getPrefix()+name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values)
    {
        parameters.set(getPrefix()+name, values);
    }
	
    /**
     * {@inheritDoc}
     */
	public void set(Parameters parameters)
	{
	    if(getPrefix().length() == 0)
	    {
	        this.parameters.set(parameters);
	    }
	    else
	    {
			remove();
            String[] keys = parameters.getParameterNames();
            for(int i=0; i<keys.length; i++)
            {
                String[] values = parameters.getStrings(keys[i]);
				for(int j=0; j<values.length; j++)
                {
                    this.parameters.add(getPrefix()+keys[i], values[j]);
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
        String[] keys = getParameterNames();
        for (String name: keys)
        {
            sb.append(name);
            sb.append('=');
            String[] values = getStrings(name);
            sb.append(DefaultParameters.toString(values));
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Get the prefix.
     * 
     * @return prefix.
     */
    protected String getPrefix() 
    {
        return prefix;
    }
}
