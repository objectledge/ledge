// 
// Copyright (c) 2003-2006, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.table.filter;

import java.util.Date;

import org.objectledge.table.TableFilter;

/**
 * Base implementation for Time based table filters.
 * It filters out the dates based on specified start and end date. One of the dates can be
 * <code>null</code>, it means an open time range. Both <code>null</code>s accept everything.
 * 
 * <p>BEWARE: The filter may not accept <code>null</code> dates.</p>
 *  
 * @author <a href="mailto:gajda@ii.pw.edu.pl">Damian Gajda</a>
 * @version $Id: TimeFilter.java,v 1.2 2006-04-20 20:46:45 zwierzem Exp $
 * @param <T> the type of the filtered objects.
 */
public abstract class TimeFilter<T>
    implements TableFilter<T>
{
    private Date start;
    private Date end;
    
    public TimeFilter(Date start, Date end)
    {
        this.start = start;
        this.end = end;
    }
    
    /**
     * Call this method in Your Time filter <code>accept()</code> implementation.
     * 
     * <p>Method is intentionally named <code>acceptDate</code> not <code>accept</code> to avoid
     * possible conflicts.
     * </p>
     * 
     * @param date the date to be accepted.
     * @return <code>true</code> if the date is in the accepted time period.
     */
    protected boolean acceptDate(Date date)
    {
        boolean accepted = true;
        if(start != null)
        {
            accepted = accepted && start.before(date);
        }
        if(end != null)
        {
            accepted = accepted && end.after(date);
        }
        return accepted;
    }
}
