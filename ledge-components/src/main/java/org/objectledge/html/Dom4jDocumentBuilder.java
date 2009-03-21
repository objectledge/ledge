package org.objectledge.html;

import java.util.Collections;
import java.util.Enumeration;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;
import org.dom4j.Document;
import org.dom4j.io.SAXContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * An implemention of Xerces XMLDocumentFilter that builds a Dom4j document from incoming XML
 * events.
 * <p>
 * This class can be used as a filter in NekoHTML parsing pipeline to create Dom4j Document
 * directly, without creating intermediate org.w3c.dom Document.
 * </p>
 * 
 * @author rafal
 */
public class Dom4jDocumentBuilder
    extends DefaultFilter
{

    /**
     * Dom4j io class that does the actual work.
     */
    private final SAXContentHandler contentHandler;

    /**
     * Creates a new instance of the filter.
     */
    public Dom4jDocumentBuilder()
    {
        contentHandler = new SAXContentHandler();
    }

    /**
     * Returns the parsed Dom4j document;
     */
    public Document getDocument()
    {
        return contentHandler.getDocument();
    }

    // ContentHandler

    @Override
    public void characters(XMLString text, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.characters(text.ch, text.offset, text.length);
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void endDocument(Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.endDocument();
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs)
        throws XNIException
    {
        contentHandler.endElement(element.uri, element.localpart, element.rawname);
    }

    @Override
    public void endPrefixMapping(String prefix, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.endPrefixMapping(prefix);
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.processingInstruction(target, data.toString());
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext,
        Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.setDocumentLocator(new LocatorAdapter(locator));
            for(String prefix : Collections.list((Enumeration<String>)nscontext.getAllPrefixes()))
            {
                contentHandler.startPrefixMapping(prefix, nscontext.getURI(prefix));
            }
            contentHandler.startDocument();
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.startElement(element.uri, element.localpart, element.rawname,
                new AttributesAdapter(attributes));
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.startPrefixMapping(prefix, uri);
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    // LexicalHandler

    @Override
    public void comment(XMLString text, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.comment(text.ch, text.offset, text.length);
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void endCDATA(Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.endCDATA();
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.startEntity(name);
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void startCDATA(Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.startCDATA();
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    @Override
    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding,
        Augmentations augs)
        throws XNIException
    {
        try
        {
            contentHandler.startEntity(name);
        }
        catch(SAXException e)
        {
            throw new XNIException(e);
        }
    }

    // Adapter classes

    /**
     * An adapter from XNI XMLAttributes to SAX Attributes interface
     */
    public static class AttributesAdapter
        implements Attributes
    {
        private final XMLAttributes attributes;

        public AttributesAdapter(XMLAttributes attributes)
        {
            this.attributes = attributes;
        }

        @Override
        public int getIndex(String name)
        {
            return attributes.getIndex(name);
        }

        @Override
        public int getIndex(String uri, String localName)
        {
            return attributes.getIndex(uri, localName);
        }

        @Override
        public int getLength()
        {
            return attributes.getLength();
        }

        @Override
        public String getLocalName(int index)
        {
            return attributes.getLocalName(index);
        }

        @Override
        public String getQName(int index)
        {
            return attributes.getQName(index);
        }

        @Override
        public String getType(int index)
        {
            return attributes.getType(index);
        }

        @Override
        public String getType(String name)
        {
            return attributes.getType(name);
        }

        @Override
        public String getType(String uri, String localName)
        {
            return attributes.getType(uri, localName);
        }

        @Override
        public String getURI(int index)
        {
            return attributes.getURI(index);
        }

        @Override
        public String getValue(int index)
        {
            return attributes.getValue(index);
        }

        @Override
        public String getValue(String name)
        {
            return attributes.getValue(name);
        }

        @Override
        public String getValue(String uri, String localName)
        {
            return attributes.getValue(uri, localName);
        }
    }

    /**
     * An adapter from XNI XMLLocator to SAX Locator interface
     */
    public static class LocatorAdapter
        implements Locator
    {
        private final XMLLocator locator;

        public LocatorAdapter(XMLLocator locator)
        {
            this.locator = locator;
        }

        @Override
        public int getColumnNumber()
        {
            return locator.getColumnNumber();
        }

        @Override
        public int getLineNumber()
        {
            return locator.getLineNumber();
        }

        @Override
        public String getPublicId()
        {
            return locator.getPublicId();
        }

        @Override
        public String getSystemId()
        {
            return locator.getExpandedSystemId();
        }
    }
}
