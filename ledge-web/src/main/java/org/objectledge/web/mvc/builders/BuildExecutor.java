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
import org.objectledge.pipeline.PipelineProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.BuildException;
import org.objectledge.web.mvc.MVCClassFinder;
import org.objectledge.web.mvc.MVCConstants;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.MVCContextImpl;
import org.objectledge.web.mvc.TemplateFinder;

/**
 * Pipeline component for executing MVC view building.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BuildExecutor.java,v 1.6 2004-01-12 15:00:57 fil Exp $
 */
public class BuildExecutor implements Runnable
{
	/** context */
	protected Context context;
	/** Finder for builder objects. */
	protected MVCClassFinder classFinder;
	/** Finder for template objects. */
	protected TemplateFinder templateFinder;
	/** maximum number of route calls per builder. */
	protected int maxRouteCalls;  
	/** maximum number of builder enclosures. */
	protected int maxEnclosures;
	/**
	 * Component constructor.
	 * 
     * @param context used application context
     * @param classFinder finder for builder objects
     * @param templateFinder finder for template objects
     * @param maxRouteCalls maxmimal number of {@link Builder#route()} calls per {@link Builder}
     * @param maxEnclosures maxmimal number of {@link Builder} enclosures
     * 	(also {@link Builder#getEnclosingViewPair()} calls)
	 */
	public BuildExecutor(Context context, MVCClassFinder classFinder, TemplateFinder templateFinder,
		int maxRouteCalls, int maxEnclosures)
	{
		this.context = context;
		this.classFinder = classFinder;
		this.templateFinder = templateFinder;
		this.maxRouteCalls = maxRouteCalls;
		this.maxEnclosures = maxEnclosures;
	}
	
	/**
	 * Run view building starting from a view builder chosen in request parameters.
	 */
	public void run()
	{
		// setup used contexts
		HttpContext httpContext = HttpContext.retrieve(context);
		MVCContext mvcContext = MVCContextImpl.retrieve(context);
		TemplatingContext templatingContext = (TemplatingContext)
			context.getAttribute(TemplatingContext.CONTEXT_KEY);
	
		// get initial builder, template and embedded result
		String embeddedResult = null;
		Builder builder = classFinder.findBuilder(mvcContext.getView());
		Template template = templateFinder.findBuilderTemplate(mvcContext.getView());
		
		// start processing
		int enclosures;
		for (enclosures = 0; enclosures < maxEnclosures; enclosures++)
		{
			// route builder
			boolean builderRouted = false; 
			for (int routeCalls = 0; routeCalls < maxRouteCalls; routeCalls++)
	        {
				Builder routeBuilder = builder.route();
				if(routeBuilder == null)
				{
					break;
				}
				builder = routeBuilder;
				builderRouted = true;
	        }

	        // get the template
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
					classFinder.findName(builder.getClass()));
	        }

			// build view level
			embeddedResult = embeddedResult == null ? "": embeddedResult;
			templatingContext.put(MVCConstants.EMBEDDED_PLACEHOLDER_KEY, embeddedResult);
			try
	        {
				embeddedResult = builder.build(template, embeddedResult);
	        }
	        catch (BuildException e)
	        {
	            throw new PipelineProcessingException(e);
	        }
	
			// get next view build level
	        ViewPair pair = builder.getEnclosingViewPair();
			if(pair != null)
			{
				// get enclosing builder
				Builder enclosingBuilder = pair.getBuilder();
				if(enclosingBuilder == null)
				{
					// shorten the builder path name, find new builder	
					enclosingBuilder = classFinder.findEnclosingBuilder(builder);
				}
				builder = enclosingBuilder;

				Template enclosingTemplate = pair.getTemplate();
				if(enclosingTemplate == null)
				{
					// shorten the builder path name, find new template	
					enclosingTemplate = templateFinder.findEnclosingBuilderTemplate(template);
				}
				template = enclosingTemplate;
			}
			else
			{
				// stop building
				break;
			}
		}

		// did not reach a closing builder
		if(enclosures >= maxEnclosures)
		{
			throw new PipelineProcessingException("Maximal number of builder enclosures exceeded");
		}

		// store building result
		mvcContext.setBuildResult(embeddedResult);
	}
}
