// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.pico;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;

/**
 * Allows hinting the component's constructor arguments of aggregated types.
 * 
 * <p>
 * Both Java arrays and <code>java.util.Collection</code> objects are supported.
 * The elements of the sequence are <code>org.picocontainer.Parameter</code> objects
 * - they may resolve to constants, component references, or nested sequences.
 * </p>
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: SequenceParameter.java,v 1.6 2005-07-07 08:30:04 zwierzem Exp $
 */
@SuppressWarnings("unchecked")
public class SequenceParameter implements Parameter
{
    private Parameter[] elements;
    private Class<?> implClass;
    
    /**
     * Creates a sequence parameter.
     * 
     * @param elements the sequence elements.
     * @param implClass collection implementation class, may be null.
     */
    public SequenceParameter(Parameter[] elements, Class implClass)
    {
        this.elements = elements;
        this.implClass = implClass;
    }

    /**
     * {@inheritDoc}
     */
    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, 
        Class expectedType)
        throws PicoInitializationException
    {
        Class<?> elementType = getElementType(expectedType);
        List<Object> items = new ArrayList<Object>(elements.length);
        for(int i = 0; i < elements.length; i++)
        {
            items.add(elements[i].resolveInstance(container, adapter, elementType));
        }
        if(expectedType.getComponentType() != null)
        {
            return toArray(items, expectedType);
        }
        if(Collection.class.isAssignableFrom(expectedType))
        {
            if(implClass != null)
            {
                return toCollection(items, (Class<Collection<Object>>)implClass);
            }
            else
            {
                return toCollection(items, (Class<Collection<Object>>)expectedType);
            }
        }
        throw new PicoIntrospectionException("not a collection nor array type");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, 
        Class expectedType)
    {
        try
        {
            Class<?> elementType = getElementType(expectedType);
            for(Parameter element : elements)
            {
                if(!element.isResolvable(container, adapter, elementType))
                {
                    return false;
                }
            }
            return true;
        }
        catch(PicoIntrospectionException e)
        {
            // non array or collection type
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType)
        throws PicoIntrospectionException
    {
        // TODO this looks really suspicious, but I'll leave it as is until Pico upgrade.
        Class<?> elementType = getElementType(expectedType);
        for(Parameter element : elements)
        {
            element.verify(container, adapter, expectedType);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void accept(PicoVisitor visitor)
    {
        visitor.visitParameter(this);
        for(Parameter element : elements)
        {
            visitor.visitParameter(element);
        }
    }
    
    private Class<?> getElementType(Class<?> expectedType)
    {
        if(expectedType.getComponentType() != null)
        {
            return expectedType.getComponentType();
        }
        if(Collection.class.isAssignableFrom(expectedType))
        {
            // Cannot determine actual type parameters from a Class object. This is not supported
            // Java5. We could determine the type parameter from the constructor though - we
            // would have to be passed java.lang.reflect.ParametrizedType, which can be obtained
            // from Constructor.getGenericParameterTypes()
            return Object.class;
        }
        throw new PicoIntrospectionException("not a collection nor array type");
    }
    
    private <T> T[] toArray(List<T> source, Class<T> targetType)
    {
        T[] target = (T[])Array.newInstance(targetType.getComponentType(), source.size());
        source.toArray(target);
        return target;
    }
    
    private <T, C extends Collection<T>> C toCollection(List<T> source, Class<C> targetType)
    {
        C target;
        try
        {
            try
            {
                Constructor<C> ctor = targetType.getConstructor(new Class[] { Integer.TYPE });
                target = ctor.newInstance(new Object[] { new Integer(source.size()) });
            }
            catch(NoSuchMethodException e)
            {
                try
                {
                    Constructor<C> ctor = targetType.getConstructor(new Class[0]);
                    target = ctor.newInstance(new Object[0]);
                }
                catch(NoSuchMethodException ee)
                {
                    throw new CollectionInstantiationException("cannot instantiate Collection "
                        + targetType.getName() + " no supported constructors found", ee);
                }
            }
        }
        catch(Exception e)
        {
            if(e instanceof PicoException)
            {
                throw (PicoException)e;
            }
            else
            {
                throw new CollectionInstantiationException("cannot instantiate Collection "
                    + targetType.getName(), e);
            }
        }
        target.addAll(source);
        return target;
    }
}
