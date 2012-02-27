package org.objectledge.forms.internal.xml.impl;

import java.util.HashMap;

import org.jcontainer.dna.Logger;
import org.xml.sax.ErrorHandler;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.VerifierFilter;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * Grammar cache - caches MSV's Grammar objects.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: XMLGrammarCache.java,v 1.1 2005-01-20 16:44:47 pablo Exp $
 */
public class XMLGrammarCache
{
    private HashMap<String, Grammar> grammars = new HashMap<String, Grammar>();
    private XMLServiceImpl xmlService;
    private Logger log;

    public XMLGrammarCache(XMLServiceImpl xmlService, Logger logger)
    {
        log = logger;
        this.xmlService = xmlService;
    }

    /**
     * Returns a verifier for a given ErrorHandler and schema SYSTEMID or PUBLICID.
     * ErrorHandler is used to report errors during validation.
     * You can set your own ErrorHandler using setErrorHandler on VerifierFilter.
     * TODO: Verifiers could be pooled together.
     */
    public Verifier getVerifier(String grammarID, ErrorHandler errorHandler)
    throws Exception
    {
        Grammar grammar = getGrammar(grammarID);

        Verifier verifier;
        if( grammar instanceof XMLSchemaGrammar )
        {
            // use verifier+identity constraint checker.
            verifier = new IDConstraintChecker( (XMLSchemaGrammar)grammar, errorHandler );
        }
        else
        {
            // validate normally by using Verifier.
            verifier = new Verifier( new REDocumentDeclaration(grammar), errorHandler );
        }

        return verifier;
    }

    /**
     * Returns a verifierFilter for a given ErrorHandler and schema SYSTEMID.
     * ErrorHandler is used to report errors during validation.
     * You can set your own ErrorHandler using setErrorHandler on Verifier.
     * TODO: VerifierFilters could be pooled.
     */
    public VerifierFilter getVerifierFilter(String grammarID, ErrorHandler errorHandler)
    throws Exception
    {
        Grammar grammar = getGrammar(grammarID);

        VerifierFilter verifierFilter;
        if( grammar instanceof XMLSchemaGrammar )
        {
            // use verifier+identity constraint checker.
            verifierFilter = new VerifierFilter(
                    new IDConstraintChecker( (XMLSchemaGrammar)grammar, errorHandler ));
        }
        else
        {
            // validate normally by using Verifier.
            verifierFilter = new VerifierFilter( new REDocumentDeclaration(grammar), errorHandler );
        }

        return verifierFilter;
    }

    /**
     * This method adds grammars to grammar map (cache),
     * that's why it has a <code>synchronized</code> block.
     */
    public Grammar getGrammar(String grammarID)
    throws Exception
    {
        if(grammars.containsKey(grammarID))
        {
            return grammars.get(grammarID);
        }

        Grammar grammar = null;
        if(grammarID != null)
        {
            grammar = loadGrammar(grammarID);
            synchronized(grammars)
            {
                grammars.put(grammarID, grammar);
            }
        }
        return grammar;
    }


    /**
     * This method loads grammars.
     */
    private Grammar loadGrammar(String grammarID)
    throws Exception
    {
        log.debug("Loading grammar "+grammarID);

        Grammar grammar=null;
        final long stime = System.currentTimeMillis();
        // parse schema and other XML-based grammars
        // GrammarLoader will detect the language.
        try
        {
            org.xml.sax.EntityResolver er = xmlService.getEntityResolver();
            LoggingGrammarReaderController controller = new LoggingGrammarReaderController(log, er);
            org.xml.sax.InputSource is = xmlService.getInputSource(grammarID);
            grammar = com.sun.msv.reader.util.GrammarLoader.loadSchema(is, controller, xmlService.getFactory());
        }
        catch(org.xml.sax.SAXParseException e)
        {
            // this error is already reported by LoggingGrammarReaderController
            errorLoadingGrammar(grammarID, e);
        }
        catch(java.io.IOException e)
        {
            //Fatal error
            errorLoadingGrammar(grammarID, e);
        }
        catch(javax.xml.parsers.ParserConfigurationException e)
        {
            //Fatal error
            errorLoadingGrammar(grammarID, e);
        }
        catch(org.xml.sax.SAXException se )
        {
            //Fatal error
            errorLoadingGrammar(grammarID, se);
        }

        if(grammar == null)
        {
            errorLoadingGrammar(grammarID, new Exception("Unknow reason for error when loading grammar"));
        }

        long parsingTime = System.currentTimeMillis();
        log.info(Localizer.localize(MSG_PARSING_TIME, Long.valueOf(parsingTime-stime))+" '"+grammarID+"'");

        return grammar;
    }

    private void errorLoadingGrammar(String grammarID, Exception e)
    throws Exception
    {
        if(e != null)
        {
            log.error(Localizer.localize(ERR_LOAD_GRAMMAR)+" '"+grammarID+"'", e);
            throw e;
        }
        else
        {
            log.error(Localizer.localize(ERR_LOAD_GRAMMAR)+" '"+grammarID+"'");
        }
    }

    public static final String MSG_START_PARSING_GRAMMAR = "GrammarCache.StartParsingGrammar";
    public static final String MSG_PARSING_TIME =          "GrammarCache.ParsingTime";
    public static final String ERR_LOAD_GRAMMAR =          "GrammarCache.ErrLoadGrammar";
}
