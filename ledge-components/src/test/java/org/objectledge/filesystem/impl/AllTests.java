/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 25, 2003
 */
package org.objectledge.filesystem.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: AllTests.java,v 1.2 2003-11-26 09:25:05 mover Exp $
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge.filesystem.impl");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(LocalFileSystemProviderTest.class));
        //$JUnit-END$
        return suite;
    }
}
