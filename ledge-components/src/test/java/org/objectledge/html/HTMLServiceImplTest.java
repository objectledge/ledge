package org.objectledge.html;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.jcontainer.dna.Configuration;
import org.objectledge.test.LedgeTestCase;

public class HTMLServiceImplTest
    extends LedgeTestCase
{
    private HTMLService htmlService;

    public void setUp()
        throws Exception
    {
        super.setUp();
        Configuration config = getConfig(getFileSystem(),
            "config/org.objectledge.html.HTMLService.xml");
        htmlService = new HTMLServiceImpl(config);
    }

    public void testCollapseSubsequentBreaksInParas()
        throws Exception
    {
        String html = getFileSystem().read("html/subsequentBreaks.html", "UTF-8");
        Document doc = htmlService.textToDom4j(html);
        for(Element para : (List<Element>)doc.selectNodes("//P"))
        {
            assertTrue("moot test "+para.asXML(), hasLeadingBreak(para) || hasTrailingBreak(para) || hasBreakSequence(para));
        }
        htmlService.collapseSubsequentBreaksInParas(doc);
        for(Element para : (List<Element>)doc.selectNodes("//P"))
        {
            assertFalse("incorrect output "+para.asXML(), hasLeadingBreak(para) || hasTrailingBreak(para) || hasBreakSequence(para));
        }
    }

    private boolean hasLeadingBreak(Element e)
    {
        for(int i = 0; i < e.nodeCount(); i++)
        {
            if(e.node(i) instanceof Text && e.node(i).getText().trim().length() > 0)
            {
                return false;
            }
            if(e.node(i) instanceof Element)
            {
                return e.node(i).getName().equals("BR");
            }
        }
        return false;
    }

    private boolean hasTrailingBreak(Element e)
    {
        for(int i = e.nodeCount() - 1; i >= 0; i--)
        {
            if(e.node(i) instanceof Text && e.node(i).getText().trim().length() > 0)
            {
                return false;
            }
            if(e.node(i) instanceof Element)
            {
                return e.node(i).getName().equals("BR");
            }
        }
        return false;
    }  
    
    private boolean hasBreakSequence(Element e)
    {
        int breakSequence = 0;
        for(int i = 0; i < e.nodeCount(); i++)
        {
            if(e.node(i) instanceof Element)
            {
                if(e.node(i).getName().equals("BR"))
                {
                    breakSequence++;
                }
                else
                {
                    breakSequence = 0;
                }
            }
            else if(e.node(i) instanceof Text && e.node(i).getText().trim().length() > 0)
            {
                breakSequence = 0;
            }
            if(breakSequence > 1)
            {
                return true;
            }
        }
        return false;
    }
    
}
