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
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCConstants;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;
import org.objectledge.web.mvc.security.SecurityHelper;

/**
 * Pipeline component for executing MVC view building.
 * 
 * <p>
 * The <code>BuilderExecutorValve</code> starts with a view name selected by the configured
 * view request parameter ({@link org.objectledge.web.WebConfigurator#getViewToken()}).
 * This parameter has to be defined and if not an
 * {@link org.objectledge.web.mvc.builders.UndefinedViewParameterException} is thrown.
 * At the same time the view name must point to either an existing
 * {@link org.objectledge.web.mvc.builders.Builder} class or an existing template.
 * If none of these exists a {@link org.objectledge.web.mvc.builders.MissingViewException}
 * is thrown.
 * If either the template or a builder has been found, the view building starts.
 * </p>
 * 
 * <p>
 * For a given view name either a builder class or a template may be found using the defaulting
 * strategy. This is useful for instance for applications having a set of different templates for
 * presenting the same data. But <b>be warned</b> - defaulting may cause a lot of trouble and You should
 * be aware of it.
 * </p>
 * 
 * <p>
 * If the builder is found it is given the possiblity to route the processing to another builder.
 * This is a rarely used functionality and has been introduced for similarity with older
 * web application frameworks. Routing changes the currently selected view name and forces a new
 * builder and template lookup.
 * </p>
 * 
 * <p>
 * Having the either a builder or a template or both at the same time the build method of the
 * builder is executed (missing builders and templates are replaced with
 * {@link org.objectledge.web.mvc.builders.DefaultBuilder} and
 * {@link org.objectledge.web.mvc.builders.DefaultTemplate} objects).
 * Building creates a <code>String</code> represenation of view contents. The only exception is
 * in case of a Builder which creates a direct response and writes it's content directly to the
 * response (via {@link org.objectledge.web.HttpContext}), in such case view processing is stopped.
 * </p>
 * 
 * <p>Having the build results for a view, an enclosing view is looked up. The choice of the
 * enclosing view is made upon:
 * </p>
 * <ul>
 * <li>the {@link org.objectledge.web.mvc.builders.EnclosingView} object given by the current
 * builder,</li>
 * <li>or the choice made by the template designer using the
 * {@link org.objectledge.web.mvc.builders.ViewEnclosureTool},</li>
 * <li>or by means of defaulting strategy executed using
 * {@link org.objectledge.web.mvc.finders.ViewFallbackSequence}.</li>
 * </ul>
 * <p>
 * A detailed diagram of the enclosing view choice algorithm follows:
 * </p>
 * 
 * <img src="doc-files/BuilderExecutorValve-1.gif" alt="Enclosing view choice algorithm" />
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BuilderExecutorValve.java,v 1.36 2006-04-04 12:34:14 pablo Exp $
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
    /** ViewEnclosureManager for access template based view enclosures. */
    protected ViewEnclosureManager viewEnclosureManager;
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
	 * @param context used application context
	 * @param classFinder finder for builder objects
	 * @param templateFinder finder for template objects
	 * @param securityHelper security helper for access checking
	 * @param viewEnclosureManager the template based enclosure manager
	 * @param maxRouteCalls maxmimal number of {@link Builder#route(String)} calls per {@link 
     * Builder}
	 * @param maxEnclosures maxmimal number of {@link Builder} enclosures
     * 	(also {@link Builder#getEnclosingView(String)} calls)
	 */
	public BuilderExecutorValve(Context context, MVCClassFinder classFinder,
        MVCTemplateFinder templateFinder, SecurityHelper securityHelper,
        ViewEnclosureManager viewEnclosureManager, int maxRouteCalls, int maxEnclosures)
	{
		this.classFinder = classFinder;
		this.templateFinder = templateFinder;
        this.securityHelper = securityHelper;
        this.viewEnclosureManager = viewEnclosureManager;
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
        HttpContext httpContext = HttpContext.getHttpContext(context);
		MVCContext mvcContext = MVCContext.getMVCContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
	
		// get initial builder, template and embedded result
        String originalViewName = mvcContext.getView();
        if(originalViewName == null || originalViewName.length() == 0)
        {
            throw new UndefinedViewParameterException();
        }
        
        String viewName = originalViewName;
		String embeddedResult = null;
		Builder builder = null;
		Template template = null;
        // usually equal to request's 'view' parameter, but may be affected by builder routing
        String innermostView = null;
		
		// start processing
		int enclosures;
		for (enclosures = 0; enclosures < maxEnclosures; enclosures++)
		{
            MVCClassFinder.Result builderResult = classFinder.findBuilder(viewName);
            builder = builderResult.getBuilder();
            if(builder != null)
            {
                // route builder -------------------------------------------------------------------
                int routeCalls;
                for (routeCalls = 0; routeCalls < maxRouteCalls && builder != null; routeCalls++)
                {
                    
                    String routeBuilderName = builder.route(viewName);
                    if(routeBuilderName == null)
                    {
                        break;
                    }
                    mvcContext.setView(routeBuilderName);
                    viewName = routeBuilderName;
                    builderResult = classFinder.findBuilder(viewName);
                    builder = builderResult.getBuilder();
                }
                if(routeCalls >= maxRouteCalls)
                {
                    throw new ProcessingException("Maximum number of builder reroutings exceeded");
                }
                // security check ------------------------------------------------------------------
                securityHelper.checkSecurity(builder, context);
            }
            // get template ------------------------------------------------------------------------
            MVCTemplateFinder.Result templateResult = templateFinder.findBuilderTemplate(viewName); 
            template = templateResult.getTemplate();
            
            if(enclosures == 0 &&
               builderResult.fallbackPerformed() && templateResult.fallbackPerformed())
            {
                throw new MissingViewException(
                    "originalViewName="+originalViewName+", viewName="+viewName);
            }
            
			// embedded results --------------------------------------------------------------------
			embeddedResult = embeddedResult == null ? "": embeddedResult;
            innermostView = innermostView == null ? viewName : innermostView;
			templatingContext.put(MVCConstants.EMBEDDED_PLACEHOLDER_KEY, embeddedResult);
            templatingContext.put(MVCConstants.INNERMOST_VIEW_KEY, innermostView);
            
            // default builder / template if not resolved up to this point -------------------------
            Builder actualBuilder = builder != null ? builder : defaultBuilder;
            Template actualTemplate = template != null ? template : defaultTemplate;
            
            // perform actual build ----------------------------------------------------------------
			try
	        {
            	embeddedResult = actualBuilder.build(actualTemplate, embeddedResult);
	        }
	        catch (BuildException e)
	        {
	            throw new ProcessingException(e);
	        }

            // escape on direct response -----------------------------------------------------------
            if(httpContext.getDirectResponse())
            {
                return;
            }
            
            // get next view build level -----------------------------------------------------------
            
            // ask the builder about requested enclosure -------------------------------------------
            EnclosingView enclosingView = EnclosingView.DEFAULT;
            if(builder != null)
            {
                enclosingView = builder.getEnclosingView(viewName);
            }
            
            // ask view enclosure manager that wraps viewEnclosureTool available in templates ------
            if(enclosingView.defaultBehaviour())
            {
                enclosingView = viewEnclosureManager.getEnclosingView(enclosingView);
            }
            
            if(enclosingView.override())
            {
                viewName = enclosingView.getView();
            }
            else if(enclosingView.top())
            {
                break;
            }
            else if(enclosingView.defaultBehaviour())
			{
                if(viewName != null) 
                {
                    viewName = classFinder.findEnclosingViewName(viewName);
                }
                else
                {
                    throw new ProcessingException("Top enclosing view not found");
                }
			}
            else
            {
                throw new ProcessingException("Invalid enclosure specification for view "+viewName);
            }
		}

		// did not reach the top builder -----------------------------------------------------------
		if(enclosures >= maxEnclosures)
		{
			throw new ProcessingException("Maximum number of builder enclosures exceeded");
		}

		// store building result -------------------------------------------------------------------
		mvcContext.setBuildResult(embeddedResult);
	}
}
