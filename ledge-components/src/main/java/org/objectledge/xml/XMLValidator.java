//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.xml;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * Validates XML files against schemata using MSV library.
 *
 * <p>The primary schema language used throughout ObjectLedge project is RelaxNG, but the MSV
 * library determines the schema languague using XML namespace of the top level element. At the
 * moment XML based schema languages supported by MSV and thus by XMLValidator are RelaxNG,
 * W3C XSD, and others.</p>
 * 
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: XMLValidator.java,v 1.10 2004-06-01 11:13:11 zwierzem Exp $
 */
public class XMLValidator
{
	private XMLGrammarCache grammarCache;
	
    private SAXParser saxParser;
    private ExceptionErrorHandler errorHandler = new ExceptionErrorHandler();

	/** Pathname of the relaxng schema.*/
	public static final String RELAXNG_SCHEMA = "org/objectledge/xml/relaxng.rng";

    /**
     * Creates a new instance of the validator.
     * 
     * @param grammarCache system grammar cache for retrieving grammars used for validation 
     * @throws ParserConfigurationException if the JAXP parser factory is misconfigured.
     * @throws SAXException if the JAXP parser factory is misconfigured.
     */
    public XMLValidator(XMLGrammarCache grammarCache) 
        throws ParserConfigurationException, SAXException
    {
		this.grammarCache = grammarCache;
    	
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        parserFactory.setValidating(false);
        saxParser = parserFactory.newSAXParser();
    }
    
    /**
     * Validates given XML file using specified schema.
     * 
     * @param fileUrl the URL of the file to be validated.
     * @param schemaUrl the URL of the schema to be used.
     * @throws ParserConfigurationException if parser is badly configured.
     * @throws IOException if the schema does not exist.
     * @throws SAXException if the schema is malformed.
     */
    public void validate(URL fileUrl, URL schemaUrl)
        throws SAXException, ParserConfigurationException, IOException
    {
        Verifier verifier = getVerifier(schemaUrl);
        XMLReader reader = saxParser.getXMLReader();
        InputSource source = new InputSource(fileUrl.toString());
		reader.setContentHandler(verifier);
		reader.setDTDHandler(verifier);
        reader.parse(source);
    }
    
    /**
     * Returns a thread-exclusive verifier instance.
     * 
     * @param schemaUrl the URL of the schema to be used.
     * @return a thread-exclusive verifier instance.
     * @throws ParserConfigurationException if parser is badly configured.
     * @throws IOException if the schema does not exist.
     * @throws SAXException if the schema is malformed.
     */
    public Verifier getVerifier(URL schemaUrl)
        throws SAXException, ParserConfigurationException, IOException
    {
		Grammar grammar = grammarCache.getGrammar(schemaUrl);
        DocumentDeclaration documentDeclaration = new REDocumentDeclaration(grammar); 
        return new Verifier(documentDeclaration, errorHandler); 
    }
}
