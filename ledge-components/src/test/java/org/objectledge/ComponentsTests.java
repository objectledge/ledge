//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ComponentsTests.java,v 1.17 2004-02-13 14:16:39 pablo Exp $
 */
public class ComponentsTests
{
	private ComponentsTests()
	{
	}

	/**
	 * @return the test.
	 */
    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.objectledge");
        //$JUnit-BEGIN$

        //$JUnit-END$
        suite.addTest(org.objectledge.cache.AllTests.suite());
        suite.addTest(org.objectledge.context.AllTests.suite());
        suite.addTest(org.objectledge.event.AllTests.suite());
        suite.addTest(org.objectledge.i18n.AllTests.suite());
        suite.addTest(org.objectledge.filesystem.AllTests.suite());
        suite.addTest(org.objectledge.naming.AllTests.suite());
        suite.addTest(org.objectledge.parameters.AllTests.suite());
        suite.addTest(org.objectledge.pipeline.AllTests.suite());
        suite.addTest(org.objectledge.selector.AllTests.suite());
        suite.addTest(org.objectledge.xml.AllTests.suite());
        suite.addTest(org.objectledge.utils.AllTests.suite());
        suite.addTest(org.objectledge.templating.AllTests.suite());
        suite.addTest(org.objectledge.threads.AllTests.suite());
        suite.addTest(org.objectledge.database.AllTests.suite());
        return suite;
    }
}
