/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 25, 2003
 */
package org.objectledge;

/**
 * Thrown to indicate that a component cannot initialize itself.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ComponentInitializationError.java,v 1.1 2003-11-25 11:01:10 fil Exp $
 */
public class ComponentInitializationError extends Error
{

    /**
     * Creates an instance of the error without sepecifying message nor cause.
     */
    public ComponentInitializationError()
    {
        super();
    }

    /**
     * Creates an instance of the error with the sepecified detail message.
     * 
     * @param message a human readable detail message.
     */
    public ComponentInitializationError(String message)
    {
        super(message);
    }

    /**
     * Creates an instance of the error with the sepecified cause.
     * 
     * @param cause the root cause of the exception.
     */
    public ComponentInitializationError(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates an instance of the error with the sepecified detail message and cause.
     * 
     * @param message a human readable detail message.
     * @param cause the root cause of the exception.
     */
    public ComponentInitializationError(String message, Throwable cause)
    {
        super(message, cause);
    }
}
