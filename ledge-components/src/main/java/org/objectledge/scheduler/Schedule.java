// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.scheduler;

import java.util.Date;

/**
 * Describes an algorithm for calculating job execution times.
 */
public interface Schedule
{
    /**
     * Initialize the schedule.
     * 
     * @param config the configuration.
     */
    public void init(String config);
    
    /**
     * Returns the name of the schedule type.
     *      
     * @return the name of the schedule type.
     */
    public String getType();

    /**
     * Return the schedule configuration.
     *
     * <p>The format of the string is dependant on the nature of
     * the schedule.</p>
     *
     * @return the schedule configuration.
     */
    public String getConfig();

    /**
     * Sets the schedule configuration.
     *
     * <p>The format of the string is dependant on the nature of
     * the schedule.</p>
     *
     * @param config schedule configuration.
     * @throws InvalidScheduleException if the specification is invalid
     */     
    public void setConfig(String config)
        throws InvalidScheduleException;

    /**
     * Checks if the job should be run at the very startup of the scheduler.
     *
     * @return <code>true</code> if the job should be run during the startup
     *         of the scheduler.
     */
    public boolean atStartup();

    /**
     * Calculates the time of the job's next run.
     *
     * @param currentTime the current time.
     * @param lastRunTime the last time the job was run, or <code>null</code> 
     * if unknown.
     * @return job's next execution time of <code>null</code> for never again.
     */
    public Date getNextRunTime(Date currentTime, Date lastRunTime);
}
