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

import org.objectledge.context.Context;
import org.objectledge.i18n.I18nAwareTemplateResolver;
import org.objectledge.templating.Template;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;
import org.objectledge.web.mvc.security.SecurityHelper;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: I18nAwareComponentTool.java,v 1.2 2004-08-20 10:04:54 zwierzem Exp $
 */
public class I18nAwareComponentTool
	extends ComponentTool
{
    /** The template resolver */
    protected I18nAwareTemplateResolver resolver;
    
    /**
     * Construct a component tool.
     * 
     * @param context thread's processing context.
     * @param classFinder class finder for finding component objects.
     * @param templateFinder template finder for finding component templates.
     * @param securityHelper security helper for access checking
     * @param resolver the i18n aware template resolver.
     */
    public I18nAwareComponentTool(Context context, MVCClassFinder classFinder,
        MVCTemplateFinder templateFinder, SecurityHelper securityHelper,
        I18nAwareTemplateResolver resolver)
    {
        super(context, classFinder, templateFinder, securityHelper);
        this.resolver = resolver;
    }

    /**
     * {@inheritDoc}
     */	
	protected Template resolveTemplate(Template template)
	{
        Template newTemplate = resolver.resolveTemplate(template);
        if(newTemplate != null)
        {
            return newTemplate;
        }
        return super.resolveTemplate(template);
	}
}
