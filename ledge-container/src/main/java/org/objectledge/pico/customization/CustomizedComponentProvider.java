/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.pico.customization;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

/**
 * Provides customized component instances, based on requesting component's key and implementation 
 * class.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizedComponentProvider.java,v 1.2 2003-12-01 13:09:45 fil Exp $
 */
public interface CustomizedComponentProvider
{
    /**
     * Returns a customized component instance.
     * 
     * @param dependenciesContainer container where the customized component dependencies should be
     *        resolved.
     * @param componentKey requesting component's key.
     * @param componentImplementaion requesting component's implmenetation class.
     * @return customized instance of the component.
     * @throws PicoInitializationException if the customized component cannot be initialized.
     * @throws PicoIntrospectionException if the customized component cannot be initialized.
     */
    public Object getCustomizedInsatnce(MutablePicoContainer dependenciesContainer, 
        Object componentKey, Class componentImplementaion)
        throws PicoInitializationException, PicoIntrospectionException;
    
    /**
     * Verifies if the customized component can be instantiated using the dependencies present
     * in the given container.
     * 
     * @param container the container to verify.
     * @throws NoSatisfiableConstructorsException if the container does not contain required
     *         dependencies.
     */
    public void verify(PicoContainer container) 
        throws NoSatisfiableConstructorsException;
}
