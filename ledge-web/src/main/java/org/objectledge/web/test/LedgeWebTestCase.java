// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.test;

import org.xml.sax.SAXException;

import junit.framework.Assert;

import net.sourceforge.jwebunit.WebTestCase;

import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.parsing.HTMLParserFactory;

/**
 * Base class for ObjectLedge Web functional testcases
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeWebTestCase.java,v 1.3 2005-03-17 10:53:27 rafal Exp $
 */
public class LedgeWebTestCase
    extends WebTestCase
{
    /**
     * Set up test context.
     */
    public void setUp() 
    {
        String url = System.getProperty("base.url", "http://localhost:8080");
        String baseUrl = url.endsWith("/") ? url.substring(0, url.length()-1) : url;
        getTestContext().setBaseUrl(baseUrl);
    }

    // -- ObjectLedge specific asserts ----------------------------------------------------------
    
    /**
     * Assert that the currently displayed view matches expected value.
     * 
     * @param expectedView the expected view.
     */
    public void assertActualView(String expectedView)
    {
        String actualView = Utils.getActualView(tester.getDialog().getResponseText());
        if(actualView == null)
        {
            Assert.fail("unable to determine current view");
        }
        if(!actualView.equals(expectedView))
        {
            Assert.fail("expected view " + expectedView+" but current view is " + actualView);
        }
    }

    /**
     * Assert that the executed action's result matches expected value.
     * 
     * @param expectedResult the expected result.
     */
    public void assertActionResult(String expectedResult)
    {
        String actionResult = Utils.getActionResult(tester.getDialog().getResponseText());
        if(actionResult == null)
        {
            Assert.fail("unable to determine action result");
        }
        if(!actionResult.equals(expectedResult))
        {
            Assert.fail("expected result " + expectedResult + " but action reported " + 
                actionResult);
        }
    }
    
    /**
     * Assert that the executed action does not report any result.
     */
    public void assertNoActionResult()
    {
        String actionResult = Utils.getActionResult(tester.getDialog().getResponseText());
        if(actionResult != null)
        {
            Assert.fail("exected no result but action reported "+actionResult);
        }
    }       
}
