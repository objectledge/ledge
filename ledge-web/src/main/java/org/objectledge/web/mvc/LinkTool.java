// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.web.mvc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.objectledge.context.Context;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.web.HttpContext;
import org.objectledge.web.HttpContextImpl;
import org.objectledge.web.parameters.RequestParameters;

/**
 * Context tool used to build web application links.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class LinkTool
{
	/** utf encoding */
	public static final String UTF_8 = "UTF-8";
	
	/** the thread context */
	private Context context;
	
	/** the configurator component */
	private MVCConfigurator config;
	
	/** the mvc context */
	private MVCContext mvcContext;
	
	/** the http context */
	private HttpContext httpContext;

	/** the request parameters */
	private Parameters requestParameters;
	
	/** view */
	private String view;
	
	/** the action */
	private String action;
	
	/** is resource link switch */
	private boolean resourceLink;
	
	/** include session information */
	private boolean includeSession;
	
	/** show the protocol in link */
	private boolean showProtocolName;
	
	/** protocol name */
	private String protocolName;
	
	/** the port */
	private int port;
	
	/** the resource path */
	private String path;
	
	/** the fragment of the url */
	private String fragment;
	
	/** the parameters */
	private Parameters parameters;
	
	/** the string buffer */
	private StringBuffer sb;
	
	/** 
	 * Component constructor.
	 * 
	 * @param context the thread context.
	 * @param config the configurator.
	 */
	public LinkTool(Context context, MVCConfigurator config)
	{
		this.context = context;
		this.config = config;
		mvcContext = MVCContextImpl.retrieve(context);
		httpContext = HttpContextImpl.retrieve(context);
		requestParameters = RequestParameters.retrieve(context);
		resourceLink = false;
		includeSession = true;
		showProtocolName = false;
		protocolName = ""; 
		view = null;
		action = "";
		parameters = new DefaultParameters();
		fragment = null;
		Set sticky = config.getStickyKeys();
		if(sticky != null)
		{
			parameters = new DefaultParameters(requestParameters);
		    parameters.removeExcept(sticky);
		}
		sb = new StringBuffer();
	}
	
	/**
     * Set the protocol to http with default port on 80.
     * 
     * @return the link tool.
     */
	public LinkTool http()
	{
		return http(80);
	}

	/**
	 * Set the protocol to http with specified port.
	 * 
	 * @param port the port.
	 * @return the link tool.
	 */
    public LinkTool http(int port)
	{
	    LinkTool target = (LinkTool)clone();
	    target.showProtocolName = true;
		target.protocolName = "http";
		target.port = port;
		return target;
	}

	/**
	 * Set the protocol to https with default port on 443.
	 * 
	 * @return the link tool.
	 */
	public LinkTool https()
	{
		return https(443);
	}

	/**
	 * Set the protocol to https with specified port.
	 * 
	 * @param port the port.
	 * @return the link tool.
	 */
	public LinkTool https(int port)
	{
		LinkTool target = (LinkTool)clone();
		target.showProtocolName = true;
		target.protocolName = "https";
		target.port = port;
		return target;
	}

    /**
     * Generate an absolute link including schema server, name and port.
     * 
     * @return the link tool.
     */
	public LinkTool absolute()
	{
	    LinkTool target = (LinkTool)clone();
		target.showProtocolName = true;
		target.port = httpContext.getRequest().getServerPort();
		if(httpContext.getRequest().isSecure())
		{
			target.protocolName = "https";
		}
		else
		{
			target.protocolName = "http";
		}
		return target;
    }

    /**
     * Avoid session information in link.
     *
     * @return the link tool. 
     */
    public LinkTool sessionless()
    {
        LinkTool target = (LinkTool)clone();
        target.includeSession = false;
        return target;
    }

    /**
     * Set link to point to the external resource.
     *
     * @param path the path to static resource.
     * @return the link tool.
     */
    public LinkTool resource(String path)
    {
        return resource(path, true);
    }

    /**
     * Set link to point to the resource.
     *
     * @param path the path to static resource.
     * @param external <code>true</code> if resource is served by external server.
     * @return the link tool.
     */
    public LinkTool resource(String path, boolean external)
    {
        LinkTool target = (LinkTool)clone();
        target.resourceLink = true;
        target.includeSession = !external;
        target.path = path;
        return target;
    }

    /**
     * Sets a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool set(String name, String value)
    {
        LinkTool target = (LinkTool)clone();
        if (name.equals(config.getViewToken()))
        {
            target.view = value;
        }
        else if (name.equals(config.getActionToken()))
        {
            target.action = value;
        }
        else
        {
            target.parameters.set(name, value);
        }
        return target;
    }

    /**
     * Sets a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool. 
     */
    public LinkTool set(String name, int value)
    {
		LinkTool target = (LinkTool)clone();
		target.parameters.set(name, value);
		return target;
    }

    /**
     * Sets a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool set(String name, long value)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.set(name, value);
        return target;
    }

    /**
     * Sets a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool set(String name, float value)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.set(name, value);
        return target;
    }

    /**
     * Sets a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool set(String name, boolean value)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.set(name, value);
        return target;
    }

    /**
     * Sets the request parameters to be equal to contents of the specified
     * parameter container.
     *
     * @param parameters a set of paramters
     * @return the link tool.
     */
    public LinkTool set(Parameters parameters)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters = new DefaultParameters(parameters);
        target.parameters.remove(config.getViewToken());
        target.parameters.remove(config.getActionToken());
        return target;
    }

    /**
     * Sets the fragment for this link.
     * 
     * @param fragment the fragment.
     * @return the link tool.
     */
    public LinkTool fragment(String fragment)
    {
        LinkTool target = (LinkTool)clone();
        target.fragment = fragment;
        return target;
    }

    /**
     * Returns the fragment for this link.
     * 
     * @return the fragment in the current link
     */
    public String fragment()
    {
        return fragment;
    }

    /**
     * Sets the parameters to be equal to the paremeters of the
     * current request.
     * 
     * @return the link tool.
     */
    public LinkTool self()
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.remove(config.getStickyKeys());
        target.parameters.add(requestParameters, true);
        target.parameters.remove(config.getViewToken());
        target.parameters.remove(config.getActionToken());
        String url = httpContext.getRequest().getRequestURI();
        if (url.indexOf('#') > 0)
        {
            target.fragment = url.substring(url.indexOf('#') + 1);
        }
        return target;
    }

    /**
     * Adds a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool add(String name, String value)
    {
        LinkTool target = (LinkTool)clone();
        if (name.equals(config.getViewToken()) || name.equals(config.getActionToken()))
        {
            throw new IllegalArgumentException("multiple values of the " + name + 
                                                " parameter are not allowed");
        }
        else
        {
            target.parameters.add(name, value);
        }
        return target;
    }

    /**
     * Adds a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool add(String name, int value)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.add(name, value);
        return target;
    }

    /**
     * Adds a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool add(String name, long value)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.add(name, value);
        return target;
    }

    /**
     * Adds a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool add(String name, float value)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.add(name, value);
        return target;
    }

    /**
     * Adds a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool add(String name, boolean value)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.add(name, value);
        return target;
    }

    /**
     * Adds a set of parameters to the request.
     *
     * @param parameters a set of paremteres.
     * @return the link tool.
     */
    public LinkTool add(Parameters parameters)
    {
        LinkTool target = (LinkTool)clone();
        target.parameters.add(parameters, false);
        return target;
    }

    /**
     * Removes a request parameter.
     *
     * @param name the name of the parameter.
     * @return the link tool.
     */
    public LinkTool unset(String name)
    {
        LinkTool target = (LinkTool)clone();
        if (name.equals(config.getViewToken()))
        {
            target.view = "";
        }
        else if (name.equals(config.getActionToken()))
        {
            target.action = "";
        }
        else
        {
            target.parameters.remove(name);
        }
        return target;
    }

    /**
     * Sets the view parameter in the link.
     *
     * @param view the view.
     * @return the link tool.
     */
    public LinkTool view(String view)
    {
        LinkTool target = (LinkTool)clone();
        target.view = view;
        return target;
    }

    /**
     * Sets the action parameter in the link.
     *
     * @param action the action.
     * @return the link tool.
     */
    public LinkTool action(String action)
    {
        LinkTool target = (LinkTool)clone();
        target.action = action;
        return target;
    }

    /**
     * Produces a String representation of this link.
     * 
     * @return the link.
     */
    public String toString()
    {
        try
        {
            // prepare server part if needed
            sb.setLength(0);
            String serverPart = null;
            if (showProtocolName)
            {
                sb.append(protocolName);
                sb.append("://");
                sb.append(httpContext.getRequest().getServerName());
                if (((protocolName.length() == 0 || protocolName.equals("http")) && port != 80)
                    || (protocolName.equals("https") && port != 443)
                    || (protocolName.length() > 0 && 
                       !protocolName.equals("https") && !protocolName.equals("http")))
                {
                    sb.append(':').append(port);
                }
                serverPart = sb.toString();
                sb.setLength(0);
            }

            sb.append(httpContext.getRequest().getContextPath());
            if (resourceLink)
            {
                if (path.length() > 0)
                {
                    if (path.charAt(0) != '/')
                    {
                        sb.append('/');
                    }
                    sb.append(path);
                }
            }
            else
            {
                String servletPath = httpContext.getRequest().getServletPath();
                if (servletPath.charAt(0) != '/')
                {
                    sb.append('/');
                }
                if (servletPath.endsWith("/"))
                {
                    sb.append(servletPath.substring(0, servletPath.length() - 1));
                }
                else
                {
                    sb.append(servletPath);
                }

                if (view != null && !view.equals(""))
                {
                    sb.append('/').append(config.getViewToken());
                    sb.append('/').append(view);
                }
                else
                {
                    String requestView = requestParameters.get(config.getViewToken(), "");
                    if (requestView.length() > 0)
                    {
                        sb.append('/').append(config.getViewToken());
                        sb.append('/').append(requestView);
                    }
                }

                String[] keys = parameters.getParameterNames();
                Set pathinfoSet = config.getPathInfoKeys();
                List pathinfoParameterKeys = new ArrayList();
                List queryParameterKeys;
                if (pathinfoSet.size() > 0)
                {
                    queryParameterKeys = Arrays.asList(keys);
                }
                else
                {
                    queryParameterKeys = new ArrayList(keys.length);
                    for (int i = 0; i < keys.length; i++)
                    {
                        if (!pathinfoSet.contains(keys[i]))
                        {
                            queryParameterKeys.add(keys[i]);
                        }
                        else
                        {
                            pathinfoParameterKeys.add(keys[i]);
                        }
                    }
                }

                for (int i = 0; i < pathinfoParameterKeys.size(); i++)
                {
                    String key = (String)pathinfoParameterKeys.get(i);
                    String[] values = parameters.getStrings(key);
                    for (int j = 0; j < values.length; j++)
                    {
                        sb.append('/').append(URLEncoder.encode(key, UTF_8));
                        sb.append('/').append(URLEncoder.encode(values[j], UTF_8));
                    }
                }

                if (!action.equals("") || queryParameterKeys.size() > 0)
                {
                    sb.append('?');
                    if (!action.equals(""))
                    {
                        sb.append(config.getActionToken());
                        sb.append('=');
                        sb.append(action);
                        if (queryParameterKeys.size() > 0)
                        {
                            sb.append(config.getQuerySeparator());
                        }
                    }
                    for (int i = 0; i < queryParameterKeys.size(); i++)
                    {
                        String key = (String)queryParameterKeys.get(i);
                        String[] values = parameters.getStrings(key);
                        for (int j = 0; j < values.length; j++)
                        {
                            sb.append(URLEncoder.encode(key, UTF_8));
                            sb.append('=');
                            sb.append(URLEncoder.encode(values[j], UTF_8));
                            if (j < values.length - 1)
                            {
                                sb.append(config.getQuerySeparator());
                            }
                        }
                        if (i < queryParameterKeys.size() - 1)
                        {
                            sb.append(config.getQuerySeparator());
                        }
                    }
                }
            }
            if (fragment != null)
            {
                sb.append('#').append(fragment);
            }
            String addressPart = sb.toString();
            if (includeSession)
            {
                addressPart = httpContext.getResponse().encodeURL(addressPart);
            }
            sb.setLength(0);
            if (serverPart == null)
            {
                return addressPart;
            }
            else
            {
                sb.append(serverPart);
                sb.append(addressPart);
                return sb.toString();
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Unsupported exception occurred", e);
        }
    }

	/**
	 * Clone the object.
	 * 
	 * @return the object.
	 */
	public Object clone()
	{
		LinkTool target = new LinkTool(context, config);
		target.view = view;
		target.action = action;
		target.resourceLink = resourceLink;
		target.includeSession = includeSession;
		target.showProtocolName = showProtocolName;
		target.protocolName = protocolName;
		target.port = port;
		target.path = path;
		target.fragment = fragment;
	    target.parameters = new DefaultParameters(parameters);
		return target;		
	}
}
