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
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * A factory of CustomizingConstructorComponentAdapters.
 * 
 * <p>Use this factory at the bottom of the factory chain for your container to take advantage of
 * component customization. You'll also need to register {@link CustomizedComponentAdapter} for 
 * each type of the component you want to have customized.<p>
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision: 1.5 $ $Date: 2004-12-27 05:27:48 $
 */
public class CustomizingConstructorComponentAdapterFactory 
    implements ComponentAdapterFactory
{
    /**
     * Constructs a new instance of the factory. 
     */
    public CustomizingConstructorComponentAdapterFactory()
    {
    }

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter createComponentAdapter(
        Object componentKey,
        Class componentImplementation,
        Parameter[] parameters)
        throws
            PicoIntrospectionException,
            AssignabilityRegistrationException,
            NotConcreteRegistrationException
    {
        return new CustomizingConstructorComponentAdapter(componentKey, componentImplementation, 
            parameters);
    }
}
