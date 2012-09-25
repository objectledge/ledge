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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.objectledge.context.Context;


/**
 * The http context encapsulates the http request and response.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: HttpContext.java,v 1.17 2008-01-03 22:25:55 rafal Exp $
 */
public class HttpContext
{
	/**
	 *  Usefull method to retrieve http context from context.
	 *
	 * @param context the context.
	 * @return the http context.
	 */
	public static HttpContext getHttpContext(Context context)
	{
		return context.getAttribute(HttpContext.class);
	}
	
	/** http request */
	private HttpServletRequest request;
	
	/** http response */
	private HttpServletResponse response;
	
	/** direct response flag */
	private boolean directResponse;
	
	/** response content type */
	private String contentType;
	
    /** the output stream. */
    private OutputStream outputStream;
    
	/** the output writer */
	private PrintWriter writer;

    /**
	 * Construct new http context.
	 * 
	 * @param request the http request.
	 * @param response the http response.
	 */
	public HttpContext(HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
		directResponse = false;
	}
	
    /**
     * Get the servlet request.
     * 
     * @return the http request
     */
    public HttpServletRequest getRequest()
    {
    	return request;
    }
    
    /**
     * Get the servlet response.
     *
     * @return the http response.
     */
	public HttpServletResponse getResponse()
	{
		return response;
	}
	
    /**
     * Sends a temporary redirect response to new location.
     *
     * @param location the redirect location URL.
     * @throws java.io.IOException If an input or output exception occurs.
     */
	public void sendRedirect(String location)
		throws IOException
	{
		directResponse = true;
		response.sendRedirect(location);
	}
	
    /**
     * Wrapping method for writing some data to response output stream.
     *  
     * @return an OutputStream.
     * @throws IOException if happens.
     */
	public OutputStream getOutputStream()
		throws IOException
	{
        if(outputStream == null)
        {
            directResponse = true;
            response.setContentType(getContentType());
            outputStream = response.getOutputStream(); 
        }
		return outputStream;
	}

	/**
	 * Returns an PrintWriter for writing characters into the response.
	 * 
	 * @return a PrintWriter.
	 * @throws IOException if happened.
	 */
	public PrintWriter getPrintWriter()
		throws IOException
	{
		if(writer == null)
		{
			directResponse = true;
			Writer osw = new OutputStreamWriter(getOutputStream(), getEncoding());
			writer = new PrintWriter(osw, false);
		}
		return writer;
	}

    /**
     * Returns the direct response flag.
     *
     * @return the direct response flag.
     */
	public boolean getDirectResponse()
	{
		return directResponse;
	}
	
    /**
     * Returns the content type.
     *
     * @return the content type.
     */
	public String getContentType()
	{
	    if(contentType.startsWith("text/") && contentType.indexOf("charset=") < 0)
	    {
	        return contentType+";charset="+getEncoding();
	    }
		return contentType;
	}

    /**
     * Sets the content type.
     *
     * @param type the content type.
     */
	public void setContentType(String type)
	{
        if(type == null)
        {
            throw new IllegalArgumentException("Content Type cannot be null");
        }
		contentType = type;
	}
	
	/**
	 * Returns the encoding.
	 *
	 * @return the encoding.
	 */
	public String getEncoding()
	{
        String encoding = request.getCharacterEncoding();
        if(encoding == null)
        {
            encoding = "ISO-8859-1";
        }
        return encoding;
	}

	/**
	 * Sets the encoding.
	 *
	 * @param encoding the encoding.
	 */
	public void setEncoding(String encoding)
	{
        try
        {
            request.setCharacterEncoding(encoding);
        }
        ///CLOVER:OFF
        catch (UnsupportedEncodingException e)
        {
            throw (IllegalArgumentException)
                new IllegalArgumentException("Unsupported encoding").initCause(e);
        }
        ///CLOVER:ON
	}

    /**
     * Set the session attribute.
     * 
     * @param key the key of attribute.
     * @param value the attribute value.
     */    
    public void setSessionAttribute(String key, Object value)
    {
        request.getSession(true).setAttribute(key, value);
    }
    
    /**
     * Get the session attribute.
     * 
     * @param key the attribute key.
     * @return the session attribute value of <code>null</code> if not defined.
     */
    public Object getSessionAttribute(String key)
    {
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(key) : null;
    }

    /**
     * Get the session attribute.
     * 
     * @param <T> type of the attribute value.
     * @param key the attribute key
     * @param defaultValue the value to return if not defined.
     * @return session attribute value of defaultValue if session attribute is not defined.
     */
    public <T> T getSessionAttribute(String key, T defaultValue)
    {
        HttpSession session = request.getSession(false);
        if(session != null)
        {
            T sessionValue = (T)session.getAttribute(key);
            return sessionValue != null ? sessionValue : defaultValue;
        }
        else
        {
            return defaultValue;
        }
    }
    
    /**
     * Remove the session attribute.
     * 
     * @param key the attribute key.
     */
    public void removeSessionAttribute(String key)
    {
        HttpSession session = request.getSession(false);
        if(session != null)
        {
            session.setAttribute(key, null);
        }
    }
    
    /**
     * Removes all session attributes.
     */
    public void clearSessionAttributes()
    {
        HttpSession session = request.getSession(false);
        if(session != null)
        {
            Enumeration<String> attrNames = session.getAttributeNames();
            ArrayList<String> temp = new ArrayList<String>();
            while(attrNames.hasMoreElements())
            {
                String name = attrNames.nextElement();
                temp.add(name);
            }
            for(int i = 0; i < temp.size(); i++)
            {
                String name = temp.get(i);
                session.removeAttribute(name);
            }
        }
    }

    /**
     * Sets the Content-Length header.
     * 
     * @param length the length of the response body.
     */
    public void setResponseLength(int length)
    {
        response.addIntHeader("Content-Length", length); 
    }
    
    /**
     * Add response headers that disable caching in the browser.
     */
    public void disableCache()
    {
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-control",
            "no-cache, no-store, must-revalidate, max_age=0, post-check=0, pre-check=0");
        response.addHeader("Expires", "Mon, 12 May 1998 00:00:00 GMT");
    }
}
