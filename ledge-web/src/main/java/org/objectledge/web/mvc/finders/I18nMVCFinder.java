// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.mvc.finders;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.i18n.I18n;
import org.objectledge.i18n.I18nContext;
import org.objectledge.templating.Templating;
import org.picocontainer.MutablePicoContainer;

/**
 * An implemention of MVCFinder that looks up localized view templates.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: I18nMVCFinder.java,v 1.1 2005-02-21 17:47:49 rafal Exp $
 */
public class I18nMVCFinder
    extends MVCFinder
{
    private final Context context;
    private final I18n i18n;

    /**
     * Creates new I18nMVCFinder instance.
     * 
     * @param container the container of UI components.
     * @param logger the logger to use.
     * @param templating the Templating component.
     * @param nameSequenceFactory the sequence factory component.
     * @param context the application context.
     * @param i18n the I18n component.
     */
    public I18nMVCFinder(MutablePicoContainer container, Logger logger, Templating templating,
        NameSequenceFactory nameSequenceFactory, Context context, I18n i18n)
    {
        super(container, logger, templating, nameSequenceFactory);
        this.context = context;
        this.i18n = i18n;
    }

    /**
     * {@inheritDoc}
     */
    protected Sequence getTemplateNameSequence(String kind, String view, boolean fallback,
        boolean enclosing)
    {
        Sequence viewLookupSequence = super
            .getTemplateNameSequence(kind, view, fallback, enclosing);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        return new I18nViewLookupSequence(viewLookupSequence, i18n, i18nContext);
    }
}
