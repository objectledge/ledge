package pl.caltha.forms;

/**
 * Thrown on any problems in form processing.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormsException.java,v 1.1 2005-01-19 06:55:23 pablo Exp $
 */
public class FormsException extends java.lang.Exception
{
    public FormsException()
    {
    }
    
    public FormsException(String msg)
    {
        super(msg);
    }

    public FormsException(String msg, Exception e)
    {
        super(msg);
    	this.exception = e;
    }

    /**
     * Return the embedded exception, if any.
     *
     * @return The embedded exception, or null if there is none.
     */
    public Exception getException ()
    {
	return exception;
    }
    
    /**
     * @serial The embedded exception if tunnelling, or null.
     */    
    private Exception exception = null;
}
