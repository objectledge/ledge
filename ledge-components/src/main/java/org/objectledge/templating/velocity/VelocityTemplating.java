//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
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

package org.objectledge.templating.velocity;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.cache.CacheFactory;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

/**
 * Simple templating implementation based on velocity engine.
 * 
 * @author <a href="mailto:pablo@caltha.com">Pawel Potempski</a>
 * @version $Id: VelocityTemplating.java,v 1.30 2008-08-28 15:52:47 rafal Exp $
 */
public class VelocityTemplating
    implements Templating
{
    private static final String CACHE_NAME = "velocityTemplates";

    /**
     * Engine configuration.
     */
    private final Config config;

    /** logger */
    private final DNALogChute logger;

    /** velocity engine */
    private volatile VelocityEngine engine;

    /** file system */
    private final FileSystem fileSystem;

    /** template objects/nulls keyed by name strings. */
    private final Map<String, Template> templateCache;

    /** boolean objects/nulls keyed by name strings. */
    private final Map<String, Boolean> templateExistsCache = new HashMap<String, Boolean>();

    /**
     * Creates a new instance of the templating system.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param fileSystem the filesystem to read files from.
     * @param templateCache template cache implementation
     * @throws ConfigurationException
     */
    private VelocityTemplating(Config config, Logger logger, FileSystem fileSystem,
        Map<String, Template> templateCache)
        throws ConfigurationException
    {
        this.config = config;
        this.logger = new DNALogChute(logger);
        this.fileSystem = fileSystem;
        this.templateCache = templateCache;
        restart();
    }

    /**
     * Creates a new instance of the templating system.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param fileSystem the filesystem to read files from.
     * @throws ConfigurationException
     */
    public VelocityTemplating(Config config, Logger logger, FileSystem fileSystem)
        throws ConfigurationException
    {
        this(config, logger, fileSystem, new HashMap<String, Template>());
    }

    /**
     * Creates a new instance of the templating system.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param fileSystem the filesystem to read files from.
     * @throws ConfigurationException
     */
    public VelocityTemplating(Configuration config, Logger logger, FileSystem fileSystem)
        throws ConfigurationException
    {
        this(new Config(config), logger, fileSystem, new HashMap<String, Template>());
    }

    /**
     * Creates a new instance of the templating system.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param fileSystem the filesystem to read files from.
     * @param cacheFactory CacheFactory component
     */
    public VelocityTemplating(Configuration config, Logger logger, FileSystem fileSystem,
        CacheFactory cacheFactory)
        throws ConfigurationException
    {
        this(new Config(config), logger, fileSystem, getCache(cacheFactory));
    }

    /**
     * Helper method to initialize cache using CacheFactory.
     * 
     * @param cacheFactory CacheFactory component.
     * @return cache implementation.
     */
    private static Map<String, Template> getCache(CacheFactory cacheFactory)
    {
        if(cacheFactory.getInstanceNames().contains(CACHE_NAME))
        {
            return cacheFactory.getInstance(CACHE_NAME);
        }
        else
        {
            // fall back to simple, permanent cache
            return new HashMap<String, Template>();
        }
    }

    /**
     * Restarts the templating subsystem.
     * 
     * @throws ComponentInitializationError if the restart fails for some reason.
     */
    @Override
    public void restart()
        throws ComponentInitializationError
    {
        // create and initialize a new engine
        VelocityEngine newEngine = new VelocityEngine();
        config.applyProperties(newEngine);
        newEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, logger);
        newEngine.setProperty(LedgeResourceLoader.LEDGE_FILE_SYSTEM, fileSystem);
        newEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "objectledge");
        newEngine.setProperty("objectledge.resource.loader.class",
            "org.objectledge.templating.velocity.LedgeResourceLoader");
        newEngine.setProperty("objectledge.resource.loader."
            + LedgeResourceLoader.LEDGE_FILE_SYSTEM, fileSystem);
        newEngine.setProperty("objectledge.resource.loader." + LedgeResourceLoader.LOG_SYSTEM,
            logger);
        newEngine.setProperty(RuntimeConstants.INPUT_ENCODING, config.getEncoding());
        try
        {
            newEngine.init();
        }
        // /CLOVER:OFF
        catch(VirtualMachineError e)
        {
            throw e;
        }
        catch(ThreadDeath e)
        {
            throw e;
        }
        catch(Throwable t)
        {
            throw new ComponentInitializationError("failed to initialze Velocity", t);
        }
        // /CLOVER:ON
        templateExistsCache.clear();
        templateCache.clear();

        // replace old engine with new one
        this.engine = newEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplatingContext createContext()
    {
        return new VelocityContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplatingContext createContext(Map<String, Object> contents)
    {
        return new VelocityContext(contents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean templateExists(String name)
    {
        if(config.isCacheEnabled())
        {
            synchronized(templateCache)
            {
                Boolean exists = templateExistsCache.get(name);
                if(exists != null)
                {
                    return exists.booleanValue();
                }
            }
        }
        boolean exists = false;
        try
        {
            for(String basePath : config.getPaths())
            {
                String path = basePath + name + config.getExtension();
                if(engine.resourceExists(path))
                {
                    exists = true;
                    break;
                }
            }
        }
        // /CLOVER OFF
        catch(Exception e)
        {
            throw new RuntimeException("Velocity internal error", e);
        }
        // /CLOVER ON
        if(config.isCacheEnabled())
        {
            synchronized(templateCache)
            {
                templateExistsCache.put(name, exists ? Boolean.TRUE : Boolean.FALSE);
            }
        }
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Template getTemplate(String name)
        throws TemplateNotFoundException
    {
        if(config.isCacheEnabled())
        {
            synchronized(templateCache)
            {
                Boolean exists = templateExistsCache.get(name);
                if(exists != null)
                {
                    if(exists.booleanValue())
                    {
                        Template template = templateCache.get(name);
                        if(template != null)
                        {
                            return template;
                        }
                    }
                    else
                    {
                        throw new TemplateNotFoundException("template " + name
                            + config.getExtension() + " not found");
                    }
                }
            }
        }

        Template template = null;
        String path = null;
        try
        {
            for(String basePath : config.getPaths())
            {
                path = basePath + name + config.getExtension();
                if(engine.resourceExists(path))
                {
                    template = new VelocityTemplate(this, name, engine.getTemplate(path));
                }
            }
        }
        // /CLOVER:OFF
        catch(Exception e)
        {
            throw new RuntimeException("Velocity internal error, template path: '" + path + "'", e);
        }
        // /CLOVER:ON
        if(template != null)
        {
            if(config.isCacheEnabled())
            {
                synchronized(templateCache)
                {
                    templateExistsCache.put(name, Boolean.TRUE);
                    templateCache.put(name, template);
                }
            }
            return template;
        }
        throw new TemplateNotFoundException("template " + name + config.getExtension()
            + " not found");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(TemplatingContext context, Reader source, Writer target, String logTag)
        throws MergingException
    {
        boolean success = false;
        try
        {
            success = engine.evaluate(((VelocityContext)context).getContext(), target, logTag,
                source);
        }
        catch(MethodInvocationException e)
        {
            throw new MergingException("failed to render template - "
                + " exception during method invocation", e.getWrappedThrowable());
        }
        catch(Exception e)
        {
            throw new MergingException("failed to render template", e);
        }
        if(!success)
        {
            throw new MergingException("failed to render template, cause in the log");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(TemplatingContext context, Template template, Writer target)
        throws MergingException
    {
        try
        {
            ((VelocityTemplate)template).getTemplate().merge(
                ((VelocityContext)context).getContext(), target);
            // re-enable rendering if #stop directive was encountered in the nested template in
            // Velocity 1.5+
            ((org.apache.velocity.VelocityContext)((VelocityContext)context).getContext())
                .setAllowRendering(true);
        }
        catch(MethodInvocationException e)
        {
            throw new MergingException("failed to render template - "
                + " exception during method invocation", e.getWrappedThrowable());
        }
        catch(Exception e)
        {
            throw new MergingException("failed to render template", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEncoding()
    {
        return config.getEncoding();
    }

    public String getTemplateExtension()
    {
        return config.getExtension();
    }

    // Velocity logging interface implementation

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidateTemplate(String name)
    {
        if(config.isCacheEnabled())
        {
            synchronized(templateCache)
            {
                templateCache.remove(name);
                templateExistsCache.remove(name);
            }
        }
    }

    /**
     * Template engine configuration.
     */
    public static class Config
    {
        /** template paths */
        private final List<String> paths = new ArrayList<>();

        /** template file extension */
        private String extension = ".vt";

        /** template encoding */
        private String encoding = "UTF-8";

        /** Caching flag. */
        private boolean cacheEnabled = false;

        /** Engine properties. */
        private final ExtendedProperties properties = new ExtendedProperties();

        /**
         * Create a blank configuration.
         */
        public Config()
        {
        }

        /**
         * Create configuration from DNA Configuration object.
         * 
         * @param config DNA Configuration
         * @throws ConfigurationException
         */
        public Config(Configuration config)
            throws ConfigurationException
        {
            extension = config.getChild("extension").getValue(extension);
            encoding = config.getChild("encoding").getValue(encoding);
            cacheEnabled = config.getChild("cache").getValueAsBoolean(cacheEnabled);

            Configuration[] path = config.getChild("paths").getChildren("path");
            for(int i = 0; i < path.length; i++)
            {
                paths.add(path[i].getValue());
            }
            Configuration node = config.getChild("properties");
            if(node != null)
            {
                Configuration[] properties = node.getChildren("property");
                for(int i = 0; i < properties.length; i++)
                {
                    String name = properties[i].getAttribute("name");
                    String value = properties[i].getAttribute("value", null);
                    if(value == null)
                    {
                        value = properties[i].getValue();
                    }
                    this.properties.addProperty(name, value);
                }
            }
        }

        public List<String> getPaths()
        {
            return paths;
        }

        public Config withPath(String path)
        {
            this.paths.add(path);
            return this;
        }

        public Config withPaths(Collection<String> paths)
        {
            this.paths.addAll(paths);
            return this;
        }

        public String getExtension()
        {
            return extension;
        }

        public Config withExtension(String extension)
        {
            this.extension = extension;
            return this;
        }

        public String getEncoding()
        {
            return encoding;
        }

        public Config withEncoding(String encoding)
        {
            this.encoding = encoding;
            return this;
        }

        public boolean isCacheEnabled()
        {
            return cacheEnabled;
        }

        public Config withCacheEnabled(boolean cacheEnabled)
        {
            this.cacheEnabled = cacheEnabled;
            return this;
        }

        public Config withProperty(String key, String value)
        {
            this.properties.addProperty(key, value);
            return this;
        }

        void applyProperties(VelocityEngine target)
        {
            target.setExtendedProperties(properties);
        }
    }

    /**
     * A passthrough between Velocity LogChute and DNA logging.
     * 
     * @author rafal
     */
    private static class DNALogChute
        implements LogChute
    {
        /** The delegate DNA logger. */
        private Logger logger;

        /**
         * Creates a new DNALogChute instance.
         * 
         * @param logger the delegate DNA logger.
         */
        public DNALogChute(Logger logger)
        {
            this.logger = logger;
        }

        @Override
        public void init(RuntimeServices rs)
        {
        }

        @Override
        public boolean isLevelEnabled(int level)
        {
            switch(level)
            {
            case LogChute.DEBUG_ID:
                return logger.isDebugEnabled();
            case LogChute.ERROR_ID:
                return logger.isErrorEnabled();
            case LogChute.WARN_ID:
                return logger.isWarnEnabled();
            case LogChute.INFO_ID:
                return logger.isInfoEnabled();
            default:
                return logger.isDebugEnabled();
            }
        }

        /**
         * {@inheritDoc}
         */
        public void log(int level, String message)
        {
            switch(level)
            {
            case LogChute.ERROR_ID:
                logger.error(message);
                break;
            case LogChute.WARN_ID:
                logger.warn(message);
                break;
            case LogChute.INFO_ID:
                logger.info(message);
                break;
            default:
                logger.debug(message);
                break;
            }
        }

        @Override
        public void log(int level, String message, Throwable e)
        {
            switch(level)
            {
            case LogChute.ERROR_ID:
                logger.error(message, e);
                break;
            case LogChute.WARN_ID:
                logger.warn(message, e);
                break;
            case LogChute.INFO_ID:
                logger.info(message, e);
                break;
            default:
                logger.debug(message, e);
                break;
            }
        }
    }
}
