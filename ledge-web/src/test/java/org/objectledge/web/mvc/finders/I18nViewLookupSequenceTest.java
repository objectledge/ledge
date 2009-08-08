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
package org.objectledge.web.mvc.finders;

import java.util.Locale;

import org.jmock.Mock;
import org.objectledge.i18n.I18n;
import org.objectledge.i18n.I18nContext;
import org.objectledge.utils.LedgeTestCase;

public class I18nViewLookupSequenceTest
    extends LedgeTestCase
{
    public void testLocalizedLookupSequence()
    {
        Sequence sequence = new ViewFallbackSequence("a.b.c", '.', '/', "Default", false);
        Mock mockI18n = mock(I18n.class);
        mockI18n.stubs().method("getDefaultLocale").will(returnValue(new Locale("en","US")));
        I18n i18n = (I18n)mockI18n.proxy();
        Mock mockI18nContext = mock(I18nContext.class);
        mockI18nContext.stubs().method("getLocale").will(returnValue(new Locale("pl","PL")));
        I18nContext i18nContext = (I18nContext)mockI18nContext.proxy();
        
        sequence = new I18nViewLookupSequence(sequence, i18n, i18nContext); 
        assertEquals("a/b/c/Default.pl_PL", sequence.next());
        assertEquals("a.b.c.Default", sequence.currentView());
        assertEquals("a/b/c/Default.en_US", sequence.next());
        assertEquals("a.b.c.Default", sequence.currentView());
        assertEquals("a/b/c/Default", sequence.next());
        assertEquals("a.b.c.Default", sequence.currentView());

        assertEquals("a/b/Default.pl_PL", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());
        assertEquals("a/b/Default.en_US", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());
        assertEquals("a/b/Default", sequence.next());
        assertEquals("a.b.Default", sequence.currentView());

        assertEquals("a/Default.pl_PL", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("a/Default.en_US", sequence.next());
        assertEquals("a.Default", sequence.currentView());
        assertEquals("a/Default", sequence.next());
        assertEquals("a.Default", sequence.currentView());

        assertEquals("Default.pl_PL", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals("Default.en_US", sequence.next());
        assertEquals("Default", sequence.currentView());
        assertEquals("Default", sequence.next());
        assertEquals("Default", sequence.currentView());
}
}
