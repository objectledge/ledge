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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A compound implementation of parameters.
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @author <a href="mailto:rafal@caltha.org">Rafal Krzewski</a>
 * @version $Id: CompoundParameters.java,v 1.3 2005-03-10 09:46:16 zwierzem Exp $
 */
public class CompoundParameters implements Parameters
{
    
    /** The underylying containers. */
    private List containers;

    /**
     * Constructs a copound parameter container.
     *
     * <p>The second container will have precedence over the first one. </p>
     *
     * @param sub a parameter container.
     * @param sup a parameter container.
     */
    public CompoundParameters(Parameters sub, Parameters sup)
    {
        containers = new ArrayList(2);
        containers.add(sub);
        containers.add(sup);
        Collections.reverse(containers);
    }

    /**
     * Constructs a copound parameter container.
     *
     * <p>The contatiners with greater indexes will have precedence over the
     * conainer with lesser indexes.</p>
     *
     * @param array the containers.
     */
    public CompoundParameters(Parameters[] array)
    {
        containers = Arrays.asList(array);
        Collections.reverse(containers);
    }
    
    /**
     * Constructs a copound parameter container.
     *
     * <p>The contatiners with greater indexes will have precenence over the
     * conainer with lesser indexes.</p>
     *
     * @param list the containers.
     */
    public CompoundParameters(List list)
    {
        containers = list;
        Iterator i = list.iterator();
        while(i.hasNext())
        {
            Object obj = i.next();
            if(!(obj instanceof Parameters))
            {
                throw new ClassCastException(obj.getClass().getName());
            }
        }
        Collections.reverse(containers);
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterNames()
    {
        SortedSet keys = new TreeSet();
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            keys.addAll(Arrays.asList(c.getParameterNames()));
        }
        String[] result = new String[keys.size()];
        keys.toArray(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String get(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.get(name);
            }
        }
        throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
    }
    
    /**
     * {@inheritDoc}
     */
    public String get(String name, String defaultValue)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.get(name);
            }
        }
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getStrings(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getStrings(name);
            }
        }
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getBoolean(name);
            }
        }
        throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String name, boolean defaultValue)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getBoolean(name);
            }
        }
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public boolean[] getBooleans(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getBooleans(name);
            }
        }
        return new boolean[0];
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getDate(name);
            }
        }
        throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String name, Date defaultValue)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getDate(name);
            }
        }
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public Date[] getDates(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getDates(name);
            }
        }
        return new Date[0];
    }
    
    /**
     * {@inheritDoc}
     */
    public int getInt(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getInt(name);
            }
        }
        throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
    }
    
    /**
     * {@inheritDoc}
     */
    public int getInt(String name, int defaultValue)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getInt(name);
            }
        }
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getInts(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getInts(name);
            }
        }
        return new int[0];
    }

    /**
     * {@inheritDoc}
     */
    public long getLong(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getLong(name);
            }
        }
        throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
    }
    
    /**
     * {@inheritDoc}
     */
    public long getLong(String name, long defaultValue)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getLong(name);
            }
        }
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public long[] getLongs(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getLongs(name);
            }
        }
        return new long[0];
    }

    /**
     * {@inheritDoc}
     */
    public float getFloat(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getFloat(name);
            }
        }
        throw new UndefinedParameterException("Parameter '" + name + "'is undefined");
    }
    
    /**
     * {@inheritDoc}
     */
    public float getFloat(String name, float defaultValue)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getFloat(name);
            }
        }
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public float[] getFloats(String name)
    {
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            if(c.isDefined(name))
            {
                return c.getFloats(name);
            }
        }
        return new float[0];
    }

    /**
     * {@inheritDoc}
     */
    public Parameters getChild(String prefix)
    {
        List list = new ArrayList();
        Iterator i = containers.iterator();
        while(i.hasNext())
        {
            Parameters c = (Parameters)i.next();
            list.add(c.getChild(prefix));
        }
        return new CompoundParameters(list);
    }
    
    //// ---------------------------------

    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, String value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, Date value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, float value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, int value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, long value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove all parameters with a name contained in given set.
     *
     * @param keys the set of keys.
     */
    public void remove(Set keys)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove all except those with a keys specified in the set.
     *
     * @param keys the set of names.
     */
    public void removeExcept(Set keys)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, int value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, int[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, long value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, long[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, String value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, String[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date[] values)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(String name, boolean value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, boolean[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, float value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, float[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, int value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, int[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, long value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, long[] values)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void add(Parameters parameters, boolean overwrite)
    {
        throw new UnsupportedOperationException();
    }
}
