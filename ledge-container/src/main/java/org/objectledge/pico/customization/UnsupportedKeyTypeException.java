/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 1, 2003
 */
package org.objectledge.pico.customization;

import org.picocontainer.PicoIntrospectionException;

/**
 * Thrown by the CustomizedComponentProvider to indicate it's unable to handle a specific component
 * key type.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: UnsupportedKeyTypeException.java,v 1.1 2003-12-01 15:55:07 fil Exp $
 */
public class UnsupportedKeyTypeException 
    extends PicoIntrospectionException
{
    /**
     * Creates a new instance of the exception object. 
     */
    public UnsupportedKeyTypeException()
    {
        super();
    }

    /**
     * Creates a new instance of the exception object.
     *  
     * @param message the detail message.
     */
    public UnsupportedKeyTypeException(String message)
    {
        super(message);
    }

    /**
     * Creates a new instance of the exception object.
     *      
     * @param cause the root cause.
     */
    public UnsupportedKeyTypeException(Throwable cause)
    {
        super();
        initCause(cause);
    }

    /**
     * Creates a new instance of the exception object.
     * 
     * @param message detail message.
     * @param cause the root cause.
     */
    public UnsupportedKeyTypeException(String message, Exception cause)
    {
        super(message, cause);
    }

}
