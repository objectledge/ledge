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
import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Configurable;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.parameters.SortedParameters;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;

/**
 * Context tool used to build web application links. It works in a pull manner. The template
 * designer provides instances of <code>LinkTool</code> with all the necessary parameters,
 * the <code>LinkTool</code> itself is responsible for generation of a proper URL string based on 
 * this parameters. An example of <code>LinkTool</code> usage in Velocity template:
 * 
 * <h4>template</h4>
 * <pre>
 * $link.view('somepackage.SomeView').action('somepackage.SomeAction').set('paramName','paramValue')
 * </pre>
 *
 * <h4>output</h4>
 * <pre>
 * /context/servlet/view/somepackage.SomeView?action=somepackage.SomeAction&amp;paramName=paramValue
 * </pre>
 * 
 * <p>The links are encoded using UTF-8 encoding no matter what encoding is used in the generated
 * HTML page. This is in line with JavaScript URI encoding and decoding functions.</p>
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LinkTool.java,v 1.18 2004-12-23 07:18:14 rafal Exp $
 */
public class LinkTool
{
	/** utf encoding. */
	public static final String PARAMETER_ENCODING = "UTF-8";
	
    /** query string parameter values encoder. */
    private static final org.objectledge.encodings.URLEncoder QUERY_STRING_ENCODER =
        new org.objectledge.encodings.URLEncoder();
    
	/** link tool factory. */
	private LinkToolFactory factory;
	
    /** configuration. */
    private LinkTool.Configuration config;
    
	/** the http context */
	private HttpContext httpContext;

    /** the mvc context */
    private MVCContext mvcContext;

    /** the request parameters */
	private RequestParameters requestParameters;
	
	/** view, null for current request parameters view value, empty string for unset value */
	private String view;
	
	/** the action, empty string for unset value */
	private String action;
	
	/** is content link switch */
	private boolean contentLink;
	
	/** include session information */
	private boolean includeSession;
	
	/** show the protocol in link */
	private boolean showProtocolName;
	
	/** protocol name */
	private String protocolName;
	
	/** the port */
	private int port;
	
	/** the content path */
	private String path;
	
    /** the special path info suffix */
    private String pathInfoSuffix;

    /** the fragment part of the url */
	private String fragment;
	
	/** the parameters */
	private Parameters parameters;
    
	/** the string buffer */
	private StringBuffer sb;
	
