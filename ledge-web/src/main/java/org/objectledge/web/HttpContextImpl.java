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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.objectledge.context.Context;


/**
 * The web context contains all needed information about http request.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: HttpContextImpl.java,v 1.2 2003-12-23 23:40:23 pablo Exp $
 */
public class HttpContextImpl implements HttpContext
{
	/**
	 *  Usefull method to retrieve http context from context.
	 *
	 * @param context the context.
	 * @return the http context.
	 */
	public static HttpContext retrieve(Context context)
	{
		return (HttpContext)context.getAttribute(CONTEXT_KEY);
	}
	
	/** http request */
	private HttpServletRequest request;
	
	/** http response */
	private HttpServletResponse response;
	
	/** direct response flag */
	private boolean directResponse;
	
	/** response content type */
	private String contentType;
	
	/**
	 * Construct new http context.
	 * 
	 * @param request the http request.
	 * @param response the http response.
	 */
	public HttpContextImpl(HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
		directResponse = false;
		contentType = request.getContentType();
	}
	
	/**
     * {@inheritDoc}
     */
    public HttpServletRequest getRequest()
    {
    	return request;
    }
    
	/**
	 * {@inheritDoc}
	 */
	public HttpServletResponse getResponse()
	{
		return response;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendRedirect(String location)
		throws IOException
	{
		directResponse = true;
		response.sendRedirect(location);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public OutputStream getOutputStream()
		throws IOException
	{
		directResponse = true;
		response.setContentType(getContentType());
		return response.getOutputStream();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDirectResponse()
	{
		directResponse = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getDirectResponse()
	{
		return directResponse;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setContentType(String type)
	{
		contentType = type;
	}
}
