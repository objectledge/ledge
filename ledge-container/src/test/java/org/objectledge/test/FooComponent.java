/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.test;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;

/**
 * A simple test component.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: FooComponent.java,v 1.3 2003-12-02 15:24:22 fil Exp $
 */
public class FooComponent
{
    private Configuration configuration;
    
    private Logger logger;
    
    public FooComponent(Configuration configuration, Logger logger)
    {
        this.configuration = configuration;
        this.logger = logger;
    }
    
    public Configuration getConfiguration()
    {
        return configuration;
    }
    
    public void log()
    {
        logger.info("yipee!");
    }
}
