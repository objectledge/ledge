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
import java.util.Arrays;
import java.util.Collection;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

/**
 * A Parameter that is a sequence of nested Parameter objects.
 * 
 * The <code>componentImplementation</code> class is the actual type excpected by the target 
 * component's constructor. The class must either implement {@link java.util.Collection} interface
 * (it has to be non-abstract, have a constructor accepting a single <code>int</code> parameter -
 * size, or a no-argument constructor) or be Java array type.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: SequenceParameter.java,v 1.1 2003-12-08 12:35:27 fil Exp $
 */
public class SequenceParameter implements Parameter
{
    private Parameter[] elements;
    
    private Class componentImplementation = null;

    /**
     * Creates a type-bounded sequence parameter.
     * 
     * @param elements the sequence elements.
     * @param componentImplementation the expected type of the paramter instance.
     *        should be a collection, or an array type.
     */
    public SequenceParameter(Parameter[] elements, Class componentImplementation)
    {
        this.elements = elements;
        this.componentImplementation = componentImplementation;
        if(componentImplementation.getComponentType() == null &&
            !Collection.class.isAssignableFrom(componentImplementation))
        {
            throw new NonCompositeTypeException(componentImplementation.getName()+
                " is not a Collection nor array type");        
        }
    }
    
    /**
     * Creates an unboudned sequence parameter.
     * 
     * @param elements the sequence elements.
     */
    public SequenceParameter(Parameter[] elements)
    {
        this.elements = elements;
    }

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter resolveAdapter(PicoContainer componentRegistry)
        throws PicoIntrospectionException
    {
        ComponentAdapter[] adapters = new ComponentAdapter[elements.length];
        Class elementType = null;
        if(componentImplementation.getComponentType() != null)
        {
            elementType = componentImplementation.getComponentType();
        }
        for (int i = 0; i < elements.length; i++)
        {
            adapters[i] = elements[i].resolveAdapter(componentRegistry);
            if(adapters[i] == null)
            {
                return null; 
            }
            if(elementType != null)
            {
                if(!elementType.isAssignableFrom(adapters[i].getComponentImplementation()))
                {
                    throw new AssignabilityRegistrationException(elementType, 
                        adapters[i].getComponentImplementation());
                }
            }
        }
        return new SequenceComponentAdapter(adapters, componentImplementation);
    }
    
    /**
     * Custom component adapter for instantiating parameter's instance.
     *
     * <p>Created on Dec 8, 2003</p>
     * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
     * @version $Id: SequenceParameter.java,v 1.1 2003-12-08 12:35:27 fil Exp $
     */
    private static class SequenceComponentAdapter
        implements ComponentAdapter
    {
        private ComponentAdapter[] adapters;
        
        private Object componentKey;
        
        private Class compoponenImplementation;
        
        public SequenceComponentAdapter(ComponentAdapter[] adapters, Class componentImplementation)
        {
            this.componentKey = new Object();
            this.compoponenImplementation = componentImplementation;
            this.adapters = adapters;
        }
        
        
        /**
         * {@inheritDoc}
         */
        public Object getComponentInstance(MutablePicoContainer dependencyContainer)
            throws PicoInitializationException, PicoIntrospectionException
        {
            Object[] instances = new Object[adapters.length];
            for (int i = 0; i < adapters.length; i++)
            {
                instances[i] = adapters[i].getComponentInstance(dependencyContainer);
            }
            if(getComponentImplementation().getComponentType() != null)
            {
                Object result = Array.newInstance(getComponentImplementation().getComponentType(), 
                    adapters.length);
                System.arraycopy(instances, 0, result, 0, adapters.length);
                return result;
            }
            if(Collection.class.isAssignableFrom(getComponentImplementation()))
            {
                Collection result;
                try
                {
                    try
                    {
                        Constructor ctor = getComponentImplementation().
                            getConstructor(new Class[] {Integer.TYPE});
                        result = (Collection)ctor.
                            newInstance(new Object[] {new Integer(adapters.length)});
                    }
                    catch(NoSuchMethodException e)
                    {
                        try
                        {
                            Constructor ctor = getComponentImplementation().
                                getConstructor(new Class[0]);
                                result = (Collection)ctor.newInstance(new Object[0]);
                        }
                        catch(NoSuchMethodException ee)
                        {
                            throw new PicoInstantiationException("cannot instantiate Collection "+
                                getComponentImplementation().getName()+
                                " no supported constructors found");
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
                        throw new PicoInstantiationException("cannot instantiate Collection "+
                            getComponentImplementation().getName(), e);
                    }
                }
                result.addAll(Arrays.asList(instances));
                return result;
            }
            throw new NonCompositeTypeException(getComponentImplementation().getName()+
                " is not a Collection nor array type");        
        }

        /**
         * {@inheritDoc}
         */
        public void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException
        {
            for (int i = 0; i < adapters.length; i++)
            {
                ComponentAdapter adapter = adapters[i];
                adapter.verify(picoContainer);
            }
        }

        /**
         * {@inheritDoc}
         */
        public Class getComponentImplementation()
        {
            return compoponenImplementation;
        }

        /**
         * {@inheritDoc}
         */
        public Object getComponentKey()
        {
            return componentKey;
        }
    }
}
