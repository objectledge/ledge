/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.pico.customization;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ConstructorComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizingConstructorComponentAdapter.java,v 1.1 2003-11-28 15:51:23 fil Exp $
 */
public class CustomizingConstructorComponentAdapter extends ConstructorComponentAdapter
{

    /**
     * @param componentKey the component's key.
     * @param componentImplementation the component implementation class.
     * @param parameters the component instantiation paramter hints.
     * @throws AssignabilityRegistrationException
     * @throws NotConcreteRegistrationException
     */
    public CustomizingConstructorComponentAdapter(
        Object componentKey,
        Class componentImplementation,
        Parameter[] parameters)
        throws AssignabilityRegistrationException, NotConcreteRegistrationException
    {
        super(componentKey, componentImplementation, parameters);
    }

    /**
     * @param componentKey the component's key.
     * @param componentImplementation the component implementation class.
     * @throws AssignabilityRegistrationException
     * @throws NotConcreteRegistrationException
     */
    public CustomizingConstructorComponentAdapter(
        Object componentKey,
        Class componentImplementation)
        throws AssignabilityRegistrationException, NotConcreteRegistrationException
    {
        super(componentKey, componentImplementation);
    }

    /**
     * {@inheritDoc}
     */
    protected Object[] getConstructorArguments(
        ComponentAdapter[] adapterDependencies,
        MutablePicoContainer picoContainer)
    {
        Object[] result = new Object[adapterDependencies.length];
        for (int i = 0; i < adapterDependencies.length; i++) {
            ComponentAdapter adapterDependency = adapterDependencies[i];
            if(adapterDependency instanceof CustomizedComponentAdapter)
            {
                result[i] = ((CustomizedComponentAdapter)adapterDependency).
                    getComponentInstance(picoContainer, getComponentKey());                                
            }
            else
            {
                result[i] = adapterDependency.getComponentInstance(picoContainer);
            }
        }
        return result;
    }
}
