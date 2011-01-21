//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge;

/**
 * Thrown to indicate that a component cannot initialize itself.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ComponentInitializationError.java,v 1.3 2003-12-29 13:32:06 fil Exp $
 */
///CLOVER:OFF
public class ComponentInitializationError extends Error
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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
