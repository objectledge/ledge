package org.objectledge.web.rest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class JerseyRestValve
    implements Valve
{
    private final ServletContainer jerseyContainer;

    private final Logger logger;

    /**
     * Creates a new jersey REST dispatcher.
     * 
     * @param pipeline the pipeline
     * @param context the thread context
     * @throws ConfigurationException if the configuration is malformed.
     * @throws ServletException
     */
    public JerseyRestValve(Logger logger, final Configuration config,
        final ServletContext servletContext)
        throws ConfigurationException, ServletException
    {
        this.logger = logger;
        String restPackageNames = config.getChild("restPackageNames").getValue();

        Configuration initParams = config.getChild("init-parmameters", true);

        final ServletConfig ledgeServletConfig = new LedgeServletConfig(servletContext, initParams);
        jerseyContainer = new ServletContainer(new PackagesResourceConfig(restPackageNames))
            {
                @Override
                public ServletConfig getServletConfig()
                {
                    return ledgeServletConfig;
                }
            };
        jerseyContainer.init();
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        HttpServletRequest request = httpContext.getRequest();
        HttpServletResponse response = httpContext.getResponse();
        try
        {
            jerseyContainer.service(request, response);
            httpContext.setDirectResponse(true);
        }
        catch(ServletException e)
        {
            throw new ProcessingException(e);
        }
        catch(IOException e)
        {
            throw new ProcessingException(e);
        }
    }

    private static class LedgeServletConfig
        implements ServletConfig
    {
        private final ServletContext context;

        private Hashtable<String, String> parameters = new Hashtable<String, String>();

        public LedgeServletConfig(ServletContext context, Configuration config)
            throws ConfigurationException
        {
            this.context = context;
            for(Configuration param : config.getChildren("init-param"))
            {
                final String name = param.getChild("param-name").getValue();
                final String value = param.getChild("param-value").getValue();
                parameters.put(name, value);
            }
        }

        @Override
        public String getServletName()
        {
            return "REST";
        }

        @Override
        public ServletContext getServletContext()
        {
            return context;
        }

        @Override
        public String getInitParameter(String name)
        {
            return parameters.get(name);
        }

        @Override
        public Enumeration getInitParameterNames()
        {
            return parameters.keys();
        }
    }
}
