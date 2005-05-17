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
 * @version $Id: URLEncoder.java,v 1.6 2005-05-17 07:40:12 zwierzem Exp $
 */
public class URLEncoder
{
    private static final boolean[] PASS_THROUGH =  new boolean[256];
    private static final String HEX_CHARS = "0123456789ABCDEF";

    static
    {
        for (int i = 0; i < 256; i++)
        {
            PASS_THROUGH[i] = false;
        }
        for (int i = 'a'; i <= 'z'; i++)
        {
            PASS_THROUGH[i] = true;
        }
        for (int i = 'A'; i <= 'Z'; i++)
        {
            PASS_THROUGH[i] = true;
        }
        int j = 0;
        for (int i = '0'; i <= '9'; i++)
        {
            PASS_THROUGH[i] = true;
        }
        PASS_THROUGH['-'] = true;
        PASS_THROUGH['_'] = true;
        PASS_THROUGH['.'] = true;
        PASS_THROUGH['*'] = true;
        PASS_THROUGH['/'] = true;
    }

    /**
     * Encodes a given text as a query string value with UTF-8 encoding.
     *
     * @param text Text to be encoded
     * @param encodingName name of a chosen encoding.
     * @return encoded text
     * @throws UnsupportedEncodingException if the requested encoding is not supported.
     */
    public String encodeQueryStringValue(String text, String encodingName)
        throws UnsupportedEncodingException
    {
        return encode(text, encodingName, true);
    }

    /**
     * Encodes a given text as a query string value with UTF-8 encoding.
     *
     * @param text Text to be encoded
     * @param encodingName name of a chosen encoding.
     * @return encoded text
     * @throws UnsupportedEncodingException if the requested encoding is not supported.
     */
    public String encodeContentPath(String text, String encodingName)
        throws UnsupportedEncodingException
    {
        return encode(text, encodingName, false);
    }
    
    /**
     * Encodes a given text as a query string value or content path with UTF-8 encoding.
     *
     * @param text Text to be encoded
     * @param encodingName name of a chosen encoding.
     * @param isQSValue <code>true</code> for encoding a query string value
     * @return encoded text
     * @throws UnsupportedEncodingException if the requested encoding is not supported.
     */
    private String encode(String text, String encodingName, boolean isQSValue)
        throws UnsupportedEncodingException
    {
        if(text == null)
        {
            return null;
        }
        if(text.length() == 0)
        {
            return text;
        }

        int length = text.length();
        StringBuilder outputBuf = new StringBuilder(length*2);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream(10); 
        Writer encodingWriter = new OutputStreamWriter(os,encodingName);
        byte[] bytes = null;
        
        // convert string
        for (int i=0; i < length; i++)
        {
            // get a character from input String
            char c = text.charAt(i);
            
            if(c < 256 && PASS_THROUGH[c])
            {
                outputBuf.append(c);
            }
            else if(c == ' ')
            {
                if(isQSValue)
                {
                    outputBuf.append('+');
                }
                else
                {
                    outputBuf.append("%20");
                }
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
                        outputBuf.append(HEX_CHARS.charAt((bytes[j] >> 4) & 0xf));
                        outputBuf.append(HEX_CHARS.charAt(bytes[j] & 0xf));
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
