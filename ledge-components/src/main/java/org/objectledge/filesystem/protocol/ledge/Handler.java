// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.filesystem.protocol.ledge;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.objectledge.filesystem.impl.URLStreamHandlerImpl;

/**
 * Glue class for the <code>java.net</code> package.
 *
 * <p>Created on Jan 8, 2004</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Handler.java,v 1.4 2004-11-04 11:12:55 rafal Exp $
 */
public class Handler extends URLStreamHandler
{
    private static final String HANDLER_PACKAGE = "org.objectledge.filesystem.protocol";
    
    private static final String HANDLER_PROPERTY = "java.protocol.handler.pkgs";
    
    /** the delegate handler */
    private static URLStreamHandlerImpl handler;
    
    /**
     * Register the handler. 
     * 
     * @param h the delegate handler.
     */
    public static synchronized void init(URLStreamHandlerImpl h)
    {
        if(handler != null)
        {
            handler = h;
        }
        else
        {
            throw new IllegalStateException("URL handler for ledge protocol already registered " +
            		"in the VM.");
        }
        
        String pkgs = System.getProperty(HANDLER_PROPERTY);
        if(pkgs == null)
        {
            pkgs = "";
        }
        if(pkgs.indexOf(HANDLER_PACKAGE) < 0)
        {
            if(pkgs.length()>0)
            {
                pkgs = pkgs + "|";
            }
            System.setProperty(HANDLER_PROPERTY, pkgs + 
                HANDLER_PACKAGE);
        }
    }

    /**
     * Unregisters the handler.
     */    
    public static synchronized void shutdown()
    {
        handler = null;
        String pkgs = System.getProperty(HANDLER_PROPERTY);
        int i = pkgs.indexOf(HANDLER_PACKAGE);
        if(i > 0)
        {
            pkgs = pkgs.substring(0, i) + pkgs.substring(i+HANDLER_PACKAGE.length());
            if(pkgs.charAt(0) == '|')
            {
                pkgs = pkgs.substring(1);
            }
            if(pkgs.charAt(pkgs.length()-1) == '|')
            {
                pkgs = pkgs.substring(0, pkgs.length()-1);
            }
            System.setProperty(HANDLER_PROPERTY, pkgs);
        }
    }
    
    /**
     * Constructs a handler instance.
     */
    public Handler()
    {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    protected URLConnection openConnection(URL u) throws IOException
    {
        if(handler == null)
        {
            throw new IllegalStateException("not initialized");
        }
        else
        {
            return handler.openConnection(u);
        }
    }
}
