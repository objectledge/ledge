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

package org.objectledge.container;

import junit.framework.TestCase;

import org.nanocontainer.Log4JNanoContainerMonitor;
import org.nanocontainer.NanoContainer;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.test.FooComponent;

/**
 *
 * <p>Created on Dec 16, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeContainerTest.java,v 1.4 2004-01-13 14:02:16 fil Exp $
 */
public class LedgeContainerTest extends TestCase
{
    /**
     * Constructor for LedgeContainerTest.
     * @param arg0
     */
    public LedgeContainerTest(String arg0)
    {
        super(arg0);
    }

    public void testLedgeContainer()
        throws Exception
    {
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
                "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        root = root+"/container1";
        FileSystem fs = FileSystem.getStandardFileSystem(root);
        
        NanoContainer container = new LedgeContainer(fs, "/config", 
            new Log4JNanoContainerMonitor());
        container.addShutdownHook();
        assertNotNull(container.getRootContainer().getComponentInstance(FooComponent.class)); 
    }
    
    public void testMain()
        throws Exception
    {
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
                "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        root = root+"/container1";
        String[] args = new String[] { "-r", root, "org.objectledge.test.FooComponent", 
            "blah1", "blah2"};
        Main.main(args);
    }
}
