// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.modules.views.system;

import java.lang.management.ManagementFactory;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: VM.java,v 1.1 2005-05-09 10:34:24 rafal Exp $
 */
public class VM
    extends PolicyProtectedBuilder
{

    /**
     * Creates new VM view instance.
     * 
     * @param context the Context component.
     * @param policySystemArg the PolicySystem component.
     */
    public VM(Context context, PolicySystem policySystemArg)
    {
        super(context, policySystemArg);
        // TODO Auto-generated constructor stub
    }

    /**
     * {@inheritDoc}
     */
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        
        templatingContext.put("classLoading", ManagementFactory.getClassLoadingMXBean());
        templatingContext.put("compilation", ManagementFactory.getCompilationMXBean());        
        templatingContext.put("garbageCollectors", ManagementFactory.getGarbageCollectorMXBeans());
        templatingContext.put("memoryManagers", ManagementFactory.getMemoryManagerMXBeans());
        templatingContext.put("memory", ManagementFactory.getMemoryMXBean());
        templatingContext.put("memoryPools", ManagementFactory.getMemoryPoolMXBeans());
        templatingContext.put("operatingSystem", ManagementFactory.getOperatingSystemMXBean());
        templatingContext.put("runtime", ManagementFactory.getRuntimeMXBean());
        templatingContext.put("thread", ManagementFactory.getThreadMXBean());
        
        return super.build(template, embeddedBuildResults);
    }
}
