package org.objectledge.pipeline;

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
        TestSuite suite = new TestSuite("Test for org.objectledge.pipeline");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(PipelineTest.class));
        //$JUnit-END$
        return suite;
    }
}
