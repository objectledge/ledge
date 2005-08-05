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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.objectledge.context.Context;
import org.objectledge.web.mvc.tools.LinkTool;

/**
 * Request parameters contain parameters from the request sorted by their names.
 *
 * <p>TODO: figure out a way to discover URL parameters encoding, now UTF-8 is used, but
 * browsers encode GET form parameters using form page encoding thus rendering UTF-8 decoding
 * useless. Problems:</p>
 * <ul>
 * <li>browsers encode URLs using UTF-8 by default (URLs typed in th location bar)</li>
 * <li>browsers do not send any information on parameters encoding</li>
 * </ul>
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RequestParameters.java,v 1.17 2005-08-05 12:48:27 rafal Exp $
 */
public class RequestParameters extends SortedParameters
{
    private Set<String> pathParams = new HashSet<String>();
    private Set<String> queryParams = new HashSet<String>();
    private Set<String> postParams = new HashSet<String>();    
    
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
     * No arg constructor for mocking.
     */
    protected RequestParameters()
    {
        // intentionally left blank
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
        queryParams.addAll(queryStringParams.keySet());
        
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
        postParams.addAll(map.keySet());
        for(String name : map.keySet())
        {
            if(queryStringParams.containsKey(name)
                && ((String[])queryStringParams.get(name)).length == getStrings(name).length)
            {
                postParams.remove(name);
            }
        }

        // get path info parameters
        addURLParams(request.getPathInfo(), "/");
        pathParams.addAll(map.keySet());
        pathParams.removeAll(queryParams);
        pathParams.removeAll(postParams);        

        // speed up sorted parameters retrieval
        Map tmp = new LinkedHashMap(map);
        map = tmp;
    }
    
    private static final int START = 0;
    private static final int NAME = 1;
    private static final int SEPARATOR_AFTER_NAME = 2;
    private static final int VALUE = 3;
    private static final int SEPARATOR_AFTER_VALUE = 4;
    
    private void addURLParams(String urlPart, String separator)
    {       
        if (urlPart == null)
        {
            return;
        }
        
        try
        {
            StringTokenizer st = new StringTokenizer(urlPart, separator, true);
            int state = START;
            String name = null;
            while (st.hasMoreTokens())
            {
                String token = st.nextToken();
                // separators
                if(token.length() == 1 && separator.indexOf(token.charAt(0)) > -1 )
                {
                    switch(state)
                    {
                        case START:
                            state = NAME;
                            break;
                        case SEPARATOR_AFTER_NAME:
                            state = VALUE;
                            break;
                        case SEPARATOR_AFTER_VALUE:
                            state = NAME;
                            break;
                        case VALUE:
                            add(name, "");
                            name = null;
                            state = NAME;
                            break;
                        case NAME:
                            throw new IllegalStateException("empty parameter name");
                        default:
                            throw new IllegalStateException(
                                "illegal state while parsing params");
                    }
                }
                // names and values
                else
                {
                    switch(state)
                    {
                        case START:
                        case NAME:
                            name = URLDecoder.decode(token, LinkTool.PARAMETER_ENCODING);
                            state = SEPARATOR_AFTER_NAME;
                            break;
                        case VALUE:
                            add(name, URLDecoder.decode(token, LinkTool.PARAMETER_ENCODING));
                            name = null;
                            state = SEPARATOR_AFTER_VALUE;
                            break;
                        default:
                            break;  
                    }
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
    
    /**
     * Checks if the parameter was passed in through request path info.
     * 
     * @param name name of the parameter.
     * @return <code>true</code> if the parameter was passed in through path info.
     */
    public boolean isPathInfoParameter(String name)
    {
        return pathParams.contains(name);
    }

    /**
     * Checks if the parameter was passed in through request query string.
     * 
     * @param name name of the parameter.
     * @return <code>true</code> if the parameter was passed in through request query string.
     */
    public boolean isQueryStringParameter(String name)
    {
        return queryParams.contains(name);
    }
    
    /**
     * Checks if the parameter was passed in through POST request body.
     * 
     * @param name name of the parameter.
     * @return <code>true</code> if the parameter was passed through POST request body.
     */
    public boolean isPOSTParameter(String name)
    {
        return postParams.contains(name);
    }
    
    /**
     * To be used by FileUploadValve, that handles multipart/form-data POSTs.
     * 
     * @param name of the parameter.
     * @param value value of the parameter.
     */
    public void addPOSTParameter(String name, String value)
    {
        super.add(name, value);
        postParams.add(name);
    }
}
