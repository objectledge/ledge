package org.objectledge.parameters;

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AmbiguousParameterException.java,v 1.1 2003-11-27 17:05:19 pablo Exp $
 */
public class AmbiguousParameterException
    extends RuntimeException
{
    /**
     * Constructs a new AmbiguousParameterException with the default message.
     */
    public AmbiguousParameterException()
    {
        super("");
    }

    /**
     * Constructs a new AmbiguousParameterException with a custom message.
     * 
     * @param message the message.
     */
    public AmbiguousParameterException(String message)
    {
        super(message);
    }
}
