package pl.caltha.services.xml;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.msv.verifier.ValidationUnrecoverableException;

/**
 * Base ErrorHandler class.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseErrorHandler.java,v 1.2 2005-02-10 17:50:27 rafal Exp $
 */
public abstract class BaseErrorHandler implements org.xml.sax.ErrorHandler
{
    //-----------------------------------------------------------------------
    // Error counters
    private int counter = 0;
    private boolean hadError = false;

    /** You can call this method to reuse an ErrorHandler. */
    public void init()
    {
        counter = 0;
        hadError = false;
    }

    //-----------------------------------------------------------------------
    // ErrorHandler methods
    /** <code>org.xml.sax.ErrorHandler</code> method, You can override this one. */
    public void error( SAXParseException e )
    throws SAXException
    {
        hadError = true;
        countCheck(e);
    }

    /** <code>org.xml.sax.ErrorHandler</code> method, You cannot override this one,
     instead override {@link #onFatalError(SAXParseException)}. */
    final public void fatalError( SAXParseException e )
    throws SAXException
    {
        hadError = true;
        onFatalError(e);
        throw new ValidationUnrecoverableException(e);
    }

    /** <code>org.xml.sax.ErrorHandler</code> method, You can override this one. */
    public void warning( SAXParseException e )
    {
        // ignored
    }
    
    //-----------------------------------------------------------------------
    // BaseErrorHandler methods
    /** Override this method to add functionality on
     * {@link #fatalError(SAXParseException)} call.
     */
    protected abstract void onFatalError(SAXParseException e)
        throws SAXException;

    /** Override this method to add functionality when there is more than 20
     * errors.
     */
    protected abstract void onTooManyErrors(SAXParseException e)
        throws SAXException;

    /** Call this method to check if there were errors. */
    public boolean hadErrors()
    {
        return hadError;
    }

    private void countCheck(SAXParseException e)
    throws SAXParseException, SAXException
    {
        if( counter++ < 20 )
        {
            return;
        }
        onTooManyErrors(e);
        throw new ValidationUnrecoverableException(e);
    }
}

