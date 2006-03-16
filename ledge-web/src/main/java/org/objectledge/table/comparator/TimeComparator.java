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

import java.util.Comparator;
import java.util.Date;

/**
 * This is a base comparator for comparing time values related to an object.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TimeComparator.java,v 1.3 2006-03-16 17:57:03 zwierzem Exp $
 */
public abstract class TimeComparator<T>
    implements Comparator<T>
{
    /** Compares two objects using their date attributes. Dates may be null, the contract is:
     *  <ul>
     *  <li>If both dates are not null, they are simply compared.</li>
     *  <li>If both are null, dates are considered equal</li>
     *  <li>If one of the dates is null it is considered to be after the non null date</li>
     *  </ul>
     *  @param d1 a date
     *  @param d2 a date
     *  @return int value &lt; 0 if d2 is earlier than d1, 0 if d1 is equal to d2, &gt; 0 if d1 is 
     *  earlier that d2.
     */
    public int compareDates(Date d1, Date d2)
    {
        
        if(d1 != null && d2 != null)
        {
            return d1.compareTo(d2);
        }
        
        if(d1 == null)
        {
            if(d2 == null) // dates are equal
            {
                return 0;
            }
            else // d1 is after d2
            {
                return 1;
            }
        }
        // if(d2 == null && d1 != null) // d1 is before d2
        return -1;
    }
}
