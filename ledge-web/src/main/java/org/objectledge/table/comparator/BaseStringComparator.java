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
package org.objectledge.table.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * This is a base comparator for string values.
 * It provides localisation for string comparisons.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseStringComparator.java,v 1.3 2005-07-07 08:29:32 zwierzem Exp $
 */
public abstract class BaseStringComparator
    implements Comparator
{
    /** The Collator to use for comparisons. */
    protected Collator collator;
    
    /**
     * Constructs a base string comparator.
     * @param locale the locale based on which the comparisons are performed.
     */
    public BaseStringComparator(Locale locale)
    {
        collator = Collator.getInstance(locale);
    }

    /**
     * Compare two strings in a locale sensitive way.
     * 
     * @param s1 a string.
     * @param s2 a string.
     * @return int value &lt; 0 if s2 precedes s1 in lexicographic ordering, 0 if both strings
     * are lexicographically equivalent, &gt; 0 if s1 precedes s2 in lexicographic ordering.
     */
    public int compareStrings(String s1, String s2)
    {
        return collator.compare(s1, s2);
    }
}
