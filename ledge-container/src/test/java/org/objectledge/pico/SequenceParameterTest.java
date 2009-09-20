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

package org.objectledge.pico;

import junit.framework.TestCase;

import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: SequenceParameterTest.java,v 1.6 2005-02-04 02:29:32 rafal Exp $
 */
public class SequenceParameterTest extends TestCase
{
    /**
     * Constructor for SequenceParameterTest.
     * @param arg0
     */
    public SequenceParameterTest(String arg0)
    {
        super(arg0);
    }

    public void testFileSystemComposition()
        throws Exception
    {
       MutablePicoContainer picoContainer = new DefaultPicoContainer();
       picoContainer.registerComponentInstance(ClassLoader.class, getClass().getClassLoader());
       picoContainer.registerComponentInstance(
            "org.objectledge.filesystem.impl.LocalFileSystemProvider.root", ".");
        
       picoContainer.registerComponentImplementation(
           "org.objectledge.filesystem.FileSystemProvider:local",
           LocalFileSystemProvider.class, 
           new Parameter[] {
               new ConstantParameter("local"),
               new ComponentParameter(
                    "org.objectledge.filesystem.impl.LocalFileSystemProvider.root")
           }
       );
       picoContainer.registerComponentImplementation(
           "org.objectledge.filesystem.FileSystemProvider:classpath",
           ClasspathFileSystemProvider.class, 
           new Parameter[] {
               new ConstantParameter("classpath"),
               new ComponentParameter(ClassLoader.class)
           }
       );
       picoContainer.registerComponentImplementation(
            FileSystem.class,
            FileSystem.class,
            new Parameter[] {
                new SequenceParameter(
                    new Parameter[] {
                        new ComponentParameter(
                            "org.objectledge.filesystem.FileSystemProvider:local"),
                        new ComponentParameter(
                            "org.objectledge.filesystem.FileSystemProvider:classpath"),
                    }, null
                ),
                new ConstantParameter(new Integer(4096)),
                new ConstantParameter(new Integer(4096))
            }
       );
       picoContainer.getComponentInstance(FileSystem.class);
    } 
}
