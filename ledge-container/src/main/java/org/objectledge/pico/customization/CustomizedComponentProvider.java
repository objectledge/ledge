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
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

/**
 * Provides customized component adapters, based on requesting component's key and implementation 
 * class.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizedComponentProvider.java,v 1.5 2003-12-03 15:13:14 mover Exp $
 */
public interface CustomizedComponentProvider
{
    /**
     * Returns a customized component instance.
     * 
     * @param dependenciesContainer container where the customized component dependencies should be
     *        resolved.
     * @param componentKey requesting component's key.
     * @param componentImplementaion requesting component's implmenetation class.
     * @return customized adapter of the component.
     * @throws PicoInitializationException if the customized component cannot be initialized.
     * @throws PicoIntrospectionException if the customized component cannot be initialized.
     * @throws UnsupportedKeyTypeException if the componentKey has unsupported type.
     */
    public ComponentAdapter getCustomizedAdapter(MutablePicoContainer dependenciesContainer, 
        Object componentKey, Class componentImplementaion)
        throws PicoInitializationException, PicoIntrospectionException,
            UnsupportedKeyTypeException;
    
    /**
     * Verifies if the customized component can be instantiated using the dependencies present
     * in the given container.
     * 
     * @param container the container to verify.
     * @throws NoSatisfiableConstructorsException if the container does not contain required
     *         dependencies.
     */
    public void verify(PicoContainer container) 
        throws NoSatisfiableConstructorsException;
}
