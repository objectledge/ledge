// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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

package org.objectledge.external;

import java.io.File;
import java.io.IOException;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.utils.LedgeTestCase;

/**
 * Test for the ScriptDirectory class.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: ScriptDirectoryTest.java,v 1.1 2006-03-24 14:27:39 rafal Exp $
 */
public class ScriptDirectoryTest
    extends LedgeTestCase
{
    static boolean linux;
    
    static 
    {
        if(System.getProperty("os.name").equals("Linux"))
        {
            linux = true;
        }        
    }
    
    public void testLedgeFS() throws IOException
    {        
        ScriptDirectory scriptDir = new ScriptDirectory(getFileSystem(), 
            ScriptDirectory.DEFAULT_PROVIDER,
            "external",
            linux ? ScriptDirectory.DEFAULT_PATH_PATTERN : null,
            ScriptDirectory.DEFAULT_COMMAND);            

        assertTrue(new File(scriptDir.getPath("test.sh")).exists());
        
        if(linux)
        {
            Runtime.getRuntime().exec(new String[] { scriptDir.getPath("test.sh") });
        }        
    }
    
    public void testHostFS() throws Exception
    {
        ScriptDirectory scriptDir = new ScriptDirectory(new File("src/test/resources/external"),
            linux ? ScriptDirectory.DEFAULT_PATH_PATTERN : null, ScriptDirectory.DEFAULT_COMMAND);

        assertTrue(new File(scriptDir.getPath("test.sh")).exists());
        
        if(linux)
        {
            Runtime.getRuntime().exec(new String[] { scriptDir.getPath("test.sh") });
        }        
    }
    
    public void testConifg()
        throws Exception
    {
        checkSchema("config/org.objectledge.external.ScriptDirectory.xml",
            "org/objectledge/external/ScriptDirectory.rng");
        final FileSystem fs = getFileSystem();
        ScriptDirectory scriptDir = new ScriptDirectory(fs, getConfig(fs,
            "config/org.objectledge.external.ScriptDirectory.xml"));
    }
}
