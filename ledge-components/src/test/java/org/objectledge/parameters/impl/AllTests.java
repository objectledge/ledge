package org.objectledge.parameters.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge.parameters.impl");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(ParametersImplTest.class));
        //$JUnit-END$
        return suite;
    }
}
