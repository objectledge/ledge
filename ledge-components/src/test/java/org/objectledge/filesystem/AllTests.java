/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 25, 2003
 */
package org.objectledge.filesystem;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: AllTests.java,v 1.1 2003-11-25 14:56:38 fil Exp $
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge.filesystem");
        //$JUnit-BEGIN$

        //$JUnit-END$
        suite.addTest(org.objectledge.filesystem.impl.AllTests.suite());
        return suite;
    }
}
