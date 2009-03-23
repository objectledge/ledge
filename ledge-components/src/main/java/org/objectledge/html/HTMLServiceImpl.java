package org.objectledge.html;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.Purifier;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;

/** Implementation of the DocumentService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLServiceImpl.java,v 1.8 2005-12-30 11:46:03 rafal Exp $
 */
public class HTMLServiceImpl
	implements HTMLService
{
    /**
     * {@inheritDoc}
     */
    @Override
	public org.dom4j.Document emptyDom4j()
    {
        DocumentFactory factory = DocumentFactory.getInstance();
        org.dom4j.Document document = factory.createDocument();
        Element html = document.addElement("HTML");
        html.addElement("HEAD").addElement("TITLE");
        html.addElement("BODY");
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document textToDom4j(String html) throws HTMLException
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Document textToDom4j(String html,  Writer errorWriter,
        Properties tidyConfiguration) throws HTMLException
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

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void dom4jToText(org.dom4j.Document dom4jDoc, Writer writer, boolean bodyContentOnly)
    throws HTMLException
    {       
        OutputFormat format = new OutputFormat();
        format.setXHTML(true);
        format.setExpandEmptyElements(true);
        format.setTrimText(false);
        format.setIndent(false);
        HTMLWriter htmlWriter = new HTMLWriter(writer, format);
        try
        {
            if(!bodyContentOnly)
            {
                htmlWriter.write(dom4jDoc);
            }
            else
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
            }
            writer.flush();
        }
        catch(IOException e)
        {
            throw new HTMLException("Could not serialize the document", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String collectText(Document html)	
    {
    	HTMLTextCollectorVisitor collector = new HTMLTextCollectorVisitor();
    	html.accept(collector);
    	return collector.getText();
    }
}