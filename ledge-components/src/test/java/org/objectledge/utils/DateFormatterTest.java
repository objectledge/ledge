// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DateFormatterTest.java,v 1.1 2004-02-09 12:09:53 fil Exp $
 */
public class DateFormatterTest extends TestCase
{
    /**
     * Constructor for DateFormatterTest.
     * @param arg0
     */
    public DateFormatterTest(String arg0)
    {
        super(arg0);
    }
    
    public void testFormat()
        throws Exception
    {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("CET"), new Locale("pl","PL"));
        cal.set(2004, 1, 4, 14, 48, 02);
    
        DateFormatter formatter = new DateFormatter("EEE, d MMM yyyy HH:mm:ss Z", "en_US");
        assertEquals("Wed, 4 Feb 2004 14:48:02 +0100", formatter.format(cal.getTime()));
    }

    public void testParse()
        throws Exception
    {
        DateFormatter formatter = new DateFormatter("EEE, d MMM yyyy HH:mm:ss Z", "en_US");
        Date parsed = formatter.parse("Wed, 4 Feb 2004 14:48:02 +0100");
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("CET"), new Locale("pl","PL"));
        cal.set(2004, 1, 4, 14, 48, 02);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime().getTime(), parsed.getTime());
    }
}
