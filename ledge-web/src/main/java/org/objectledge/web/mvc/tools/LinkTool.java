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

package org.objectledge.web.mvc.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Configurable;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.context.Context;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;

/**
 * Context tool used to build web application links.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class LinkTool
{
	/** utf encoding */
	public static final String PARAMETER_ENCODING = "UTF-8";
	
	/** link tool factory */
	private LinkToolFactory factory;
	
	/** the thread context */
	private Context context;
	
    /** configuration. */
    private LinkTool.Configuration config;
    
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
	
	/** the fragment part of the url */
	private String fragment;
	
	/** the parameters */
	private Parameters parameters;
	
	/** the string buffer */
	private StringBuffer sb;
	
	/** 
	 * Component constructor.
	 * 
	 * @param config the link tool configuraiton.
	 * @param context the thread context.
	 */
	public LinkTool(Context context, LinkTool.Configuration config)
	{
		this.context = context;
        this.config = config;
		mvcContext = MVCContext.getMVCContext(context);
		httpContext = HttpContext.getHttpContext(context);
		requestParameters = RequestParameters.getRequestParameters(context);
		resourceLink = false;
		includeSession = true;
		showProtocolName = false;
		protocolName = ""; 
		view = null;
		action = "";
		parameters = new DefaultParameters();
		fragment = null;
		if(config.hasStickyParameters())
		{
			parameters = new DefaultParameters(requestParameters);
		    parameters.removeExcept(config.getStickyParameters());
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
	    LinkTool target = getLinkTool(this);
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
		LinkTool target = getLinkTool(this);
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
	    LinkTool target = getLinkTool(this);
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
        LinkTool target = getLinkTool(this);
        target.includeSession = false;
        return target;
    }

    /**
     * Set link to point to the resource.
     *
     * @param path the path to static resource.
     * @return the link tool.
     */
    public LinkTool resource(String path)
    {
        LinkTool target = getLinkTool(this);
        target.resourceLink = true;
        target.includeSession = !config.isContentExternal();
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
		checkSetParamName(name);
        LinkTool target = getLinkTool(this);
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
    public LinkTool set(String name, int value)
    {
		checkSetParamName(name);
		LinkTool target = getLinkTool(this);
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
		checkSetParamName(name);
        LinkTool target = getLinkTool(this);
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
		checkSetParamName(name);
        LinkTool target = getLinkTool(this);
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
		checkSetParamName(name);
        LinkTool target = getLinkTool(this);
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
		if (parameters.isDefined(config.getViewToken()))
		{
			checkSetParamName(config.getViewToken());
		}
		else if (parameters.isDefined(config.getActionToken()))
		{
			checkSetParamName(config.getActionToken());
		}
        LinkTool target = getLinkTool(this);
        target.parameters = new DefaultParameters(parameters);
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
        LinkTool target = getLinkTool(this);
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
        LinkTool target = getLinkTool(this);
        target.parameters.remove(config.getStickyParameters());
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
		checkAddParamName(name);
        LinkTool target = getLinkTool(this);
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
    public LinkTool add(String name, int value)
    {
		checkAddParamName(name);
        LinkTool target = getLinkTool(this);
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
		checkAddParamName(name);
        LinkTool target = getLinkTool(this);
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
		checkAddParamName(name);
        LinkTool target = getLinkTool(this);
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
		checkAddParamName(name);
        LinkTool target = getLinkTool(this);
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
		if (parameters.isDefined(config.getViewToken()))
		{
			checkAddParamName(config.getViewToken());
		}
		else if (parameters.isDefined(config.getActionToken()))
		{
			checkAddParamName(config.getActionToken());
		}
        LinkTool target = getLinkTool(this);
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
        LinkTool target = getLinkTool(this);
        if (name.equals(config.getViewToken()))
        {
			throw new IllegalArgumentException("to unset the value of the view parameter, " +				"call the unsetView() method");
        }
        else if (name.equals(config.getActionToken()))
        {
			throw new IllegalArgumentException("to unset the value of the action parameter, " +
				"call the unsetAction() method");
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
        LinkTool target = getLinkTool(this);
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
        LinkTool target = getLinkTool(this);
        target.action = action;
        return target;
    }

	/**
	 * Removes the view parameter.
	 *
	 * @return the link tool.
	 */
	public LinkTool unsetView()
	{
		LinkTool target = getLinkTool(this);
		target.view = "";
		return target;
	}

	/**
	 * Removes the action parameter.
	 *
	 * @return the link tool.
	 */
	public LinkTool unsetAction()
	{
		LinkTool target = getLinkTool(this);
		target.action = "";
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
                List pathinfoParameterKeys = new ArrayList();
                List queryParameterKeys;
                if (config.hasPathInfoParamters())
                {
                    queryParameterKeys = Arrays.asList(keys);
                }
                else
                {
                    queryParameterKeys = new ArrayList(keys.length);
                    for (int i = 0; i < keys.length; i++)
                    {
                        if (!config.isPathInfoParameter(keys[i]))
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
                        sb.append('/').append(URLEncoder.encode(key, PARAMETER_ENCODING));
                        sb.append('/').append(URLEncoder.encode(values[j], PARAMETER_ENCODING));
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
                            sb.append(URLEncoder.encode(key, PARAMETER_ENCODING));
                            sb.append('=');
                            sb.append(URLEncoder.encode(values[j], PARAMETER_ENCODING));
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
	 * Clone the given LinkTool.
	 * 
     * @param source to LinkTool to clone
	 * @return the clone.
	 */
	private LinkTool getLinkTool(LinkTool source)
	{
		LinkTool target = new LinkTool(context, source.config);
		target.view = source.view;
		target.action = source.action;
		target.resourceLink = source.resourceLink;
		target.includeSession = source.includeSession;
		target.showProtocolName = source.showProtocolName;
		target.protocolName = source.protocolName;
		target.port = source.port;
		target.path = source.path;
		target.fragment = source.fragment;
	    target.parameters = new DefaultParameters(source.parameters);
		return target;		
	}
    
    private void checkSetParamName(String name)
    {
		if (name.equals(config.getViewToken()))
		{
			throw new IllegalArgumentException("to set the value of the view parameter, " +
				"call the view(String) method");
		}
		else if (name.equals(config.getActionToken()))
		{
			throw new IllegalArgumentException("to set the value of the action parameter, " +
				"call the action(String) method");
		}
    }

	private void checkAddParamName(String name)
	{
		if (name.equals(config.getViewToken()))
		{
			throw new IllegalArgumentException(
				"multiple values of the view parameter are not allowed");
		}
		else if (name.equals(config.getActionToken()))
		{
			throw new IllegalArgumentException(
				"multiple values of the action parameter are not allowed");
		}
	}
    
    /**
     * Represents the shared configuration of the LinkTools
     *
     * <p>Created on Jan 14, 2004</p>
     * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
     */
    public static class Configuration
        implements Configurable
    {        
        /** the default query separator */
        public static final String DEFAULT_QUERY_SEPARATOR = "&";

        /** the sticky parameters keys */
        private Set stickyKeys = new HashSet();
    
        /** the pathinfo parameters keys */
        private Set pathinfoKeys = new HashSet();
    
        /** the query separator */
        private String querySeparator;
    
        /** external resource switch */
        private boolean externalContent;

        /** the web configurator. */
        private WebConfigurator webConfigurator;

        /**
         * Initializes the configuraiton object.
         * 
         * @param config DNA configuration
         * @param webConfigurator the configuration of the web subsystem.
         * @throws ConfigurationException if the configuration is invalid.
         */
        public Configuration(org.jcontainer.dna.Configuration config, 
            WebConfigurator webConfigurator)
            throws ConfigurationException
        {
            this.webConfigurator = webConfigurator;
            configure(config);
        }
        
        /**
         * Initializes the internal state from DNA configuration object.
         * 
         * <p>This method may be used to reconfigure link tool at runtime.</p>
         * 
         * @param config DNA configuration
         * @throws ConfigurationException if the configuration is invalid.
         */
        public void configure(org.jcontainer.dna.Configuration config)
            throws ConfigurationException
        {
            try
            {
                org.jcontainer.dna.Configuration[] keys = 
                    config.getChild("sticky").getChildren("key");
                for (int i = 0; i < keys.length; i++)
                {
                    stickyKeys.add(keys[i].getValue());
                }
                keys = config.getChild("pathinfo").getChildren("key");
                for (int i = 0; i < keys.length; i++)
                {
                    pathinfoKeys.add(keys[i].getValue());
                }
            }
            catch (ConfigurationException e)
            {
                throw new ComponentInitializationError("failed to configure the component", e);
            }
            querySeparator = config.getChild("query_separator").getValue(DEFAULT_QUERY_SEPARATOR);
            externalContent = config.getChild("external_resource").getValueAsBoolean(false);
        }

        /**
         *  Get the query string separator. 
         *
         * @return the query separator. 
         */
        public String getQuerySeparator()
        {
            return querySeparator;
        }
    
        /**
         * Is external content.
         * 
         * @return <code>true</code>if resources are external.
         */
        public boolean isContentExternal()
        {
            return externalContent;
        }
        
        /**
         * Check if a parameter is sticky.
         * 
         * @param paramName the parameter name.
         * @return <code>true</code> if sticky.
         */
        public boolean isStickyParameter(String paramName)
        {
            return stickyKeys.contains(paramName);
        }
        
        /**
         * Returns the set of sticky parameter names.
         * 
         * @return the set of sticky parameter names.
         */
        public Set getStickyParameters()
        {
            return stickyKeys;
        }
        
        /**
         * Checks if there are any sticky parameters.
         * 
         * @return <code>true</code> if there are any sticky parameters.
         */
        public boolean hasStickyParameters()
        {
            return !stickyKeys.isEmpty();
        }
        
        /**
         * Check if a parameter belongs to pathinfo.
         * 
         * @param paramName the parameter name.
         * @return <code>true</code> belongs to pathinfo.
         */
        public boolean isPathInfoParameter(String paramName)
        {
            return pathinfoKeys.contains(paramName);
        }        
        
        /**
         * Checks if there are any pathinfo parametrers.
         * 
         * @return <code>true</code> if there are any pathinfo parameters.
         */
        public boolean hasPathInfoParamters()
        {
            return !pathinfoKeys.isEmpty();
        }
        
        /**
         * Returns the view parameter name,
         * 
         * @return view parameter name.
         */
        public String getViewToken()
        {
            return webConfigurator.getViewToken();
        }
        
        /**
         * Return the action parameter name.
         * 
         * @return action parameter name.
         */
        public String getActionToken()
        {
            return webConfigurator.getActionToken();
        }
    }
}
