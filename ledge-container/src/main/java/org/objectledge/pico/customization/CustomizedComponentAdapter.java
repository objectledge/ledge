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
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

/**
 * An adapter for a component that is customized on behalf of a component that is asking for it.
 * <p>
 * When another component is being initialized and it depends on the component that is managed by
 * this adapter (identified by the componentKey), the customizedContainerProvider is asked to
 * produce an instance of the customized component. Thus, different components may recieve different
 * instnaces of the managed component, depending on the customizedComponentProvider's semantics.
 * </p>
 * 
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl </a>
 * @version $Id: CustomizedComponentAdapter.java,v 1.15 2005-02-04 02:28:00 rafal Exp $
 */
public class CustomizedComponentAdapter
    implements ComponentAdapter
{
    private Object componentKey;

    private CustomizedComponentProvider customizedComponentProvider;

    /**
     * Crates a new instance of the adapter.
     * 
     * @param componentKey the component key this adapter manages.
     * @param customizedComponentProvider a provider of customized components.
     */
    public CustomizedComponentAdapter(Object componentKey,
        CustomizedComponentProvider customizedComponentProvider)
    {
        this.componentKey = componentKey;
        this.customizedComponentProvider = customizedComponentProvider;
    }

    /**
     * {@inheritDoc}
     */
    public Object getComponentInstance(PicoContainer container)
        throws PicoInitializationException, PicoIntrospectionException
    {
        // lacking a better idea...
        return customizedComponentProvider.getCustomizedComponentInstance(container, null, null);
    }

    /**
     * Returns a customized components instance.
     * <p>
     * This method is called by the {@link CustomizingConstructorComponentAdapter}.
     * </p>
     * 
     * @param container the container for resolving references.
     * @param componentKey the key of the requesting component.
     * @param componentImplementation the implemenation class of the requesting component.
     * @return a customized component instance.
     * @throws PicoInitializationException if the customized component cannot be instantiated.
     * @throws PicoIntrospectionException if there is a problem introspecting classes.
     * @throws UnsupportedKeyTypeException if the requesting component key if an unsupported type.
     */
    public Object getComponentInstance(PicoContainer container, Object componentKey,
        Class componentImplementation)
        throws PicoInitializationException, PicoIntrospectionException, UnsupportedKeyTypeException
    {
        return customizedComponentProvider.getCustomizedComponentInstance(container, componentKey,
            componentImplementation);
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer container)
        throws PicoVerificationException
    {
        customizedComponentProvider.verify(container);
    }

    /**
     * {@inheritDoc}
     */
    public Class getComponentImplementation()
    {
        return customizedComponentProvider.getCustomizedComponentImplementation();
    }

    /**
     * {@inheritDoc}
     */
    public Object getComponentKey()
    {
        return componentKey;
    }

    /**
     * {@inheritDoc}
     */
    public void accept(PicoVisitor visitor)
    {
        visitor.visitComponentAdapter(this);
    }
}
