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

import junit.framework.Assert;

import com.meterware.httpunit.WebImage;
import com.meterware.httpunit.WebLink;

import net.sourceforge.jwebunit.WebTestCase;

/**
 * Base class for ObjectLedge Web functional testcases
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeWebTestCase.java,v 1.5 2005-05-04 10:55:40 pablo Exp $
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
        String actualView = Utils.getActualView(getTester().getDialog().getResponseText());
        if(actualView == null)
        {
            Assert.fail("unable to determine current view, expected: "+expectedView);
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
        String actionResult = Utils.getActionResult(getTester().getDialog().getResponseText());
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
        String actionResult = Utils.getActionResult(getTester().getDialog().getResponseText());
        if(actionResult != null)
        {
            Assert.fail("exected no result but action reported "+actionResult);
        }
    }      
    
    
    // some usefull methods..
    protected WebLink getLinkWithString(String text)
        throws Exception
    {
        WebLink[] links = getTester().getDialog().getResponse().getLinks();
        WebLink link = null;
        for(WebLink l: links)
        {
            if(l.getURLString().contains(text))
            {
                return l;
            }
        }
        return null;
    }
    
    protected int countLinksWithString(String text)
        throws Exception
    {
        int i = 0;
        WebLink[] links = getTester().getDialog().getResponse().getLinks();
        for(WebLink l: links)
        {
            if(l.getText().contains(text))
            {
                i++;
            }
        }
        return i;
    }
    
    protected int countImagesWithSource(String text)
        throws Exception
    {
        int i = 0;
        WebImage[] images = getTester().getDialog().getResponse().getImages();
        for(WebImage l: images)
        {
            if(l.getSource().contains(text))
            {
                i++;
            }
        }
        return i;
    }
     
}
