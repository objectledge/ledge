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
package org.objectledge.web.mvc.finders;

import junit.framework.TestCase;

/**
 * Generates a view lookup sequence based on a prefix, and fallback sequence.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ViewLookupSequenceTest.java,v 1.1 2004-01-16 15:01:17 fil Exp $
 */
public class ViewLookupSequenceTest extends TestCase
{
    /**
     * Constructor for ViewLookupSequenceTest.
     * @param arg0
     */
    public ViewLookupSequenceTest(String arg0)
    {
        super(arg0);
    }
    
    public void testLookup()
    {
        ViewFallbackSequence fallbackSequence = 
            new ViewFallbackSequence("a.b.c", ".", "/", "Default");
        String[] prefices = { "one", "two" };
        ViewLookupSequence sequence = new ViewLookupSequence(prefices, fallbackSequence, "/");
        assertEquals("one/a/b/c/Default", sequence.next());
        assertEquals("one/a/b/Default", sequence.next());
        assertEquals("one/a/Default", sequence.next());
        assertEquals("one/Default", sequence.next());
        assertEquals("two/a/b/c/Default", sequence.next());
        assertEquals("two/a/b/Default", sequence.next());
        assertEquals("two/a/Default", sequence.next());
        assertEquals("two/Default", sequence.next());
        assertEquals(false, sequence.hasNext());
        sequence.reset();
        assertEquals(true, sequence.hasNext());
    }
}
