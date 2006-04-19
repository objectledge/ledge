//
// Copyright (c) 2005-2006, Warsaw University of Technology. 
// All rights reserved. 
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
 * @version $Id: TimeFilter.java,v 1.1 2006-04-19 19:20:07 zwierzem Exp $
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
