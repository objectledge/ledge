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

import java.io.IOException;

import org.objectledge.utils.LedgeTestCase;

/**
 * Test for the ProcessExecutor class.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: ProcessExecutorTest.java,v 1.2 2006-11-06 16:51:12 zwierzem Exp $
 */
public class ProcessExecutorTest
    extends LedgeTestCase
{
    private ScriptDirectory scriptDir;
    
    private ProcessExecutor executor;
    
    public void setUp()
    {
        boolean unix = false;
        String osName = System.getProperty("os.name");
        if(osName.equals("Linux") || osName.equals("Mac OS X"))
        {
            unix = true;
        }
        
        scriptDir = new ScriptDirectory(getFileSystem(), 
            ScriptDirectory.DEFAULT_PROVIDER,
            unix ? "external" : null,
            ScriptDirectory.DEFAULT_PATH_PATTERN,
            ScriptDirectory.DEFAULT_COMMAND);

        executor = new ProcessExecutor(getLogger(), ProcessExecutor.DEFAULT_SHELL);
        
        initLog4J("ERROR");
    }
    
    public void testPlainExec() throws IOException
    {
        ExecutionResult res = executor.exec(scriptDir.getPath("test.sh"));
        assertEquals(0, res.getExitValue());
    }    
    
    public void testExitValue() throws IOException
    {
        ExecutionResult res = executor.exec(scriptDir.getPath("exitValue.sh"));
        assertEquals(4, res.getExitValue());        
    }
    
    public void testInput() throws IOException
    {
        ExecutionResult res;
        res = executor.exec("bad input", false, false, scriptDir.getPath("input.sh"));
        assertEquals(1, res.getExitValue());
        res = executor.exec("good input", false, false, scriptDir.getPath("input.sh"));
        assertEquals(0, res.getExitValue());
    }
    
    public void testOuptut() throws IOException
    {
        ExecutionResult res;
        res = executor.exec(true, false, scriptDir.getPath("test.sh"));
        assertEquals("test.sh\n", res.getOutput());
    }

    public void testError() throws IOException
    {
        ExecutionResult res;
        res = executor.exec(false, true, scriptDir.getPath("error.sh"));
        assertEquals(0, res.getExitValue());
        assertEquals("error.sh\n", res.getError());
    }
    
    public void testConifg()
        throws Exception
    {
        checkSchema("config/org.objectledge.external.ProcessExecutor.xml",
            "org/objectledge/external/ProcessExecutor.rng");
        executor = new ProcessExecutor(getLogger(), getConfig(getFileSystem(),
            "config/org.objectledge.external.ProcessExecutor.xml"));
    }
}
