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
package org.objectledge.logging;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.LogLog;
import org.objectledge.filesystem.FileSystem;

/**
 * A derivate of log4j.FileAppender that accepts paths within Ledge file system.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeFileAppender.java,v 1.1 2004-06-16 11:06:13 fil Exp $
 */
public class LedgeFileAppender extends FileAppender
{
    private FileSystem fileSystem;
    
    public LedgeFileAppender(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }

    /**
     * <p>
     * Sets and <i>opens </i> the file where the log output will go. The specified file must be
     * writable.
     * 
     * <p>
     * If there was already an opened file, then the previous file is closed first.
     * 
     * <p>
     * <b>Do not use this method directly. To configure a FileAppender or one of its subclasses, set
     * its properties one by one and then call activateOptions. </b>
     * 
     * @param fileName
     *            The path to the log file.
     * @param append
     *            If true will append to fileName. Otherwise will truncate fileName.
     */
    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO,
        int bufferSize) throws IOException
    {
        LogLog.debug("setFile called: " + fileName + ", " + append);

        // It does not make sense to have immediate flush and bufferedIO.
        if(bufferedIO)
        {
            setImmediateFlush(false);
        }

        reset();
        Writer fw = createWriter(fileSystem.getOutputStream(fileName, append));
        if(bufferedIO)
        {
            fw = new BufferedWriter(fw, bufferSize);
        }
        this.setQWForFiles(fw);
        this.fileName = fileName;
        this.fileAppend = append;
        this.bufferedIO = bufferedIO;
        this.bufferSize = bufferSize;
        writeHeader();
        LogLog.debug("setFile ended");
    }    
}
