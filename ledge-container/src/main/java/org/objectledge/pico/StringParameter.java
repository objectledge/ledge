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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picoextras.reflection.StringToObjectConverter;

/**
 * A parameter that is intialized with a string, and is converted to the desired type on demand.
 *
 * <p>Created on Jan 8, 2004</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: StringParameter.java,v 1.2 2004-01-16 08:51:19 fil Exp $
 */
public class StringParameter 
    implements Parameter
{
    /** the string representation of the value */
    private String stringValue;
    
    /** the parmeter class */
    private Class parameterType;
    
    /**
     * Creates a new StringParamter instance.
     * 
     * @param stringValue the textual value of the component.
     * @param parameterType the class of the parameter, or <code>null</code> to 
     *        determine dynamically.
     */
    public StringParameter(String stringValue, Class parameterType)
    {
        this.stringValue = stringValue;
        this.parameterType = parameterType;
    }
    
    /**
     * Creates a new StringParamter instance.
     * 
     * @param stringValue the textual value of the component.
     */
    public StringParameter(String stringValue)
    {
        this(stringValue, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public ComponentAdapter resolveAdapter(PicoContainer componentRegistry, Class expectedType)
        throws PicoIntrospectionException
    {
        StringToObjectConverter converter = (StringToObjectConverter)componentRegistry.
            getComponentInstance(StringToObjectConverter.class);
        Object value;
        if(parameterType == null)
        {
           value = converter.convertTo(expectedType, stringValue);
        }
        else
        {
            value = converter.convertTo(parameterType, stringValue);
        }
        return new InstanceComponentAdapter(new Object(), value);
    }
}
