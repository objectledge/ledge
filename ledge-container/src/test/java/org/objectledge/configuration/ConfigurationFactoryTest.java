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

package org.objectledge.configuration;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.impl.ClasspathFileSystemProvider;
import org.objectledge.filesystem.impl.LocalFileSystemProvider;
import org.objectledge.xml.XMLValidator;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ConfigurationFactoryTest.java,v 1.5 2003-12-05 08:27:03 fil Exp $
 */
public class ConfigurationFactoryTest 
    extends TestCase
{
    private ConfigurationFactory cf;
    
    /**
     * Constructor for ConfigurationFactoryTest.
     * @param arg0
     */
    public ConfigurationFactoryTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() 
        throws Exception
    {
        super.setUp();
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
                "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystemProvider lfs = new LocalFileSystemProvider("local", root);
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        XMLValidator xv = new XMLValidator(fs);
        cf = new ConfigurationFactory(null, fs, xv, "config");
    }

    public void testGetConfig()
        throws ConfigurationException
    {
        Configuration config = cf.getConfig(org.objectledge.test.FooComponent.class);
        Configuration a = config.getChild("a");
        assertEquals(a.getValue(), "a");
        Configuration b = config.getChild("b");
        assertEquals(b.getAttribute("attr"), "b");
        Configuration[] d = config.getChild("c").getChildren("d");
        assertEquals(d[0].getValue(), "d1");
        assertEquals(d[1].getValue(), "d2");
    }
}
