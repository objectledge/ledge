/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 2, 2003
 */
package org.objectledge.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: AllTests.java,v 1.1 2003-12-02 13:09:18 fil Exp $
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge.xml");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(XMLValidatorTest.class));
        //$JUnit-END$
        return suite;
    }
}
