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

package org.objectledge.pico.customization;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ConstructorComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizingConstructorComponentAdapter.java,v 1.5 2004-01-27 16:54:49 fil Exp $
 */
public class CustomizingConstructorComponentAdapter extends ConstructorComponentAdapter
{

    /**
     * Constructs a new instnace of the adapter.
     * 
     * @param componentKey the component's key.
     * @param componentImplementation the component implementation class.
     * @param parameters the component instantiation paramter hints.
     * @throws AssignabilityRegistrationException if the componentKey is a class not assignable to
     *         componentImplementation class.
     * @throws NotConcreteRegistrationException if the componentImplementation type is  an abstract
     *         class or interface.
     */
    public CustomizingConstructorComponentAdapter(
        Object componentKey,
        Class componentImplementation,
        Parameter[] parameters)
        throws AssignabilityRegistrationException, NotConcreteRegistrationException
    {
        super(componentKey, componentImplementation, parameters);
    }

    /**
     * {@inheritDoc}
     */
    protected Object[] getConstructorArguments(
        ComponentAdapter[] adapterDependencies,
        MutablePicoContainer picoContainer)
    {
        Object[] result = new Object[adapterDependencies.length];
        for (int i = 0; i < adapterDependencies.length; i++) {
            ComponentAdapter adapterDependency = adapterDependencies[i];
            if(adapterDependency instanceof CustomizedComponentAdapter)
            {
                result[i] = ((CustomizedComponentAdapter)adapterDependency).
                    getComponentInstance(picoContainer, getComponentKey(), 
                    getComponentImplementation());                                
            }
            else
            {
                result[i] = adapterDependency.getComponentInstance(picoContainer);
            }
        }
        return result;
    }
}