	/** 
	 * Component constructor.
	 * 
	 * @param httpContext the http context.
     * @param mvcContext the mvc context.
     * @param requestParameters the request parameters.
     * @param config the link tool configuraiton.
	 */
	public LinkTool(HttpContext httpContext, MVCContext mvcContext,
        RequestParameters requestParameters, LinkTool.Configuration config)
	{
        this.config = config;
		this.httpContext = httpContext;
        this.mvcContext = mvcContext;
		this.requestParameters = requestParameters;
		contentLink = false;
		includeSession = true;
		showProtocolName = false;
		protocolName = ""; 
        port = 0;
        // this means no override for view
        view = null;
		action = "";
        pathInfoSuffix = null;
		fragment = null;
		if(config.stickyParameterNames.size() > 0)
		{
			parameters = new SortedParameters(requestParameters);
		    parameters.removeExcept(config.stickyParameterNames);
		}
        else
        {
            parameters = new SortedParameters();
        }
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
        if(target.port == 0)
        {
            target.port = httpContext.getRequest().getServerPort();    
        }
        if(target.protocolName == null || target.protocolName.length()==0)
        {
 		    if(httpContext.getRequest().isSecure())
		    {
			    target.protocolName = "https";
		    }
		    else
		    {
			    target.protocolName = "http";
		    }
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
     * Set link to point to the content stored in <code>/</code> directory of servlet context.
     *
     * @param path the path to content.
     * @return the link tool.
     */
    public LinkTool rootContent(String path)
    {
        LinkTool target = getLinkTool(this);
        target.contentLink = true;
        target.includeSession = !config.externalContent;
        target.path = path;
        return target;
    }

    /**
     * Set link to point to the content stored in <code>/content</code> directory of servlet
     * context.
     *
     * @param path the path to content.
     * @return the link tool.
     */
    public LinkTool content(String path)
    {
        if (path.length() == 0)
        {
            path = config.baseContentPath;
        }
        else if (path.charAt(0) != '/')
        {
            path = config.baseContentPath + '/' + path;
        }
        else
        {
            path = config.baseContentPath + path;
        }
        return rootContent(path);
    }

    // parameter set methods ---------------------------------------------------------------------- 
    
    /**
     * Sets a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool set(String name, String value)
    {
        LinkTool target = getSetTargetLinkTool(name);
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
        LinkTool target = getSetTargetLinkTool(name);
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
        LinkTool target = getSetTargetLinkTool(name);
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
        LinkTool target = getSetTargetLinkTool(name);
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
        LinkTool target = getSetTargetLinkTool(name);
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
		if (parameters.isDefined(config.viewToken))
		{
			checkSetParamName(config.viewToken);
		}
		else if (parameters.isDefined(config.actionToken))
		{
			checkSetParamName(config.actionToken);
		}
		// TODO: Add RFC characters check
        LinkTool target = getLinkTool(this);
        target.parameters = new SortedParameters(parameters);
        return target;
    }

    /**
     * Checks the name of the set parameter and returns target link tool.
     * Method created for code reuse.
     *  
     * @param paramName checked name of the set parameter.
     * @return target link tool
     */
    private LinkTool getSetTargetLinkTool(String paramName)
    {
        checkSetParamName(paramName);
        return getLinkTool(this);
    }
    
    // --------------------------------------------------------------------------------------------
    
    /**
     * Sets the path info suffix for this link.
     * 
     * @param pathInfoSuffix the path info suffix.
     * @return the link tool.
     */
    public LinkTool pathInfoSuffix(String pathInfoSuffix)
    {
        LinkTool target = getLinkTool(this);
        target.pathInfoSuffix = pathInfoSuffix;
        return target;
    }

    /**
     * Returns the path info suffix for this link.
     * 
     * @return the path info suffx in the current link
     */
    public String pathInfoSuffix()
    {
        return pathInfoSuffix;
    }

    /**
     * Sets the fragment for this link.
     * 
     * @param fragment the fragment.
     * @return the link tool.
     */
    public LinkTool fragment(String fragment)
    {
		// TODO: Add RFC characters check
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
     * <p>TODO: This method creates links different from the request URI if some of the parameters
     * were passed as path info parameters and not configured as such.</p>  
     * 
     * @return the link tool.
     */
    public LinkTool self()
    {
        LinkTool target = getLinkTool(this);
        target.parameters.remove(config.stickyParameterNames);
        target.parameters.add(requestParameters, true);
        target.parameters.remove(config.viewToken);
        target.parameters.remove(config.actionToken);
        String url = httpContext.getRequest().getRequestURI();
        if (url.indexOf('#') > 0)
        {
            target.fragment = url.substring(url.lastIndexOf('#') + 1);
        }
        return target;
    }

    // parameter add methods ---------------------------------------------------------------------- 
    
    /**
     * Adds a request parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @return the link tool.
     */
    public LinkTool add(String name, String value)
    {
        LinkTool target = getAddTargetLinkTool(name);
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
        LinkTool target = getAddTargetLinkTool(name);
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
        LinkTool target = getAddTargetLinkTool(name);
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
        LinkTool target = getAddTargetLinkTool(name);
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
        LinkTool target = getAddTargetLinkTool(name);
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
		if (parameters.isDefined(config.viewToken))
		{
			checkAddParamName(config.viewToken);
		}
		else if (parameters.isDefined(config.actionToken))
		{
			checkAddParamName(config.actionToken);
		}
		// TODO: Add RFC characters check
        LinkTool target = getLinkTool(this);
        target.parameters.add(parameters, false);
        return target;
    }

    /**
     * Checks the name of the set parameter and returns target link tool.
     * Method created for code reuse.
     *  
     * @param paramName checked name of the set parameter.
     * @return target link tool
     */
    private LinkTool getAddTargetLinkTool(String paramName)
    {
        checkAddParamName(paramName);
        return getLinkTool(this);
    }

    // ---------------------------------------------------------------------------------------------
    
    /**
     * Removes a request parameter.
     *
     * @param name the name of the parameter.
     * @return the link tool.
     */
    public LinkTool unset(String name)
    {
        LinkTool target = getLinkTool(this);
        if (name.equals(config.viewToken))
        {
			throw new IllegalArgumentException("to unset the value of the view parameter, " +				"call the unsetView() method");
        }
        else if (name.equals(config.actionToken))
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
        if(sb != null)
        {
            sb.setLength(0);
        }
        else
        {
            sb = new StringBuffer();
        }

        try
        {
            // prepare server part if needed
            appendServerPart(sb);

            // prepare address part
            sb.append(httpContext.getRequest().getContextPath());

            if (contentLink)
            {
                appendContentLink(sb);
            }
            else
            {
                sb.append(httpContext.getRequest().getServletPath());

                String[] keys = parameters.getParameterNames();
                appendPathInfo(sb, keys);
                appendPathInfoSuffix(sb, pathInfoSuffix);
                appendQueryString(sb, keys);
            }
            
            if (fragment != null)
            {
                sb.append('#').append(fragment);
            }
            
            // return link
            String link = sb.toString(); 
            if (includeSession)
            {
                link = httpContext.getResponse().encodeURL(link);
            }
            return link;
        }
        ///CLOVER:OFF
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Exception occurred", e);
        }
        ///CLOVER:ON
    }

    private void appendServerPart(StringBuffer sb)
    {
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
        }
    }

    private void appendContentLink(StringBuffer sb)
        throws UnsupportedEncodingException
    {
        if (path.length() > 0)
        {
            if (path.charAt(0) != '/')
            {
                sb.append('/');
            }
            sb.append(QUERY_STRING_ENCODER.encodeQueryStringValue(path, PARAMETER_ENCODING));
        }
    }

    private void appendPathInfo(StringBuffer sb, String[] keys)
        throws UnsupportedEncodingException
    {
        String outView = view; 
        if (outView == null) // override with current view
        {
            outView = mvcContext.getView();
        }
        if (outView != null && outView.length() > 0)
        {
            sb.append('/').append(config.viewToken).append('/').append(outView);
        }

        for (int i = 0; i < keys.length; i++)
        {
            String key = keys[i];
            if (config.pathinfoParameterNames.contains(key))
            {
                String[] values = parameters.getStrings(key);
                for (int j = 0; j < values.length; j++)
                {
                    sb.append('/').append(URLEncoder.encode(key, PARAMETER_ENCODING));
                    sb.append('/').append(URLEncoder.encode(values[j], PARAMETER_ENCODING));
                }
            }
        }
    }

    private void appendPathInfoSuffix(StringBuffer sb, String pathInfoSuffix)
        throws UnsupportedEncodingException
    {
        if(pathInfoSuffix != null && pathInfoSuffix.length() > 0)
        {
            if(pathInfoSuffix.charAt(0) != '/')
            {
                sb.append('/');    
            }
            sb.append(URLEncoder.encode(pathInfoSuffix, PARAMETER_ENCODING));
        }
    }
    
    private void appendQueryString(StringBuffer sb, String[] keys)
        throws UnsupportedEncodingException
    {
        String querySeparator = "?";
        String querySeparator2 = config.queryStringSeparator;

        if (!action.equals(""))
        {
            sb.append(querySeparator);
            sb.append(config.actionToken).append('=').append(action);
            querySeparator = querySeparator2;
        }
        
        for (int i = 0; i < keys.length; i++)
        {
            String key = keys[i];
            if (!config.pathinfoParameterNames.contains(key))
            {
                String[] values = parameters.getStrings(key);
                for (int j = 0; j < values.length; j++)
                {
                    sb.append(querySeparator);
                    
                    sb.append(URLEncoder.encode(key, PARAMETER_ENCODING));
                    sb.append('=');
                    sb.append(QUERY_STRING_ENCODER
                        .encodeQueryStringValue(values[j], PARAMETER_ENCODING));

                    querySeparator = querySeparator2;
                }
            }
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
		LinkTool target = createInstance(source);
		target.view = source.view;
		target.action = source.action;
		target.contentLink = source.contentLink;
		target.includeSession = source.includeSession;
		target.showProtocolName = source.showProtocolName;
		target.protocolName = source.protocolName;
		target.port = source.port;
		target.path = source.path;
        target.pathInfoSuffix = source.pathInfoSuffix;
		target.fragment = source.fragment;
	    target.parameters = new SortedParameters(source.parameters);
		return target;		
	}
    
    /**
     * Creates the LinkTool instance for copying. This method is intended to be overriden by
     * extending classes in order to provide LinkTool instances of proper class.
     * 
     * @param source copied object
     * @return created instance of the linktool.
     */
    protected LinkTool createInstance(LinkTool source)
    {
        return new LinkTool(source.httpContext, source.mvcContext, source.requestParameters,
            source.config);
    }
    
    private void checkSetParamName(String name)
    {
		// TODO: Add RFC characters check
		if (name.equals(config.viewToken))
		{
			throw new IllegalArgumentException("to set the value of the view parameter, " +
				"call the view(String) method");
		}
		else if (name.equals(config.actionToken))
		{
			throw new IllegalArgumentException("to set the value of the action parameter, " +
				"call the action(String) method");
		}
    }

	private void checkAddParamName(String name)
	{
		// TODO: Add RFC characters check
		if (name.equals(config.viewToken))
		{
			throw new IllegalArgumentException(
				"multiple values of the view parameter are not allowed");
		}
		else if (name.equals(config.actionToken))
		{
			throw new IllegalArgumentException(
				"multiple values of the action parameter are not allowed");
		}
	}
    
    /**
     * Represents the shared configuration of the LinkTools.
     *
     * <p>Created on Jan 14, 2004</p>
     * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
     */
    public static class Configuration
        implements Configurable
    {        
        /** the default query separator. */
        public static final String DEFAULT_QUERY_SEPARATOR = "&";

        /** the default base content path. */
        public static final String DEFAULT_BASE_CONTENT_PATH = "/content";

        /** the sticky parameters keys */
        private Set stickyParameterNames = new HashSet();
    
        /** the pathinfo parameters keys */
        private Set pathinfoParameterNames = new HashSet();
    
        /** the query separator */
        private String queryStringSeparator;
    
        /** external content switch */
        private boolean externalContent;

        /** the web configurator. */
        private WebConfigurator webConfigurator;
        
        /** base content path */
        private String baseContentPath;
        
        /** currently used view parameter name */
        private String viewToken;

        /** currently used action parameter name */
        private String actionToken;

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
                    stickyParameterNames.add(keys[i].getValue());
                }
                keys = config.getChild("pathinfo").getChildren("key");
                for (int i = 0; i < keys.length; i++)
                {
                    pathinfoParameterNames.add(keys[i].getValue());
                }
                baseContentPath =
                    config.getChild("base_content_path").getValue(DEFAULT_BASE_CONTENT_PATH);
            }
            ///CLOVER:OFF
            catch (ConfigurationException e)
            {
                throw new ComponentInitializationError("failed to configure the component", e);
            }
            ///CLOVER:ON
            queryStringSeparator = config.getChild("query_separator").
                getValue(DEFAULT_QUERY_SEPARATOR);
            externalContent = config.getChild("external_content").getValueAsBoolean(false);
            // TODO: remove WebConfigurator
            viewToken = webConfigurator.getViewToken();
            actionToken = webConfigurator.getActionToken();
        }
    }
}
