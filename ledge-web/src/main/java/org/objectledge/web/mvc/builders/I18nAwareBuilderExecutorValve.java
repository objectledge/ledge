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

import java.util.Locale;

import org.objectledge.context.Context;
import org.objectledge.i18n.I18n;
import org.objectledge.templating.Template;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: I18nAwareBuilderExecutorValve.java,v 1.3 2004-01-19 14:44:27 zwierzem Exp $
 */
public class I18nAwareBuilderExecutorValve extends BuilderExecutorValve
{
	private Locale defaultLocale;
	
    /**
     * {@inheritDoc}
     * 
     * @param i18n an i18n component for choosing templates upon currently selected Locale.
     */
    public I18nAwareBuilderExecutorValve(
        Context context,
        MVCClassFinder classFinder,
        MVCTemplateFinder templateFinder,
        int maxRouteCalls,
        int maxEnclosures,
        I18n i18n)
    {
        super(context, classFinder, templateFinder, maxRouteCalls, maxEnclosures);
		this.defaultLocale = i18n.getDefaultLocale();
    }
    
    /**
     * {@inheritDoc}
     */
    protected Template resolveTemplate(Template template)
    {
    	// get current locale && try to get template
    	MVCContext mvcContext = MVCContext.getMVCContext(context);
		Template newTemplate = resolveTemplate(template, mvcContext.getLocale());
		if(newTemplate != null)
		{
			return newTemplate;
		}
    	// get default locale && try to get template
		newTemplate = resolveTemplate(template, defaultLocale);
		if(newTemplate != null)
		{
			return newTemplate;
		}
		// return provided template if others failed 
        return super.resolveTemplate(template);
    }

    /**
     * Gets a template for a given base template and locale.
     * 
     * @param template base template object
     * @param locale locale to find template for
     * @return found template or null
     */
    Template resolveTemplate(Template template, Locale locale)
    {
		if(locale == null)
		{
			return null;
		}
		
    	String localeStr = locale.toString();
    	if(localeStr.length() != 0)
    	{
    		String templateName = template.getName() + "." + localeStr;
    		Template newTemplate = templateFinder.getTemplate(templateName);
    		if(newTemplate != null)
    		{
    			return newTemplate;
    		}
    	}
        return template;
    }
}
