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

package org.objectledge.modules.views.system;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id$
 */
public class Threads
    extends PolicyProtectedBuilder
{

    /**
     * Creates a new Threads instance.
     * 
     * @param context
     * @param policySystemArg
     */
    public Threads(Context context, PolicySystem policySystemArg)
    {
        super(context, policySystemArg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(TemplatingContext templatingContext)
    {
        templatingContext.put("thread", ManagementFactory.getThreadMXBean());
        Parameters parameters = context.getAttribute(RequestParameters.class);
        if(parameters.isDefined("runnable"))
        {
            templatingContext.put("threadFilter", RunnableThreadFilter.INSTANCE);
        }
        else
        {
            templatingContext.put("threadFilter", ThreadFilter.INSTANCE);
        }
    }

    public static class ThreadFilter
    {
        public static final ThreadFilter INSTANCE = new ThreadFilter();

        private ThreadFilter()
        {

        }

        public boolean accept(ThreadInfo threadInfo)
        {
            return true;
        }
    }

    public static class RunnableThreadFilter
        extends ThreadFilter
    {
        public static final ThreadFilter INSTANCE = new RunnableThreadFilter();

        private RunnableThreadFilter()
        {

        }

        @Override
        public boolean accept(ThreadInfo threadInfo)
        {
            if(!threadInfo.getThreadState().equals(State.RUNNABLE)
                || threadInfo.getThreadId() == Thread.currentThread().getId())
            {
                return false;
            }
            final String threadName = threadInfo.getThreadName().toLowerCase();
            if(threadName.contains("accept") || threadName.contains("listen")
                || threadName.contains("poll") || threadName.contains("dispatch")
                || threadName.equals("main"))
            {
                return false;
            }
            return true;
        }
    }
}
