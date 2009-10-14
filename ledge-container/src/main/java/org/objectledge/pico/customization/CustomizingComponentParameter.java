// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.pico.customization;

import java.lang.reflect.Field;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.defaults.ComponentParameter;

/**
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CustomizingComponentParameter.java,v 1.1 2005-02-04 02:28:25 rafal Exp $
 */
public class CustomizingComponentParameter
    extends ComponentParameter 
{
    /** A CustomizedComponentParameter with null componentKey */
    public static final Parameter DEFAULT = new CustomizingComponentParameter();
    
    // TODO copied from pico 
    private Object componentKey;
    
    /**
     * Creates new CustomizedComponentParameter instance.
     */
    public CustomizingComponentParameter()
    {
        super();
    }

    /**
     * Creates new CustomizedComponentParameter instance.
     * 
     * @param componentKey the component key.
     */
    public CustomizingComponentParameter(Object componentKey)
    {
        super(componentKey);
        this.componentKey = componentKey;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter,
        Class expectedType)
        throws PicoInstantiationException
    {
        ComponentAdapter resolvedAdapter = resolveAdapter(container, adapter, expectedType);
        if(resolvedAdapter instanceof CustomizedComponentAdapter)
        {
            return ((CustomizedComponentAdapter)resolvedAdapter).getComponentInstance(container,
                adapter.getComponentKey(), adapter.getComponentImplementation());
        }
        return super.resolveInstance(container, adapter, expectedType);
    }

    // private ///////////////////////////////////////////////////////////////////////////////////
    
    // TODO copied from pico
    private ComponentAdapter resolveAdapter(PicoContainer container, ComponentAdapter adapter,
        Class expectedType)
    {

        final ComponentAdapter result = getTargetAdapter(container, expectedType);
        if(result == null)
        {
            return null;
        }

        // can't depend on ourselves
        if(adapter != null && adapter.getComponentKey().equals(result.getComponentKey()))
        {
            return null;
        }

        if(!expectedType.isAssignableFrom(result.getComponentImplementation()))
        {
            // check for primitive value
            if(expectedType.isPrimitive())
            {
                try
                {
                    final Field field = result.getComponentImplementation().getField("TYPE");
                    final Class type = (Class)field.get(result.getComponentInstance(null));
                    if(expectedType.isAssignableFrom(type))
                    {
                        return result;
                    }
                }
                catch(NoSuchFieldException e)
                {
                    // ignored
                }
                catch(IllegalArgumentException e)
                {
                    // ignored
                }
                catch(IllegalAccessException e)
                {
                    // ignored
                }
                catch(ClassCastException e)
                {
                    // ignored
                }
            }
            return null;
        }
        return result;
    }

    // TODO copied from pico
    private ComponentAdapter getTargetAdapter(PicoContainer container, Class expectedType)
    {
        if(componentKey != null)
        {
            // key tells us where to look so we follow
            return container.getComponentAdapter(componentKey);
        }
        else
        {
            return container.getComponentAdapterOfType(expectedType);
        }
    }
}
