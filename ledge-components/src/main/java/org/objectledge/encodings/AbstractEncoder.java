// 
//Copyright (c) 2003, 2004 Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.encodings;

import org.objectledge.ComponentInitializationError;
import org.objectledge.encodings.encoders.CharEncoder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * Base encoder using CharacterEncoders.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractEncoder.java,v 1.1 2004-07-05 12:38:47 zwierzem Exp $
 */
public abstract class AbstractEncoder
{
	private static final String ENCODER_CLASS_PREFIX = 
        "org.objectledge.encodings.encoders.CharEncoder";
	private MutablePicoContainer container;

	/**
	 * Constructs the base encoder component.
	 */
	public AbstractEncoder()
	{
        // non caching container
        this.container =
            new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
		CharEncoder ref1 = getCharsetEncoder("UTF-16");
		CharEncoder ref2 = getCharsetEncoder("UTF-16");
		if(ref1 == null || ref2 == null)
		{
			throw new ComponentInitializationError("cannot get basic UTF-16 encoder");
		}
		if(ref1 == ref2)
		{
			throw new ComponentInitializationError(
				"container configured for component instance caching");
		}
	}

	// implementation ----------------------------------------------------------------------------
	
    protected CharEncoder getCharsetEncoder(String encodingName)
    {
    	if(encodingName == null)
    	{
    		return null;    		
    	}
    	
		try
		{
			encodingName = EncodingMap.getIANA2JavaMapping(encodingName);
			Object encoderInstance = container.getComponentInstance(encodingName); 
			if(encoderInstance == null)
			{
				Class clazz = Class.forName(ENCODER_CLASS_PREFIX + encodingName);
				container.registerComponentImplementation(encodingName, clazz);
				encoderInstance = container.getComponentInstance(encodingName);
			}
			return (CharEncoder) encoderInstance;
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalArgumentException(
				"unknown or unsupported encoding '"+encodingName+"'"); 
		}
    }
}
