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

import org.picocontainer.Parameter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * An implementation of CDI ComponentAdapter aware of dependencies using CustomizedComponentAdapter.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizingConstructorComponentAdapter.java,v 1.9 2005-02-04 02:28:15 rafal Exp $
 */
public class CustomizingConstructorComponentAdapter extends ConstructorInjectionComponentAdapter
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates new CustomizingConstructorComponentAdapter instance.
     * 
     * @param componentKey the component key.
     * @param componentImplementation the component implementation class.
     * @throws AssignabilityRegistrationException if the component key is a type, and given 
     * component implementation is not assignable to it.
     * @throws NotConcreteRegistrationException if the given implementation class is not concrete.
     */
    public CustomizingConstructorComponentAdapter(Object componentKey, 
        Class componentImplementation)
        throws AssignabilityRegistrationException, NotConcreteRegistrationException
    {
        super(componentKey, componentImplementation);
    }
    
    /**
     * Creates new CustomizingConstructorComponentAdapter instance.
     * 
     * @param componentKey the component key.
     * @param componentImplementation the component implementation class.
     * @param parameters component parameters.
     */
    public CustomizingConstructorComponentAdapter(Object componentKey,
        Class componentImplementation, Parameter[] parameters)
    {
        super(componentKey, componentImplementation, parameters);
    }
    
    /**
     * Creates new CustomizingConstructorComponentAdapter instance.
     * 
     * @param componentKey the component key.
     * @param componentImplementation the component implementation class.
     * @param parameters component parameters.
     * @param allowNonPublicClasses true to allow instantiating non-public classes.
     * @throws AssignabilityRegistrationException if the component key is a type, and given 
     * component implementation is not assignable to it.
     * @throws NotConcreteRegistrationException if the given implementation class is not concrete.
     */
    public CustomizingConstructorComponentAdapter(Object componentKey,
        Class componentImplementation, Parameter[] parameters, boolean allowNonPublicClasses)
        throws AssignabilityRegistrationException, NotConcreteRegistrationException
    {
        super(componentKey, componentImplementation, parameters, allowNonPublicClasses);
    }
    
    /**
     * {@inheritDoc}
     */
    protected Parameter[] createDefaultParameters(Class[] parameters)
    {
        Parameter[] componentParameters = new Parameter[parameters.length];
        for(int i = 0; i < parameters.length; i++)
        {
            componentParameters[i] = CustomizingComponentParameter.DEFAULT_PARAMETER;
        }
        return componentParameters;
    }
}
