// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.pico.xml;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.objectledge.pico.SequenceParameter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.reflection.DefaultReflectionFrontEnd;
import org.picoextras.reflection.ReflectionFrontEnd;
import org.picoextras.reflection.StringToObjectConverter;
import org.picoextras.script.PicoCompositionException;
import org.picoextras.script.xml.EmptyCompositionException;
import org.picoextras.script.xml.XmlFrontEnd;
import org.picoextras.script.xml.XmlPseudoComponentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * An extension to DefaultXmlFront end that supports specification of paramters in components
 * declarations.
 *
 * <p>Created on Dec 8, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeXmlFrontEnd.java,v 1.1 2003-12-09 12:41:42 fil Exp $
 */
public class LedgeXmlFrontEnd 
    implements XmlFrontEnd
{
    // instance variables ////////////////////////////////////////////////////////////////////////
    
    private StringToObjectConverter converter = new StringToObjectConverter();

    // XmlFrontEnd interface /////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */    
    public PicoContainer createPicoContainer(Element rootElement) 
        throws IOException, SAXException, ClassNotFoundException, PicoCompositionException 
    {
        MutablePicoContainer mutablePicoContainer = loadContainer(rootElement);
        return createPicoContainer(rootElement, mutablePicoContainer);
    }    
    
    /**
     * {@inheritDoc}
     */    
    public PicoContainer createPicoContainer(Element rootElement, 
        MutablePicoContainer mutablePicoContainer)
        throws IOException, SAXException, ClassNotFoundException, PicoCompositionException 
    {
        ReflectionFrontEnd rootReflectionFrontEnd = createReflectionFrontEnd(mutablePicoContainer);
        return loadContainerContents(rootReflectionFrontEnd, rootElement).
            getPicoContainer();
    }
    
    // implementation ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Registers a component using information from an DOM element.
     *  
     * @param reflectionFrontEnd the ReflectionFrontEnd to use.
     * @param componentElement the element,
     * @throws ClassNotFoundException if the DOM model references a nonexistent class.
     * @throws PicoCompositionException if the DOM model contains invalid data.
     */
    protected void loadComponent(ReflectionFrontEnd reflectionFrontEnd, 
        Element componentElement) 
        throws ClassNotFoundException, PicoCompositionException
    {
        String className = componentElement.getAttribute("class");
        String stringKey = componentElement.getAttribute("key");
        String preload = componentElement.getAttribute("preload");

        Parameter[] parameters = loadParameters(componentElement);

        Class implementation = loadClass(className);
        Object key;
        if (stringKey == null || stringKey.equals("")) 
        {
            key = implementation;
        }
        else
        {
            key = stringKey;    
        }
        if(parameters.length != 0)
        {
            reflectionFrontEnd.getPicoContainer().
                registerComponentImplementation(key, implementation, parameters);
        }
        else
        {
            reflectionFrontEnd.registerComponent(key, className);
        }
        if(preload != null && preload.equals("true"))
        {
            reflectionFrontEnd.getPicoContainer().getComponentInstance(key);
        }
    }

    private Parameter[] loadParameters(Element element)
        throws ClassNotFoundException, PicoCompositionException
    {
        ArrayList parameters = new ArrayList();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) 
        {
            Node child = children.item(i);
            if(child.getNodeType() == Node.ELEMENT_NODE)
            {
                parameters.add(loadParameter((Element)child));        
            }
        }
        Parameter[] result = new Parameter[parameters.size()];
        parameters.toArray(result);
        return result;
    }

    /**
     * Returns a parameter based on a DOM element.
     * 
     * @param element the element to be used.
     * @return an array a parameter.
     * @throws ClassNotFoundException if the DOM model references a nonexistent class.
     * @throws PicoCompositionException if the DOM model contains invalid data.
     */
    protected Parameter loadParameter(Element element)
        throws ClassNotFoundException, PicoCompositionException
    {
        if(element.getNodeName().equals("constant-parameter"))
        {
            return loadConstantParameter(element);
        }
        else if(element.getNodeName().equals("component-parameter"))
        {
            return loadComponentParameter(element);
        }
        else if(element.getNodeName().equals("sequence-parameter"))
        {
            return loadSequenceParameter(element);
        }
        else
        {
            throw new UnknownElementException("unknown element "+element.getTagName()); 
        }
    }

    /**
     * @param element
     * @return
     */
    private Parameter loadConstantParameter(Element element)
        throws ClassNotFoundException, PicoCompositionException
    {
        String className = element.getAttribute("class");
        String stringValue = element.getAttribute("value");
        if(className == null || className.equals(""))
        {
            className = "java.lang.String";
        }
        Class desiredClass = loadClass(className);
        Object value = converter.convertTo(desiredClass, stringValue);
        return new ConstantParameter(value);
    }
    
    /**
     * @param element
     * @return
     */
    private Parameter loadComponentParameter(Element element)
        throws ClassNotFoundException, PicoCompositionException
    {
        String componentClass = element.getAttribute("class");
        String componentName = element.getAttribute("key");
        Object key;
        if(componentClass != null && !componentClass.equals(""))
        {
            key = loadClass(componentClass);
        }
        else
        {
            key = componentName;
        }
        return new ComponentParameter(key);
    }
    
    /**
     * @param element
     * @return
     */
    private Parameter loadSequenceParameter(Element element)
        throws ClassNotFoundException, PicoCompositionException
    {
        String paramteterClassName = element.getAttribute("class");
        Parameter[] parameters = loadParameters(element);
        Class parameterClass = loadClass(paramteterClassName);
        return new SequenceParameter(parameters, parameterClass);
    }


    private ComponentAdapterFactory loadComponentAdapterFactory(Element element)
        throws ClassNotFoundException
    {
        NodeList children = element.getChildNodes();
        ComponentAdapterFactory delegate = null;
        for (int i = 0; i < children.getLength(); i++) 
        {
            if(children.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                Element child = (Element)children.item(i);
                if(child.getTagName().equals("adapter-factory"))
                {
                    delegate = loadComponentAdapterFactory(child);
                    break;
                }
            }
        }
        String className = element.getAttribute("class");
        Class cl = loadClass(className);
        try
        {
            if(delegate != null)
            {
                try
                {
                    Constructor ctor = cl.
                        getConstructor(new Class[] { ComponentAdapterFactory.class } );
                    return (ComponentAdapterFactory)ctor.newInstance(new Object[] { delegate });
                }
                catch(NoSuchMethodError e)
                {
                    throw new NonChainableFactoryException("adapter factory "+className+
                        " does not have a nesting constructor");
                }
            }
            else
            {
                return (ComponentAdapterFactory)cl.newInstance();
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("failed to instatiate "+className, e);
        }
    }

    /**
     * 
     * @param reflectionFrontEnd
     * @param containerElement
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws PicoCompositionException
     */
    protected ReflectionFrontEnd loadContainerContents(ReflectionFrontEnd reflectionFrontEnd, 
        Element containerElement) 
        throws ClassNotFoundException, IOException, PicoCompositionException 
    {
        NodeList children = containerElement.getChildNodes();
        int componentCount = 0;
        for (int i = 0; i < children.getLength(); i++) 
        {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Node.ELEMENT_NODE) 
            {
                String name = child.getNodeName();
                if (name.equals("pseudo-component")) 
                {
                    loadPseudoComponent(reflectionFrontEnd, (Element) child);
                    componentCount++;
                } 
                if (name.equals("component")) 
                {
                    loadComponent(reflectionFrontEnd, (Element) child);
                    componentCount++;
                } 
                else if (name.equals("container")) 
                {
                    MutablePicoContainer mutablePicoContainer = 
                        loadContainer(child);
                    ReflectionFrontEnd childFrontEnd = 
                        createReflectionFrontEnd(reflectionFrontEnd, mutablePicoContainer);
                    loadContainerContents(childFrontEnd, (Element) child);
                    String replace = ((Element)child).getAttribute("replace");
                    if(replace != null && replace.equals("true"))
                    {
                        String stringKey = ((Element)child).getAttribute("key");
                        Class classKey = MutablePicoContainer.class;
                        if(stringKey != null && !stringKey.equals(""))
                        {
                            classKey = loadClass(stringKey);
                        }
                        reflectionFrontEnd = createReflectionFrontEnd((MutablePicoContainer)
                            childFrontEnd.getPicoContainer().getComponentInstance(classKey));
                    }
                    componentCount++;
                }
            }
        }
        if (componentCount == 0) 
        {
            throw new EmptyCompositionException();
        }
        return reflectionFrontEnd;
    }

    /**
     * {@inheritDoc}
     */
    protected MutablePicoContainer loadContainer(Node node)
        throws ClassNotFoundException
    {
        Element element = (Element)node;
        String picoContainerClassName = element.getAttribute("class");
        String stringKey = element.getAttribute("key");
        Class classKey = MutablePicoContainer.class;
        if(stringKey != null && !stringKey.equals(""))
        {
            classKey = loadClass(stringKey);
        } 
        String adapterFactoryClass = element.getAttribute("adapter-factory");

        ReflectionFrontEnd tempContainer = new DefaultReflectionFrontEnd();

        if (adapterFactoryClass != null && !adapterFactoryClass.equals("")) {
            tempContainer.registerComponent(ComponentAdapterFactory.class, adapterFactoryClass);
        }
        else {
            ComponentAdapterFactory adapterFactory = new DefaultComponentAdapterFactory();
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) 
            {
                if(children.item(i).getNodeType() == Node.ELEMENT_NODE)
                {
                    Element child = (Element)children.item(i);
                    if(child.getTagName().equals("adapter-factory"))
                    {
                        adapterFactory = loadComponentAdapterFactory(child);
                        break;
                    }
                }
            }                 
            tempContainer.getPicoContainer().
                registerComponentInstance(ComponentAdapterFactory.class, adapterFactory);
        }
        if (picoContainerClassName == null || picoContainerClassName.equals("")) 
        {
            tempContainer.registerComponent(classKey, 
                DefaultPicoContainer.class.getName());
        } 
        else 
        {
            tempContainer.registerComponent(classKey, picoContainerClassName); 
        }
        MutablePicoContainer container = (MutablePicoContainer) tempContainer.getPicoContainer().
            getComponentInstance(classKey);
        if(stringKey != null && !stringKey.equals(""))
        {
            container.registerComponentInstance(classKey, container);
        }
        return (MutablePicoContainer) tempContainer.getPicoContainer().
            getComponentInstance(classKey);
    }

    /**
     * 
     * @param pico
     * @param componentElement
     * @throws ClassNotFoundException
     * @throws PicoCompositionException
     */
    protected void loadPseudoComponent(ReflectionFrontEnd pico, Element componentElement) 
        throws ClassNotFoundException, PicoCompositionException 
    {
        String factoryClass = componentElement.getAttribute("factory");

        if(factoryClass == null || factoryClass.equals("")) 
        {
            throw new java.lang.IllegalArgumentException(
                "factory attribute should be specified for pseudocomponent element");
            // unless we provide a default.
        }

        ReflectionFrontEnd tempContainer = new DefaultReflectionFrontEnd();
        tempContainer.registerComponent(XmlPseudoComponentFactory.class.getName(), factoryClass);
        XmlPseudoComponentFactory factory = (XmlPseudoComponentFactory)tempContainer.
            getPicoContainer().getComponentInstances().get(0);

        NodeList nl = componentElement.getChildNodes();
        Element childElement = null;
        for (int i = 0; i < nl.getLength(); i++) 
        {
            if (nl.item(i) instanceof Element) 
            {
                childElement = (Element) nl.item(i);
                break;
            }
        }

        try 
        {
            Object pseudoComp = factory.makeInstance(childElement);
            pico.getPicoContainer().registerComponentInstance(pseudoComp);
        } 
        catch (final SAXException e) 
        {
            throw new PicoCompositionException(e);
        }
    }

    /**
     * 
     * @param container
     * @return
     */
    protected ReflectionFrontEnd createReflectionFrontEnd(MutablePicoContainer container) 
    {
        return new DefaultReflectionFrontEnd(container);
    }
    
    /**
     * 
     * @param parent
     * @param container
     * @return
     */
    protected ReflectionFrontEnd createReflectionFrontEnd(ReflectionFrontEnd parent, 
        MutablePicoContainer container) 
    {
        return new DefaultReflectionFrontEnd(parent, container);
    }

    /**
     * 
     * @param className
     * @return
     * @throws ClassNotFoundException
     */    
    protected Class loadClass(String className)
        throws ClassNotFoundException
    {
        return Class.forName(className);
    }
}
