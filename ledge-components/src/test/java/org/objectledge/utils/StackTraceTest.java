// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import org.objectledge.test.LedgeTestCase;


/**
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: StackTraceTest.java,v 1.3 2004-07-22 16:39:42 zwierzem Exp $
 */
public class StackTraceTest extends LedgeTestCase
{
    private Thingy thingy = new Thingy();
    
    public void testTracingException()
    {
        String[] trace = new StackTrace(new TracingException(4)).toStringArray();
        assertEquals(5, trace.length);
    }

    public void testOrdinaryTrace()
    {
        try
        {
            thingy.d();
        }
        catch(Exception e)
        {
            String[] trace = new StackTrace(e).toStringArray();
            assertEquals(3, trace.length);
        }
    }
    
    public void testNestingTrace()
    {
        try
        {
            thingy.a();
        }
        catch(Exception e)
        {
            String[] trace = new StackTrace(e).toStringArray();
            assertEquals(10, trace.length);
        }        
    }

    public void testLegacyExceptions()
    {
        try
        {
            thingy.l();
        }
        catch(Exception e)
        {
            String[] trace = new StackTrace(e).toStringArray();
            assertEquals(6, trace.length);
        }        
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    
    public static class Thingy
    {
        public void a()
        	throws Exception
        {
            try
            {
                b();
            }
            catch(Exception e)
            {
                throw new Exception("exception in b", e);
            }            
        }
        
        public void b()
        	throws Exception
        {
            try
            {
                c();
            }
            catch(Exception e)
            {
                throw new Exception("exception in c", e);
            }
        }
        
        public void c()
        	throws Exception
        {
            d();
        }
        
        public void d()
        	throws Exception
        {
            throw new Exception("d failed");
        }
        
        /////////////////////////////////////////////////////////////////////////////////////////
        
        public void l()
        	throws Exception
    	{
            try
            {
                m();
            }
            catch(Exception e)
            {
                throw new LegacyException("exception in m", e);
            }
    	}
        
        public void m()
        	throws Exception
        {
            throw new Exception("m failed");
        }
    }
    
    public static class LegacyException
    	extends Exception
    {
        private Throwable cause;
        
        public LegacyException(String message, Throwable cause)
        {
            super(message);
            this.cause = cause;
        }
        
        public Throwable getRootCause()
        {
            return cause;
        }
    }    
}
