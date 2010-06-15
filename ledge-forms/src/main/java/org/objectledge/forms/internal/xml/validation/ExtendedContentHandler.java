package org.objectledge.forms.internal.xml.validation;

import org.xml.sax.SAXException;

/**
 * An interface for extended document information set.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ExtendedContentHandler.java,v 1.1 2005-01-20 16:44:51 pablo Exp $
 */
public interface ExtendedContentHandler
{
    /** receives notification of the start of a document.
     */
    public void startDocument()
    throws SAXException;

    /** receives notification of the end of a document.
     */
    public void endDocument()
    throws SAXException;

    /** receives notification of the start of an element.
     *
     * If this element has attributes, the start/endAttribute methods are
     * called after this method.
     */
    public void startElement(String namespaceURI, String localName, String qName)
    throws SAXException;

    /** receives notification of the start of an attribute.
     *
     * the value of the attribute is reported through the characterChunk method.
     */
    public void startAttribute(String namespaceURI, String localName, String qName)
    throws SAXException;

    /** receives notification of the end of an attribute.
     */
    public void endAttribute(String namespaceURI, String localName, String qName)
    throws SAXException;

    /** this method is called after the start/endAttribute method are called
     * for all attributes.
     */
    public void endAttributePart()
    throws SAXException;

    /** receives notification of the end of an element.
     */
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException;

    /** receives notification of a string.
     *
     * @param literal
     * 		the contents.
     */
    public void characterChunk(String literal)
    throws SAXException;
}
