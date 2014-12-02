package org.objectledge.web.rewrite;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class RewriteInfoBuilder
{
    private final HttpServletRequest request;

    private String servletPath;

    private String pathInfo;

    private List<String[]> qsParams; // list of pairs

    private Map<String, List<String>> params;

    private RewriteInfoBuilder(final HttpServletRequest request)
    {
        this.request = request;
        this.servletPath = request.getServletPath();
        this.pathInfo = request.getPathInfo();
        final String characterEncoding = request.getCharacterEncoding();
        this.qsParams = parseQueryString(request.getQueryString(),
            characterEncoding != null ? characterEncoding : "UTF-8");
        this.params = parseParameterMap(request.getParameterMap());
    }

    private static List<String[]> parseQueryString(final String params, final String encoding)
    {
        try
        {
            final List<String[]> qsParams = new ArrayList<>();
            if(params != null)
            {
                int state = 0; // 0 - param name, 1 - param value
                final StringBuilder b = new StringBuilder();
                String[] pair = new String[2];
                for(char c : params.toCharArray())
                {
                    if(c == '=')
                    {
                        if(state == 0)
                        {
                            pair[0] = URLDecoder.decode(b.toString(), encoding);
                            b.setLength(0);
                            state = 1;
                        }
                        else
                        {
                            b.append(c);
                        }
                    }
                    else if(c == '&')
                    {
                        if(state == 1)
                        {
                            pair[1] = URLDecoder.decode(b.toString(), encoding);
                            b.setLength(0);
                        }
                        qsParams.add(pair);
                        pair = new String[2];
                        state = 0;
                    }
                    else
                    {
                        b.append(c);
                    }
                }
                if(state == 1)
                {
                    pair[1] = URLDecoder.decode(b.toString(), encoding);
                    qsParams.add(pair);
                }
            }
            return qsParams;
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<String>> parseParameterMap(final Map<?, ?> parameters)
    {
        final Map<String, List<String>> map = new LinkedHashMap<>();
        for(Map.Entry<String, String[]> entry : ((Map<String, String[]>)parameters).entrySet())
        {
            map.put(entry.getKey(), new ArrayList<String>(Arrays.asList(entry.getValue())));
        }
        return map;
    }

    private static String formatQueryString(final List<String[]> queryString,
        final String characterEncoding)
    {
        final String encoding = characterEncoding != null ? characterEncoding : "UTF-8";
        try
        {
            final StringBuilder b = new StringBuilder();
            final Iterator<String[]> i = queryString.iterator();
            while(i.hasNext())
            {
                final String[] pair = i.next();
                b.append(URLEncoder.encode(pair[0], encoding));
                b.append('=');
                if(pair[1] != null)
                {
                    b.append(URLEncoder.encode(pair[1], encoding));
                }
                if(i.hasNext())
                {
                    b.append('&');
                }
            }
            if(b.length() == 0)
            {
                return null;
            }
            else
            {
                return b.toString();
            }
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String[]> formatParameterMap(
        final Map<String, List<String>> parameters)
    {
        return Maps.transformValues(parameters, new Function<List<String>, String[]>()
            {
                public String[] apply(List<String> input)
                {
                    return input.toArray(new String[input.size()]);
                }
            });
    }

    private static String formatRequestURL(final HttpServletRequest request, String servletPath,
        final String pathInfo)
    {
        final StringBuilder b = new StringBuilder();
        b.append(request.isSecure() ? "https" : "http");
        b.append("://");
        final String host = request.getHeader("Host");
        if(host != null)
        {
            b.append(host);
        }
        else
        {
            b.append(request.getLocalName());
        }
        if(host == null || !host.contains(":"))
        {
            if((!request.isSecure() && request.getLocalPort() != 80)
                || (request.isSecure() && request.getLocalPort() != 443))
            {
                b.append(':').append(request.getLocalPort());
            }
        }
        b.append(request.getContextPath());
        b.append(servletPath);
        if(pathInfo != null)
        {
            b.append(pathInfo);
        }
        return b.toString();
    }

    private RewriteInfoBuilder(final RewriteInfo info)
    {
        this.request = info.getRequest();
        this.servletPath = info.getServletPath();
        this.pathInfo = info.getPathInfo();
        this.qsParams = info.getQueryParameters();
        this.params = info.getParameters();
    }

    public static RewriteInfoBuilder fromRequest(final HttpServletRequest request)
    {
        return new RewriteInfoBuilder(request);
    }

    public static RewriteInfoBuilder fromRewriteInfo(final RewriteInfo info)
    {
        return new RewriteInfoBuilder(info);
    }

    public RewriteInfoBuilder withServletPath(final String servletPath)
    {
        this.servletPath = servletPath;
        return this;
    }

    public RewriteInfoBuilder withPathInfo(final String pathInfo)
    {
        this.pathInfo = pathInfo;
        return this;
    }

    public RewriteInfoBuilder withFormParameters(final Map<String, List<String>> params)
    {
        this.params = params;
        return this;
    }

    public RewriteInfoBuilder withFormParameter(final String name, final String... values)
    {
        params.put(name, Arrays.asList(values));
        return this;
    }

    public RewriteInfoBuilder withFormParameter(final String name, final List<String> values)
    {
        params.put(name, values);
        return this;
    }

    public RewriteInfoBuilder withFormParameterValue(final String name, final String value)
    {
        final List<String> values = params.get(name);
        if(values == null)
        {
            final ArrayList<String> valueList = new ArrayList<>();
            valueList.add(value);
            params.put(name, valueList);
        }
        else
        {
            values.add(value);
        }
        return this;
    }

    public RewriteInfoBuilder withQueryParameters(final List<String[]> qsParams)
    {
        this.qsParams = qsParams;
        return this;
    }

    public RewriteInfoBuilder withQueryParameter(final String name, final String... values)
    {
        return withQueryParameter(name, Arrays.asList(values));
    }

    public RewriteInfoBuilder withQueryParameter(final String name, final List<String> values)
    {
        final Iterator<String[]> i = qsParams.iterator();
        while(i.hasNext())
        {
            final String[] pair = i.next();
            if(pair[0].equals("name"))
            {
                i.remove();
            }
        }
        for(String value : values)
        {
            final String[] pair = new String[2];
            pair[0] = name;
            pair[1] = value;
            qsParams.add(pair);
        }
        return this;
    }

    public RewriteInfoBuilder withQueryParameterValue(final String name, final String value)
    {
        final String[] pair = new String[2];
        pair[0] = name;
        pair[1] = value;
        qsParams.add(pair);
        return this;
    }

    public RewriteInfo build()
    {
        return new RewriteInfoWrapper();
    }

    private class RewriteInfoWrapper
        implements RewriteInfo
    {
        private HttpServletRequest wrappedRequest = new RequestWrapper();

        @Override
        public HttpServletRequest getRequest()
        {
            return wrappedRequest;
        }

        @Override
        public String getServletPath()
        {
            return servletPath;
        }

        @Override
        public String getPathInfo()
        {
            return pathInfo;
        }

        @Override
        public List<String[]> getQueryParameters()
        {
            return qsParams;
        }

        @Override
        public Map<String, List<String>> getParameters()
        {
            return params;
        }
    }

    private class RequestWrapper
        extends HttpServletRequestWrapper
    {
        private final String queryString;

        private final Map<String, String[]> parameterMap;

        private final String requestURL;

        private final String requestURI;

        public RequestWrapper()
        {
            super(request);
            this.queryString = formatQueryString(qsParams, request.getCharacterEncoding());
            this.parameterMap = formatParameterMap(params);
            this.requestURL = formatRequestURL(request, servletPath, pathInfo);
            this.requestURI = requestURL + (queryString == null ? "" : "?" + queryString);
        }

        @Override
        public String getPathInfo()
        {
            return pathInfo;
        }

        @Override
        public String getQueryString()
        {
            return queryString;
        }

        @Override
        public String getRequestURI()
        {
            return requestURI;
        }

        @Override
        public StringBuffer getRequestURL()
        {
            return new StringBuffer(requestURL);
        }

        @Override
        public String getServletPath()
        {
            return servletPath;
        }

        @Override
        public String getParameter(final String name)
        {
            final List<String> values = params.get(name);
            if(values != null)
            {
                return values.get(0);
            }
            return null;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Map getParameterMap()
        {
            return parameterMap;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Enumeration getParameterNames()
        {
            final Iterator<String> i = parameterMap.keySet().iterator();
            return new Enumeration()
                {
                    @Override
                    public boolean hasMoreElements()
                    {
                        return i.hasNext();
                    }

                    @Override
                    public Object nextElement()
                    {
                        return i.next();
                    }
                };
        }

        @Override
        public String[] getParameterValues(String name)
        {
            return parameterMap.get(name);
        }
    }
}
