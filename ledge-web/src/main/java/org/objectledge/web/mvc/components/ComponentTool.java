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
package org.objectledge.web.mvc.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.DefaultTemplate;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;
import org.objectledge.web.mvc.security.SecurityHelper;

/**
 * A template tool for embedding components in application UI.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ComponentTool.java,v 1.11 2005-02-02 22:27:41 pablo Exp $
 */
public class ComponentTool
{
    /** The context. */
    protected Context context;
    
	/** The class finder for finding component objects. */
	protected MVCClassFinder classFinder;
    
	/** The template finder for finding component templates. */
	protected MVCTemplateFinder templateFinder;

    /** SecurityHelper for access checking. */
    protected SecurityHelper securityHelper;
    
    /** the default component implementation. */
    protected Component defaultComponent;
    
    /** the default template. */
    protected Template defaultTemplate;
	
    /**
     * Construct a component tool.
     * 
     * @param context thread's processing context.
     * @param classFinder class finder for finding component objects.
     * @param templateFinder template finder for finding component templates.
     * @param securityHelper security helper for access checking
     */
    public ComponentTool(Context context, MVCClassFinder classFinder, 
        MVCTemplateFinder templateFinder, SecurityHelper securityHelper)
    {
        this.context = context;
		this.classFinder = classFinder;
		this.templateFinder = templateFinder;
        this.securityHelper = securityHelper;
        this.defaultComponent = new DefaultComponent(context);
        this.defaultTemplate = new DefaultTemplate();
    }

    /**
     *  Embeds a component with a givemn name.
     *
     * @param componentName name of the component to be embedded.
     * @return contents of a rendered component
     * @throws BuildException on problems with component building
     * @throws ProcessingException if processing fails.
     */
    public String embed(String componentName) 
        throws BuildException, ProcessingException
    {
        return embed(componentName, (Map)null);
    }
    
    /**
     *  Embeds a component with a givemn name.
     *
     * @param componentName name of the component to be embedded.
     * @param config localy scoped configuration in list form.
     * @return contents of a rendered component
     * @throws BuildException on problems with component building
     * @throws ProcessingException if processing fails.
     */
    public String embed(String componentName, List config) 
        throws BuildException, ProcessingException
    {
        return embed(componentName, listToMap(config));
    }
    
    /**
	 *  Embeds a component with a givemn name.
	 *
	 * @param componentName name of the component to be embedded.
     * @param config localy scoped configuration.
	 * @return contents of a rendered component
	 * @throws BuildException on problems with component building
     * @throws ProcessingException if processing fails.
	 */
	public String embed(String componentName, Map config) 
        throws BuildException, ProcessingException
	{
		Component component = classFinder.getComponent(componentName);
		Template template = templateFinder.getComponentTemplate(componentName);
        template = resolveTemplate(template);
		if(component != null)
		{
			Template newTemplate = component.getTemplate();
			if(newTemplate != null)
			{
				template = newTemplate;
			}
			if(template == null)
			{
				template = defaultTemplate;
			}
		}
		else
		{
			if(template == null)
			{
				throw new IllegalArgumentException("No component nor template with name "
					+ componentName);			
			}
			component = defaultComponent;
		}		
        securityHelper.checkSecurity(component, context);
        String result = null;
        if(config != null)
        {
            Map store = new HashMap();
            TemplatingContext tContext = 
                 TemplatingContext.getTemplatingContext(context);
            Iterator i = config.keySet().iterator();
            while(i.hasNext())
            {
                String key = (String)i.next();
                store.put(key, tContext.get(key));
                tContext.put(key, config.get(key));
            }
            result = component.build(template);
            i = config.keySet().iterator();
            while(i.hasNext())
            {
                String key = (String)i.next();
                tContext.put(key, store.get(key));
            }
        }
        else
        {
            result = component.build(template);
        }
		return result; 
	}
	
    /**
     * {@inheritDoc}
     */	
	protected Template resolveTemplate(Template template)
	{
	    return template;
	}
    
    /**
     * Converst a list of two element lists (key, value) into a map.
     *
     * @param list the list
     * @return a map
     */
    private Map listToMap(List list)
    {
        Map map = new HashMap(list.size());
        Iterator i = list.iterator();
        while(i.hasNext())
        {
            List l = (List)i.next();
            map.put(l.get(0), l.get(1));
        }
        return map;
    }
}
