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
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: CustomizationProvider.java,v 1.2 2003-12-01 09:18:32 fil Exp $
 */
public interface CustomizationProvider
{
    public Object getCustomizedInsatnce(MutablePicoContainer dependenciesContainer, 
        Object componentKey, Class componentImplementaion)
        throws PicoInitializationException, PicoIntrospectionException;
    
    public void verify(PicoContainer container) 
        throws NoSatisfiableConstructorsException;
}
