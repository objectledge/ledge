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
 * An adapter for a component that is customized on behalf of a component that is asking for it.
 * 
 * <p>When another component is being initialized and it depends on the component that is managed 
 * by this adapter (identified by the componentKey), the customizedComponentContainer is consulted 
 * for a cached instance. If not present, the customizedContainerProvider is asked to produce an
 * instance of the customized component. Thus, different components may recieve different instnaces
 * of the managed component, depending on the customizedComponentProvider's semantics.</p>
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizedComponentAdapter.java,v 1.6 2003-12-02 15:23:33 fil Exp $
 */
public class CustomizedComponentAdapter
    implements ComponentAdapter
{
    private Object componentKey;

    private MutablePicoContainer customizedComponentContainer;
    
    private CustomizedComponentProvider customizedComponentProvider;
    
    /**
     * Crates a new instance of the adapter.
     * 
     * @param componentKey the component key this adapter manages.
     * @param customizedComponentContainer a container for storing customized components.
     * @param customizedComponentProvider a provider of customized components.
     */
    public CustomizedComponentAdapter(
        Object componentKey,
        MutablePicoContainer customizedComponentContainer,
        CustomizedComponentProvider customizedComponentProvider)
    {
        this.componentKey = componentKey;
        this.customizedComponentContainer = customizedComponentContainer;
        this.customizedComponentProvider = customizedComponentProvider;
    }

    /**
     * {@inheritDoc}
     */
    public Object getComponentInstance(MutablePicoContainer dependencyContainer)
        throws PicoInitializationException, PicoIntrospectionException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getComponentInstance(MutablePicoContainer dependencyContainer, 
        Object componentKey, Class componentImplementation)
        throws PicoInitializationException, PicoIntrospectionException, UnsupportedKeyTypeException
    {
        String marker = ((Class)componentKey).getName();
        if(customizedComponentContainer.hasComponent(marker))
        {
            return customizedComponentContainer.getComponentInstance(marker);
        }
        else
        {
            ComponentAdapter adapter = customizedComponentProvider.
                getCustomizedAdapter(dependencyContainer, componentKey, componentImplementation);
            customizedComponentContainer.registerComponent(adapter);
            return adapter.getComponentInstance(dependencyContainer);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException
    {
        customizedComponentProvider.verify(picoContainer);    
    }
    
    /**
     * {@inheritDoc}
     */
    public Class getComponentImplementation()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object getComponentKey()
    {
        return componentKey;
    }
}
