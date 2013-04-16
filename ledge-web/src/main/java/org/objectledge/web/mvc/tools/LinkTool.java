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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jcontainer.dna.Configurable;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.parameters.SortedParameters;
import org.objectledge.utils.StringUtils;
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
 * $link.view('somepackage.SomeView').action('somepackage.SomeAction').set('paramName','paramValue').fragment('this_line')
 * </pre>
 *
 * <h4>output</h4>
 * <pre>
 * /context/servlet/view/somepackage.SomeView?action=somepackage.SomeAction&amp;paramName=paramValue#this_line
 * </pre>
 * 
 * <p>The links are encoded using UTF-8 encoding no matter what encoding is used in the generated
 * HTML page. This is in line with JavaScript URI encoding and decoding functions.</p>
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LinkTool.java,v 1.33 2006-04-24 13:35:50 zwierzem Exp $
 */
public class LinkTool
{
	/** utf encoding. */
	public static final String PARAMETER_ENCODING = "UTF-8";
	
    /** query string parameter values and content paths encoder. */
    private static final org.objectledge.encodings.URLEncoder URL_ENCODER =
        new org.objectledge.encodings.URLEncoder();
    
    /** configuration. */
    protected LinkTool.Configuration config;
    
	/** the http context. */
    protected HttpContext httpContext;

    /** the mvc context. */
    protected MVCContext mvcContext;

    /** the request parameters. */
    protected RequestParameters requestParameters;
	
	/** view, null for current request parameters view value, empty string for unset value */
	private String view;
	
	/** the action, empty string for unset value */
	private String action;
	
	/** is content link switch */
	private boolean contentLink;
	
	/** include session information */
	private boolean includeSession;
	
    /** include context path */
    private boolean includeContext;

	/** show the protocol in link */
	private boolean showProtocolName;
	
	/** protocol name */
	private String protocolName;
	
    /** host name */
    private String host;
    
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
	private StringBuilder sb;
	
	/** 
	 * Tool constructor.
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
        host = null;
        includeContext = true;
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
     * Generate an absolute link including protocol name (schema), server name and port number.
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
     * Geneate an absolute link to another host.
     * 
     * @param host the host domain name.
     * @return the link tool.
     */
    public LinkTool host(String host)
    {
        LinkTool target = absolute();
        target.host = host;
        return target;
    }
    
    /**
     * Avoid session information in link - useful for content served using external HTTP server. 
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
     * May be used to generate relative content paths (not recommended).
     *
     * @param path the path to content.
     * @return the link tool.
     */
    public LinkTool rootContent(String path)
    {
        LinkTool target = getLinkTool(this);
        target.contentLink = true;
        target.includeSession = this.includeSession && !config.externalContent;
        target.path = path;
        return target;
    }

    /**
     * Set link to point to content stored in the root directory of the host.
     * 
     * @param path path to the content.
     * @return the link tool.
     */
    public LinkTool serverContent(String path)
    {
        LinkTool target = getLinkTool(this);
        target.contentLink = true;
        target.includeSession = false;
        target.includeContext = false;
        target.path = path;
        return target;
    }

    /**
     * Set link to point to the content stored in configured content (<code>/content</code>)
     * directory of servlet context. 
     *
     * @param path the relative path to content.
     * @return the link tool.
     */
    public LinkTool content(String path)
    {
        String relativeContentPath;
        if (path.length() == 0)
        {
            relativeContentPath = config.baseContentPath;
        }
        else if (path.charAt(0) != '/')
        {
            relativeContentPath = config.baseContentPath + '/' + path;
        }
        else
        {
            relativeContentPath = config.baseContentPath + path;
        }
        return rootContent(relativeContentPath);
    }

    // parameter set methods ---------------------------------------------------------------------- 
    
