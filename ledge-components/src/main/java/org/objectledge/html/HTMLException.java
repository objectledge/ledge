package org.objectledge.html;

/**
 * Thrown by html service
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLException.java,v 1.2 2005-01-12 21:02:42 pablo Exp $
 */
public class HTMLException
    extends Exception
{
    /**
     * Constructs a new <code>HTMLException</code>.
     * 
     * @param message detail message.
     */
    public HTMLException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>HTMLException</code>..
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be handled.
     */
    public HTMLException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
