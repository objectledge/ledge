package org.objectledge.html;

import java.io.Writer;
import java.util.List;
import java.util.Set;

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
     * Documents that contains XML entities might have the text chopped into multiple adjacent text
     * nodes, this method fixes this by merging text nodes together.
     * 
     * @param html a HTML document that will be altered.
     */
    public void mergeAdjecentTextNodes(Document html);

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

    /**
     * Collapse whitespace sequences (ordinary spaces, non-breakable spaces and tabs) into single
     * spaces.
     * 
     * @param html a HTML document that will be altered.
     */
    public void collapseWhitespace(Document html);

    /**
     * Converts sequences of paragraphs starting with - (minus-hyphen, U+002D) or · (middle dot,
     * U+00B7) characters into UL / LI sturctures.
     * 
     * @param doc html a HTML document that will be altered.
     */
    public void bulletParasToLists(Document html);

    /**
     * Identifiers of document cleanup methods, used as apart of cleanup profile.
     */
    public enum Cleanup
    {
        /** {@link HTMLService#removeEmptyParas(Document)} */
        REMOVE_EMPTY_PARAS,

        /** {@link HTMLService#trimBreaksFromParas(Document)} */
        TRIM_BREAKS_FROM_PARAS,

        /** {@link HTMLService#collapseSubsequentBreaksInParas(Document)} */
        COLLAPSE_SUBSEQUENT_BREAKS_IN_PARAS,

        /** {@link HTMLService#collapseWhitespace(Document)} */
        COLLAPSE_WHITESPACE,

        /** {@link HTMLService#bulletsToLists(Document) } */
        BULLET_PARAS_TO_LISTS
    }

    /**
     * Apply selected cleanup methods to the document.
     * 
     * @param doc a HTML document that will be altered.
     * @param cleanups selected cleanup methods.
     */
    public void applyCleanups(Document doc, Set<Cleanup> cleanups);

    /**
     * Returns the names of available cleanup profiles.
     * 
     * @return the names of available cleanup profiles.
     */
    public List<String> availableCleanupProfiles();
}