    /**
     * Sets a request parameter, replacing previously set value.
     * Unless configured differently it will be rendered in the link as query string parameter.
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
     * Sets a request parameter, replacing previously set value.
     * Unless configured differently it will be rendered in the link as query string parameter.
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
     * Sets a request parameter, replacing previously set value.
     * Unless configured differently it will be rendered in the link as query string parameter.
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
     * Sets a request parameter, replacing previously set value.
     * Unless configured differently it will be rendered in the link as query string parameter.
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
     * Sets a request parameter, replacing previously set value.
     * Unless configured differently it will be rendered in the link as query string parameter.
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
     * Sets multiple values of a parameter using a {@link java.util.List}.
     *
     * @param name the name of the parameter.
     * @param list a list of parameter values.
     * @return the link tool.
     */
    public LinkTool set(String name, List<String> list)
    {
        LinkTool target = getSetTargetLinkTool(name);
        target.parameters.remove(name);
        for (String value : list) 
        {
            target.parameters.add(name, value);
        }
        return target;
    }

    /**
     * Adds a set of parameters defined as a {@link java.util.Map} to the request.
     * Removes old values of the parameters.
     *
     * @param map a set of parametres as a Map.
     * @return the link tool.
     */
    public LinkTool set(Map<String, String> map)
    {
        if (map.containsKey(config.viewToken))
        {
            checkSetParamName(config.viewToken);
        }
        else if (map.containsKey(config.actionToken))
        {
            checkSetParamName(config.actionToken);
        }
        LinkTool target = getLinkTool(this);
        for (Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry<String, String> e = iter.next();
            target.parameters.remove(e.getKey());
            target.parameters.add(e.getKey(), e.getValue());
        }
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
     * May be used for file download links.
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
     * Fragment is appended as <code>#fragment-value</code> to the rendered link.
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
     * <p>WARN: This method creates links different from the request URI if some of the parameters
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

    /**
     * Prepare link tool pointed to referer page
     * @return the link tool
     */
    @SuppressWarnings("unchecked")
    public LinkTool getReferer()
    {
        Enumeration<String> enumeration = httpContext.getRequest().getHeaders("referer");
        if( enumeration != null && enumeration.hasMoreElements())
        {
            return parseURL(enumeration.nextElement());
        }
        else
        {
            return null;
        }
    }
    
    public LinkTool uri(URI uri)
    {
        LinkTool target = getLinkTool(this);
        target = target.absolute();
        target = target.unsetView();
        if(uri.getScheme().equals("http"))
        {
            target = target.http();
        }
        if(uri.getScheme().equals("https"))
        {
            target = target.https();
        }
        if(uri.getHost() != null)
        {
            target = target.host(uri.getHost());
        }
        if(uri.getPath() != null)
        {
            target = target.serverContent(uri.getPath());
        }
        if(uri.getFragment() != null)
        {
            target = target.fragment(uri.getFragment());
        }
        return target;
    }

    public LinkTool url(URL url)
        throws URISyntaxException
    {
        return uri(url.toURI());
    }

    /**
     * Parse given string to obtain link tool object
     * @param url - string of the toString() shape
     * @return the link tool
     * @see #toString()
     */
    private LinkTool parseURL(String url)
    {   
        LinkTool target = getLinkTool(this);
        int index = url.indexOf(config.viewToken+"/");
        if( StringUtils.isEmpty(url) || index < 0)
        {
            return target;
        }
        target.setFromURL(url.substring(index));
        
        return target;
    }
    
    private static final int START = 0;
    private static final int NAME = 1;
    private static final int SEPARATOR_AFTER_NAME = 2;
    private static final int VALUE_PARAM = 3;
    private static final int VALUE_VIEW = 4;
    private static final int VALUE_ACTION = 5;
    private static final int SEPARATOR_AFTER_VALUE = 6;
    private static final String URL_SEPARATOR_CHARS = "&=?/";
    
    @SuppressWarnings("null")
    private void setFromURL(String url)
    {       
        if (url == null)
        {
            return;
        }
        try
        {
            StringTokenizer st = new StringTokenizer(url, URL_SEPARATOR_CHARS, true);
            int state = START;
            String name = null;
            while (st.hasMoreTokens() || state != NAME)
            {
                String token = null;
                boolean hasMoreTokens = st.hasMoreTokens();
                if( hasMoreTokens)
                {
                    token = st.nextToken();
                }
                // separators and empty values
                if(!hasMoreTokens || (token.length() == 1 && URL_SEPARATOR_CHARS.indexOf(token.charAt(0)) > -1))
                {
                    switch(state)
                    {
                    case START:
                        state = NAME;
                        break;
                    case SEPARATOR_AFTER_NAME:
                        if( config.viewToken.equals(name))
                        {
                            state = VALUE_VIEW;
                        }
                        else if( config.actionToken.equals(name)) 
                        {
                            state = VALUE_ACTION;
                        }
                        else
                        {
                            state = VALUE_PARAM;
                        }
                        break;
                    case SEPARATOR_AFTER_VALUE:
                        state = NAME;
                        break;
                    case VALUE_ACTION:
                        action = null;
                        name = null;
                        state = NAME;
                        break;
                    case VALUE_VIEW:
                        view = null;
                        name = null;
                        state = NAME;
                        break;
                    case VALUE_PARAM:
                        parameters.add(name, "");
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
                        name = URLDecoder.decode(token, PARAMETER_ENCODING);
                        state = SEPARATOR_AFTER_NAME;
                        break;
                    case VALUE_PARAM:
                        parameters.add(name, URLDecoder.decode(token, PARAMETER_ENCODING));
                        name = null;
                        state = SEPARATOR_AFTER_VALUE;
                        break;
                    case VALUE_ACTION:
                        action = URLDecoder.decode(token, PARAMETER_ENCODING);
                        name = null;
                        state = SEPARATOR_AFTER_VALUE;
                        break;
                    case VALUE_VIEW:
                        view = URLDecoder.decode(token, PARAMETER_ENCODING);
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

    private Parameters parseParameters(String queryString)
    {
        Parameters result = new DefaultParameters();
        String[] pp = queryString.split("&");
        try
        {
            for(String p : pp)
            {
                if(p.indexOf('=') > 0)
                {
                    String[] kv = p.split("=");
                    result.add(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                }
                else
                {
                    result.add(URLDecoder.decode(p, "UTF-8"), "");
                }
            }
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 not supported?", e);
        }
        return result;
    }

    // parameter add methods ---------------------------------------------------------------------- 
    
    /**
     * Adds a request parameter, extending it's values set.
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
     * Adds a request parameter, extending it's values set.
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
     * Adds a request parameter, extending it's values set.
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
     * Adds a request parameter, extending it's values set.
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
     * Adds a request parameter, extending it's values set.
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
     * Adds multiple parameter values using a {@link java.util.List}.
     *
     * @param name the name of the parameter.
     * @param list a set of parameter values.
     * @return the link tool.
     */
    public LinkTool add(String name, List<String> list)
    {
        LinkTool target = getAddTargetLinkTool(name);
        for (String value : list) 
        {
            target.parameters.add(name, value);
        }
        return target;
    }

    /**
     * Adds a set of parameters defined as a {@link java.util.Map} to the request.
     *
     * @param map a set of parametres as a Map.
     * @return the link tool.
     */
    public LinkTool add(Map<String, String> map)
    {
        if (map.containsKey(config.viewToken))
        {
            checkAddParamName(config.viewToken);
        }
        else if (map.containsKey(config.actionToken))
        {
            checkAddParamName(config.actionToken);
        }
        LinkTool target = getLinkTool(this);
        for (Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry<String, String> e = iter.next();
            target.parameters.add(e.getKey(), e.getValue());
        }
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
     * Removes all values of a request parameter.
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
     * @return the name of the action parameter.
     */
    public String actionParam()
    {
        return config.actionToken;
    }
    
    /**
     * @return the name of the view parameter.
     */
    public String viewParam()
    {
        return config.viewToken;
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

    // start toString() - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    /**
     * Produces a {@link java.lang.String} representation of this link.
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
            sb = new StringBuilder();
        }

        try
        {
            // prepare server part if needed
            appendServerPart(sb);

            // prepare address part
            sb.append(getContextPath());

            if (contentLink)
            {
                appendContentLink(sb);
            }
            else
            {
                sb.append(getServletPath());

                Parameters parametersTmp = getParameters(); 
                String[] keys = parametersTmp.getParameterNames();
                appendPathInfo(sb, keys, parametersTmp);
                appendPathInfoSuffix(sb);
                appendQueryString(sb, keys, parametersTmp);
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

    /**
     * Allows other Tools to check link properties and 
     * creaton of subclasses which can override action name 
     * rendered in {@link #toString()} method.
     * 
     * <p>WARN: This implementation only returns a reference to this <code>LinkTool</code>'s
     * parameters field. If the subclass needs to change the rendered parameters, it should copy
     * the parameters object.</p> 
     * 
     * @return the overriden parameters container
     */
    public Parameters getParameters()
    {
        return parameters;
    }
    
    private void appendServerPart(StringBuilder sb)
    {
        if (showProtocolName)
        {
            final String protocolNameTmp = getProtocolName();
            sb.append(protocolNameTmp);
            sb.append("://");
            sb.append(getServerName());
            final int portTmp = getPort();
            if (mustAppendPort(protocolNameTmp, portTmp))
            {
                sb.append(':').append(portTmp);
            }
        }
    }

    /**
     * Allows subclasses to override protocol name rendered in {@link #toString()} method.
     * 
     * @return the overriden protocol name
     */
    protected String getProtocolName()
    {
        return protocolName;
    }

    /**
     * Allows subclasses to override server name rendered in {@link #toString()} method.
     * 
     * @return the overriden server name
     */
    protected String getServerName()
    {
        if(host == null)
        {
            return httpContext.getRequest().getServerName();
        }
        else
        {
            return host;
        }
    }

    /**
     * Allows subclasses to override cotext path name rendered in {@link #toString()} method.
     * 
     * @return the overriden context path
     */
    protected String getContextPath()
    {
        if(config.rewrite || !includeContext)
        {
            // rely on mod rewrite to put context path into URL
            return "";
        }
        else
        {
            return httpContext.getRequest().getContextPath();
        }
    }

    /**
     * Allows subclasses to override servlet path name rendered in {@link #toString()} method.
     * 
     * @return the overriden serlvet path
     */
    protected String getServletPath()
    {
        if(config.rewrite)
        {
            // rely on mod rewrite to put servlet path into URL
            return "";
        }
        else
        {
            return httpContext.getRequest().getServletPath();
        }
    }

    /**
     * Allows subclasses to override check for port number inclusion requirement in
     * {@link #toString()} method.
     * This implementation returns <code>true</code> if port number is different from default
     * protocol port (default port number for http is 80, for https 443). 
     * 
     * @param protocolNameTmp appended protocol name
     * @param portTmp appended (or not) port number
     * @return <code>true</code> if port number must be appended.
     */
    protected boolean mustAppendPort(String protocolNameTmp, int portTmp)
    {
        return ((protocolNameTmp.length() == 0 || protocolNameTmp.equals("http")) && portTmp != 80)
               || (protocolNameTmp.equals("https") && portTmp != 443)
               || (protocolNameTmp.length() > 0
                   && !protocolNameTmp.equals("https") && !protocolNameTmp.equals("http"));
    }

    /**
     * Allows subclasses to override port number rendered in {@link #toString()} method.
     * 
     * @return the overriden port number
     */
    protected int getPort()
    {
        return port;
    }
    
    
    private void appendContentLink(StringBuilder sb)
        throws UnsupportedEncodingException
    {
        final String pathTmp = getPath();
        if (pathTmp.length() > 0)
        {
            if (pathTmp.charAt(0) != '/')
            {
                sb.append('/');
            }
            sb.append(URL_ENCODER.encodeContentPath(pathTmp, PARAMETER_ENCODING));
        }
    }
    
    /**
     * Allows subclasses to override static content path rendered in {@link #toString()} method.
     * 
     * @return the overriden content path
     */
    protected String getPath()
    {
        return path;
    }

    private void appendPathInfo(StringBuilder sb, String[] keys, Parameters parametersTmp)
        throws UnsupportedEncodingException
    {
        String outView = getView(); 
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
                String[] values = parametersTmp.getStrings(key);
                for (int j = 0; j < values.length; j++)
                {
                    sb.append('/').append(URLEncoder.encode(key, PARAMETER_ENCODING));
                    sb.append('/').append(URLEncoder.encode(values[j], PARAMETER_ENCODING));
                }
            }
        }
    }
    
    /**
     * Allows other Tools to check link properties and 
     * creaton of subclasses which can override action name 
     * rendered in {@link #toString()} method.
     * 
     * @return the overriden view name or <code>null</code> if it should be replaced with view
     *  from current request.
     */
    public String getView()
    {
        return view;
    }

    private void appendPathInfoSuffix(StringBuilder sb)
        throws UnsupportedEncodingException
    {
        String pathInfoSuffixTmp = getPathInfoSuffix();
        if(pathInfoSuffixTmp != null && pathInfoSuffixTmp.length() > 0)
        {
            if(pathInfoSuffixTmp.charAt(0) != '/')
            {
                sb.append('/');    
            }
            pathInfoSuffixTmp = URLEncoder.encode(pathInfoSuffixTmp, PARAMETER_ENCODING);
            // if someone puts slashes into the suffix, assume they know what they are doing.
            pathInfoSuffixTmp = pathInfoSuffixTmp.replace("%2F","/");
            sb.append(pathInfoSuffixTmp);
        }
    }
    
    /**
     * Allows subclasses to override pathinfo suffix rendered in {@link #toString()} method.
     * 
     * @return the overriden pathinfo suffix
     */
    protected String getPathInfoSuffix()
    {
        return pathInfoSuffix;
    }
    
    private void appendQueryString(StringBuilder sb, String[] keys, Parameters parametersTmp)
        throws UnsupportedEncodingException
    {
        String querySeparator = "?";
        final String querySeparator2 = config.queryStringSeparator;

        String actionTmp = getAction();
        if (actionTmp != null && actionTmp.length() > 0)
        {
            sb.append(querySeparator);
            sb.append(config.actionToken).append('=').append(actionTmp);
            querySeparator = querySeparator2;
        }
        
        for (int i = 0; i < keys.length; i++)
        {
            String key = keys[i];
            if (!config.pathinfoParameterNames.contains(key))
            {
                String[] values = parametersTmp.getStrings(key);
                for (int j = 0; j < values.length; j++)
                {
                    sb.append(querySeparator);
                    
                    sb.append(URLEncoder.encode(key, PARAMETER_ENCODING));
                    sb.append('=');
                    sb.append(URL_ENCODER
                        .encodeQueryStringValue(values[j], PARAMETER_ENCODING));

                    querySeparator = querySeparator2;
                }
            }
        }
    }
    
    /**
     * Allows other Tools to check link properties and 
     * creaton of subclasses which can override action name 
     * rendered in {@link #toString()} method.
     * 
     * @return the overriden action name
     */
    public String getAction()
    {
        return action;
    }

    // end toString() - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
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
        target.host = source.host;
		target.path = source.path;
        target.includeContext = source.includeContext;
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
        private Set<String> stickyParameterNames = new HashSet<String>();
    
        /** the pathinfo parameters keys */
        private Set<String> pathinfoParameterNames = new HashSet<String>();
    
        /** the query separator */
        private String queryStringSeparator;
    
        /** external content switch */
        private boolean externalContent;
        
        /** rewrite (drop context and servlet path from URLs) switch. */
        private boolean rewrite;

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
            rewrite = config.getChild("rewrite").getValueAsBoolean(false);
            // TODO: remove WebConfigurator
            viewToken = webConfigurator.getViewToken();
            actionToken = webConfigurator.getActionToken();
        }
    }
}
