package pl.caltha.services.xml;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pl.caltha.services.xml.validation.DOM4JValidator;

/** XMLService. It can be used to read and validate XML data from
 * different sources.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: XMLService.java,v 1.3 2005-03-23 10:39:11 zwierzem Exp $
 */
public interface XMLService 
{
    /*Sk?d czytamy XMLa
     *
     * 1. InputSource
     * 2. URL
     *
     * 1.1. bez schemy - schema podana jest wewn?trz pliku
     *      Na razie nie jest zaimplementowane -
     *      MSV nie wspiera takiego sprz?tu.
     * 1.2. W og?le bez schemy
     * 2. ze schem? - PUBLICID lub SYSTEMID, InputSource
     */

    /** Returns an XMLDataReader object to be used by an application to read
     * XML data.
     */
    public XMLDataReader getXMLDataReader()
    throws SAXException, ParserConfigurationException;

    /** Reads XML data and returns a DOM4J Document. Uses schema for validation
     * if <code>schemaID</code> is not <code>null</code>.
     * @param is InputSource for data to be read.
     * @param schemaID Public or System ID of schema defining read XML data, if <code>null</code>
     * no validation is performed.
     * @param errorHandler SAX ErrorHandler to handle errors from read XML data.
     * @throws SAXException Thrown on problems with parsing XML data.
     * @throws ParserConfigurationException Thrown on problems with JAXP parser configuration.
     * @throws Exception Validation and other Exceptions.
     * @return DOM4J Document object representing read XML data.
     */
    public Document readDOM4J(InputSource is, String schemaID, ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception;
    
    /** Reads XML data and generates SAX events. Uses schema for validation
     * if <code>schemaID</code> is not <code>null</code>.
     * @param is InputSource for data to be read.
     * @param schemaID Public or System ID of schema defining read XML data, if <code>null</code>
     * no validation is performed.
     * @param contentHandler SAX ContentHandler to handle generated SAX events.
     * @param errorHandler SAX ErrorHandler to handle errors from read XML data.
     * @throws SAXException Thrown on problems with parsing XML data.
     * @throws ParserConfigurationException Thrown on problems with JAXP parser configuration.
     * @throws Exception Validation and other Exceptions.
     */
    public void readSAX(InputSource is, String schemaID, ContentHandler contentHandler, ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception;
    
    /** Generates SAX events for a given DOM4J Document object. */
    public void readSAXfromDOM4J(Document doc, ContentHandler contentHandler, ErrorHandler errorHandler)
    throws SAXException;
    
    /** Returns an InputSource for a given URI - it uses
     * <code>CalthaEntityResolver</code> internally 
     * - look at {@link #getEntityResolver()}. */
    public InputSource getInputSource(String uri)
    throws SAXException, java.io.IOException;

    /** Returns a default EntityResolver used by XMLService -
     * in this case <code>CalthaEntityResolver</code> which implements <code>EntityResolver</code>'s
     * only method {@link org.xml.sax.EntityResolver#resolveEntity(String,String)}
     * in a following way:
     *
     * <p><i>WARNING!</i>
     * <br />
     * In this version it resolves only SYSTEMIDs. PUBLICIDs are not used.</p>
     *
     * <p><b>Supported URI schemes:</b>
     * <ul>
     * <li><code>ledge:</code> - scheme used to access resources via
     * Ledge <code>FileSystem</code></li>
     * <li><code>classpath:</code> - scheme used to access resources via
     * <code>ClassLoader</code>'s <code>getResourceAsStream(String)</code></li>
     * <li>Schemes supported by java.net.URL (except from <code>file:</code> scheme)
     * - schemes used to retrieve network resources available for instance through HTTP.</li>
     * </ul>
     * </p>
     *
     * <p><b>Unsupported URI schemes:</b>
     * <br />
     * Because of application portability issues, URIs with <code>file:</code>
     * scheme are not supported, and are considered harmful (SecurityException
     * is thrown). Ledge aplications should use <code>ledge:</code> scheme instead.
     * </p>
     */
    public org.xml.sax.EntityResolver getEntityResolver();

    /** Fabricates a DOM4JValidator object. */
    public DOM4JValidator getDOM4JValidator();

    /** Returns a SAXParserFactory object used by XMLService. */
    public javax.xml.parsers.SAXParserFactory getFactory();

    /** Preloads a grammar into grammar cache.
     * @throws Exception Thrown on errors loading Schema.
     */
    public void loadGrammar(String uri)
    throws Exception;
}
