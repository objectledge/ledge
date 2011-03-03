package org.objectledge.html;

import java.io.Writer;

import org.dom4j.Document;

/**
 * DocumentService is used to operate on CMS documents.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLService.java,v 1.3 2005-01-20 10:59:17 pablo Exp $
 */
public interface HTMLService
{
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
     * Parse HTML text into a Dom4j document with cleanup and validation.
     * <p>
     * This method will fix trivial errors like improperly closed tags or missing quotes on
     * attributes but will fail on serious errors illegal tags.
     * </p>
     * 
     * @param html HTML text.
     * @param errorWriter writer to receive error messages.
     * @param cleanupProfile name of the cleanup profile to be used, or <code>null</code> to skip
     *        cleanup.
     * @return HTML document or <code>null</code> on unsuccessful validation.
     * @throws HTMLException if the there is a technical problem running the validation. All that
     *         end user can possibly fix will be reported to errorWriter.
     */
    public Document textToDom4j(String html, Writer errorWriter, String cleanupProfile)
        throws HTMLException;

    /**
     * Serialized HTML document from Dom4j tree into text.
     * <p>
     * Document will be pretty printed as XHTML.
     * </p>
     * 
     * @param writer writer to receive text.
     * @param bodyContentOnly only children nodes of BODY element will be serialized when enabled.
     * @throws HTMLException if the document could not be serialized.
     */
    public void dom4jToText(Document dom4jDoc, Writer writer, boolean bodyContentOnly)
        throws HTMLException;

    /**
     * Collect all text content in a HTML document.
     * <p>
     * Provided for building full text search indexes.
     * </p>
     * 
     * @param dom4jDoc HTML document.
     * @return collected text content.
     * @throws HTMLException if the document could not be parsed.
     */
    public String collectText(Document dom4jDoc);

    /**
     * Removes from the documents all P tags that contain only whitespace.
     * 
     * @param html a HTML document that will be altered.
     */
    public void removeEmptyParas(Document html);
    
    /**
     * Removes those BR tags from within P tags that are either preceded by whitespace content only, or followed by whitespace content only.
     * 
     * @param html a HTML document that will be altered.
     */
    public void trimBreaksFromParas(Document html);
    
    /**
     * Collapses each sequence of BR elements (whith possible intervening whitestpace) into a single BR element in each P element of the document.
     * 
     * @param html a HTML document that will be altered.
     */
    public void collapseSubsequentBreaksInParas(Document html);
}
