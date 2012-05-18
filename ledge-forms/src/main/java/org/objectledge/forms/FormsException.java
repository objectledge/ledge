package org.objectledge.forms;

/**
 * Thrown on any problems in form processing.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormsException.java,v 1.2 2005-02-10 17:46:48 rafal Exp $
 */
public class FormsException extends java.lang.Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FormsException()
    {
        // default constructor
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
