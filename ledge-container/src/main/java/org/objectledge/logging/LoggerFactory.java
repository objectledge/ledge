/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 2, 2003
 */
package org.objectledge.logging;

import org.apache.log4j.LogManager;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.pico.customization.CustomizedComponentAdapter;
import org.objectledge.pico.customization.CustomizedComponentProvider;
import org.objectledge.pico.customization.UnsupportedKeyTypeException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.picocontainer.extras.DecoratingComponentAdapter;
import org.picocontainer.extras.ImplementationHidingComponentAdapter;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LoggerFactory.java,v 1.1 2003-12-02 15:23:45 fil Exp $
 */
public class LoggerFactory
    implements CustomizedComponentProvider
{
    private MutablePicoContainer loggerContainer;

    /**
     * Creates a new instance of Factory and installs apropriate component adapter.
     * 
     * @param container the container to connect to.
     */
    public LoggerFactory(MutablePicoContainer container)
    {
        loggerContainer = new DefaultPicoContainer();
        registerAdapter(container);
    }

    /**
     * Returns a logger for the specified component.
     *
     * @param key the component key.
     * @return the logger.
     */
    public Logger getLogger(Object key)
    {
        String marker = getComponentMarker(key);
        if(!loggerContainer.hasComponent(marker))
        {
            Logger logger = createLogger(marker);
            ComponentAdapter adapter = createLoggerAdapter(marker, logger);
            loggerContainer.registerComponent(adapter);
        }
        return (Logger)loggerContainer.getComponentInstance(key);
    }
    
    /**
     * Sets the logger for the specified component.
     * 
     * @param key the component key.
     * @param logger the logger.
     */
    public void setLogger(Object key, Logger logger)
    {
        String marker = getComponentMarker(key);
        if(!loggerContainer.hasComponent(marker))
        {
            ComponentAdapter adapter = createLoggerAdapter(marker, logger);
            loggerContainer.registerComponent(adapter);
        }
        else
        {                
            ComponentAdapter adapter = loggerContainer.findComponentAdapter(marker);
            ImplementationHidingComponentAdapter proxyAdapter = (ImplementationHidingComponentAdapter)
                ((DecoratingComponentAdapter)adapter).getDelegate();
            proxyAdapter.hotSwap(logger);
        }
    }

    // CustomizedComponentProvider interface ////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter getCustomizedAdapter(
        MutablePicoContainer dependenciesContainer,
        Object componentKey,
        Class componentImplementaion)
        throws PicoInitializationException, PicoIntrospectionException, UnsupportedKeyTypeException
    {
        String marker = getComponentMarker(componentKey);
        if(!loggerContainer.hasComponent(marker))
        {
            Logger logger = createLogger(marker);
            return createLoggerAdapter(marker, logger);
        }
        else
        {
            return loggerContainer.findComponentAdapter(marker);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer container) throws NoSatisfiableConstructorsException
    {
        // no dependencies
    }
    
    // implementation ///////////////////////////////////////////////////////////////////////////


    protected String getComponentMarker(Object key)
    {
        if(key instanceof Class)
        {
            return ((Class)key).getName();
        }
        else
        {
            return key.toString();
        }
    }
    
    protected ComponentAdapter createLoggerAdapter(String marker, Logger logger)
    {
        ComponentAdapter adapter = new InstanceComponentAdapter(marker, logger);
        adapter = new ImplementationHidingComponentAdapter(adapter);
        adapter = new CachingComponentAdapter(adapter);
        return adapter;
    }
    
    /**
     * Registers a CustomizedComponentAdapter for the {@link Configuration} type in the
     * specified container.
     * 
     * @param container the container.
     */
    protected void registerAdapter(MutablePicoContainer container)
    {
        ComponentAdapter loggerAdapter = new CustomizedComponentAdapter(Logger.class, 
            loggerContainer, this);
        container.registerComponent(loggerAdapter);
    }
    
    /**
     * Creates a plain logger instance.
     * 
     * @param marker component key marker.
     * @return a plain logger.
     */
    protected Logger createLogger(String marker)
    {
        return new Log4JLogger(LogManager.getLogger(marker));
    }
}
