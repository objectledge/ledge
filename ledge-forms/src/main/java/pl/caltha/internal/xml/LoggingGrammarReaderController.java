package pl.caltha.internal.xml;

import org.jcontainer.dna.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.reader.GrammarReaderController;

/**
 * GrammarReaderController that logs all errors and warnings.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: LoggingGrammarReaderController.java,v 1.2 2005-02-21 13:54:34 zwierzem Exp $
 */
public class LoggingGrammarReaderController implements GrammarReaderController
{
    /** Entity resolution is delegated to this object, it can be null. */
    private EntityResolver entityResolver;

    /** Used to log errors and warnings. */
    private Logger log;

    /** Used to compose information of location of errors and warnings. */
    private StringBuilder locationMessage = new StringBuilder(64);

    public LoggingGrammarReaderController(Logger log, EntityResolver entityResolver)
    {
        this.log = log;
        this.entityResolver = entityResolver;
    }

    public LoggingGrammarReaderController(Logger log)
    {
        this(log, null);
    }

    //------------------------------------------------------------------------
    // EntityResolver methods

    /** Resolves an entity, uses externaly set EntityResolver or returns <code>null</code>. */
    public InputSource resolveEntity( String publicId, String systemId )
    throws java.io.IOException, SAXException
    {
        if(entityResolver!=null)
        {
            return entityResolver.resolveEntity(publicId, systemId);
        }
        else
        {
            return null;
        }
    }

    //------------------------------------------------------------------------
    // GrammarReaderController methods

    /** Reports a Grammar warning. */
    public void warning(Locator[] loc, String errorMessage)
    {
        log.warn( getLocationMessage(loc, errorMessage) );
    }

    /** Reports a Grammar error. */
    public void error( Locator[] loc, String errorMessage, Exception nestedException )
    {
        String message;
        if(nestedException instanceof SAXException)
        {
            message = "SAXException occured";
        }
        else
        {
            message = errorMessage;
        }

        if(nestedException != null)
        {
            log.error(getLocationMessage(loc, message), nestedException);
        }
        else
        {
            log.error(getLocationMessage(loc, message));
        }
    }

    //------------------------------------------------------------------------
    // Utility methods

    private String getLocationMessage(Locator[] loc, String errorMessage)
    {
        // init buffer
        locationMessage.setLength(0);

        locationMessage.append(errorMessage);
        locationMessage.append("  ");

        if(loc == null || loc.length == 0)
        {
            locationMessage.append("  location unknown ");
        }
        else
        {
            for( int i=0; i<loc.length; i++ )
            {
                getLocation(loc[i]);
            }
        }

        return locationMessage.toString();
    }

    private void getLocation(Locator loc)
    {
        if(loc.getColumnNumber()>=0)
        {
            locationMessage.append("column:");
            locationMessage.append(loc.getColumnNumber());
        }

        locationMessage.append(" line:");
        locationMessage.append(loc.getLineNumber());
        locationMessage.append("   ");
        locationMessage.append(loc.getSystemId());
        locationMessage.append("  ");
    }
}

