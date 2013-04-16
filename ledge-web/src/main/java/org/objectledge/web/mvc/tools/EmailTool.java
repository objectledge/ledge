//
//Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

/**
 * Converts a given e-mail to a JavaScript code which displays the e-mail in the browser and
 * prevents e-mail harvesting by spammers.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EmailTool.java,v 1.3 2005-02-08 19:11:31 rafal Exp $
 */
public class EmailTool
{
    /**
     * Encode the given email address as html/javascript.
     * 
     * @param eMail the address to encode.
     * @return encoded representation.
     */
    public String encode(String eMail)
    {
        StringBuilder jSEncode = new StringBuilder();
        jSEncode.append("document.write('<a href=\"mailto:").append(eMail).append("\">")
            .append(eMail).append("</a>');");
        String value = jSEncode.toString();

        jSEncode.setLength(0);
        jSEncode.append("<script language=\"javascript\">eval(unescape('");
        jSEncode.append(string2hex(value));
        jSEncode.append("'))</script>");

        return jSEncode.toString();
    }

    public String encodeSafe(String eMail)
    {
        StringBuilder jSEncode = new StringBuilder();
        jSEncode.append("<span class='emailEncode' data-encoded='");
        jSEncode.append(string2hex(eMail));
        jSEncode.append("'></span>");
        return jSEncode.toString();
    }

    /**
     * Return javascript code generating A element with mailto href.
     * 
     * @param eMail the email address for A href.
     * @param eMailText A tag text.
     * @return encoded representation.
     */
    public String encodeLink(String eMail, String eMailText)
    {
        StringBuilder js = new StringBuilder();
        js.append("document.write('<a href=\"mailto:").append(eMail).append("\">")
            .append(eMailText).append("</a>');");
        StringBuilder jsEncode = new StringBuilder();
        jsEncode.append("eval(unescape('");
        jsEncode.append(string2hex(js.toString()));
        jsEncode.append("'))");
        return jsEncode.toString();
    }

    /**
     * Return javascript code generating email address.
     * 
     * @param eMail the email address.
     * @return encoded representation.
     */
    public String encodeAddress(String eMail)
    {
        StringBuilder js = new StringBuilder();
        js.append("document.write('").append(eMail).append("');");
        StringBuilder jsEncode = new StringBuilder();
        jsEncode.append("eval(unescape('");
        jsEncode.append(string2hex(js.toString()));
        jsEncode.append("'));");
        return jsEncode.toString();
    }

    /**
     * Provide hexadecimal ordinal of an UTF character.
     * 
     * @param c a character
     * @return hexadecimal representation of character's ordinal nubmer.
     */
    private String string2hex(String value)
    {
        StringBuilder stringEncode = new StringBuilder();
        for(int i = 0; i < value.length(); i++)
        {
            int ch = value.charAt(i);
            if(ch > 127)
            {
                stringEncode.append("%u").append(String.format("%04x", ch));
            }
            else
            {
                stringEncode.append('%').append(String.format("%02x", ch));
            }
        }
        return stringEncode.toString();
    }
}
