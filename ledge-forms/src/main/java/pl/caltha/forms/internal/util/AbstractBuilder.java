package pl.caltha.forms.internal.util;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.caltha.forms.ConstructionException;
import pl.caltha.services.xml.XMLDataReader;

/**
 * Base class for XML builders.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: AbstractBuilder.java,v 1.4 2005-02-21 13:54:18 zwierzem Exp $
 */
public abstract class AbstractBuilder 
    extends DefaultHandler
{
    /** Accepted namespace. */
    protected String acceptedNamespace;
    /** Schema URI. */
    protected String schemaURI;
    /** URI of built object's definition. */
    protected String definitionURI;

    /** Messages prefix, should be overriden. */
    protected String MSG_PREFIX = "";

    protected java.lang.StringBuilder buffer;
    protected Stack context;

    public AbstractBuilder(String acceptedNamespace, String schemaURI)
    {
        this.acceptedNamespace = acceptedNamespace;
        this.schemaURI = schemaURI;

        buffer = new StringBuilder(111);
        context = new Stack();
    }

    //------------------------------------------------------------------------
    // Builder
    /** Called on start of building process. */
    protected abstract void startBuild(Object builtObject)
        throws ConstructionException;

    /** Called on end of building process. */
    protected abstract void endBuild(Object builtObject)
        throws ConstructionException;

    /** Builds upon element name and its attributes. */
    protected abstract void startElement(String elementName, Attributes atts)
        throws SAXException;

    /** Builds an object. */
    public void build(Object builtObject, XMLDataReader reader, InputSource is, org.xml.sax.ErrorHandler errorHandler)
    throws ConstructionException
    {
        definitionURI = is.getSystemId();
        startBuild(builtObject);

        try
        {
            reader.readSAX(is, schemaURI, this, errorHandler);
        }
        catch (Exception e)
        {
            throw new ConstructionException(MSG_PREFIX+"Error building '"+definitionURI+"'", e);
        }

        endBuild(builtObject);
    }

    protected String getPathFromElementStack()
    {
        StringBuilder sb = new StringBuilder(24);
        for(java.util.Iterator iter = context.iterator(); iter.hasNext();)
        {
            // {qName, new org.xml.sax.helpers.AttributesImpl(atts)}
            Object[] element = (Object[])(iter.next());
            String qName = (String)(element[0]);
            sb.append('/');
            sb.append(qName);
        }
        return sb.toString();
    }

    //------------------------------------------------------------------------
    // ContentHandler
    /** XML element locator. */
    public Locator locator;

    /**
     * Receive a Locator object for document events.
     *
     * @param locator A locator for all SAX document events.
     * @see org.xml.sax.ContentHandler#setDocumentLocator
     * @see org.xml.sax.Locator
     */
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }

    /**
     * Starts building the form object.
     * Receive notification of the beginning of the document.
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startDocument
     */
    public void startDocument()
    throws SAXException
    {
        if(locator == null)
        {
            throw new SAXException("Locator not set.");
        }
    }

    /**
     * Finishes building form object.
     * Receive notification of the end of the document.
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#endDocument
     */
    public void endDocument()
    throws SAXException
    {
        this.locator = null;
    }

    /**
     * Starts building a element.
     */
    public final void startElement(String namespaceURI, String localName,
    String qName, Attributes atts)
    throws SAXException
    {
        // Store data on stack
        dispatch(true);
        context.push(new Object[]{qName, new org.xml.sax.helpers.AttributesImpl(atts)});

        // 1. Check namespace uri -- is namespace accepted
        if(namespaceURI != null &&
        !namespaceURI.equals(this.acceptedNamespace))
        { // Unknown namespace.
          // TODO: Treat as text?
            return;
        }
        // 2. Check element name
        String elementName;
        // 2.1. Check localname
        if(localName != null)
        {
            elementName = localName;
        } // 2.2. Check qName
        else if(qName != null)
        {
            elementName = qName;
        } // bad thing happened
        else
        {
            elementName = "";
        }
        // 3. Build new form element
        this.startElement(elementName, atts);
    }

    /**
     * Cleans up the stack and String buffer.
     */
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException
    {
        dispatch(false);
        context.pop();
    }

    /**
     * Adds characters to the String buffer.
     */
    public final void characters(char[] chars, int start, int len)
    throws SAXException
    {
        buffer.append(chars, start, len);
    }

    /** Netbeans generated stuff. TODO: Do something with it. */
    protected void dispatch(final boolean fireOnlyIfMixed)
    throws SAXException
    {
        if (fireOnlyIfMixed && buffer.length() == 0)
        {
            return; //skip it
        }

        buffer.delete(0, buffer.length());
    }
}
