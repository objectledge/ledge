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
package org.objectledge.configuration;

import org.jcontainer.dna.Configuration;
import org.objectledge.pico.customization.CustomizedComponentProvider;
import org.objectledge.pico.customization.UnsupportedKeyTypeException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;

/**
 * Provides Configuration objects to the components being initialized using ConfigurationFactory.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CustomizedConfigurationProvider.java,v 1.4 2005-02-04 02:28:03 rafal Exp $
 */
public class CustomizedConfigurationProvider
	implements CustomizedComponentProvider
{
    private ConfigurationFactory configurationFactory;
    
    /**
     * Creates new CustomizedConfigurationProvider instance.
     * 
     * @param configurationFactory the configuration factory.
     */
    public CustomizedConfigurationProvider(ConfigurationFactory configurationFactory)
    {
        this.configurationFactory = configurationFactory;
    }

    /**
     * {@inheritDoc}
     */
    public Object getCustomizedComponentInstance(PicoContainer container, Object componentKey, 
        Class componentImplementation)
        throws PicoInitializationException, PicoIntrospectionException
    {
        String componentName = getComponentName(componentKey);
        return configurationFactory.getConfig(componentName, componentImplementation);
    }
    
    /**
     * {@inheritDoc}
     */
    public Class getCustomizedComponentImplementation()
    {
        return Configuration.class;
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer container) throws PicoVerificationException
    {
        // no dependencies
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns human readable name of the component.
     * 
     * @param componentKey the component key.
     * @return human readable name of the component.
     * @throws UnsupportedKeyTypeException if the component key is of unsupported type.
     */
    protected String getComponentName(Object componentKey)
        throws UnsupportedKeyTypeException
    {
        if(componentKey instanceof Class)
        {
            return ((Class)componentKey).getName();
        }
        else if(componentKey instanceof String)
        {
            return componentKey.toString();
        }
        else
        {
            throw new UnsupportedKeyTypeException("unsupported component key type "+
                componentKey.getClass().getName());
        }
    }        
}
