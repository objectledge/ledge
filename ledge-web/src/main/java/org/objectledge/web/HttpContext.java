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


/**
 * The web context contains all needed information about http request.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: HttpContext.java,v 1.2 2003-12-23 23:40:23 pablo Exp $
 */
public interface HttpContext
{
	/** the key that points the http context is thread context. */ 
	public static final String CONTEXT_KEY = "objectledge.web.http_context";
	
	/**
     * Get the servlet request.
     * 
     * @return the http request
     */
    public HttpServletRequest getRequest();
    
	/**
	 * Get the servlet response.
	 *
	 * @return the http response.
	 */
	public HttpServletResponse getResponse();
	
	/**
	 * Sends a temporary redirect response to new location
	 *
	 * @param location the redirect location URL.
	 * @throws java.io.IOException If an input or output exception occurs.
	 */
	public void sendRedirect(String location)
		throws IOException;
		
	/**
	 * Wrapping method for writing some data to response output stream.
	 *  
	 * @return an OutputStream.
	 * @throws IOException if happens.
	 */
	public OutputStream getOutputStream()
		throws IOException;

	/**
	 * Sets the direct response flag.
	 */
	public void setDirectResponse();

	/**
	 * Returns the direct response flag.
	 *
	 * @return the direct response flag.
	 */
	public boolean getDirectResponse();
	
	/**
	 * Returns the content type.
	 *
	 * @return the content type.
	 */
	public String getContentType();

	/**
	 * Sets the content type.
	 *
	 * @param type the content type.
	 */
	public void setContentType(String type);
}
