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

package org.objectledge.web.mvc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ErrorHandlingPipeline;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;

/**
 * The valve that redirects to a configured view in case of exception.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class ExceptionRedirectorValve implements Valve
{
    /** logger the logger */
    private Logger logger;

    /** error view */
    private String errorView;

    /** error log level */
    private String errorLevel;

    /** exception - view mapping */
    private Map<Class<?>, String> exceptionViewMap;

    /** exception - log level mapping */
    private Map<Class<?>, String> exceptionLoggingLevelMap;

    /**
     * Component constructor.
     * 
     * @param config the configuration.
     * @param logger the logger.
     */
    public ExceptionRedirectorValve(Configuration config, Logger logger)
    {
        this.logger = logger;
        exceptionViewMap = new HashMap<Class<?>, String>();
        exceptionLoggingLevelMap = new HashMap<Class<?>, String>();

        try
        {
            errorView = config.getChild("error_view").getValue("Error");
            errorLevel = config.getChild("error_level").getValue("ERROR");
            Configuration[] exception = config.getChildren("exception");
            for (int i = 0; i < exception.length; i++)
            {
                String name = exception[i].getAttribute("class");
                String view = exception[i].getAttribute("view");
                String level = exception[i].getAttribute("level");
                Class<?> clazz = Class.forName(name);
                exceptionViewMap.put(clazz, view);
                exceptionLoggingLevelMap.put(clazz, level);
            }
            if (!exceptionViewMap.containsKey(Throwable.class))
            {
                exceptionViewMap.put(Throwable.class, errorView);
                exceptionLoggingLevelMap.put(Throwable.class, errorLevel);
            }
        }
        ///CLOVER:OFF
        catch (Exception e)
        {
            throw new ComponentInitializationError(e);
        }
        ///CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context) throws ProcessingException
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        Throwable t = (Throwable)context.getAttribute(ErrorHandlingPipeline.PIPELINE_EXCEPTION);
        if(mvcContext != null && t != null)
        {
            Class<?> leafException = null;
            Throwable tt;
            for(tt = t; tt != null; tt = tt.getCause()){

                Iterator<Class<?>> i = exceptionViewMap.keySet().iterator();
                while (i.hasNext())
                {
                    Class<?> temp = i.next();
                    if (temp.isAssignableFrom(tt.getClass()))
                    {
                        if (leafException == null || leafException.isAssignableFrom(temp))
                        {
                            leafException = temp;
                        }
                    }
                }
            }
            log(exceptionLoggingLevelMap.get(leafException), t);
            String view = exceptionViewMap.get(leafException);
            TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
            if (templatingContext != null)
            {
                templatingContext.put("originalView", mvcContext.getView());
                templatingContext.put("stackTrace", new StackTrace(t).toString());
            }
            mvcContext.setView(view);
        }
    }

    private void log(String verbosity, Throwable t)
    {
        if (verbosity.equals("WARN"))
        {
            logger.warn("Exception occured during processing", t);
            return;
        }
        if (verbosity.equals("INFO"))
        {
            logger.info("Exception occured during processing", t);
            return;
        }
        if (verbosity.equals("DEBUG"))
        {
            logger.debug("Exception occured during processing", t);
            return;
        }
        if (verbosity.equals("TRACE"))
        {
            logger.trace("Exception occured during processing", t);
            return;
        }
        logger.error("Exception occured during processing", t);
    }
}
