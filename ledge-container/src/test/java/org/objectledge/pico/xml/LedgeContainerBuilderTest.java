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
package org.objectledge.pico.xml;

import java.net.URL;

import junit.framework.TestCase;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeContainerBuilderTest.java,v 1.1 2005-02-04 02:29:26 rafal Exp $
 */
public class LedgeContainerBuilderTest
    extends TestCase
{
    public void testLedgeContainerBuilder()
        throws Exception
    {
        MutablePicoContainer parentContainer = new DefaultPicoContainer();
        FileSystem fileSystem = FileSystem.getStandardFileSystem("");
        ClassLoader classLoader = getClass().getClassLoader();
        
        parentContainer.registerComponentInstance(ClassLoader.class, classLoader);
        parentContainer.registerComponentInstance(FileSystem.class, fileSystem);
        parentContainer.registerComponentImplementation(XMLGrammarCache.class);
        parentContainer.registerComponentImplementation(XMLValidator.class);
        parentContainer.registerComponentInstance("org.objectledge.filesystem.FileSystem:root", 
            "src/test/resources");
        
        URL composition = fileSystem.getResource("src/test/resources/composition/adapter.xml");
        LedgeContainerBuilder builder = new LedgeContainerBuilder(composition, classLoader);
        builder.createContainerFromScript(parentContainer, null);
    }
}
