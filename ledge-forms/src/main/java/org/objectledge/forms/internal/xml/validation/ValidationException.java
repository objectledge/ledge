package org.objectledge.forms.internal.xml.validation;

/**
 * Thrown on fatal errors during validation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidationException.java,v 1.1 2005-01-20 16:44:51 pablo Exp $
 */
public class ValidationException extends java.lang.Exception
{
    /**
     * Constructs an instance of <code>XMLValidationException</code> without a message.
     */
    public ValidationException()
    {
        super();
    }
    /**
     * Constructs an instance of <code>XMLValidationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ValidationException(String msg)
    {
        super(msg);
    }
    

    public ValidationException(String msg, Exception e)
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
