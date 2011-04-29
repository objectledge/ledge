// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.filesystem;

import org.objectledge.filesystem.impl.URLStreamHandlerImpl;
import org.objectledge.filesystem.protocol.ledge.Handler;
import org.picocontainer.Startable;

/**
 * A Startable component that configures support for ledge:// URLs.
 * 
 * <p>NOTE! Only one such component should be present in a virtual machine!</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeURLSupport.java,v 1.1 2004-11-04 11:17:31 rafal Exp $
 */
public class LedgeURLSupport implements Startable
{
    private final FileSystem fs;

    /**
     * Creates new LedgeURLSupport instance.
     * 
     * @param fs the filesystem.
     */
    public LedgeURLSupport(FileSystem fs)
    {
        this.fs = fs;
    }
    
    /**
     * {@inheritDoc}
     */
    public void start()
    {
        URLStreamHandlerImpl handler = new URLStreamHandlerImpl(fs);
        Handler.init(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        Handler.shutdown();
    }
}
