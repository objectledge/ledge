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
package org.objectledge.logging;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * LoggingEvent subclass, created to plug in {@link org.objectledge.utils.StackTrace} throwable 
 * formatter.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeLoggingEvent.java,v 1.3 2005-02-08 00:35:24 rafal Exp $
 */
public class LedgeLoggingEvent
	extends LoggingEvent
{
    private LedgeThrowableInformation ledgeThrowableInfo = null;
    
    /**
     * Creates new LedgeLoggingEvent instance.
     * 
     * @param fqnOfCategoryClass the fully qualified name of the logger class.
     * @param logger the logger object.
     * @param timeStamp time stamp of the event.
     * @param priority event priority.
     * @param message the message.
     * @param throwable the attached throwable if any.
     */
    public LedgeLoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp,
        Priority priority, Object message, Throwable throwable)
    {
        super(fqnOfCategoryClass, logger, timeStamp, priority, message, throwable);
        if(throwable != null)
        {
            ledgeThrowableInfo = new LedgeThrowableInformation(throwable);
        }
    }

    /**
     * Creates new LedgeLoggingEvent instance.
     * 
     * @param fqnOfCategoryClass the fully qualified name of the logger class.
     * @param logger the logger object.
     * @param priority event priority.
     * @param message the message.
     * @param throwable the attached throwable if any.
     */
    public LedgeLoggingEvent(String fqnOfCategoryClass, Category logger, Priority priority,
        Object message, Throwable throwable)
    {
        super(fqnOfCategoryClass, logger, priority, message, throwable);
        if(throwable != null)
        {
            ledgeThrowableInfo = new LedgeThrowableInformation(throwable);
        }
    }


    /**
     * Returns the throwable information contained within this event. May be <code>null</code> if
     * there is no such information.
     * 
     * <p> Note that the {@link Throwable}object contained within a {@link ThrowableInformation}does
     * not survive serialization. </p>
     * 
     * @return ThrowableInformation contained within this event.
     */
    public ThrowableInformation getThrowableInformation() 
    {
        return ledgeThrowableInfo;
    }
    
    
    /**
     * Return this event's throwable's string[] representation.
     * 
     * @return this event's throwable's string[] representation.
     */
    public String[] getThrowableStrRep()
    {
        return ledgeThrowableInfo != null ? ledgeThrowableInfo.getThrowableStrRep() : null;
    }
}
