package pl.caltha.services.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * ErrorHandler that collects all errors and warnings, it can be reused
 * after calling {@link #init()}.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CollectingErrorHandler.java,v 1.2 2005-02-10 17:50:27 rafal Exp $
 */
public class CollectingErrorHandler extends BaseErrorHandler
{
    //-----------------------------------------------------------------------
    // Error counters
    /** Collection of errors. */
    protected ArrayList errors = new ArrayList();
    /** Collection of warnings. */
    protected ArrayList warnings = new ArrayList();

    //-----------------------------------------------------------------------
    // BaseErrorHandler methods
    /** You can call this method to reuse an ErrorHandler. */
    public void init()
    {
        super.init();
        errors.clear();
        warnings.clear();
    }

    protected void onFatalError(SAXParseException e)
    throws SAXException
    {
        saveError(e);
    }
    
    protected void onTooManyErrors(SAXParseException e)
        throws SAXException
    {
        // ignored
    }

    //-----------------------------------------------------------------------
    // ErrorHandler methods
    public void error(SAXParseException e)
    throws SAXException
    {
        super.error(e);
        saveError(e);
    }

    public void warning( SAXParseException e )
    {
        super.warning(e);
        saveWarning(e);
    }

    //-----------------------------------------------------------------------
    // Other methods
    /** Saves an error information in a list.
     * @return Saved ErrorInfo
     */
    protected ErrorInfo saveError(SAXParseException spe)
    {
        return saveErrorOrWarning(spe, errors);
    }

    /** Saves a warning information in a list.
     * @return Saved ErrorInfo
     */
    protected ErrorInfo saveWarning(SAXParseException spe)
    {
        return saveErrorOrWarning(spe, warnings);
    }

    /** Saves an error or warning information in a list.
     * @return Saved ErrorInfo
     */
    protected ErrorInfo saveErrorOrWarning( SAXParseException spe, List collection)
    {
        ErrorInfo ei = new ErrorInfo(spe);
        collection.add(ei);
        return ei;
    }

    /** Returns errors list.
     * @return List of errors.
     */
    public List getErrors()
    {
        return errors;
    }

    /** Returns warnings list.
     * @return List of warnings.
     */
    public List getWarnings()
    {
        return warnings;
    }

    /** Dumps all errors as a String. Uses ErrorInfo.toString()
     * @return Errors as a String.
     */
    public String dumpErrors()
    {
        return dumpErrorsOrWarnings(errors);
    }

    /** Dumps all warnings as a String. Uses ErrorInfo.toString()
     * @return Warnings as a String.
     */
    public String dumpWarnings()
    {
        return dumpErrorsOrWarnings(warnings);
    }

    protected String dumpErrorsOrWarnings(List collection)
    {
        StringBuffer sb = new StringBuffer();
        for(java.util.Iterator iter = collection.iterator(); iter.hasNext();)
        {
            sb.append(iter.next().toString());
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Simple class for storing error or warning information.
     * We need to store location info (line and column) because Locator
     * objects may be reused and it can be lost during processing.
     */
    public class ErrorInfo
    {
        private int column;
        private int line;
        private String publicID;
        private String systemID;
        private SAXParseException exception;

        public ErrorInfo(SAXParseException spe)
        {
            column = spe.getColumnNumber();
            line = spe.getLineNumber();
            publicID = spe.getPublicId();
            systemID = spe.getSystemId();
            exception = spe;
        }

        /** The format of this String is:
         * linenum:columne PUBLIC publicID SYSTEM systemID exceptionMessage
         */
        public String toString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append(line);
            sb.append(':');
            sb.append(column);
            sb.append(" PUBLIC ");
            sb.append(publicID);
            sb.append(" SYSTEM ");
            sb.append(systemID);
            sb.append(' ');
            sb.append(exception.getLocalizedMessage());
            return sb.toString();
        }

        /** Getter for property column.
         * @return Value of property column.
         */
        public int getColumn()
        {
            return column;
        }


        /** Getter for property line.
         * @return Value of property line.
         */
        public int getLine()
        {
            return line;
        }

        /** Getter for property publicID.
         * @return Value of property publicID.
         */
        public java.lang.String getPublicID()
        {
            return publicID;
        }

        /** Getter for property systemID.
         * @return Value of property systemID.
         */
        public java.lang.String getSystemID()
        {
            return systemID;
        }

        /** Getter for property exception.
         * @return Value of property exception.
         */
        public org.xml.sax.SAXParseException getException()
        {
            return exception;
        }
    }
}
