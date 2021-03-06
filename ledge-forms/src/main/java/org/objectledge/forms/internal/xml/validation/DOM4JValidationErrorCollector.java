package org.objectledge.forms.internal.xml.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.dom4j.Element;
import org.dom4j.Node;
import org.objectledge.forms.internal.xml.CollectingErrorHandler;
import org.xml.sax.SAXException;


/**
 * ErrorHandler that collects all errors.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DOM4JValidationErrorCollector.java,v 1.4 2005-05-20 00:46:27 rafal Exp $
 */
public class DOM4JValidationErrorCollector
     extends CollectingErrorHandler
implements DOM4JContentHandler, ExtendedContentHandler
{
    //------------------------------------------------------------------------
    // DOM Nodes information
    private Stack<Element> elementStack =  new Stack<Element>();
    private Node currentNode;
    private org.dom4j.Attribute currentAttribute;
    private HashMap<Node, Object> errorsByNode = new HashMap<Node, Object>();
    private HashMap<Node, Object> warningsByNode = new HashMap<Node, Object>();

    /** Getter for property errorsByNode.
     * @return Value of property errorsByNode.
     */
    public HashMap<Node, Object> getErrorsByNode()
    {
        return errorsByNode;
    }

    /** Getter for property warningsByNode.
     * @return Value of property warningsByNode.
     */
    public HashMap<Node, Object> getWarningsByNode()
    {
        return warningsByNode;
    }

    /** You can call this method to reuse an ErrorHandler. */
    public void init()
    {
        super.init();
        elementStack.clear();
        errorsByNode.clear();
        warningsByNode.clear();
        currentNode = null;
        currentAttribute = null;
    }

    //------------------------------------------------------------------------
    // DOMContentHandler methods

    public void startElementNode(Element node)
    {
        currentNode = node;
        elementStack.push(node);    
    }

    public void endElementNode(Element node)
    {
        currentNode = null;
        elementStack.pop();
    }

    public void setCurrentNode(Node node)
    {
        currentNode = node;
    }

    //------------------------------------------------------------------------
    // TypedContentHandler

    /** receives notification of the start of a document.
     */
    public void startDocument()
        throws SAXException
    {
        // ignored
    }

    /** receives notification of the end of a document.
     */
    public void endDocument()
        throws SAXException
    {
        // ignored
    }

    /** receives notification of the start of an element.
     *
     * If this element has attributes, the start/endAttribute methods are
     * called after this method.
     */
    public void startElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        // ignored
    }

    /** receives notification of the start of an attribute.
     *
     * the value of the attribute is reported through the characterChunk method.
     */
    public void startAttribute(String namespaceURI, String localName, String qName)
    throws SAXException
    {
        Element element = elementStack.peek();
        currentAttribute = element.attribute(qName);
    }

    /** receives notification of the end of an attribute.
     */
    public void endAttribute(String namespaceURI, String localName, String qName)
    throws SAXException
    {
        currentAttribute = null;
    }

    /** this method is called after the start/endAttribute method are called
     * for all attributes.
     */
    public void endAttributePart()
        throws SAXException
    {
        // ignored
    }

    /** receives notification of the end of an element.
     */
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        // ignored
    }

    /** receives notification of a string.
     *
     * @param literal
     *      the contents.
     */
    public void characterChunk(String literal)
    throws SAXException
    {
        // TODO: ????
    }

    //------------------------------------------------------------------------
    // CollectingErrorHandler methods
    protected ErrorInfo saveError(org.xml.sax.SAXParseException spe)
    {
        ErrorInfo ei = super.saveError(spe);
        return saveErrorOrWarning(ei, errorsByNode);
    }

    protected ErrorInfo saveWarning(org.xml.sax.SAXParseException spe)
    {
        ErrorInfo ei = super.saveWarning(spe);
        return saveErrorOrWarning(ei, warningsByNode);
    }

    protected ErrorInfo saveErrorOrWarning(ErrorInfo ei, HashMap<Node, Object> map)
    {
        Node node = null;
        if(currentAttribute != null)
        {
            //save attribute
            node = currentAttribute;
        }
        else
        {
            //save element - TODO: What about other nodes?
            node = elementStack.peek();
        }

        map.put(node, ei);
        return ei;
    }

    /** Dumps all errors as a String. Uses ErrorInfo.toString()
     * @return Errors as a String.
     */
    public String dumpErrors()
    {
        return dumpErrorsOrWarnings(errorsByNode);
    }

    /** Dumps all warnings as a String. Uses ErrorInfo.toString()
     * @return Warnings as a String.
     */
    public String dumpWarnings()
    {
        return dumpErrorsOrWarnings(warningsByNode);
    }

    public String dumpErrorsOrWarnings(Map<Node, Object> map)
    {
        StringBuilder sb = new StringBuilder();
        for(java.util.Iterator<Node> iter = map.keySet().iterator(); iter.hasNext();)
        {
            Node node = iter.next();
            sb.append(node.getUniquePath());
            sb.append(' ');
            sb.append(map.get(node).toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
