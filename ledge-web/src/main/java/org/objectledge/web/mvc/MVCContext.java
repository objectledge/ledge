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

package org.objectledge.web.mvc;

import java.security.Principal;
import java.util.Locale;

/**
 * The pieline context contains all needed information for pipeline processing.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCContext.java,v 1.2 2003-12-30 17:26:25 zwierzem Exp $
 */
public interface MVCContext
{
	/** key to store this context in thread context */
	public static final String CONTEXT_KEY = "objectledge.web.pipeline_context";
	
	/**
	 * Returns the action paremeter.
	 * 
	 * @return the value of action parameter.
	 */
	public String getAction();

	/**
	 * Sets the action parameter.
	 *
	 * @param action the action parameter.
	 */
	public void setAction(String action);

	/**
	 * Returns the view paremeter.
	 *
	 * @return the value of view parameter.
	 */
	public String getView();
	
	/**
	 * Sets the view parameter.
	 *
	 * @param view the view parameter.
	 */
	public void setView(String view);
	
	/**
	 * Returns the locale.
	 *
	 * @return the locale
	 */
	public Locale getLocale();

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale.
	 */
	public void setLocale(Locale locale);

	/**
	 * Returns the encoding.
	 *
	 * @return the encoding.
	 */
	public String getEncoding();

	/**
	 * Sets the encoding.
	 *
	 * @param encoding the encoding.
	 */
	public void setEncoding(String encoding);
	
	/**
	 * Returns the media.
	 *
	 * @return the media.
	 */
	public String getMedia();

	/**
	 * Sets the media.
	 *
	 * @param media the media.
	 */
	public void setMedia(String media);

	/**
	 * Returns the user performing the request.
	 *
	 * @return the user.
	 */
	public Principal getUserPrincipal();

	/**
	 * Checks whether user is authenticated by system. 
	 * 
	 * @return <code>true</code> if the current user is not an anounymous.
	 */
	public boolean isUserAuthenticated();

	/**
	 * Sets the current authenticated user.
	 *
	 * @param user the current authenticated user.
	 * @param authenticated <code>true</code> if named user is authenticated.
	 */
	public void setUserPrincipal(Principal user, boolean authenticated);

	/**
	 * Gets the result of building the view part of MVC pipeline.
	 * 
	 * @return the result of building the MVC view
	 */
	public String getBuildResult();

    /**
     * Sets the result of building the view part of MVC pipeline.
     * 
     * @param buildResult a string representing built view which should be sent to the browser
     */
    public void setBuildResult(String buildResult);
}
