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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.utils.StringUtils;

import clover.org.apache.velocity.app.event.d;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: ProcessExecutor.java,v 1.1 2006-03-24 14:27:37 rafal Exp $
 */
public class ProcessExecutor
{
    /**
     * Default shell invocation format.
     */
    public static final String DEFAULT_SHELL = "sh -c";

    /**
     * Shell invocation format.
     */
    private final String[] shellTokens;

    /** The logger. */
    private final Logger log;

    /**
     * Creates a new ProcessExecutor instance.
     * 
     * @param logger the logger to use.
     * @param shell the shell invocation format.
     */
    public ProcessExecutor(Logger logger, String shell)
    {
        this.log = logger;
        this.shellTokens = shell.split(" ");
    }

    /**
     * Creates a new ProcessExecutor instance.
     * 
     * @param logger the logger to use.
     * @param configuration component configuration.
     */
    public ProcessExecutor(Logger logger, Configuration configuration)
    {
        this(logger, configuration.getChild("shell").getValue(DEFAULT_SHELL));
    }

    /**
     * Execute an external process.
     * <p>
     * No data is sent to the process input stream, output and error streams are not captured.
     * </p>
     * 
     * @param args the script name and arguments.
     * @return execution result.
     * @throws IOException If the execution fails.
     */
    public ExecutionResult exec(String... args)
        throws IOException
    {
        String cmd = concat(args);
        log.debug("executing " + cmd);
        try
        {
            Process process = Runtime.getRuntime().exec(args);
            process.waitFor();
            return new ExecutionResult(process.exitValue());
        }
        catch(InterruptedException e)
        {
            throw new IOException("execution interrupted");
        }
    }

    /**
     * Executes an external process.
     * 
     * @param input data to be passed to the process inpupt stream.
     * @param captureOutput should process output stream be captured.
     * @param captureError should process error stream be captured.
     * @param args process executable name and arguments.
     * @return execution result.
     * @throws IOException If the execution fails.
     */
    public ExecutionResult exec(byte[] input, boolean captureOutput, boolean captureError,
        String... args)
        throws IOException
    {
        String cmd = concat(args);
        File in = null;
        File out = null;
        File err = null;
        try
        {
            boolean redirect = false;
            if(input != null)
            {
                in = File.createTempFile("ledge-in-", ".tmp");
                write(in, input);
                args = StringUtils.push(args, "<" + in.getPath());
                redirect = true;
            }
            if(captureOutput)
            {
                out = File.createTempFile("ledge-out-", ".tmp");
                args = StringUtils.push(args, ">" + out.getPath());
                redirect = true;
            }
            if(captureError)
            {
                err = File.createTempFile("ledge-err-", ".tmp");
                args = StringUtils.push(args, "2>" + err.getPath());
                redirect = true;
            }

            Process process;
            if(redirect)
            {
                args = StringUtils.push(shellTokens, concat(args));
                cmd = concat(args);
            }
            log.debug("executing " + cmd);
            process = Runtime.getRuntime().exec(args);
            process.waitFor();
            return new ExecutionResult(process.exitValue(), read(out), read(err));
        }
        catch(InterruptedException e)
        {
            throw new IOException("execution interrupted");
        }
        finally
        {
            release(in);
            release(out);
            release(err);
        }
    }

    /**
     * Executes an external process.
     * 
     * @param input data to be passed to the process inpupt stream.
     * @param captureOutput should process output stream be captured.
     * @param captureError should process error stream be captured.
     * @param args process executable name and arguments.
     * @return execution result.
     * @throws IOException
     * @throws IOException If the execution fails.
     */
    public ExecutionResult exec(String input, boolean captureOutput, boolean captureError,
        String... args)
        throws IOException
    {
        return exec(StringUtils.toUTF8(input), captureOutput, captureError, args);
    }

    /**
     * Executes an external process.
     * 
     * @param captureOutput should process output stream be captured.
     * @param captureError should process error stream be captured.
     * @param args process executable name and arguments.
     * @return execution result.
     * @throws IOException 
     * @throws IOException If the execution fails.
     */
    public ExecutionResult exec(boolean captureOutput, boolean captureError, String... args)
        throws IOException
    {
        return exec((byte[])null, captureOutput, captureError, args);
    }

    private byte[] read(File file)
        throws IOException
    {
        if(file == null)
        {
            return null;
        }
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        byte[] result = new byte[(int)file.length()];
        is.read(result);
        return result;
    }

    private void write(File file, byte[] bytes)
        throws IOException
    {
        OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
        fos.write(bytes);
        fos.close();
    }

    private void release(File file)
    {
        if(file != null && file.exists() && !log.isDebugEnabled())
        {
            file.delete();
        }
    }

    private String concat(String[] args)
    {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < args.length; i++)
        {
            buff.append(args[i]);
            if(i < args.length - 1)
            {
                buff.append(' ');
            }
        }
        return buff.toString();
    }
}
