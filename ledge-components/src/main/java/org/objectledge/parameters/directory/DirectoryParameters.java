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

package org.objectledge.parameters.directory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.directory.shared.ldap.util.GeneralizedTime;
import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.UndefinedParameterException;

/**
 * Parameters implementation based on directory context.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DirectoryParameters.java,v 1.6 2005-07-22 17:19:41 pablo Exp $
 */
public class DirectoryParameters
    extends DefaultParameters
{
    /** the context */
    private DirContext ctx;

    /**
     * Create a new container.
     * 
     * @param ctx the underlying directory context.
     */
    public DirectoryParameters(DirContext ctx)
    {
        this.ctx = ctx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String name)
    {
        try
        {
            Attributes attrs = ctx.getAttributes("", new String[] { name });
            if(attrs == null)
            {
                throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
            }
            Attribute attr = attrs.get(name);
            if(attr == null || attr.size() == 0)
            {
                throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
            }
            if(attr.size() > 1)
            {
                throw new AmbiguousParameterException("Parameter '" + name + "'has multiple values");
            }
            return attr.get().toString();
        }
        catch(NamingException e)
        {
            throw new UndefinedParameterException("Parameter '" + name + "'is undefined", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String get(String name, String defaultValue)
    {
        try
        {
            return get(name);
        }
        catch(UndefinedParameterException e)
        {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String name, int defaultValue)
    {
        try
        {
            return Integer.parseInt(get(name));
        }
        catch(UndefinedParameterException e)
        {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String name)
    {
        return Integer.parseInt(get(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String name)
    {
        return Boolean.parseBoolean(get(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String name, boolean defaultValue)
    {
        try
        {
            return Boolean.parseBoolean(get(name));
        }
        catch(UndefinedParameterException e)
        {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean[] getBooleans(String name)
    {
        String[] strings = getStrings(name);
        boolean[] booleans = new boolean[strings.length];
        for(int i = 0; i < strings.length; ++i)
        {
            booleans[i] = Boolean.parseBoolean(strings[i]);
        }
        return booleans;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(String name)
    {
        String dateStr = get(name);
        try
        {
            return new GeneralizedTime(dateStr).getCalendar().getTime();
        }
        catch(ParseException e)
        {
            throw new UndefinedParameterException("Failed to parse timestamp", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(String name, Date defaultValue)
    {
        try
        {
            return getDate(name);
        }
        catch(UndefinedParameterException e)
        {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date[] getDates(String name)
    {
        String[] datesStr = getStrings(name);
        Date[] dates = new Date[datesStr.length];
        for(int i = 0; i < datesStr.length; i++)
        {
            try
            {
                dates[i] = new GeneralizedTime(datesStr[i]).getCalendar().getTime();
            }
            catch(ParseException e)
            {
                throw new UndefinedParameterException("Failed to parse timestamp: " + datesStr[i],
                    e);
            }
        }
        return dates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String name)
        throws NumberFormatException
    {
        return Float.parseFloat(get(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String name, float defaultValue)
    {
        try
        {
            return getFloat(name);
        }
        catch(UndefinedParameterException e)
        {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getInts(String name)
    {
        String[] intStrs = getStrings(name);
        int[] ints = new int[intStrs.length];
        for(int i = 0; i < intStrs.length; i++)
        {
            ints[i] = Integer.parseInt(intStrs[i]);
        }
        return ints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String name)
        throws NumberFormatException
    {
        return Long.parseLong(get(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String name, long defaultValue)
    {
        try
        {
            return getLong(name);
        }
        catch(UndefinedParameterException e)
        {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long[] getLongs(String name)
        throws NumberFormatException
    {
        String[] longStrs = getStrings(name);
        long[] longs = new long[longStrs.length];
        for(int i = 0; i < longStrs.length; i++)
        {
            longs[i] = Long.parseLong(longStrs[i]);
        }
        return longs;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getStrings(String name)
    {
        try
        {
            Attributes attrs = ctx.getAttributes("", new String[] { name });
            if(attrs == null)
            {
                return new String[0];
            }
            Attribute attr = attrs.get(name);
            if(attr == null || attr.size() == 0)
            {
                return new String[0];
            }
            String[] values = new String[attr.size()];
            int i = 0;
            NamingEnumeration<?> e = attr.getAll();
            while(e.hasMore())
            {
                values[i++] = e.next().toString();
            }
            return values;
        }
        catch(NamingException e)
        {
            return new String[0];
        }
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterNames()
    {
        try
        {
            Attributes attrs = ctx.getAttributes("");
            if(attrs == null)
            {
                return new String[0];
            }
            String[] keys = new String[attrs.size()];
            int i = 0;
            NamingEnumeration<String> e = attrs.getIDs();
            while(e.hasMore())
            {
                keys[i++] = e.next().toString();
            }
            return keys;
        }
        catch(NamingException e)
        {
            return new String[0];
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        try
        {
            Attributes attrs = ctx.getAttributes("");
            if(attrs == null)
            {
                return false;
            }
            return (attrs.get(name) == null) ? false : true;
        }
        catch(NamingException e)
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        try
        {
            ctx.modifyAttributes("", DirContext.REMOVE_ATTRIBUTE, ctx.getAttributes(""));
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to remove all parameter", e);
        }
        // /CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name)
    {
        try
        {
            ModificationItem[] items = new ModificationItem[] { new ModificationItem(
                DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(name)) };
            ctx.modifyAttributes("", items);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to remove '" + name + "' parameter", e);
        }
        // /CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, String value)
    {
        try
        {
            Attributes atts = new BasicAttributes();
            atts.put(new BasicAttribute(name, value));
            ctx.modifyAttributes("", DirContext.REMOVE_ATTRIBUTE, atts);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to remove '" + name + "' parameter", e);
        }
        // /CLOVER:ON
    }

    /**
     * Remove all parameters with a name contained in given set.
     * 
     * @param keys the set of keys.
     */
    public void remove(Set<String> keys)
    {
        try
        {
            Attributes attrs = ctx.getAttributes("");
            List<String> filteredKeys = new ArrayList<String>(keys.size());
            NamingEnumeration<String> e = attrs.getIDs();
            while(e.hasMore())
            {
                String name = e.next().toString();
                if(keys.contains(name))
                {
                    filteredKeys.add(name);
                }
            }
            ModificationItem[] items = new ModificationItem[filteredKeys.size()];
            for(int i = 0; i < filteredKeys.size(); i++)
            {
                items[i] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(
                    filteredKeys.get(i)));
            }
            ctx.modifyAttributes("", items);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to remove some parameters", e);
        }
        // /CLOVER:ON
    }

    /**
     * Remove all except those with a keys specified in the set.
     * 
     * @param keys the set of names.
     */
    public void removeExcept(Set<String> keys)
    {
        try
        {
            Attributes attrs = ctx.getAttributes("");
            List<String> filteredKeys = new ArrayList<String>(keys.size());
            NamingEnumeration<String> e = attrs.getIDs();
            while(e.hasMore())
            {
                String name = e.next().toString();
                if(!keys.contains(name))
                {
                    filteredKeys.add(name);
                }
            }
            ModificationItem[] items = new ModificationItem[filteredKeys.size()];
            for(int i = 0; i < filteredKeys.size(); i++)
            {
                items[i] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(
                    filteredKeys.get(i)));
            }
            ctx.modifyAttributes("", items);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to remove some parameters", e);
        }
        // /CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String value)
    {
        try
        {
            Attributes atts = new BasicAttributes();
            atts.put(new BasicAttribute(name, value));
            ctx.modifyAttributes("", DirContext.REPLACE_ATTRIBUTE, atts);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to set '" + name + "' parameter", e);
        }
        // /CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values)
    {
        try
        {
            ModificationItem[] items = new ModificationItem[values.length + 1];
            items[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(name));
            for(int i = 0; i < values.length; i++)
            {
                items[i + 1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(
                    name, values[i]));
            }
            ctx.modifyAttributes("", items);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to add '" + name + " parameter", e);
        }
        // /CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean[] values)
    {
        String[] target = new String[values.length];
        for(int i = 0; i < values.length; i++)
        {
            target[i] = Boolean.toString(values[i]);
        }
        set(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float[] values)
    {
        String[] target = new String[values.length];
        for(int i = 0; i < values.length; i++)
        {
            target[i] = Float.toString(values[i]);
        }
        set(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, int[] values)
    {
        String[] target = new String[values.length];
        for(int i = 0; i < values.length; i++)
        {
            target[i] = Integer.toString(values[i]);
        }
        set(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, long[] values)
    {
        String[] target = new String[values.length];
        for(int i = 0; i < values.length; i++)
        {
            target[i] = Long.toString(values[i]);
        }
        set(name, target);
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, String value)
    {
        try
        {
            ModificationItem[] items = new ModificationItem[1];
            items[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(name,
                value));
            ctx.modifyAttributes("", items);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to add '" + name + "' parameter", e);
        }
        // /CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, String[] values)
    {
        try
        {
            ModificationItem[] items = new ModificationItem[values.length];
            for(int i = 0; i < values.length; i++)
            {
                items[i] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(name,
                    values[i]));
            }
            ctx.modifyAttributes("", items);
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("Failed to add '" + name + " parameter", e);
        }
        // /CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void add(Parameters parameters, boolean overwrite)
    {
        try
        {
            Attributes attrs = ctx.getAttributes("");
            String[] keys = parameters.getParameterNames();
            for(int i = 0; i < keys.length; i++)
            {
                String[] values = parameters.getStrings(keys[i]);
                if(attrs != null && attrs.get(keys[i]) != null && overwrite)
                {
                    remove(keys[i]);
                }
                for(int j = 0; j < values.length; j++)
                {
                    add(keys[i], values[j]);
                }
            }
        }
        // /CLOVER:OFF
        catch(NamingException e)
        {
            throw new RuntimeException("failed to add parameters to the context", e);
        }
        // /CLOVER:ON

    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String[] keys = getParameterNames();
        for(int i = 0; i < keys.length; i++)
        {
            String name = keys[i];
            sb.append(name);
            sb.append('=');
            String[] values = getStrings(keys[i]);
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
        Parameters target = new DefaultParameters();
        String[] keys = getParameterNames();
        for(int i = 0; i < keys.length; i++)
        {
            String name = keys[i];
            if(name.startsWith(prefix) && name.length() > prefix.length())
            {
                target.set(name.substring(prefix.length()), getStrings(name));
            }
        }
        return target;
    }
}
