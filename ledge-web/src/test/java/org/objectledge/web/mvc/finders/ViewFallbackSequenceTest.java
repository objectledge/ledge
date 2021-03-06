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
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ViewFallbackSequenceTest.java,v 1.8 2005-02-16 18:39:34 rafal Exp $
 */
public class ViewFallbackSequenceTest extends TestCase
{
    /**
     * Constructor for ViewFallbackSequenceTest.
     * @param arg0
     */
    public ViewFallbackSequenceTest(String arg0)
    {
        super(arg0);
    }

    public void testPackage()
    {
        Sequence sequence = new ViewFallbackSequence("a.b.c", '.', '/', "Default", false);
        assertEquals("a/b/c/Default", sequence.next());
        assertEquals("a.b.c.Default", sequence.currentView());
        assertEquals("a/b/Default", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        try
        {
            sequence.next();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
        sequence.reset();
        assertEquals(true, sequence.hasNext());
    }

    public void testClass()
    {
        Sequence sequence = new ViewFallbackSequence("a.b.C", '.', '/', "Default", false);
        assertEquals("a/b/C", sequence.next());
        assertEquals("a.b.C", sequence.currentView());
        assertEquals("a/b/Default", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        try
        {
            sequence.next();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
        sequence.reset();
        assertEquals(true, sequence.hasNext());
    }

    public void testDefault()
    {
        Sequence sequence = new ViewFallbackSequence("a.b.Default", '.', '/', "Default", false);
        assertEquals("a/b/Default", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        try
        {
            sequence.next();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
        sequence.reset();
        assertEquals(true, sequence.hasNext());
    }
    
    public void testSingle()
    {
        Sequence sequence = new ViewFallbackSequence("a", '.', '/', "Default", false);
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        sequence = new ViewFallbackSequence("A", '.', '/', "Default", false);
        assertEquals("A", sequence.next());
        assertEquals("A", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        sequence = new ViewFallbackSequence("Default", '.', '/', "Default", false);
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
    }

    public void testPackageEnclosing()
    {
        Sequence sequence = new ViewFallbackSequence("a.b.c", '.', '/', "Default", true);
        assertEquals("a/b/Default", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        try
        {
            sequence.next();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
        sequence.reset();
        assertEquals(true, sequence.hasNext());
    }

    public void testClassEnclosing()
    {
        Sequence sequence = new ViewFallbackSequence("a.b.C", '.', '/', "Default", true);
        assertEquals("a/b/Default", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        try
        {
            sequence.next();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
        sequence.reset();
        assertEquals(true, sequence.hasNext());
    }

    public void testDefaultEnclosing()
    {
        Sequence sequence = new ViewFallbackSequence("a.b.Default", '.', '/', "Default", true);
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        try
        {
            sequence.next();
            fail("exception expected");
        }
        catch(Exception e)
        {
            // success
        }
        sequence.reset();
        assertEquals(true, sequence.hasNext());
    }
    
    public void testSingleEnclosing()
    {
        Sequence sequence = new ViewFallbackSequence("a", '.', '/', "Default", true);
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        sequence = new ViewFallbackSequence("A", '.', '/', "Default", true);
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals(false, sequence.hasNext());
        sequence = new ViewFallbackSequence("Default", '.', '/', "Default", true);
        assertEquals(false, sequence.hasNext());
    }
}
