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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.objectledge.context.Context;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RequestParameters.java,v 1.6 2004-01-12 12:14:58 pablo Exp $
 */
public class RequestParameters extends DefaultParameters
{
    /** The context key under wich the request parameters are stored. */
	public static final String CONTEXT_KEY = "objectledge.web.parameters";

	/**
	 * Usefull method to retrieve parameters from context.
	 *
	 * @param context the context.
	 * @return the request parameters.
	 */
	public static Parameters retrieve(Context context)
	{
		return (Parameters)context.getAttribute(CONTEXT_KEY);
	}
	
    /**
     * Load the parameter container with parameters found in http request.
     *
     * @param request the request
     * @param encoding the encoding
     * @throws IllegalArgumentException if illegal escape sequences appears.
     */
    public void init(HttpServletRequest request, String encoding)
    	throws IllegalArgumentException
    {
        try
        {
            remove();
            Enumeration names = request.getParameterNames();
            while (names.hasMoreElements())
            {
                String name = (String)names.nextElement();
                String[] values = request.getParameterValues(name);
                name = fixEncoding(name, encoding);
                for (int i = 0; i < values.length; i++)
                {
                    add(name, fixEncoding(values[i], encoding));
                }
            }

            if (request.getPathInfo() != null)
            {
                StringTokenizer st = new StringTokenizer(request.getPathInfo(), "/");
                boolean isName = true;
                String name = null;
                String value = null;
                while (st.hasMoreTokens())
                {
                    if (isName)
                    {
                        name = URLDecoder.decode(st.nextToken(), encoding);
                    }
                    else
                    {
                        value = URLDecoder.decode(st.nextToken(), encoding);
                        add(name, value);
                    }
                    isName = !isName;
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalArgumentException("Unsupported encoding exception " + e.getMessage());
        }
    }

    /**
     * Converts the string to different encoding.
     *
     * @param name parameter name
     * @param encoding the encoding
     * @return converted name
     */
    private String fixEncoding(String name, String encoding) throws UnsupportedEncodingException
    {
        String fixed = new String(name.getBytes("ISO-8859-1"), encoding);
        return fixed.trim();
    }
}
