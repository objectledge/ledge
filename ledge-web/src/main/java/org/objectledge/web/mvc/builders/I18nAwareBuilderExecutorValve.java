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
import org.objectledge.i18n.I18nAwareTemplateResolver;
import org.objectledge.templating.Template;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: I18nAwareBuilderExecutorValve.java,v 1.8 2004-08-19 13:40:05 pablo Exp $
 */
public class I18nAwareBuilderExecutorValve extends BuilderExecutorValve
{
	/** i18n aware template resolver */
    private I18nAwareTemplateResolver resolver;
    
    /**
     * {@inheritDoc}
     * 
     * @param context the context.
     * @param classFinder the class finder.
     * @param templateFinder the template finder.
     * @param maxRouteCalls the max route calls.
     * @param maxEnclosures the max enclosures. 
     * @param i18n an i18n component for choosing templates upon currently selected Locale.
     * @param templating the templating component.
     */
    public I18nAwareBuilderExecutorValve(
        Context context,
        MVCClassFinder classFinder,
        MVCTemplateFinder templateFinder,
        int maxRouteCalls,
        int maxEnclosures,
        I18nAwareTemplateResolver resolver)
    {
        super(context, classFinder, templateFinder, maxRouteCalls, maxEnclosures);
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
