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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.objectledge.i18n.I18n;
import org.objectledge.i18n.I18nContext;

/**
 * A view sequence that appends localization dependent suffixes.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: I18nViewLookupSequence.java,v 1.1 2005-02-21 17:47:49 rafal Exp $
 */
public class I18nViewLookupSequence
    implements Sequence
{
    private final Sequence viewLookupSequence;
    private final List<String> suffices = new ArrayList<String>(3);
    private Iterator<String> iterator;
    private String lastPrefix;
    private final StringBuilder buff = new StringBuilder();

    /**
     * Creates new LocalizedViewLookupSequence instance.
     * 
     * @param viewLookupSequence the view lookup sequence.
     * @param i18n the I18n component, for determining default locale.
     * @param i18nContext the I18n context for determining requested locale.
     */
    public I18nViewLookupSequence(Sequence viewLookupSequence, I18n i18n, 
        I18nContext i18nContext)
    {
        this.viewLookupSequence = viewLookupSequence;
        suffices.add(i18nContext.getLocale().toString());
        suffices.add(i18n.getDefaultLocale().toString());
        suffices.add("");
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasNext()
    {
        return viewLookupSequence.hasNext() || iterator.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public String next()
        throws NoSuchElementException
    {
        if(lastPrefix == null || !iterator.hasNext())
        {
            lastPrefix = viewLookupSequence.next();
            iterator = suffices.iterator();
        }
        String suffix = iterator.next();
        buff.setLength(0);
        buff.append(lastPrefix);
        if(suffix.length() > 0)
        {
            buff.append('.').append(suffix);
        }
        return buff.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void reset()
    {
        viewLookupSequence.reset();
        iterator = suffices.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public String currentView()
    {
        return viewLookupSequence.currentView();
    }
}
