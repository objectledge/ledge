/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.pico.customization;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizingConstructorComponentAdapterFactory.java,v 1.1 2003-11-28 15:51:23 fil Exp $
 */
public class CustomizingConstructorComponentAdapterFactory 
    implements ComponentAdapterFactory
{
    /**
     * Constructs a new instance of the factory. 
     */
    public CustomizingConstructorComponentAdapterFactory()
    {
    }

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter createComponentAdapter(
        Object componentKey,
        Class componentImplementation,
        Parameter[] parameters)
        throws
            PicoIntrospectionException,
            AssignabilityRegistrationException,
            NotConcreteRegistrationException
    {
        return new CustomizingConstructorComponentAdapter(componentKey, componentImplementation, parameters);
    }
}
