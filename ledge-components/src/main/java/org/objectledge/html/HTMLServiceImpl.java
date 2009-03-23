package org.objectledge.html;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.parsers.SAXParser;
import org.dom4j.Comment;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.jcontainer.dna.Logger;
import org.objectledge.encodings.HTMLEntityEncoder;

/** Implementation of the DocumentService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLServiceImpl.java,v 1.8 2005-12-30 11:46:03 rafal Exp $
 */
public class HTMLServiceImpl
	implements HTMLService
{
    private Logger log;

    private HTMLEntityEncoder encoder = new HTMLEntityEncoder();
    
    public HTMLServiceImpl(Logger logger)
    {
        log = logger;
    }

    // net.cyklotron.cms.documents.HTMLService methods /////////////////////////////////////////

	public String encodeHTML(String html, String encodingName)
	{ 
		String encodedHtml = "";
		if(html != null && html.length() > 0)
		{
			encodedHtml = encoder.encodeHTML(html, encodingName);
		}
		return encodedHtml;
	}    

    public String encodeHTMLAttribute(String html, String encodingName)
    {
        String encodedHtml = "";
        if(html != null && html.length() > 0)
        {
            encodedHtml = encoder.encodeAttribute(html, encodingName);
        }
        return encodedHtml;
    }   
	
	public String collectText(String html)
	throws HTMLException
	{
		HTMLTextCollectorVisitor collector = new HTMLTextCollectorVisitor();
		textToDom4j(html).accept(collector);
		return collector.getText();
	}
    
    public org.dom4j.Document textToDom4j(String html) throws HTMLException
    {
        try
        {
            XMLParserConfiguration parser = new HTMLConfiguration();
            Dom4jDocumentBuilder dom4jBuilder = new Dom4jDocumentBuilder();
            XMLDocumentFilter[] filters = { new Purifier(), dom4jBuilder };
            parser.setProperty("http://cyberneko.org/html/properties/filters", filters);

            XMLInputSource source = new XMLInputSource("", "", "", new StringReader(html), "UTF-8");
            parser.parse(source);

            return dom4jBuilder.getDocument();
        }
        catch(Exception e)
        {
            throw new HTMLException("failed to parse HTML document", e);
        }
    }

    public boolean cleanUpAndValidate(String html, Writer outputWriter, Writer errorWriter,
        Properties tidyConfiguration)
    {
        try
        {
            XMLParserConfiguration parser = new HTMLConfiguration();
            org.cyberneko.html.filters.Writer nekoWriter = new org.cyberneko.html.filters.Writer(
                outputWriter, "UTF-8");
            XMLDocumentFilter[] filters = { new Purifier(), nekoWriter };
            parser.setProperty("http://cyberneko.org/html/properties/filters", filters);

            XMLInputSource source = new XMLInputSource("", "", "", new StringReader(html), "UTF-8");
            parser.parse(source);

            return true;
        }
        catch(Exception e)
        {
            try
            {
                errorWriter.append("failed to validate HTML: "+e.getMessage());
                errorWriter.flush();
            }
            catch(IOException e1)
            {
                throw new RuntimeException("unexpected exception", e);
            }
            log.error("HTML validation error", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public String dom4jToText(org.dom4j.Document dom4jDoc)
    throws HTMLException
    {
        String html;
        
        StringWriter writer = new StringWriter(4096);
        OutputFormat format = new OutputFormat();
        format.setXHTML(true);
        format.setExpandEmptyElements(true);
        format.setTrimText(false);
        format.setIndent(false);
        HTMLWriter htmlWriter = new HTMLWriter(writer, format);
        try
        {
            for(Node node : (List<Node>)dom4jDoc.getRootElement().element("BODY").content())
            {
            	if(node instanceof Element)
            	{
            		htmlWriter.write((Element)node);
            	}
            	if(node instanceof Text)
            	{
            		writer.append(node.getText());
            	}
            	if(node instanceof Comment)
            	{
            		writer.append("<!--").append(node.getText()).append("-->");
            	}
            }
            html = writer.toString();
        }
        catch(IOException e)
        {
            throw new HTMLException("Could not serialize the document", e);
        }

        // hack: decode &apos;
        html = html.replace("&apos;","'");
        // rafal: I have no idea why the hack above would be necessary. ' does not get encoded as
        // &apos; by HTMLWriter. Clues anyone?
        return html;
    }

    public org.dom4j.Document parseXmlAttribute(String value, String attributeName)
        throws HTMLException
    {
        // parse a document fragment
        org.dom4j.Document fragment = null;
        try
        {
            fragment = DocumentHelper.parseText(value);
        }
        catch(org.dom4j.DocumentException e)
        {
            throw new HTMLException("The XML value for attribute '" + attributeName + "' is invalid", e);
        }
        return fragment;
    }

    @SuppressWarnings("unchecked")
    public String selectAllText(org.dom4j.Document metaDom, String xpath)
    {
        StringBuilder buf = new StringBuilder(256);
        collectText((List<Element>)metaDom.selectNodes(xpath), buf);
        return buf.toString().trim();        
    }

    @SuppressWarnings("unchecked")
    private void collectText(List<Element> elements, StringBuilder buff)  
    {
        for(Element e : elements)
        {
            buff.append(e.getTextTrim()).append(' ');
            collectText((List<Element>)e.elements(), buff);
        }
    }

    @SuppressWarnings("unchecked")
    public String selectFirstText(org.dom4j.Document metaDom, String xpath)
    {
        List<Element> elements = (List<Element>)metaDom.selectNodes(xpath);
        if(elements.size() == 0)
        {
            return "";
        }
        else
        {
            return elements.get(0).getTextTrim();
        }
    }

    /** Removes everything but <code>&lt;body&gt;</code> tag contents.
     *  This one is stupid and assumes that there is no > cahractr in any of body
     *  tags attribute values.
     */
    public String stripHTMLHead(String htmlDoc)
    {
        int bodyStartIndex = htmlDoc.indexOf("<body");
        int bodyEndIndex = htmlDoc.indexOf("</body>");
    
        if(bodyStartIndex > -1)
        {
            for(int i = bodyStartIndex; i < bodyEndIndex; i++)
            {
                if(htmlDoc.charAt(i) == '>')
                {
                    bodyStartIndex = i+1;
                    break;
                }
            }
    
            if(bodyStartIndex < bodyEndIndex)
            {
                return htmlDoc.substring(bodyStartIndex, bodyEndIndex);
            }
        }
        
        return htmlDoc;
    }

    public org.dom4j.Document emptyDom4j()
    {
        DocumentFactory factory = DocumentFactory.getInstance();
        org.dom4j.Document document = factory.createDocument();
        Element html = document.addElement("HTML");
        html.addElement("HEAD").addElement("TITLE");
        html.addElement("BODY");
        return document;
    }
}