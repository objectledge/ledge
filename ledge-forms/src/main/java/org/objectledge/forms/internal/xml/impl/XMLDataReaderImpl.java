package org.objectledge.forms.internal.xml.impl;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.objectledge.forms.internal.xml.XMLDataReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;


import com.sun.msv.verifier.VerifierFilter;

/**
 * An implemetation of XMLDataReader.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: XMLDataReaderImpl.java,v 1.1 2005-01-20 16:44:47 pablo Exp $
 */
public class XMLDataReaderImpl
    implements XMLDataReader
{
    public XMLDataReaderImpl(XMLServiceImpl xmlService)
    throws SAXException, ParserConfigurationException
    {
        this.xmlService = xmlService;
        reader = xmlService.getFactory().newSAXParser().getXMLReader();
        entityResolver = xmlService.getEntityResolver();
        saxPipe = new java.util.ArrayList<XMLFilter>(3);
    }

    private XMLServiceImpl xmlService;
    private XMLReader reader;

    private EntityResolver entityResolver;
    private DTDHandler dtdHandler;
    private java.util.ArrayList<XMLFilter> saxPipe;

    //------------------------------------------------------------------------
    // XMLDataReader methods
    
    public Document readDOM4J(InputSource is, String schemaID,
                         ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception
    {
        if(schemaID != null)
        {
            return readDOM4JValidate(is, schemaID, errorHandler);
        }

        XMLReader xmlReader = setupReader();
        xmlReader.setErrorHandler(errorHandler);
        return readFinally(xmlReader, is);
    }

    public void readSAX(InputSource is, String schemaID,
                        ContentHandler contentHandler,
                        ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception
    {
        if(schemaID != null)
        {
            readSAXValidate(is, schemaID, contentHandler, errorHandler);
            return;
        }

        setupReader();
        reader.setErrorHandler(errorHandler);
        reader.setContentHandler(contentHandler);
        readSAXFinally(reader, is);
    }

    public EntityResolver getEntityResolver()
    {
        return entityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver)
    {
        this.entityResolver = entityResolver;
    }
    
    public DTDHandler getDTDHandler()
    {
        return dtdHandler;
    }
    
    public void setDTDHandler(DTDHandler dtdHandler)
    {
        this.dtdHandler = dtdHandler;
    }

    public void addFilter(org.xml.sax.XMLFilter xmlFilter)
    {
        saxPipe.add(xmlFilter);
    }

    public void removeFilters()
    {
        saxPipe.clear();
    }

    //------------------------------------------------------------------------
    // Implementation methods
    
    /** Sets up a SAX processing pipe.
     * Connects XMLFilters, EntityResolver and a DTDHandler. */
    private XMLReader setupReader()
    {
        // TODO: get one from the pool
        // TODO: after reading return one to the pool
        //reader = get one from pool
        
        if(entityResolver != null)
        {
            reader.setEntityResolver(entityResolver);
        }
        if(dtdHandler != null)
        {
            reader.setDTDHandler(dtdHandler);
        }
     
        XMLReader lastReader = this.reader;
        for(java.util.Iterator<XMLFilter> iter =  saxPipe.iterator(); iter.hasNext();)
        {
            org.xml.sax.XMLFilter filter = iter.next();
            filter.setParent(lastReader);
            lastReader = filter;
        }
        
        return lastReader;
    }
    
    /** Connects a VerifierFilter to SAX processing pipe.
     * If <code>DTDHandler</code> is <code>null</code>
     * <code>VerifierFilter</code> is used as such. */
    private VerifierFilter setupVerifierFilter(XMLReader xmlReader, String schemaID, ErrorHandler errorHandler)
    throws Exception
    {
        VerifierFilter verifierFilter = xmlService.getVerifierFilter(schemaID, errorHandler);
        // WARN DTDHandler is set on original XMLReader
        if(dtdHandler == null)
        {
            reader.setDTDHandler(verifierFilter);
        }
        verifierFilter.setParent(xmlReader);
        verifierFilter.setErrorHandler(errorHandler);
        return verifierFilter;
    }
   
    /** Reads XML data and returns a DOM4J Document. Uses schema for validation.
     * @param is InputSource for data to be read.
     * @param schemaID Public or System ID of schema defining read XML data.
     * @param errorHandler SAX ErrorHandler to handle errors from read XML file.
     * @throws SAXException Thrown on problems with parsing XML data.
     * @throws ParserConfigurationException Thrown on problems with JAXP parser configuration.
     * @throws Exception Validation and other Exceptions.
     * @return DOM4J Document object representing read XML data.
     */
    private Document readDOM4JValidate(InputSource is, String schemaID,
                                 ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception
    {
        XMLReader xmlReader = setupReader();
        VerifierFilter verifierFilter = setupVerifierFilter(xmlReader, schemaID, errorHandler);
        Document doc = readFinally(verifierFilter, is);

        if(verifierFilter.isValid())
        {
            return doc;
        }
        else
        {
            throw new DocumentException("The document was invalid.");
        }
    }



    private Document readFinally(XMLReader xmlReader, InputSource is)
    throws DocumentException
    {
        SAXReader saxReader = new SAXReader(xmlReader);

        saxReader.setStripWhitespaceText(true);
        // it gives errors on DOM Validation.
        //saxReader.setMergeAdjacentText(true);

        return saxReader.read(is);
    }


    /** Reads XML data and generates SAX events. Uses schema for validation.
     * @param is InputSource for data to be read.
     * @param schemaID Public or System ID of schema defining read XML data.
     * @param contentHandler SAX ContentHandler to handle generated SAX events.
     * @param errorHandler SAX ErrorHandler to handle errors from read XML data.
     * @throws SAXException Thrown on problems with parsing XML data.
     * @throws ParserConfigurationException Thrown on problems with JAXP parser configuration.
     * @throws Exception Validation and other Exceptions.
     */
    private void readSAXValidate(InputSource is, String schemaID,
                                ContentHandler contentHandler,
                                ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception
    {
        XMLReader xmlReader = setupReader();
        VerifierFilter verifierFilter = setupVerifierFilter(xmlReader, schemaID, errorHandler);
        verifierFilter.setContentHandler(contentHandler);
        readSAXFinally(verifierFilter, is);

        if(!verifierFilter.isValid())
        {
            throw new SAXException("The document '"+is.getSystemId()+"' was invalid");
        }
    }


    private void readSAXFinally(XMLReader reader, InputSource is)
    throws Exception
    {
        reader.parse(is);
    }
}
