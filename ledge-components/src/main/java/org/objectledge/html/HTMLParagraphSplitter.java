/**
 * 
 */
package org.objectledge.html;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.filters.Purifier;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.VisitorSupport;
import org.objectledge.diff.Splitter;

public class HTMLParagraphSplitter
    implements Splitter
{
    public static final HTMLParagraphSplitter INSTANCE = new HTMLParagraphSplitter();
    
    private HTMLParagraphSplitter()
    {
        // use INSTANCE
    }
    
    private static final String[] NONE = {};

    public List<String> split(String html)
    {
        XMLParserConfiguration parser = new HTMLConfiguration();
        XMLDocumentFilter[] filters = new XMLDocumentFilter[3];
        filters[0] = new Purifier();
        ElementRemover elementRemover = new ElementRemover();
        elementRemover.acceptElement("HTML", NONE);
        elementRemover.acceptElement("BODY", NONE);
        elementRemover.acceptElement("P", NONE);
        filters[1] = elementRemover;
        Dom4jDocumentBuilder dom4jBuilder = new Dom4jDocumentBuilder();
        filters[2] = dom4jBuilder;
        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        final LinkedList<String> blocks = new LinkedList<String>();

        try
        {
            XMLInputSource source = new XMLInputSource("", "", "", new StringReader(html), "UTF-8");
            parser.parse(source);
            Document doc = dom4jBuilder.getDocument();
            doc.accept(new VisitorSupport()
                {
                    private Node last = null;
                    
                    public void visit(Element element)
                    {
                        if("P".equals(element.getName()))
                        {
                            blocks.add(element.getTextTrim().trim());
                            last = element;
                        }
                    }

                    public void visit(Text text)
                    {
                        if("BODY".equals(text.getParent().getName()))
                        {
                            // filter out whitespace TEXT nodes between P elements
                            if(text.getText().trim().length() > 0)
                            {
                                if(last instanceof Text)
                                {
                                    // concatenate subsequent TEXT nodes
                                    blocks.add(blocks.removeLast() + " " + text.getText().trim());
                                }
                                else
                                {
                                    blocks.add(text.getText().trim());
                                }
                                last = text;
                            }
                        }
                    }
                });
        }
        catch(Exception e)
        {
            throw new RuntimeException("failed to process HTML", e);
        }
        return blocks;
    }
}
