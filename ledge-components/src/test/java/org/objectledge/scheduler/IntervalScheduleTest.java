// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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

package org.objectledge.scheduler;

import org.objectledge.utils.LedgeTestCase;

/**
 *
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: IntervalScheduleTest.java,v 1.1 2006-04-21 12:08:09 rafal Exp $
 */
public class IntervalScheduleTest
    extends LedgeTestCase
{
    private IntervalSchedule schedule;
    
    public void setUp()
    {
        schedule = new IntervalSchedule();
    }
    
    public void testSeconds() throws InvalidScheduleException
    {
        schedule.setConfig("10 s");
        assertEquals(10 * 1000L, schedule.getInterval());
    }

    public void testMinutes() throws InvalidScheduleException
    {
        schedule.setConfig("10 m");
        assertEquals(10 * 60 * 1000L, schedule.getInterval());
    }

    public void testHours() throws InvalidScheduleException
    {
        schedule.setConfig("10 h");
        assertEquals(10 * 60 * 60 * 1000L, schedule.getInterval());
    }

    public void testDays() throws InvalidScheduleException
    {
        schedule.setConfig("10 d");
        assertEquals(10 * 24 * 60 * 60 * 1000L, schedule.getInterval());
    }
    
    public void testInvalid()
    {
        try
        {
            schedule.setConfig("10");
            fail("exception expected");
        }
        catch(InvalidScheduleException e)
        {
            // OK
        }
    }
}
