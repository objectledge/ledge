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

package org.objectledge.i18n;

import java.util.Locale;

import org.objectledge.context.Context;
import org.objectledge.i18n.I18n;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;

/**
 * A context tool for retrieving user agent info.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: I18nAwareTemplateResolver.java,v 1.2 2004-08-31 11:08:31 pablo Exp $
 */
public class I18nAwareTemplateResolver
{
    /** the default locale */
	private Locale defaultLocale;
	
	/** the templating */
	private Templating templating;
	
	/** the context */
    private Context context;

    // public interface ///////////////////////////////////////////////////////
    
    /**
     * The constructor.
     * 
     * @param context the context.
     * @param i18n an i18n component for choosing templates upon currently selected Locale.
     * @param templating the templating component.
     */
    public I18nAwareTemplateResolver(Context context, I18n i18n, Templating templating)
    {
		this.defaultLocale = i18n.getDefaultLocale();
		this.templating = templating;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public Template resolveTemplate(Template template)
    {
    	// get current locale && try to get template
    	I18nContext i18nContext = I18nContext.getI18nContext(context);
		Template newTemplate = resolveTemplate(template, i18nContext.getLocale());
		if(newTemplate != null)
		{
			return newTemplate;
		}
    	// get default locale && try to get template
		return resolveTemplate(template, defaultLocale);
    }

    /**
     * Gets a template for a given base template and locale.
     * 
     * @param template base template object
     * @param locale locale to find template for
     * @return found template or null
     */
    private Template resolveTemplate(Template template, Locale locale)
    {
		if(locale == null || template == null)
		{
			return null;
		}
		
    	String localeStr = locale.toString();
    	if(localeStr.length() != 0)
    	{
            try
            {
				String templateName = template.getName() + "." + localeStr;
                return templating.getTemplate(templateName);
            }
            catch (TemplateNotFoundException e)
            {
            	return null;
            }
    	}
        return template;
    }
}
