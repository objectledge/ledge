package pl.caltha.services.xml;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * All in one XML data reader and validator.
 * It can be used to build SAX processing pipelines.
 * Objects of this type are reusable, but not thread safe.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: XMLDataReader.java,v 1.2 2005-03-23 07:52:15 zwierzem Exp $
 */
public interface XMLDataReader
{
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
    public Document readDOM4J(InputSource is, String schemaID,
                         ErrorHandler errorHandler)
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
    public void readSAX(InputSource is, String schemaID,
                        ContentHandler contentHandler,
                        ErrorHandler errorHandler)
    throws SAXException, ParserConfigurationException, Exception;

    /** Returns EntityResolver used during XML data reading
     * - default one is <code>pl.caltha.internal.xml.CalthaEntityResolver</code>
     * @return The EntityResolver.
     */
    public EntityResolver getEntityResolver();

    /** Setter for EntityResolver.
     * @param entityResolver New EntityResolver.
     */
    public void setEntityResolver(EntityResolver entityResolver);
    
    /** Returns a DTDHandler for internal XMLReader
     * - default one is <code>com.sun.msv.verifier.Verifier</code>
     * @return The DTDHandler.
     */
    public DTDHandler getDTDHandler();
    
    /** Setter for DTDHandler.
     * @param dtdHandler New DTDHandler.
     */
    public void setDTDHandler(DTDHandler dtdHandler);

    /** Adds an XMLFilter to SAX processing pipe, added filter
     * will be the last one before the Verifier in the pipe.
     * <p><i>WARNING!!</i> All filters added must not be used by another thread, 
     * because they might not be thread safe.</p>
     */
    public void addFilter(org.xml.sax.XMLFilter xmlFilter);
    /** Clears the SAX processing pipe. */
    public void removeFilters();
}
