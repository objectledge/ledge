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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Tool for encoding URLs in a less restrictive manner, ie. allowing / (slash) character in encoded
 * values, what is useful for putting path values in Query String fields.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: URLEncoder.java,v 1.1 2004-07-05 12:39:19 zwierzem Exp $
 */
public class URLEncoder
{
    private static final boolean[] passThrough =  new boolean[256];
    private static final String hexChars = "0123456789ABCDEF";

    static
    {
        for (int i = 0; i < 256; i++)
        {
            passThrough[i] = false;
        }
        for (int i = 'a'; i <= 'z'; i++)
        {
            passThrough[i] = true;
        }
        for (int i = 'A'; i <= 'Z'; i++)
        {
            passThrough[i] = true;
        }
        int j = 0;
        for (int i = '0'; i <= '9'; i++)
        {
            passThrough[i] = true;
        }
        passThrough['-'] = true;
        passThrough['_'] = true;
        passThrough['.'] = true;
        passThrough['*'] = true;
        passThrough['/'] = true;
    }
    
    /**
     * Encodes a given text as an attribute with UTF-8 encoding.
     *
     * @param text Text to be encoded
     * @param encodingName name of a chosen encoding.
     * @return encoded text
     */
    public String encodeQueryStringValue(String text, String encodingName)
        throws UnsupportedEncodingException
    {
        if(text == null || text.length() == 0)
        {
            return null;
        }

        int length = text.length();
        StringBuffer outputBuf = new StringBuffer(length*2);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream(10); 
        Writer encodingWriter = new OutputStreamWriter(os,encodingName);
        byte[] bytes = null;
        
        // convert string
        for (int i=0; i < length; i++)
        {
            // get a character from input String
            char c = text.charAt(i);
            
            if(c < 256 && passThrough[c])
            {
                outputBuf.append(c);
            }
            else if(c == ' ')
            {
                outputBuf.append('+');
            }
            else
            {
                try
                {
                    // encode using encoding and store as hex string
                    encodingWriter.write(c);

                    // check if this is a two character being :) from new unicode
                    if(c >= 0xd800 && c <= 0xdbff && i+1 < length) // - high surrogate area
                    {
                        char c2 = text.charAt(i+1);
                        if(c2 >= 0xd800 && c2 <= 0xdbff) // - low surrogate area
                        {
                            encodingWriter.write(c2);
                            // avoid reading character c2 again
                            i++;
                        }
                    }
    
                    // dump encoded bytes
                    encodingWriter.flush();
                    bytes = os.toByteArray();
                    // convert bytes to hex strings
                    for (int j=0; j < bytes.length; j++)
                    {
                        outputBuf.append('%');
                        outputBuf.append(hexChars.charAt((bytes[j] >> 4) & 0xf));
                        outputBuf.append(hexChars.charAt(bytes[j] & 0xf));
                    }
                }
                catch(IOException e)
                {
                    // reset the OutputStream in finally section
                }
                finally
                {
                    // clean accumulated bytes
                    os.reset();
                }
            }
        }
        return outputBuf.toString();
    }
}
