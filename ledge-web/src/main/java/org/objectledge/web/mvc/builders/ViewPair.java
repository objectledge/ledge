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

import org.objectledge.templating.Template;

/**
 * Contains a pair of view construction elements - a builder and template.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ViewPair.java,v 1.2 2004-01-15 10:30:31 fil Exp $
 */
public class ViewPair
{
	/** Builder bound by this pair. */
	private Builder builder;
	
	/** Template bound by this pair. */
	private Template template;
	
	/**
	 * Constructs a pair of view construction elements.
	 * 
     * @param builder builder bound by this pair - may be <code>null</code>.
     * @param template template bound by this pair - may be <code>null</code>.
     */
    public ViewPair(Builder builder, Template template)
	{
		this.builder = builder;
		this.template = template;
	}
	
    /**
     * Returns builder bound by this pair - may be <code>null</code>.
     * 
     * @return builder bound by this pair.
     */
    public Builder getBuilder()
    {
        return builder;
    }

    /**
     * Returns template bound by this pair - may be <code>null</code>.
     * 
     * @return template bound by this pair.
     */
    public Template getTemplate()
    {
        return template;
    }
}
