// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.logging;

import org.apache.log4j.Appender;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.xml.DOMConfigurator;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A derivate of Log4j DOMConfigurator that uses constructor dependency injection on instantiated 
 * objects.
 *
 * <p>At this point, only FileSystem is injected as necessary to the benefit of appender 
 * implementation.</p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeDOMConfigurator.java,v 1.2 2004-12-21 06:24:27 rafal Exp $
 */
public class LedgeDOMConfigurator
	extends DOMConfigurator
{
    private MutablePicoContainer dependencyContainer;

    /**
     * Creates new LedgeDOMConfigurator instance.
     * 
     * @param fileSystem the file system the appenders should use.
     */
    public LedgeDOMConfigurator(FileSystem fileSystem)
    {
        dependencyContainer = new DefaultPicoContainer();
        dependencyContainer.registerComponentInstance(FileSystem.class, fileSystem);
    }
    
    private Class loadClass(String className) 
        throws ClassNotFoundException
    {
        ClassLoader cl = null;
        try
        {
            cl = Thread.currentThread().getContextClassLoader();
            if(cl == null)
            {
                cl = getClass().getClassLoader();
            }
            return cl.loadClass(className);
        }
        catch(ClassNotFoundException e)
        {
            throw new ClassNotFoundException("could not find "+className+" in "+cl, e);
        }
    }        

    private Object newInstance(Class clazz) throws InstantiationException
    {
        ComponentAdapter adapter = new ConstructorInjectionComponentAdapter(clazz, clazz, null);
        adapter.setContainer(dependencyContainer);
        return adapter.getComponentInstance(); 
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////

    // these should be public or protected constants in DOMConfigurator. Oh well.
    
    static final String CONFIGURATION_TAG = "log4j:configuration";

    static final String OLD_CONFIGURATION_TAG = "configuration";

    static final String RENDERER_TAG = "renderer";

    static final String APPENDER_TAG = "appender";

    static final String APPENDER_REF_TAG = "appender-ref";

    static final String PARAM_TAG = "param";

    static final String LAYOUT_TAG = "layout";

    static final String CATEGORY = "category";

    static final String LOGGER = "logger";

    static final String LOGGER_REF = "logger-ref";

    static final String CATEGORY_FACTORY_TAG = "categoryFactory";

    static final String NAME_ATTR = "name";

    static final String CLASS_ATTR = "class";

    static final String VALUE_ATTR = "value";

    static final String ROOT_TAG = "root";

    static final String ROOT_REF = "root-ref";

    static final String LEVEL_TAG = "level";

    static final String PRIORITY_TAG = "priority";

    static final String FILTER_TAG = "filter";

    static final String ERROR_HANDLER_TAG = "errorHandler";

    static final String REF_ATTR = "ref";

    static final String ADDITIVITY_ATTR = "additivity";

    static final String THRESHOLD_ATTR = "threshold";

    static final String CONFIG_DEBUG_ATTR = "configDebug";

    static final String INTERNAL_DEBUG_ATTR = "debug";

    static final String RENDERING_CLASS_ATTR = "renderingClass";

    static final String RENDERED_CLASS_ATTR = "renderedClass";
    
    // Too bad that that the configurator does not have the instantiation factored out into a 
    // separate method. Otherwise we need to duplicate large amount of code.
    
    /**
     * Used internally to parse an appender element.
     */
    protected Appender parseAppender(Element appenderElement)
    {
        String className = subst(appenderElement.getAttribute(CLASS_ATTR));
        LogLog.debug("Class name: [" + className + ']');
        try
        {
            Object instance = newInstance(loadClass(className));
            Appender appender = (Appender)instance;
            PropertySetter propSetter = new PropertySetter(appender);

            appender.setName(subst(appenderElement.getAttribute(NAME_ATTR)));

            NodeList children = appenderElement.getChildNodes();
            final int length = children.getLength();

            for (int loop = 0; loop < length; loop++)
            {
                Node currentNode = children.item(loop);

                /* We're only interested in Elements */
                if(currentNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element currentElement = (Element)currentNode;

                    // Parse appender parameters
                    if(currentElement.getTagName().equals(PARAM_TAG))
                    {
                        setParameter(currentElement, propSetter);
                    }
                    // Set appender layout
                    else if(currentElement.getTagName().equals(LAYOUT_TAG))
                    {
                        appender.setLayout(parseLayout(currentElement));
                    }
                    // Add filters
                    else if(currentElement.getTagName().equals(FILTER_TAG))
                    {
                        parseFilters(currentElement, appender);
                    }
                    else if(currentElement.getTagName().equals(ERROR_HANDLER_TAG))
                    {
                        parseErrorHandler(currentElement, appender);
                    }
                    else if(currentElement.getTagName().equals(APPENDER_REF_TAG))
                    {
                        String refName = subst(currentElement.getAttribute(REF_ATTR));
                        if(appender instanceof AppenderAttachable)
                        {
                            AppenderAttachable aa = (AppenderAttachable)appender;
                            LogLog.debug("Attaching appender named [" + refName
                                + "] to appender named [" + appender.getName() + "].");
                            aa.addAppender(findAppenderByReference(currentElement));
                        }
                        else
                        {
                            LogLog
                                .error("Requesting attachment of appender named ["
                                    + refName
                                    + "] to appender named ["
                                    + appender.getName()
                                    + "] which does not implement " 
                                    + "org.apache.log4j.spi.AppenderAttachable.");
                        }
                    }
                }
            }
            propSetter.activate();
            return appender;
        }
        /* Yes, it's ugly.  But all of these exceptions point to the same
         problem: we can't create an Appender */
        catch(Exception oops)
        {
            LogLog.error("Could not create an Appender. Reported error follows.", oops);
            return null;
        }
    }    
}
