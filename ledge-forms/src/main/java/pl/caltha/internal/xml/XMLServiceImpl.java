package pl.caltha.internal.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.io.SAXWriter;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pl.caltha.internal.xml.validation.DOM4JValidatorImpl;
import pl.caltha.services.xml.XMLDataReader;
import pl.caltha.services.xml.XMLService;
import pl.caltha.services.xml.validation.DOM4JValidator;

//import com.sun.resolver.tools.CatalogResolver;

/**
 * Implementation of Ledge XMLService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: XMLServiceImpl.java,v 1.2 2005-03-23 07:52:14 zwierzem Exp $
 */
public class XMLServiceImpl 
    implements XMLService
{
    public static final String MSG_DTDVALIDATION = "Service.DTDValidation";
    public static final String MSG_PARSER =        "Service.Parser";

    private Logger log;

    private SAXParserFactory factory;
    private EntityResolver entityResolver;
    //private DTDHandler dtdHandler;
    private XMLGrammarCache xmlGrammarCache;

    private FileSystem fileSystem;
    /** Called when the broker is starting.
     */
    public XMLServiceImpl(Logger logger, FileSystem fileSystem)
    {
        log = logger;
        this.fileSystem = fileSystem;
		entityResolver = new CalthaEntityResolver(this, fileSystem, log);

        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        log.info(Localizer.localize(MSG_PARSER, factory.getClass().getName()));

        xmlGrammarCache = new XMLGrammarCache(this, logger);

        //factory.setFeature("http://apache.org/xml/features/validation/dynamic",true);
        //log.info(Localizer.localize(MSG_DTDVALIDATION));
    }

    public XMLDataReader getXMLDataReader()
    throws SAXException, ParserConfigurationException
    {
        XMLDataReaderImpl xmlDataReader = new XMLDataReaderImpl(this);
        return xmlDataReader;
    }

    public Document readDOM4J(InputSource is, String schemaID, ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception
    {
        XMLDataReader xmlDataReader = getXMLDataReader();
        return xmlDataReader.readDOM4J(is, schemaID, errorHandler);
    }

    public void readSAX(InputSource is, String schemaID,
                                ContentHandler contentHandler,
                                ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception
    {
        XMLDataReader xmlDataReader = getXMLDataReader();
        xmlDataReader.readSAX(is, schemaID, contentHandler, errorHandler);
    }

    public void readSAXfromDOM4J(Document doc, ContentHandler contentHandler,
                               ErrorHandler errorHandler)
    throws SAXException
    {
        SAXWriter writer = new SAXWriter(contentHandler);
        writer.setErrorHandler(errorHandler);
        writer.write(doc);
    }

    public EntityResolver getEntityResolver()
    {
        return entityResolver;
    }

    public InputSource getInputSource(String uri)
    throws SAXException, java.io.IOException
    {
        CalthaEntityResolver er = new CalthaEntityResolver(this, fileSystem, log);
        return er.resolveEntity(null, uri);
    }

    public SAXParserFactory getFactory()
    {
        return factory;
    }

    public void loadGrammar(String uri)
    throws Exception
    {
        getGrammar(uri);
    }

    //------------------------------------------------------------------------
    public DOM4JValidator getDOM4JValidator()
    {
        return new DOM4JValidatorImpl(this);
    }

    public com.sun.msv.grammar.Grammar getGrammar(String grammarID)
    throws Exception
    {
        return xmlGrammarCache.getGrammar(grammarID);
    }

    public com.sun.msv.verifier.Verifier getVerifier(String grammarID, ErrorHandler errorHandler)
    throws Exception
    {
        return xmlGrammarCache.getVerifier(grammarID, errorHandler);
    }

    public com.sun.msv.verifier.VerifierFilter getVerifierFilter(String grammarID, ErrorHandler errorHandler)
    throws Exception
    {
        return xmlGrammarCache.getVerifierFilter(grammarID, errorHandler);
    }

    //------------------------------------------------------------------------
    // Utility methods
    public Logger getLogFacility()
    {
        return log;
    }
}
