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

package org.objectledge.utils;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StringUtilsTest extends TestCase
{

    /**
     * Constructor for StringUtilsTest.
     * @param arg0
     */
    public StringUtilsTest(String arg0)
    {
        super(arg0);
    }

    public void testCookieValue()
        throws Exception
    {
        assertEquals(StringUtils.cookieNameSafeString("test"),"test");
        assertEquals(StringUtils.cookieNameSafeString("te.st"),"te.st");
        assertEquals(StringUtils.cookieNameSafeString("te=st"),"te.st");
        assertEquals(StringUtils.cookieNameSafeString("te=st",'='),"te.st");
        assertNull(StringUtils.cookieNameSafeString(null));
        
    }
    
    public void testBackslashEscape()
            throws Exception
    {
        assertEquals(StringUtils.backslashEscape("te.st", "."),"te\\.st");
    }
    
    public void testUnicodeEscapeExpand()
                throws Exception
    {    
        assertEquals(StringUtils.escapeNonASCIICharacters("a\u0123bc\u1234"),"a\\u0123bc\\u1234");
        assertEquals(StringUtils.expandUnicodeEscapes("a\\u1234b"),"a\u1234b");
        
        try
        {
            StringUtils.expandUnicodeEscapes("a\\u12b");
            fail("should throw the exception");
        }
        catch(IllegalArgumentException e)
        {
            //ok!
        }
        try
        {
            StringUtils.expandUnicodeEscapes("a\\u12xfe");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
        try
        {
            StringUtils.expandUnicodeEscapes("a\\ufd4e");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }

        assertEquals("abc",StringUtils.expandUnicodeEscapes("abc"));
    }
    
    public void testGetLocale()
    {
        Locale locale = StringUtils.getLocale("pl_PL");
        assertEquals(locale.getCountry(),"PL");
        assertEquals(locale.getLanguage(),"pl");
        locale = StringUtils.getLocale("en_US_TEST");
        assertEquals(locale.getCountry(),"US");
        assertEquals(locale.getLanguage(),"en");
        assertEquals(locale.getVariant(),"TEST");
        try
        {
            locale = StringUtils.getLocale("pl-PL");
            fail("should throw the exception");
        }
        catch(IllegalArgumentException e)
        {
            //ok!
        }
    }
    
    public void testGetByteCount()
        throws Exception
    {
        assertEquals(StringUtils.getByteCount("abc","ISO-8859-1"),3);
        assertEquals(StringUtils.getByteCount("abc","UTF-16"),6);
        assertEquals(StringUtils.getByteCount("abc","UTF-8"),3);
        try
        {
            assertEquals(StringUtils.getByteCount("abc³","TEST"),6);
            fail("should throw the exception");
        }
        catch(UnsupportedEncodingException e)
        {
            //ok!
        }
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 4000; i++)
        {
            sb.append("www.objectledge.org ");
        }
        assertEquals(StringUtils.getByteCount(sb.toString(),"UTF-8"),80000);
    }
    
    public void testSubstitute()
        throws Exception
    {
        String[] values = new String[]{"foo","bar"};
        assertEquals(StringUtils.substitute("abc$1",values),"abcfoo");
        assertEquals(StringUtils.substitute("abc$2",values),"abcbar");
        assertEquals(StringUtils.substitute("ab$1c$*",values),"abfoocbar");
        assertEquals(StringUtils.substitute("ab$$c",values),"ab$c");
        assertEquals(StringUtils.substitute("a$1b$2c$*",values),"afoobbarc");
        assertEquals(StringUtils.substitute("a$1b$2c$3",values),"afoobbarc");
        assertEquals(StringUtils.substitute("a$1b$1c$1",values),"afoobfoocfoo");
        assertEquals(StringUtils.substitute("a$+",values),"a$+");
    }
    
    public void testIndent()
        throws Exception
    {
        StringBuffer sb = new StringBuffer();
        StringUtils.indent(sb, 4);
        assertEquals("    ", sb.toString());        
    }
    
}
