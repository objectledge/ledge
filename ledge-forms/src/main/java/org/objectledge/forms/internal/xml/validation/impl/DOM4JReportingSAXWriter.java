package org.objectledge.forms.internal.xml.validation.impl;

import org.dom4j.Element;
import org.objectledge.forms.internal.xml.validation.DOM4JContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/** <code>DOM4JReportingSAXWriter</code> is an extension of
 * <code>SAXWriter</code> which writes a DOM4J tree to a SAX ContentHandler.
 * <code>DOM4JReportingSAXWriter</code> also reports currently visited Node
 * to a set <code>DOM4JContentHandler</code>.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DOM4JReportingSAXWriter.java,v 1.1 2005-01-20 16:44:57 pablo Exp $
 */
public class DOM4JReportingSAXWriter extends org.dom4j.io.SAXWriter
{
    /** <code>DOMContentHandler</code> to which DOM events are raised */
    private DOM4JContentHandler dom4jContentHandler;

    public DOM4JReportingSAXWriter()
    {
        super();
    }

    public DOM4JReportingSAXWriter(ContentHandler contentHandler)
    {
        super(contentHandler);
    }

    public DOM4JReportingSAXWriter(ContentHandler contentHandler,
                            org.xml.sax.ext.LexicalHandler lexicalHandler,
                            org.xml.sax.EntityResolver entityResolver)
    {
        super(contentHandler, lexicalHandler, entityResolver);
    }


    /** Generates SAX events for the given text, This method is
      * a patched version of SAXWriter.write(String)
      * It does not report empty strings to avoid problems with MSV
      * validation - ie. incomplete content errors for elements which
      * may have empty strings.
      *
      * @param text is the text to send to the SAX ContentHandler
      * @throw SAXException if there is a SAX error processing the events
      */
    public void write( String text ) throws SAXException
    {
        if ( text != null && text.length() > 0)
        {
            super.write(text);
        }
    }



    public void write(org.dom4j.CDATA cdata)
    throws SAXException
    {
        currentNode(cdata);
        super.write(cdata);
    }

    public void write(org.dom4j.Comment comment)
    throws SAXException
    {
        currentNode(comment);
        super.write(comment);
    }

    public void write(org.dom4j.Entity entity)
    throws SAXException
    {
        currentNode(entity);
        super.write(entity);
    }

    public void write(org.dom4j.ProcessingInstruction pi)
    throws SAXException
    {
        currentNode(pi);
        super.write(pi);
    }

    private void currentNode(org.dom4j.Node node)
    {
        if(dom4jContentHandler != null)
        {
            dom4jContentHandler.setCurrentNode(node);
        }
    }

    //-------------------------------------------------------------------------
    // XMLReader alike methods

    /** @return the <code>ContentHandler</code> called when SAX events
     * are raised
     */
    public DOM4JContentHandler getDOM4JContentHandler()
    {
        return dom4jContentHandler;
    }

    /** Sets the <code>ContentHandler</code> called when SAX events
     * are raised
     *
     * @param contentHandler is the <code>ContentHandler</code> called when SAX events
     * are raised
     */
    public void setDOM4JContentHandler(DOM4JContentHandler contentHandler)
    {
        this.dom4jContentHandler = contentHandler;
    }

    //-------------------------------------------------------------------------
    // Implementation methods

    protected void startElement(Element element,
                    org.xml.sax.helpers.AttributesImpl namespaceAttributes)
    throws SAXException
    {
        if(dom4jContentHandler != null)
        {
            dom4jContentHandler.startElementNode(element);
        }
        super.startElement(element, namespaceAttributes);
    }

    protected void endElement( Element element )
    throws SAXException
    {
        super.endElement(element);
        if(dom4jContentHandler != null)
        {
            dom4jContentHandler.endElementNode(element);
        }
    }
}
