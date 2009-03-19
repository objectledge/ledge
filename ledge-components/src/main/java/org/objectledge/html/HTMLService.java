package org.objectledge.html;

import org.dom4j.Document;

/**
 * DocumentService is used to operate on CMS documents.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLService.java,v 1.3 2005-01-20 10:59:17 pablo Exp $
 */
public interface HTMLService
{
    public static final String SERVICE_NAME = "html";

    public static final String LOGGING_FACILITY = "html";

    public String encodeHTML(String html, String encodingName)
        throws Exception;

    public String htmlToText(String html)
        throws HTMLException;

    public Document parseHTML(String html)
        throws HTMLException;

    public String serializeHTML(Document dom4jDoc)
        throws HTMLException;

    public Document parseXmlAttribute(String value, String attributeName)
        throws HTMLException;

    public String getAllText(org.dom4j.Document metaDom, String xpath);

    public String getFirstText(Document metaDom, String xpath);

    /**
     * Removes everything but <code>&lt;body&gt;</code> tag contents. This one is stupid and assumes
     * that there is no > cahractr in any of body tags attribute values.
     */
    public String stripHTMLHead(String htmlDoc);

    public Document emptyHtmlDom();
}
