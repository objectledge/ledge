package pl.caltha.internal.xml.validation;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import pl.caltha.internal.xml.Localizer;
import pl.caltha.services.xml.validation.ExtendedContentHandler;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.identity.IDConstraintChecker;

/**
 * assign types to the incoming SAX2 events and reports them to
 * the application handler through ExtendedContentHandler.
 *
 * <p><i>INFO</i> This class is a modified copy of a
 * <code>com.sun.msv.verifier.psvi.TypeDetector</code> by KohsukeKawaguchi.
 * It inherits from IDConstraintChecker (to provide
 * W3C's XMLSchema support) and does not provide any information on datatypes
 * of validated data.</p>
 *
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 */
public class ExtendedVerifier extends IDConstraintChecker {

    /** characters that were read (but not processed)  */
    private StringBuilder text = new StringBuilder();

    protected ExtendedContentHandler handler;

    public ExtendedVerifier(XMLSchemaGrammar grammar, ErrorHandler errorHandler) {
        super(grammar, errorHandler);
        // WARN: Using a resource bundle from foreign package
        Localizer.init();
    }

    public ExtendedVerifier(XMLSchemaGrammar grammar, ExtendedContentHandler handler, ErrorHandler errorHandler) {
        this(grammar, errorHandler);
        setContentHandler(handler);
    }

    /**
     * sets the ExtendedContentHandler which will received the extended
     * infoset.
     */
    public void setContentHandler( ExtendedContentHandler handler ) {
        this.handler = handler;
    }

    protected void verifyText() throws SAXException {
        // store characters to report them
        String txt = null;
        if(text.length()!=0)
        {
            txt = new String(text);
        }
        
        super.verifyText();
        
        // if characters are validated. report to the handler.
        if(txt != null)
        {
            handler.characterChunk(txt);
        }
    }

    protected Datatype[] feedAttribute( Acceptor child, String uri, String localName, String qName, String value ) throws SAXException {
        // WARN: changes from original - now errors are reported on time
        handler.startAttribute( uri, localName, qName );

        Datatype[] result = super.feedAttribute(child,uri,localName,qName,value);
        handler.characterChunk(value);

        handler.endAttribute( uri, localName, qName);

        return result;
    }

    public void startElement( String namespaceUri, String localName, String qName, Attributes atts )
        throws SAXException {

        super.startElement( namespaceUri, localName, qName, atts );

        handler.endAttributePart();
    }

    protected void onNextAcceptorReady( StartTagInfo sti, Acceptor nextAcceptor ) throws SAXException {
        /*
            You cannot call handler.startElement before super.startElement invocation
            because unconsumed text maybe processed here.
        */
        handler.startElement( sti.namespaceURI, sti.localName, sti.qName );
    }

    public void endElement( String namespaceUri, String localName, String qName )
        throws SAXException {
        super.endElement(namespaceUri,localName,qName);

        handler.endElement(namespaceUri, localName, qName);
    }

    public void characters( char[] buf, int start, int len ) throws SAXException {
        super.characters(buf,start,len);
        text.append(buf,start,len); // TODO:
    }
    public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
        super.ignorableWhitespace(buf, start, len);
        text.append(buf,start,len); // TODO:
    }

    public void startDocument() throws SAXException {
        super.startDocument();
        handler.startDocument();
    }

    public void endDocument() throws SAXException {
        super.endDocument();
        handler.endDocument();
    }
}
