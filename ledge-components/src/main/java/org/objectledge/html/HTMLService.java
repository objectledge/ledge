package org.objectledge.html;

import java.io.Writer;
import java.util.Properties;

import org.dom4j.Document;

/**
 * DocumentService is used to operate on CMS documents.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLService.java,v 1.3 2005-01-20 10:59:17 pablo Exp $
 */
public interface HTMLService
{
    public String encodeHTML(String html, String encodingName);

    public String encodeHTMLAttribute(String html, String encodingName);

    /**
     * Collect all text content in a HTML document.
     * <p>
     * Provided for building full text search indexes.
     * </p>
     * 
     * @param html HTML document.
     * @return collected text content.
     * @throws HTMLException if the document could not be parsed.
     */
    public String collectText(String html)
        throws HTMLException;

    /**
     * Create Dom4j tree representing an empty HTML document.
     * 
     * @return empty document.
     */
    public Document emptyDom4j();

    /**
     * Parse HTML text into a Dom4j document.
     * <p>
     * This method attempts to silently fix any problems encountered in HTML. Only fatal errors will
     * be reported.
     * </p>
     * 
     * @param html HTML text.
     * @return HTML document.
     * @throws HTMLException if the document could not be parsed.
     */
    public Document textToDom4j(String html)
        throws HTMLException;

    /**
     * Serialized HTML document from Dom4j tree into text.
     * <p>
     * Document will be pretty printed as XHTML.
     * </p>
     * 
     * @throws HTMLException if the document could not be serialized.
     */
    public String dom4jToText(Document dom4jDoc)
        throws HTMLException;

    // the following three methods don't belong here - it's not HTML processing
    public Document parseXmlAttribute(String value, String attributeName)
        throws HTMLException;

    public String selectAllText(Document dom4jDoc, String xpath);

    public String selectFirstText(Document dom4jDoc, String xpath);

    // ...
    public String stripHTMLHead(String htmlDoc);

    public boolean cleanUpAndValidate(String value, Writer outputWriter, Writer errorWriter,
        Properties tidyConfiguration);
}
