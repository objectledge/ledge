// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.database.persistence;

import org.objectledge.pico.customization.CustomizingConstructorComponentAdapter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * An implementation of the {@link PersistentFactory} interface that uses a PicoContainer and
 * {@link CustomizingConstructorComponentAdapter}.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PicoPersistentFactory.java,v 1.2 2005-02-04 02:28:02 rafal Exp $
 */
public class PicoPersistentFactory implements PersistentFactory
{
    private PicoContainer container;
    
    private Class type;
    
    /**
     * Crates an instantiator instance.
     * 
     * @param container the pico container to resolve dependencies from.
     * @param type an implmentation of {@link Persistent} interface.
     * @throws IllegalArgumentException if the clazz does not implement required interface.
     */
    public PicoPersistentFactory(PicoContainer container, Class type)
        throws IllegalArgumentException
    {
        this.container = container;
        this.type = type;
        if(!Persistent.class.isAssignableFrom(type))
        {
            throw new IllegalArgumentException(type.getName()+
                " does not implmement Persistent interface");
        }
    }

    /** 
     * {@inheritDoc}
     */
    public Persistent newInstance() throws Exception
    {
        ComponentAdapter adapter = new CustomizingConstructorComponentAdapter(type, type, null);
        return (Persistent)adapter.getComponentInstance(container); 
    }
}
