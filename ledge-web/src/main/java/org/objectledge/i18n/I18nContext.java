// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.i18n;

import java.util.Locale;

import org.objectledge.context.Context;

/**
 * The web context contains all needed information about mvc processing parameters.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: I18nContext.java,v 1.1 2004-06-29 13:40:12 zwierzem Exp $
 */
public class I18nContext
{
	/**
	 *  Usefull method to retrieve http context from context.
	 *
	 * @param context the context.
	 * @return the http context.
	 */
	public static I18nContext getI18nContext(Context context)
	{
		return (I18nContext)context.getAttribute(I18nContext.class);
	}

	/** the locale */
	private Locale locale;

	/**
	 * Construct new pipeline context.
	 */
	public I18nContext()
	{
		locale = null;
	}
	
    /**
     * Returns the locale.
     *
     * @return the locale
     */
	public Locale getLocale()
	{
		return locale;
	}

    /**
     * Sets the locale.
     *
     * @param locale the locale.
     */
    public void setLocale(Locale locale)
    {
    	this.locale = locale;
    }
}
