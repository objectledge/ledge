package org.objectledge.parameters;

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
        TestSuite suite = new TestSuite("Test for org.objectledge.parameters");
        //$JUnit-BEGIN$

        //$JUnit-END$
		suite.addTest(org.objectledge.parameters.impl.AllTests.suite());
        return suite;
    }
}
