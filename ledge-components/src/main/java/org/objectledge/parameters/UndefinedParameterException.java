package org.objectledge.parameters;

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UndefinedParameterException.java,v 1.1 2003-11-27 17:05:19 pablo Exp $
 */
public class UndefinedParameterException
    extends RuntimeException
{
    /**
     * Constructs a new UndefinedParameterException with the default message.
     */
    public UndefinedParameterException()
    {
        super("");
    }

    /**
     * Constructs a new UndefinedParameterException with a custom message.
     * 
     * @param message the message.
     */
    public UndefinedParameterException(String message)
    {
        super(message);
    }
}
