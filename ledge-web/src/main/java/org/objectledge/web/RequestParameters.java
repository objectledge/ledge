package org.objectledge.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.objectledge.parameters.impl.ParametersImpl;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RequestParameters.java,v 1.1 2003-12-02 16:03:18 pablo Exp $
 */
public class RequestParameters extends ParametersImpl
{
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
