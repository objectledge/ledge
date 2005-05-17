// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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
package org.objectledge.modules.actions.scheduler;

import java.text.ParseException;
import java.util.Date;

import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.scheduler.AbstractJobDescriptor;
import org.objectledge.scheduler.AbstractScheduler;
import org.objectledge.scheduler.Schedule;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.PolicyProtectedAction;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * Create new job.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CreateJob.java,v 1.1 2005-05-17 08:52:50 pablo Exp $
 */
public class CreateJob 
    extends PolicyProtectedAction
{
    private AbstractScheduler scheduler;
    
    /**
     * Action constructor.
     * 
     * @param i18n the I18n component.
     */
    public CreateJob(PolicySystem policySystemArg, AbstractScheduler scheduler)
    {
        super(policySystemArg);
		this.scheduler = scheduler;
    }

    /**
     * Run the valve.
     * 
     * @param context the context.
     * @throws ProcessingException if action processing fails.
     */
    public void process(Context context) throws ProcessingException
    {
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
		Parameters parameters = RequestParameters.getRequestParameters(context);
		String name = parameters.get("name", "");
	    String scheduleType = parameters.get("scheduleType","");
        String scheduleConfig = parameters.get("scheduleConfig","");
        String jobClassName = parameters.get("jobClassName","");
        if(name.equals(""))
        {
			templatingContext.put("result", "scheduler.invalid_name");
			return;
        }
        if(scheduleType.equals(""))
        {
			templatingContext.put("result", "scheduler.schedule_type_empty");
			return;
		}
		if(scheduleConfig.equals(""))
        {
			templatingContext.put("result", "scheduler.schedule_config_empty");
			return;
        }
        if(jobClassName.equals(""))
        {
			templatingContext.put("result", "scheduler.job_class_empty");
			return;
        }
        int runCountLimit = parameters.getInt("runCountLimit",-1);
        String runTimeLimitStartStr = parameters.get("runTimeLimitStart",null);
        String runTimeLimitEndStr = parameters.get("runTimeLimitEnd",null);
        boolean reentrant = parameters.getBoolean("reentrant",false);
        boolean enabled = parameters.getBoolean("enabled",false);

        Date runTimeLimitStart = null;
        Date runTimeLimitEnd = null;
        try
        {
            if(runTimeLimitStartStr != null && !runTimeLimitStartStr.equals(""))
            {
                runTimeLimitStart = scheduler.getDateFormat().parse(runTimeLimitStartStr);
            }
        }
        catch(ParseException e)
        {
			templatingContext.put("trace", new StackTrace(e));
            templatingContext.put("result", "scheduler.invalid_run_time_limit_start");
        }
        try
        {
            if(runTimeLimitEndStr != null && !runTimeLimitEndStr.equals(""))
            {
                runTimeLimitEnd = scheduler.getDateFormat().parse(runTimeLimitEndStr);
            }
        }
        catch(ParseException e)
        {
			templatingContext.put("trace", new StackTrace(e));
            templatingContext.put("result", "scheduler.invalid_run_time_limit_end");
        }
		try
		{
            Schedule schedule = scheduler.createSchedule(scheduleType, scheduleConfig);
            AbstractJobDescriptor job = scheduler.createJobDescriptor(name, schedule, jobClassName);
            job.setRunCountLimit(runCountLimit);
            job.setTimeLimit(runTimeLimitStart, runTimeLimitEnd);
            job.setReentrant(reentrant);
			if(enabled)
			{
				scheduler.enable(job);
			}
			templatingContext.put("result", "scheduler.updated_successfully");
			MVCContext mvcContext = MVCContext.getMVCContext(context);
			mvcContext.setView("scheduler.Jobs");
        }
		catch(Exception e)
		{
			throw new ProcessingException("failed to create job", e);
		}
    }
}

