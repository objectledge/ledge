// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

package org.objectledge.encodings.encoders;

/**
 * Base class for Encoder classes. Defines masks and encoding method.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CharEncoder.java,v 1.1 2004-02-02 18:59:00 zwierzem Exp $
 */
public abstract class CharEncoder
{
    private final int prefixMask = 0xff00;
    private final int suffixMask = 0x00ff;
    private final int prefixShift = 8;

    /** This encoding's name. */
    protected String encodingName = null;

    /** Returns this encoding name.
     * @return name of the encoding.
     */
    public String getEncoding()
    {
        return encodingName;
    }

    /**
     * Encodes a char in a given encoding.
     * @param c character to be encoded
     * @return array of characters representing encoded character -
     * 		<code>null</code> if character cannot be represented in this encoding.
     */
    public char[] encode(char c)
    {
        return suffixIndex[ prefixIndex[(c & prefixMask) >> prefixShift] + (c & suffixMask) ];
    }

    /** Index of mappings - prefix part */
    protected int[] prefixIndex = null;

    /** Index of mappings - suffix part */
    protected char[][] suffixIndex = null;
}

