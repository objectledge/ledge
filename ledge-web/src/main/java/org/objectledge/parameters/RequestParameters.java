// 
// Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

package org.objectledge.parameters;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.objectledge.context.Context;
import org.objectledge.web.mvc.tools.LinkTool;

/**
 * Request parameters contain parameters from the request sorted by their names.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RequestParameters.java,v 1.11 2004-08-10 10:17:31 zwierzem Exp $
 */
public class RequestParameters extends SortedParameters
{
	/**
	 * Usefull method to retrieve parameters from context.
	 *
	 * @param context the context.
	 * @return the request parameters.
	 */
	public static RequestParameters getRequestParameters(Context context)
	{
		return (RequestParameters)context.getAttribute(RequestParameters.class);
	}
	
    /**
     * Create the parameter container with parameters found in http request.
     * 
     * @param request the request
     * @throws IllegalArgumentException if illegal escape sequences appears.
     */
    public RequestParameters(HttpServletRequest request)
    	throws IllegalArgumentException
    {
        super();
        
        // get query string parameters 
        addURLParams(request.getQueryString(), "&=");

        // copy querystring params to extract only post params from request params parsed by the
        // servlet container
        HashMap queryStringParams = new HashMap();
        queryStringParams.putAll(this.map);
        
        // post parameters
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements())
        {
            String name = (String)names.nextElement();
            String[] values = request.getParameterValues(name);
            // avoid duplicate parameters from queryString
            int start = 0;
            if(queryStringParams.containsKey(name))
            {
                start = ((String[])(queryStringParams.get(name))).length;
            }
            for (int i = start; i < values.length; i++)
            {
                add(name, values[i]);
            }
        }

        // get path info parameters
        addURLParams(request.getPathInfo(), "/");

        // speed up sorted parameters retrieval
        Map tmp = new LinkedHashMap(map);
        map = tmp;
    }
    
    private void addURLParams(String urlPart, String separator)
    {
        try
        {
            if (urlPart != null)
            {
                StringTokenizer st = new StringTokenizer(urlPart, separator);
                boolean isName = true;
                String name = null;
                String value = null;
                while (st.hasMoreTokens())
                {
                    if (isName)
                    {
                        name = URLDecoder.decode(st.nextToken(), LinkTool.PARAMETER_ENCODING);
                    }
                    else
                    {
                        value = URLDecoder.decode(st.nextToken(), LinkTool.PARAMETER_ENCODING);
                        add(name, value);
                    }
                    isName = !isName;
                }
            }
        }
        ///CLOVER:OFF
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalArgumentException("Unsupported encoding exception " + e.getMessage());
        }
        ///CLOVER:ON
    }
}
