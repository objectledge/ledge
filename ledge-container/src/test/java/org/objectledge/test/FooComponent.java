/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.test;

import org.jcontainer.dna.Configuration;

/**
 * A simple test component.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: FooComponent.java,v 1.2 2003-11-28 15:53:07 fil Exp $
 */
public class FooComponent
{
    private Configuration configuration;
    
    public FooComponent(Configuration configuration)
    {
        this.configuration = configuration;
    }
    
    public Configuration getConfiguration()
    {
        return configuration;
    }
}
