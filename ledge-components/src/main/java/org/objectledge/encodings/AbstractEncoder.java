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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.encodings.encoders.CharEncoder;

/**
 * Base encoder using CharacterEncoders.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractEncoder.java,v 1.2 2004-12-22 08:35:13 rafal Exp $
 */
public abstract class AbstractEncoder
{
    private static final String ENCODER_CLASS_PREFIX = "org.objectledge.encodings.encoders.CharEncoder";

    private Map<String, CharEncoder> encoders = Collections
        .synchronizedMap(new HashMap<String, CharEncoder>());

    // implementation ----------------------------------------------------------------------------

    /**
     * Returns an encoder instance for the specific encoding.
     * 
     * @param encodingName the name of character encoding.
     * @return the encoder object.
     */
    protected CharEncoder getCharsetEncoder(String encodingName)
    {
        if(encodingName == null)
        {
            return null;
        }

        String javaEncodingName = EncodingMap.getIANA2JavaMapping(encodingName);
        try
        {
            CharEncoder encoderInstance = encoders.get(javaEncodingName);
            if(encoderInstance == null)
            {
                @SuppressWarnings("unchecked")
                Class<? extends CharEncoder> clazz = (Class<? extends CharEncoder>)Class
                    .forName(ENCODER_CLASS_PREFIX + javaEncodingName);
                encoderInstance = clazz.newInstance();
                encoders.put(javaEncodingName, encoderInstance);
            }
            return encoderInstance;
        }
        catch(ClassNotFoundException e)
        {
            throw new IllegalArgumentException("unknown or unsupported encoding '"
                + javaEncodingName + "'", e);
        }
        catch(InstantiationException e)
        {
            throw new IllegalArgumentException("instantiation failed", e);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalArgumentException("intstantiation failed", e);
        }
    }
}
