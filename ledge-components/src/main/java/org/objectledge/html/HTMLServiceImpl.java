package org.objectledge.html;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.dom4j.Comment;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.DOMReader;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.jcontainer.dna.Logger;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

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
	
	public String htmlToText(String html)
	throws HTMLException
	{
		HTMLTextCollectorVisitor collector = new HTMLTextCollectorVisitor();
		parseHTML(html).accept(collector);
		return collector.getText();
	}
    
    public org.dom4j.Document parseHTML(String html) throws HTMLException
    {
    	try
    	{
    		DOMFragmentParser parser = new DOMFragmentParser();
    		parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
    		HTMLDocument document = new HTMLDocumentImpl();
    		DocumentFragment fragment = document.createDocumentFragment();
    		InputSource source = new InputSource(new StringReader(html));
    		parser.parse(source, fragment);
    		document.getBody().appendChild(fragment);
    		DOMReader domReader = new DOMReader();
    		return domReader.read(document);
    	}
    	catch(Exception e)
    	{
    		throw new HTMLException("failed to parse HTML document", e);
    	}
    }

    public boolean cleanUpAndValidate(String value, Writer outputWriter, Writer errorWriter, Properties tidyConfiguration)
    {
        // do HTML processing
        // 1. clean up the value using jTidy
        // 1.1. get tidy
        TidyWrapper tidyWrap = TidyWrapper.getInstance();
        try
        {
            // 1.2. setup tidy
            Tidy tidy = tidyWrap.getTidy();
            tidy.setConfigurationFromProps(tidyConfiguration);
            tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
            tidy.setXHTML(true);
            tidy.setShowWarnings(false);
            // 1.3. setup streams
            ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes("UTF-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(value.length()+256);
            // 1.4. setup error information writer
            tidy.setErrout(new PrintWriter(errorWriter));
            // 1.5. run cleanup
            tidy.parse(inputStream, outputStream);
            // dump buffer to output writer
            outputWriter.write(outputStream.toString("UTF-8"));
            outputWriter.flush();
            // return true if there were no errors
            return tidy.getParseErrors() == 0;
        }
        catch(IOException e)
        {
            throw new RuntimeException("unexpected exception", e);
        }
        finally
        {
            // return tidy wrapper to the pool
            tidyWrap.release();
        }
    }

    public String serializeHTML(org.dom4j.Document dom4jDoc)
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
    public String getAllText(org.dom4j.Document metaDom, String xpath)
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
    public String getFirstText(org.dom4j.Document metaDom, String xpath)
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

    public org.dom4j.Document emptyHtmlDom()
    {
        DocumentFactory factory = DocumentFactory.getInstance();
        org.dom4j.Document document = factory.createDocument();
        Element html = document.addElement("HTML");
        html.addElement("HEAD").addElement("TITLE");
        html.addElement("BODY");
        return document;
    }
}