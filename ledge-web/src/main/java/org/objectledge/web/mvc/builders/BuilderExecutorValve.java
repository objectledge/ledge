// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.mvc.builders;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.MVCConstants;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;
import org.objectledge.web.mvc.security.SecurityHelper;

/**
 * Pipeline component for executing MVC view building.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BuilderExecutorValve.java,v 1.21 2004-09-23 09:32:52 zwierzem Exp $
 */
public class BuilderExecutorValve 
    implements Valve
{
	/** Finder for builder objects. */
	protected MVCClassFinder classFinder;
	/** Finder for template objects. */
	protected MVCTemplateFinder templateFinder;
    /** SecurityHelper for access checking. */
    protected SecurityHelper securityHelper;
	/** maximum number of route calls per builder. */
	protected int maxRouteCalls;  
	/** maximum number of builder enclosures. */
	protected int maxEnclosures;
    /** the default builder. */
    protected Builder defaultBuilder;
    /** the default template. */
    protected Template defaultTemplate;    
    
	/**
	 * Component constructor.
	 * 
     * @param context used application context
     * @param classFinder finder for builder objects
     * @param templateFinder finder for template objects
     * @param securityHelper security helper for access checking
     * @param maxRouteCalls maxmimal number of {@link Builder#route()} calls per {@link Builder}
     * @param maxEnclosures maxmimal number of {@link Builder} enclosures
     * 	(also {@link Builder#getEnclosingViewPair()} calls)
	 */
	public BuilderExecutorValve(Context context, MVCClassFinder classFinder,
        MVCTemplateFinder templateFinder, SecurityHelper securityHelper,
        int maxRouteCalls, int maxEnclosures)
	{
		this.classFinder = classFinder;
		this.templateFinder = templateFinder;
        this.securityHelper = securityHelper;
        this.maxRouteCalls = maxRouteCalls;
		this.maxEnclosures = maxEnclosures;
        // take them in through pico?        
        this.defaultBuilder = new DefaultBuilder(context);
        this.defaultTemplate = new DefaultTemplate();
	}
	
	/**
	 * Run view building starting from a view builder chosen in request parameters.
     * 
     * @param context the thread's processing context.
     * @throws ProcessingException if the processing fails.
	 */
	public void process(Context context)
        throws ProcessingException
	{
		// setup used contexts
		MVCContext mvcContext = MVCContext.getMVCContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
	
		// get initial builder, template and embedded result
		String embeddedResult = null;
		Builder builder = classFinder.findBuilder(mvcContext.getView());
		Template template = templateFinder.findBuilderTemplate(mvcContext.getView());
		
		// start processing
		int enclosures;
		for (enclosures = 0; enclosures < maxEnclosures; enclosures++)
		{
            if(builder != null)
            {
                // route builder -------------------------------------------------------------------
                boolean builderRouted = false;
                int routeCalls;
                for (routeCalls = 0; routeCalls < maxRouteCalls; routeCalls++)
                {
                    Builder routeBuilder = builder.route();
                    if(routeBuilder == null)
                    {
                        break;
                    }
                    builder = routeBuilder;
                    builderRouted = true;
                }
                if(routeCalls >= maxRouteCalls)
                {
                    throw new ProcessingException("Maximum number of builder reroutings exceeded");
                }
                // security check ------------------------------------------------------------------
                securityHelper.checkSecurity(builder, context);
                // get the template ----------------------------------------------------------------
                // let builder override the template
                Template overrideTemplate = builder.getTemplate(); 
                if(overrideTemplate != null)
                {
                    template = overrideTemplate;
                }
                // find a template for this builder
                if(overrideTemplate == null && builderRouted)
                {
                    template = templateFinder.findBuilderTemplate(
                        classFinder.findViewName(builder));
                }
            }

			// build view level --------------------------------------------------------------------
			embeddedResult = embeddedResult == null ? "": embeddedResult;
			templatingContext.put(MVCConstants.EMBEDDED_PLACEHOLDER_KEY, embeddedResult);
            Builder actualBuilder = builder != null ? builder : defaultBuilder;
            Template actualTemplate = template != null ? resolveTemplate(template)
                 : defaultTemplate;
			try
	        {
            	embeddedResult = actualBuilder.build(actualTemplate, embeddedResult);
	        }
	        catch (BuildException e)
	        {
	            throw new ProcessingException(e);
	        }

            // get next view build level -----------------------------------------------------------
            Builder enclosingBuilder = null;
            Template enclosingTemplate = null;
            if(builder != null)
            {
                ViewPair pair = builder.getEnclosingViewPair(actualTemplate);
                if(pair == null)
                {
                    break;
                }
                enclosingBuilder = pair.getBuilder();
                enclosingTemplate = pair.getTemplate();
            }

			if(enclosingBuilder == null && builder != null)
			{
				// shorten the builder path name, find new builder	
				enclosingBuilder = classFinder.findEnclosingBuilder(builder);
			}
			builder = enclosingBuilder;

			if(enclosingTemplate == null && template != null)
			{
				// shorten the template path name, find new template	
				enclosingTemplate = templateFinder.findEnclosingBuilderTemplate(template);
			}
			template = enclosingTemplate;
	        // -------------------------------------------------------------------------------------
			
			if(template == null && builder == null)
			{
				break;
			}
		}

		// did not reach a closing builder
		if(enclosures >= maxEnclosures)
		{
			throw new ProcessingException("Maximum number of builder enclosures exceeded");
		}

		// store building result
		mvcContext.setBuildResult(embeddedResult);
	}

    /**
     * This method is to be overriden by BuilderExecutorValves which need a more specific template
     * choosing. An example for such a valve is a I18nAwareBuilderExecutorValve.
     * 
     * @param template used as a base template.
     * @return a template chosen upon the base template, in this case same template object.
     */
    protected Template resolveTemplate(Template template)
    {
        return template;
    }
}
