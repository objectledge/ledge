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
package org.objectledge.modules.views.logging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
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
 * @version $Id: EditLogger.java,v 1.3 2005-07-07 08:29:34 zwierzem Exp $
 */
public class EditLogger
    extends PolicyProtectedBuilder
{

    /**
     * Creates new EditLogger instance.
     * 
     * @param context Context component.
     * @param policySystemArg PolicySystem component.
     */
    public EditLogger(Context context, PolicySystem policySystemArg)
    {
        super(context, policySystemArg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        RequestParameters requestParameters = RequestParameters.getRequestParameters(context);
        String id = requestParameters.get("id");
        Logger logger;
        if(id.equals("root"))
        {
            logger = LogManager.getRootLogger();
        }
        else
        {
            if(LogManager.exists(id) == null)
            {
                throw new ProcessingException("invalid logger id "+id);
            }
            logger = LogManager.getLogger(id);
        }
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        templatingContext.put("logger", logger);
        List<Appender> appenders = new ArrayList<Appender>();
        getAppenders(logger, appenders);
        List<Appender> inheritedAppenders = new ArrayList<Appender>();
        getInheritedAppenders(logger, inheritedAppenders);
        inheritedAppenders.removeAll(appenders);
        templatingContext.put("appenders", appenders);
        templatingContext.put("inheritedAppenders", inheritedAppenders);
        return super.build(template, embeddedBuildResults);
    }

    /**
     * Returns appenders attached to the logger.
     * 
     * @param logger the logger.
     * @param appenders attached to the logger.
     */
    private void getAppenders(Logger logger, List<Appender> appenders)
    {
        Enumeration<Appender> appenderEnumeration = logger.getAllAppenders();
        while(appenderEnumeration.hasMoreElements())
        {
            appenders.add(appenderEnumeration.nextElement());
        }
    }
    
    /**
     * Returns appenders inherited by the logger.
     * 
     * @param logger the logger.
     * @param appenders inherited by the logger.
     */
    private void getInheritedAppenders(Logger logger, List<Appender> appenders)
    {
        getAppenders(logger, appenders);
        if(logger.getAdditivity() && logger.getParent() != null)
        {
            getInheritedAppenders((Logger)logger.getParent(), appenders);
        }
    }
}
