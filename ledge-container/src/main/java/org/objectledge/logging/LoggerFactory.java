// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
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

package org.objectledge.logging;

import org.apache.log4j.LogManager;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.pico.customization.CustomizedComponentProvider;
import org.objectledge.pico.customization.UnsupportedKeyTypeException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LoggerFactory.java,v 1.14 2004-12-22 08:02:09 rafal Exp $
 */
public class LoggerFactory
    implements CustomizedComponentProvider
{
    private MutablePicoContainer loggerContainer;

    private PicoContainer container;

    /**
     * Creates a new instance of Factory and installs apropriate component adapter.
     * 
     * <p>We depend LoggingConfigurator to make sure that logging is configured before we create any
     * logs.</p>
     * 
     * @param loggingConfigurator the LoggingConfigurator.
     */
    public LoggerFactory(LoggingConfigurator loggingConfigurator)
    {
        this.loggerContainer = new DefaultPicoContainer();
    }

    /**
     * {@inheritDoc}
     */
    public void setContainer(PicoContainer container)
    {
        this.container = container;
    }
    
    /**
     * {@inheritDoc}
     */
    public PicoContainer getContainer()
    {
        return container;
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
        if(loggerContainer.getComponentInstance(marker) == null)
        {
            Logger logger = createLogger(marker);
            ComponentAdapter adapter = createLoggerAdapter(marker, logger);
            loggerContainer.registerComponent(adapter);
        }
        return (Logger)loggerContainer.getComponentInstance(marker);
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
        if(loggerContainer.getComponentInstance(marker) != null)
        {
            ComponentAdapter adapter = createLoggerAdapter(marker, logger);
            loggerContainer.registerComponent(adapter);
        }
        else
        {   
            // Swappable proxy = (Swappable)loggerContainer.getComponentInstance(marker);
            // proxy.hotswap(logger);             
        }
    }

    // CustomizedComponentProvider interface ////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter getCustomizedAdapter(Object componentKey, Class componentImplementaion)
        throws PicoInitializationException, PicoIntrospectionException, UnsupportedKeyTypeException
    {
        String marker = getComponentMarker(componentKey);
        if(loggerContainer.getComponentInstance(marker) == null)
        {
            Logger logger = createLogger(marker);
            return createLoggerAdapter(marker, logger);
        }
        else
        {
            return loggerContainer.getComponentAdapter(marker);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class getCustomizedComponentImplementation()
    {
        return Logger.class;
    }

    /**
     * {@inheritDoc}
     */
    public void verify() throws PicoVerificationException
    {
        // no dependencies
    }
    
    // implementation ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns a marker for the component (used as key in the logger container).
     * 
     * @param key reqesting component key.
     * @return key for the logger.
     */
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
    
    /**
     * Creates a new logger adapter.
     * 
     * @param marker the marker for the logger.
     * @param logger the logger object.
     * @return a component adapter.
     */
    protected ComponentAdapter createLoggerAdapter(String marker, Logger logger)
    {
        ComponentAdapter adapter = new InstanceComponentAdapter(marker, logger);
        // adapter = new ImplementationHidingComponentAdapter(adapter);
        // adapter = new CachingComponentAdapter(adapter);
        return adapter;
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
