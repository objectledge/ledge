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

package org.objectledge.encodings;

/**
 * Mapping entry for encoder mappings defnitions.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MappingEntry.java,v 1.2 2004-03-12 15:49:16 zwierzem Exp $
 */
public class MappingEntry
{
	/** Unicode character value for this mapping entry. */
	private short unicodeCode;
	/** Name of this mapping entry. */
	private String value;

	/**
	 * Constructs a mapping entry.
	 * @param unicodeCode code of unicode character
	 * @param name character encoded as a string
	 */
    public MappingEntry(short unicodeCode, String name)
    {
        this.unicodeCode = unicodeCode;
        this.value = name.intern();
    }

	/**
	 * Constructs a mapping entry.
	 * @param unicodeCode code of unicode character
	 * @param name character encoded as a string
	 */
    public MappingEntry(int unicodeCode, String name)
    {
        this((short)unicodeCode, name);
    }

	/**
	 * Constructs a mapping entry.
	 * @param unicodeCode code of unicode character
	 * @param code character encoded as a single char
	 */
    public MappingEntry(short unicodeCode, char code)
    {
        this(unicodeCode, new String(new char[] { code }));
    }

	/**
	 * Constructs a mapping entry.
	 * @param unicodeCode code of unicode character
	 * @param code character encoded as a single byte
	 */
    public MappingEntry(int unicodeCode, byte code)
    {
        this((short)unicodeCode, (char)code);
    }

	/**
	 * Constructs a mapping entry.
	 * @param unicodeCode code of unicode character
	 * @param code character encoded as an int
	 */
    public MappingEntry(int unicodeCode, int code)
    {
        this((short)unicodeCode, (char)code);
    }

    /**
     * @return Unicode character value for this mapping entry.
     */
    public short getUnicodeCode()
    {
        return unicodeCode;
    }

    /**
     * @return Encoded character value for this mapping entry.
     */
    public String getValue()
    {
        return value;
    }
}
