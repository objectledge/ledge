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

import org.nanocontainer.reflection.Converter;
import org.nanocontainer.reflection.InvalidConversionException;
import org.nanocontainer.reflection.StringToObjectConverter;

/**
 * Converts strings to a number of predefined Java classes.
 *
 * <p>Created on Jan 8, 2004</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeStringToObjectConverter.java,v 1.3 2004-02-17 15:50:29 fil Exp $
 */
public class LedgeStringToObjectConverter extends StringToObjectConverter
{
    /**
     * 
     */
    public LedgeStringToObjectConverter()
    {
        super();
        register(Integer.TYPE, 
            new Converter() 
            {
                public Object convert(String in)
                {
                    return in == null ? new Integer(0) : Integer.valueOf(in);
                }    
            }
        );
        register(Long.TYPE, 
            new Converter() 
            {
                public Object convert(String in)
                {
                    return in == null ? new Long(0) : Long.valueOf(in);
                }    
            }
        );
        register(Class.class,
            new Converter()
            {
                public Object convert(String in)
                {
                    try
                    {
                        return Class.forName(in);
                    }
                    catch(ClassNotFoundException e)
                    {
                        throw new InvalidConversionException("class "+in+" not found");
                    }
                }
            }
        );
    }
}
