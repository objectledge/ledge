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
import org.objectledge.pico.StringParameter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.reflection.DefaultReflectionFrontEnd;
import org.picoextras.reflection.ReflectionFrontEnd;
import org.picoextras.script.PicoCompositionException;
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
 * @version $Id: LedgeXmlFrontEnd.java,v 1.11 2004-01-15 15:38:59 fil Exp $
 */
public class LedgeXmlFrontEnd 
    implements XmlFrontEnd
{
    // constants /////////////////////////////////////////////////////////////////////////////////
    
    /** Location of the container composition schema. */
    public static final String SCHEMA_PATH = "org/objectledge/pico/xml/container.rng";
    
    // instance variables ////////////////////////////////////////////////////////////////////////
    
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
        String preload = componentElement.getAttribute("preload");

        Parameter[] parameters = loadParameters(componentElement);

        Class implementation = loadClass(className);
        Object key = getKey(componentElement, implementation);
        if(parameters.length != 0)
        {
            reflectionFrontEnd.getPicoContainer().
                registerComponentImplementation(key, implementation, parameters);
        }
        else
        {
            reflectionFrontEnd.registerComponentImplementation(key, className);
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
        String stringValue = element.getAttribute("value");
        return new StringParameter(stringValue);
    }
    
    /**
     * @param element
     * @return
     */
    private Parameter loadComponentParameter(Element element)
        throws ClassNotFoundException, PicoCompositionException
    {
        return new ComponentParameter(getKey(element, null));
    }
    
    /**
     * @param element
     * @return
     */
    private Parameter loadSequenceParameter(Element element)
        throws ClassNotFoundException, PicoCompositionException
    {
        Parameter[] parameters = loadParameters(element);
        return new SequenceParameter(parameters);
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
     * Returns a key for a component based on element's attributes.
     * 
     * @param elm the DOM Element.
     * @param key the default key.
     * @return a component key.
     * @throws ClassNotFoundException if class-key is specified, and the value does not denote an
     *         existing class.
     * @throws PicoCompositionException if key nor class-key are not defined, and default key is 
     *         <code>null</code>.
     */
    private Object getKey(Element elm, Object key) 
        throws ClassNotFoundException
    {
        String stringKey = elm.getAttribute("key");
        String classKey = elm.getAttribute("class-key");
        if(classKey != null && !classKey.equals(""))
        {
            return loadClass(classKey);
        } 
        else if(stringKey != null && !stringKey.equals(""))
        {
            return stringKey;
        }
        return key;
    }

    /**
     * Loads the contents of a container.
     * 
     * @param reflectionFrontEnd the reflection front end.
     * @param containerElement the container composition element.
     * @return a reflection front end (same as recieved, or different in the container has 
     *         replace="true" attribute.
     * @throws ClassNotFoundException if a missing class is referenced.
     * @throws PicoCompositionException if the composition data is invalid.
     */
    protected ReflectionFrontEnd loadContainerContents(ReflectionFrontEnd reflectionFrontEnd, 
        Element containerElement) 
        throws ClassNotFoundException, PicoCompositionException 
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
                    MutablePicoContainer childContainer =  loadContainer((Element)child);
                    ReflectionFrontEnd childFrontEnd = createReflectionFrontEnd(reflectionFrontEnd, 
                        childContainer);
                    loadContainerContents(childFrontEnd, (Element)child);
                    Object key = getKey((Element)child, null);
                    if(key != null)
                    {
                        reflectionFrontEnd.getPicoContainer().registerComponentInstance(key, 
                            childContainer);
                    }
                    String replace = ((Element)child).getAttribute("replace");
                    if(replace != null && replace.equals("true"))
                    {
                        reflectionFrontEnd = createReflectionFrontEnd((MutablePicoContainer)
                            childContainer);
                    }
                    componentCount++;
                }
            }
        }
        return reflectionFrontEnd;
    }

    /**
     * Loads a container.
     * 
     * @param element composition element.
     * @return a pico container.
     * @throws ClassNotFoundException if a missing class is referenced.
     */
    protected MutablePicoContainer loadContainer(Element element)
        throws ClassNotFoundException
    {
        Object key = getKey(element, MutablePicoContainer.class);
        String picoContainerClassName = element.getAttribute("class");
        String adapterFactoryClass = element.getAttribute("adapter-factory");

        ReflectionFrontEnd tempContainer = new DefaultReflectionFrontEnd();

        if (adapterFactoryClass != null && !adapterFactoryClass.equals("")) {
            tempContainer.registerComponentImplementation(ComponentAdapterFactory.class, 
                adapterFactoryClass);
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
            tempContainer.registerComponentImplementation(key, 
                DefaultPicoContainer.class.getName());
        } 
        else 
        {
            tempContainer.registerComponentImplementation(key, picoContainerClassName); 
        }
        MutablePicoContainer container = (MutablePicoContainer) tempContainer.getPicoContainer().
            getComponentInstance(key);
        container.registerComponentInstance(key, container);
        return (MutablePicoContainer) tempContainer.getPicoContainer().
            getComponentInstance(key);
    }

    /**
     * Loads a pseudo component.
     * 
     * @param pico the reflection front end.
     * @param componentElement pseudo component composition element.
     * @throws ClassNotFoundException if a missing class is refernced.
     * @throws PicoCompositionException if the composition data is invalid.
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
        tempContainer.registerComponentImplementation(XmlPseudoComponentFactory.class.getName(), 
            factoryClass);
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
     * Creates a reflection front end.
     *  
     * @param container the container.
     * @return a reflection front end.
     */
    protected ReflectionFrontEnd createReflectionFrontEnd(MutablePicoContainer container) 
    {
        return new DefaultReflectionFrontEnd(container);
    }
    
    /**
     * Creates a reflection front end.
     *  
     * @param parent the parent front end.
     * @param container the container.
     * @return a reflection front end.
     */
    protected ReflectionFrontEnd createReflectionFrontEnd(ReflectionFrontEnd parent, 
        MutablePicoContainer container) 
    {
        return new DefaultReflectionFrontEnd(parent, container);
    }

    /**
     * Loads a class definition.
     * 
     * @param className the name of the class.
     * @return class definition object.
     * @throws ClassNotFoundException if the class is missing.
     */    
    protected Class loadClass(String className)
        throws ClassNotFoundException
    {
        return Class.forName(className);
    }
}
