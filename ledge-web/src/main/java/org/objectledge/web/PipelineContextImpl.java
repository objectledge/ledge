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

package org.objectledge.web;

import java.security.Principal;
import java.util.Locale;

import org.objectledge.context.Context;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;

/**
 * The web context contains all needed information about http request.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PipelineContextImpl.java,v 1.2 2003-12-29 13:09:29 pablo Exp $
 */
public class PipelineContextImpl implements PipelineContext
{
	/**
	 *  Usefull method to retrieve http context from context.
	 *
	 * @param context the context.
	 * @return the http context.
	 */
	public static PipelineContext retrieve(Context context)
	{
		return (PipelineContext)context.getAttribute(CONTEXT_KEY);
	}

	/** The screen template. */
	private Template screenTemplate;

	/** The Action for this request */
	//protected Action action;

	/** the locale */
	private Locale locale;

	/** the encoding */
	private String encoding;

	/** the media */
	private String media;

    /** the user. */
	private Principal user;

    /** is the user authenticated */
	private boolean authenticated;

	/** The name of the view paramter. */
	private String viewToken;
	 
	/**
	 * Construct new pipeline context.
	 */
	public PipelineContextImpl()
	{
		user = null;
		authenticated = false;
		// TODO load some default values - but from?
		locale = new Locale("pl","PL");
		encoding = "ISO-8859-2";
		viewToken = "Default";
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Template getScreenTemplate()
	{
		return screenTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScreenTemplate(Template template)
	{
		screenTemplate = template;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Locale getLocale()
	{
		return locale;
	}

    /**
     * {@inheritDoc}
     */
    public void setLocale(Locale locale)
    {
    	this.locale = locale;
    }

	/**
	 * {@inheritDoc}
	 */
    public String getEncoding()
	{
		return encoding;
	}

    /**
     * {@inheritDoc}
     */
    public void setEncoding(String encoding)
    {
    	this.encoding = encoding;
    }
	
	/**
	 * {@inheritDoc}
	 */
    public String getMedia()
    {
    	return media;
    }

	/**
	 * {@inheritDoc}
	 */
    public void setMedia(String media)
    {
    	this.media = media;
    }

	/**
	 * {@inheritDoc}
	 */
    public Principal getUserPrincipal()
    {
    	return user;
    }

	/**
	 * {@inheritDoc}
	 */
    public boolean isUserAuthenticated()
    {
    	return authenticated;
    }

	/**
	 * {@inheritDoc}
	 */
    public void setUserPrincipal(Principal user, boolean authenticated)
    {
    	this.user = user;
    	this.authenticated = authenticated;
    }
}
