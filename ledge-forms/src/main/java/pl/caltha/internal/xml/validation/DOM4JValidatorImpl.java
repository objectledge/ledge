package pl.caltha.internal.xml.validation;

import org.dom4j.Document;

import pl.caltha.internal.xml.XMLServiceImpl;
import pl.caltha.services.xml.validation.DOM4JValidationErrorCollector;
import pl.caltha.services.xml.validation.DOM4JValidator;
import pl.caltha.services.xml.validation.ValidationException;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;

/** Class for DOM4J trees validation.
 */
public class DOM4JValidatorImpl
implements DOM4JValidator
{
    private XMLServiceImpl xmlService;
    private DOM4JReportingSAXWriter saxWriter;
    private com.sun.msv.verifier.Verifier verifier;

    public DOM4JValidatorImpl(XMLServiceImpl xmlService)
    {
        this.xmlService = xmlService;
        saxWriter = new DOM4JReportingSAXWriter();
    }
    
    private void setGrammar(String grammarID)
    throws Exception
    {
        com.sun.msv.grammar.Grammar grammar =  xmlService.getGrammar(grammarID);
        if(grammar instanceof XMLSchemaGrammar)
        {
            verifier = new  ExtendedVerifier(
                            ((XMLSchemaGrammar)grammar),
                            (org.xml.sax.ErrorHandler)null); // ErrorHandler
        }
        else
        {
            throw new RuntimeException("Unsupported schema type");
        }
    }

    public boolean validate(Document document, String grammarID,
                            DOM4JValidationErrorCollector errorAndContentHandler)
    throws ValidationException, Exception
    {
        // No document
        if (document == null)
        {
            throw new ValidationException("There is no document to validate");
        }

        setGrammar(grammarID);
        
        // WARN errorAndContentHandler can be null - it means that the errors will 
        // not be reported
        
        // set up TypeDetector
        verifier.setErrorHandler(errorAndContentHandler);
        if(verifier instanceof ExtendedVerifier)
        {
            ((ExtendedVerifier)verifier).setContentHandler(errorAndContentHandler);
        }
        else
        {
            throw new RuntimeException("Unsupported schema type");
        }
        
        // set up SAXWriter
        saxWriter.setContentHandler(verifier);
        saxWriter.setDOM4JContentHandler(errorAndContentHandler);
        
        //validate
        try
        {
            saxWriter.write(document);
        }
        catch(org.xml.sax.SAXException e)
        {
            throw new ValidationException("Problems during validation", e);
        }
        
        return verifier.isValid();
    }
}
