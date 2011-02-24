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
package org.objectledge.selector;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: IntrospectionVariablesTest.java,v 1.4 2005-02-10 17:47:00 rafal Exp $
 */
public class IntrospectionVariablesTest extends TestCase
{
    /**
     * Constructor for IntrospectionVariablesTest.
     * @param arg0
     */
    public IntrospectionVariablesTest(String arg0)
    {
        super(arg0);
    }
    
    public void testIsDefined()
        throws Exception
    {
        Variables variables;
        
        variables = new IntrospectionVariables(new PlainObject());
        assertTrue(variables.isDefined("publicMethod"));
        assertTrue(variables.isDefined("publicBooleanMethod"));
        assertTrue(variables.isDefined("publicBooleanPrimitiveMethod"));
        assertFalse(variables.isDefined("undefined"));
        assertFalse(variables.isDefined("voidPublicMethod"));
        assertFalse(variables.isDefined("nonBooleanIsMethod"));
        
        variables = new IntrospectionVariables(new StringGetObject());
        assertTrue(variables.isDefined("defined"));
        // TODO caveat!
        // assertFalse(variables.isDefined("undefined"));
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("defined", "defined");
        map.put("null", null);
        variables = new IntrospectionVariables(map);
        assertTrue(variables.isDefined("defined"));
        // TODO caveat!
        // assertFalse(variables.isDefined("null"));
        // assertFalse(variables.isDefined("undefined"));
    }
    
    public void testNesting()
        throws Exception
    {
        Variables variables;
        variables = new IntrospectionVariables(new PlainObject());
        assertTrue(variables.isDefined("nested.publicMethod"));
        assertTrue(variables.isDefined("nested.publicBooleanMethod"));
        assertTrue(variables.isDefined("nested.publicBooleanPrimitiveMethod"));
        assertFalse(variables.isDefined("nested.undefined"));
        assertFalse(variables.isDefined("nested.voidPublicMethod"));
        assertFalse(variables.isDefined("nested.nonBooleanIsMethod"));
    }
    
    public void testGet()
        throws Exception
    {
        Variables variables;
        variables = new IntrospectionVariables(new PlainObject());
        assertEquals("publicMethod", variables.get("nested.publicMethod"));
        assertEquals(Boolean.TRUE, variables.get("publicBooleanMethod"));
        assertEquals(Boolean.TRUE, variables.get("publicBooleanPrimitiveMethod"));
        
        assertEquals("publicMethod", variables.get("nested.publicMethod"));
        assertEquals(Boolean.TRUE, variables.get("nested.publicBooleanMethod"));
        assertEquals(Boolean.TRUE, variables.get("nested.publicBooleanPrimitiveMethod"));
        
        try
        {
            variables.get("undefined");
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("Undefined variable undefined", e.getMessage());
        }

        try
        {
            variables.get("nested.undefined");
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("Undefined variable nested.undefined", e.getMessage());
        }
    }
    
    public void testExceptions()
    {
        Variables variables;
        variables = new IntrospectionVariables(new PlainObject());
        try
        {
            variables.get("failing");
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals(EvaluationException.class, e.getClass());
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
        variables = new IntrospectionVariables(new StringGetObject());
        try
        {
            variables.get("failing");
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals(EvaluationException.class, e.getClass());
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }

        variables = new IntrospectionVariables(new FailingObjectGetObject());
        try
        {
            variables.get("failing");
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals(EvaluationException.class, e.getClass());
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
    }
    
    @SuppressWarnings("unused")
    private static class PlainObject
    {
        public String getPublicMethod()
        {
            return "publicMethod";
        }
        
        public void getVoidPublicMethod()
        {
            // does nothing
        }
        
        public String isNonBooleanIsMethod()
        {
            return null;
        }
        
        public boolean isPublicBooleanPrimitiveMethod()
        {
            return true;
        }
        
        public Boolean isPublicBooleanMethod()
        {
            return Boolean.TRUE;
        }
        
        public PlainObject getNested()
        {
            return this;
        }
        
        public Object getFailing()
        {
            throw new IllegalStateException("failed");
        }
    }
    
    @SuppressWarnings("unused")
    private static class StringGetObject
    {
        public String get(String name)
        {
            if(name.equals("defined"))
            {
                return "defined";
            }
            else if(name.equals("failing"))
            {
                throw new IllegalStateException("failed");
            }
            else
            {
                return null;
            }
        }
    }
    
    @SuppressWarnings("unused")
    private static class FailingObjectGetObject
    {
        public Object get(Object param)
        {
            throw new IllegalStateException("failed");
        }
    }
}
