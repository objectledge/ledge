/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.pico.customization;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizedComponentAdapter.java,v 1.1 2003-11-28 15:51:23 fil Exp $
 */
public class CustomizedComponentAdapter
    implements ComponentAdapter
{
    private Object componentKey;

    private MutablePicoContainer customizedInstanceContainer;
    
    private CustomizationProvider customizationProvider;
    
    /**
     * 
     */
    public CustomizedComponentAdapter(
        Object componentKey,
        MutablePicoContainer customizedInstanceContainer,
        CustomizationProvider customizationProvider)
    {
        this.componentKey = componentKey;
        this.customizedInstanceContainer = customizedInstanceContainer;
        this.customizationProvider = customizationProvider;
    }

    /**
     * {@inheritDoc}
     */
    public Object getComponentInstance(MutablePicoContainer dependencyContainer)
        throws PicoInitializationException, PicoIntrospectionException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Object getComponentInstance(MutablePicoContainer dependencyContainer, Object target)
        throws PicoInitializationException, PicoIntrospectionException
    {
        String marker = ((Class)target).getName();
        if(customizedInstanceContainer.hasComponent(marker))
        {
            return customizedInstanceContainer.getComponentInstance(marker);
        }
        else
        {
            Object instance = customizationProvider.getCustomizedInsatnce(dependencyContainer, target);
            customizedInstanceContainer.registerComponentInstance(marker, instance);
            return instance;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException
    {
        customizationProvider.verify(picoContainer);    
    }
    /**
     * {@inheritDoc}
     */
    public Class getComponentImplementation()
    {
        System.out.println("ouch!");
        return Object.class;
    }

    /**
     * {@inheritDoc}
     */
    public Object getComponentKey()
    {
        return componentKey;
    }
}
