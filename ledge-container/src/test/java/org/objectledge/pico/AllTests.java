/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 28, 2003
 */
package org.objectledge.pico;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: AllTests.java,v 1.1 2003-11-28 15:52:52 fil Exp $
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge.pico");
        //$JUnit-BEGIN$

        //$JUnit-END$
        suite.addTest(org.objectledge.pico.customization.AllTests.suite());
        return suite;
    }
}
