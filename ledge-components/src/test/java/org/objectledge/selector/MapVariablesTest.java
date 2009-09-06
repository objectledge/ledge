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
 * @version $Id: MapVariablesTest.java,v 1.1 2004-01-23 13:58:26 fil Exp $
 */
public class MapVariablesTest extends TestCase
{

    private Variables variables;

    /**
     * Constructor for MapVariablesTest.
     * @param arg0
     */
    public MapVariablesTest(String arg0)
    {
        super(arg0);
    }
    
    public void setUp()
    {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("object", "object");
        values.put("null", null);
        variables = new MapVariables(values);
    }
    
    public void testIsDefined()
        throws Exception
    {
        assertTrue(variables.isDefined("object"));
        assertTrue(variables.isDefined("null"));
        assertFalse(variables.isDefined("undefined"));
    }
    
    public void testGet()
        throws Exception
    {
        assertEquals("object", variables.get("object"));
        assertNull(variables.get("null"));
        try
        {
            variables.get("undefined");
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("Undefined variable undefined", e.getMessage());
        }
    }
}
