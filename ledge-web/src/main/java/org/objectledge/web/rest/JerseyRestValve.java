package org.objectledge.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.i18n.I18nLocale;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.picocontainer.MutablePicoContainer;

/**
 * JerseyRestValve provides Jersey intergation. JerseyRestValve can be configured using
 * {@link JerseyConfigurationHook} as well as {@link AbstractBinder} JerseyRestValve by default
 * configured Jersey to use Jackson as JSON marshaller. See {@link JacksonMapper} if you want
 * non-default configuration of ObjectMapper for some specific type
 * <p>
 * Example configuration:
 * </p>
 * 
 * <pre>
 * 
 * {@code
 *  <container key="forwardMeToParent"/>
 *  <component class="org.objectledge.web.rest.JerseyRestValve">
 *     <parameter key="forwardMeToParent" />
 *     <parameter />
 *     <parameter />
 *     <parameter />
 *     <parameter />
 *     <sequence>
 *       <!-- Add binders for @Inject. Binders can be used to add Factories -->
 *       <component class="org.objectledge.web.rest.I18nLocaleFactoryBinder"/>
 *     </sequence>
 *     <sequence>
 *       <!-- Jersey configuration hooks, register filters, features, properties etc. -->
 *       <component class="org.objectledge.web.rest.RegisterAuthenticationFilter" />
 *     </sequence>
 *   </component>
 * 
 * }
 * </pre>
 * 
 * @author Marta Kalamar - original design
 * @author Marek Lewandowski - rewrite for JAX-RS 2
 * @see JacksonMapper
 * @see CompositeJacksonMapper
 * @see I18nLocale
 * @see I18nLocaleFactoryBinder
 * @see JerseyConfigurationHook
 * @see JerseyRestAuthenticationFilter
 */
public class JerseyRestValve
    implements Valve
{
    private final ServletContainer jerseyContainer;

    private final Logger logger;

    /**
     * Creates a new jersey REST dispatcher.
     * 
     * @param logger the logger
     * @param config the configuration
     * @param servletContext the servlet context
     * @throws ConfigurationException if the configuration is malformed.
     * @throws ServletException
     */
    public JerseyRestValve(MutablePicoContainer container, Logger logger,
        final Configuration config, final ServletContext servletContext,
        final CompositeJacksonMapper compositeJacksonMapper, AbstractBinder[] binders,
        JerseyConfigurationHook[] configurationHooks)
        throws ConfigurationException, ServletException
    {
        this.logger = logger;

        ArrayList<String> packageNames = getPackageNamesFromConfig(config);
        Configuration initParams = config.getChild("init-parameters", true);
        final LedgeServletConfig ledgeServletConfig = new LedgeServletConfig(servletContext,
            initParams);
        final String[] packageNamesArray = packageNames.toArray(new String[packageNames.size()]);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages(packageNamesArray);
        resourceConfig.register(JacksonFasterXmlFeature.class);
        resourceConfig.register(compositeJacksonMapper);
        resourceConfig.register(MultiPartFeature.class);

        resourceConfig.register(new LedgeBinder(container));
        for(AbstractBinder binder : binders)
        {
            resourceConfig.register(binder);
        }

        for(JerseyConfigurationHook configurationHook : configurationHooks)
        {
            configurationHook.configure(resourceConfig);
        }

        final Map<String, Object> parameters = ledgeServletConfig.getParameters();
        resourceConfig.addProperties(parameters);

        jerseyContainer = new ServletContainer(resourceConfig)
            {
                @Override
                public ServletConfig getServletConfig()
                {
                    return ledgeServletConfig;
                }
            };

        jerseyContainer.init();
    }

    private ArrayList<String> getPackageNamesFromConfig(Configuration config)
        throws ConfigurationException
    {
        ArrayList<String> packageNames = new ArrayList<>();
        final Configuration packages = config.getChild("packages");
        for(Configuration packageConfig : packages.getChildren("package"))
        {
            packageNames.add(packageConfig.getValue());
        }
        return packageNames;
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
            throw new ProcessingException(e.getCause() != null ? e.getCause() : e);
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

        private Hashtable<String, Object> parameters = new Hashtable<String, Object>();

        public LedgeServletConfig(ServletContext context, Configuration config)
            throws ConfigurationException
        {
            this.context = context;
            for(Configuration param : config.getChildren("init-parameter"))
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
            return (String)parameters.get(name);
        }

        @Override
        public Enumeration getInitParameterNames()
        {
            return parameters.keys();
        }

        public Map<String, Object> getParameters()
        {
            return (Map<String, Object>)parameters;
        }

    }
}
