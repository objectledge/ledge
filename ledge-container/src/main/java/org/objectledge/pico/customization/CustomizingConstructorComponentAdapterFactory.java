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
 * A factory of CustomizingConstructorComponentAdapters.
 * 
 * <p>Use this factory at the bottom of the factory chain for your container to take advantage of
 * component customization. You'll also need to register {@link CustomizedComponentAdapters} for 
 * the types of the components you want to have customized.<p>
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizingConstructorComponentAdapterFactory.java,v 1.2 2003-12-01 13:09:45 fil Exp $
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
        return new CustomizingConstructorComponentAdapter(componentKey, componentImplementation, 
            parameters);
    }
}
