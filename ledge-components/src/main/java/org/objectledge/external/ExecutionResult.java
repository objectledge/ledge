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

import java.io.UnsupportedEncodingException;

import org.objectledge.utils.StringUtils;

/**
 * Represents the result of script execution.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: ExecutionResult.java,v 1.2 2007-11-18 21:19:55 rafal Exp $
 */
public class ExecutionResult
{
    private final int exitValue;
    private final byte[] output;
    private final byte[] error;

    /**
     * Creates a new ExecutionResult instance.
     *
     * @param exitValue the exit value of the process.
     * @param captured output stream the process, or null if not available.
     * @param captured error stream the process, or null if not available.
     */
    public ExecutionResult(int exitValue, byte[] output, byte[] error)
    {
        this.exitValue = exitValue;
        this.output = output;
        this.error = error;        
    }
    
    /**
     * Creates a new ExecutionResult instance.
     *
     * @param exitValue process exit value.
     */
    public ExecutionResult(int exitValue)
    {
        this.exitValue = exitValue;
        this.output = null;
        this.error = null;
    }

    /**
     * Returns the process exit value.
     * 
     * @return the process exit value.
     */
    public int getExitValue()
    {
        return exitValue;
    }
    
    /**
     * Returns the data captured from process output stream as bytes.
     * 
     * @return the data captured from process output stream as bytes, or null if not available.
     */
    public byte[] getOutputBytes()
    {
        return output;
    }
    
    /**
     * Returns the data captured from process output stream as a string.
     * 
     * @param charset output stream encoding.
     * @return the data captured from process output stream as a string, or null if not available.
     * @throws UnsupportedEncodingException if the encoding is not supported by the VM.
     */
    public String getOutput(String charset) throws UnsupportedEncodingException
    {
        if(output == null)
        {
            return null;
        }
        return new String(output, charset);
    }
    
    /**
     * Returns the data captured from process output stream as an UTF-8 string.
     * 
     * @return the data captured from process output stream as an UTF-8 string, or null if not available.
     */
    public String getOutput()
    {
        return StringUtils.fromUTF8(output);
    }
    
    /**
     * Returns the data captured from process error stream as bytes.
     * 
     * @return the data captured from process error stream as bytes, or null if not available.
     */
    public byte[] getErrorBytes()
    {
        return error;
    }
    
    /**
     * Returns the data captured from process error stream as a string.
     * 
     * @param charset output stream encoding.
     * @return the data captured from process error stream as a string, or null if not available.
     * @throws UnsupportedEncodingException if the encoding is not supported by the VM.
     */
    public String getError(String charset) throws UnsupportedEncodingException
    {
        if(error == null)
        {
            return null;
        }
        return new String(error, charset);
    }

    /**
     * Returns the data captured from process error stream as an UTF-8 string.
     * 
     * @return the data captured from process error stream as an UTF-8 string, or null if not available.
     */
    public String getError()
    {
        return StringUtils.fromUTF8(error);
    }
}
