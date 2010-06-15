package org.objectledge.forms.internal.xml.validation.impl;

import org.dom4j.Document;
import org.objectledge.forms.internal.xml.impl.XMLServiceImpl;
import org.objectledge.forms.internal.xml.validation.DOM4JValidationErrorCollector;
import org.objectledge.forms.internal.xml.validation.DOM4JValidator;
import org.objectledge.forms.internal.xml.validation.ValidationException;


import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;

/** Class for DOM4J trees validation.
 */
public class DOM4JValidatorImpl
implements DOM4JValidator
{
    private XMLServiceImpl xmlService;

    public DOM4JValidatorImpl(XMLServiceImpl xmlService)
    {
        this.xmlService = xmlService;
    }
    
    private com.sun.msv.verifier.Verifier getVerifier(String grammarID)
        throws Exception
    {
        com.sun.msv.grammar.Grammar grammar = xmlService.getGrammar(grammarID);
        if(grammar instanceof XMLSchemaGrammar)
        {
            return new ExtendedVerifier(((XMLSchemaGrammar)grammar), (org.xml.sax.ErrorHandler)null); // ErrorHandler
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

        DOM4JReportingSAXWriter saxWriter = new DOM4JReportingSAXWriter();
        com.sun.msv.verifier.Verifier verifier = getVerifier(grammarID);
        
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
