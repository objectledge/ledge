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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.meterware.httpunit.HTMLElementPredicate;
import com.meterware.httpunit.WebImage;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebTable;

import net.sourceforge.jwebunit.WebTestCase;

/**
 * Base class for ObjectLedge Web functional testcases
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeWebTestCase.java,v 1.12 2005-05-20 00:47:22 rafal Exp $
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
		beginAt("/action/i18n.SetLocale/locale/pl_PL");
        beginAt("/action/webcore,SetLocale/locale/pl_PL");
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
        if(actionResult != null && actionResult.length() > 0)
        {
            Assert.fail("exected no result but action reported "+actionResult);
        }
    }      
    
    
    // -- utiltity methods ----------------------------------------------------------------------
    
    /**
     * Retruns the first link that contains the specified text in it's href attribute.
     * 
     * @param text the text to find.
     * @return first matching link, or null if not found.
     * @throws Exception if document traversal fails. 
     */
    protected WebLink getLinkWithURL(String text)
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

    /**
     * Returns the count of the links that contain the specified text in their body.
     * 
     * @param text the text to find.
     * @return the number of matching links.
     * @throws Exception if document traversal fails.
     */
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

    /**
     * Returns the count of the links with specified text in their body.
     * 
     * @param text the text to find.
     * @return the number of matching links.
     * @throws Exception if document traversal fails.
     */
    protected int countLinksWithExactString(String text)
	    throws Exception
	{
	    int i = 0;
	    WebLink[] links = getTester().getDialog().getResponse().getLinks();
	    for(WebLink l: links)
	    {
	        if(l.getText().equals(text))
	        {
	            i++;
	        }
	    }
	    return i;
	}

    /**
     * Returns the count of images with source URL containing the specified text.
     * 
     * @param text text to find.
     * @return nubmer of matching images.
     * @throws Exception if document traversal fails.
     */
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
    
    /**
     * Returns the count of occurances of a specified string in the page body.
     * 
     * @param text the text to find.
     * @return the number of occurances.
     */
	protected int countString(String text)
	{
		String response = getTester().getDialog().getResponseText();
		int counter = 0;
		int index = response.indexOf(text);
		while(index >= 0)
		{
			counter++;
			index = response.indexOf(text, index + text.length());
		}
		return counter;
	}

    /**
     * Returns the nubmer of occurances of a specified tag in the page body.
     * 
     * @param tagName name of the tag.
     * @return the number of occurances.
     * @throws Exception if document traversal fails.
     */
    protected int countElements(String tagName)
        throws Exception
    {
        Document doc = getTester().getDialog().getResponse().getDOM();
        DOMTreeWalker walker = new DOMTreeWalker(doc.getDocumentElement());
        return walker.countTags(tagName);
    }

    /**
     * Returns the table element that contains the specified text.  
     * 
     * @param text the text to find.
     * @return matching table element.
     * @throws Exception if tree traversal fails.
     */
    public Element getTableWithText(String text)
        throws Exception
    {
        Document doc = getTester().getDialog().getResponse().getDOM();
        DOMTreeWalker walker = new DOMTreeWalker(doc.getDocumentElement());
        return getTableWithText(walker, text);
    }
    
    /**
     * Returns the table element that contains the specified text.  
     *
     * @param walker the DOMTreeWalker component instance.
     * @param text the text to find.
     * @return matching table element.
     */
    public Element getTableWithText(DOMTreeWalker walker, String text)
    {
        return walker.findElementWithText(text,"table");
    }
    
    /**
     * Returns the contents of a table cell.
     * 
     * @param table table element.
     * @param row row number.
     * @param cell cell number.
     * @return the cell content.
     * @throws Exception if document travelsal fails.
     */
    public String getTableCellText(Element table, int row, int cell)
        throws Exception
    {
        Document doc = getTester().getDialog().getResponse().getDOM();
        DOMTreeWalker walker = new DOMTreeWalker(doc.getDocumentElement());
        return getTableRowText(walker, table, row, cell);
    }
    
    /**
     * Returns the contents of a table row.
     * 
     * @param walker DOMTreeWalker component.
     * @param table table element.
     * @param row row number.
     * @param cell starting cell number.
     * @return contents of the table row.
     */
    public String getTableRowText(DOMTreeWalker walker, Element table, int row, int cell)
    {
        walker.gotoElement(table);
        for(int i = 0; i < row+1; i++)
        {
            walker.getNextElement(0,"tr");
        }
        for(int i = 0; i < cell+1; i++)
        {
            walker.getNextElement(0,"td");
        }
        return walker.getNextText();
    }

    /**
     * A predicate for picking out table element that contains the specified string.  
     */
    public class TableContentPredicate implements HTMLElementPredicate
    {
        /**
         * {@inheritDoc}
         */
        public boolean matchesCriteria(Object htmlElement, Object criteria)
        {
            if(!(htmlElement instanceof WebTable))
            {
                return false;
            }
            WebTable table = (WebTable)htmlElement;
            String value = table.getText();
            if(value != null && value.contains((String)criteria))
            {
                return true;
            }
            return false;
        }
    }
}
