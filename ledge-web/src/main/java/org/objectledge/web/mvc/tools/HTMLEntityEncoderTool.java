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

package org.objectledge.web.mvc.tools;

import org.objectledge.encodings.HTMLEntityEncoder;

/**
 * Tool for encoding HTML text to a text which supports a chosen encoding using HTML entities.
 * If an encoding does not support a character code, a HTML or numeric entity is being generated
 * for this character, if a character is supported it is not changed.
 *
 * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: HTMLEntityEncoderTool.java,v 1.1 2004-02-03 13:51:42 zwierzem Exp $
 */
public class HTMLEntityEncoderTool
{
	private HTMLEntityEncoder encoder;
	private String encodingName;
	
	/**
	 * Constructor for HTML entity encoder tool.
	 * @param encoder html entity encoder.
	 * @param encodingName preset character encoding for this tool. 
	 */
	public HTMLEntityEncoderTool( HTMLEntityEncoder encoder, String encodingName )
	{
		this.encoder = encoder;
		this.encodingName = encodingName;
	}
	
	/**
	 * Encodes string as html attribute using entities, allows setting attribute quote
	 * character.
	 * @param text text to be encoded
	 * @param doubleQuoteQuote if set to <code>true</code> attribute is encoded as if double quote
	 * 	was used to quote the attribute, else attribute is encoded as if single quote was used to
	 *  quote it.
	 * @return encoded text
	 */
    public String encodeAttribute( String text, boolean doubleQuoteQuote )
    {
        return encoder.encodeAttribute(text, encodingName, doubleQuoteQuote);
    }

	/**
	 * Encodes string as html attribute using entities, assumes attribute is quoted using
	 * double quotes - <code>&quot;</code>.
	 * @param text text to be encoded
	 * @return encoded text
	 */
    public String encodeAttribute( String text )
    {
        return encoder.encodeAttribute(text, encodingName);
    }

    /**
     * Encodes string as html using entities.
     * @param htmlText text to be encoded
     * @return encoded text
     */
    public String encodeHTML( String htmlText )
    {
        return encoder.encodeHTML(htmlText, encodingName);
    }
}
