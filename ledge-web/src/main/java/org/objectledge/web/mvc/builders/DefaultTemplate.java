// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.mvc.builders;

import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.MVCConstants;

/**
 * A default template which behaves as if it only contained <code>$embeddedPlaceholder</code>.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DefaultTemplate.java,v 1.6 2004-08-02 13:59:03 zwierzem Exp $
 */
public class DefaultTemplate implements Template
{    
    /**
     * {@inheritDoc}
     */
    ///CLOVER:OFF
    public String getName()
    {
        throw new IllegalStateException("should never be called");
    }
    ///CLOVER:ON

    /**
     * Merges default template. 
     * 
     * <p> This method returns string stored in templating context under
     * @link{MVCConstants.EMBEDDED_PLACEHOLDER_KEY} key.</p>
     * 
     * @param context used templating context
     * @return results of merging
     * @throws MergingException if errors occur while merging 
     */
    public String merge(TemplatingContext context) throws MergingException
    {
        return (String)(context.get(MVCConstants.EMBEDDED_PLACEHOLDER_KEY));
    }
}
