/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 25, 2003
 */
package org.objectledge;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ComponentsTests.java,v 1.3 2003-12-02 13:09:25 fil Exp $
 */
public class ComponentsTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge");
        //$JUnit-BEGIN$

        //$JUnit-END$
        suite.addTest(org.objectledge.filesystem.AllTests.suite());
		suite.addTest(org.objectledge.parameters.AllTests.suite());
		suite.addTest(org.objectledge.pipeline.AllTests.suite());
        suite.addTest(org.objectledge.xml.AllTests.suite());
        return suite;
    }
}
