/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 3, 2003
 */
package org.objectledge.logging;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: AllTests.java,v 1.1 2003-12-03 15:38:36 fil Exp $
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge.logging");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(LoggingConfiguratorTest.class));
        //$JUnit-END$
        return suite;
    }
}
