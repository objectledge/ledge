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
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.objectledge.filesystem.FileSystem;

/**
 * A derivate of log4j.RollingFileAppender that accepts paths within Ledge file system.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeRollingFileAppender.java,v 1.3 2004-09-24 11:25:24 zwierzem Exp $
 */
public class LedgeRollingFileAppender
	extends RollingFileAppender
{
    private FileSystem fileSystem;
    
    public LedgeRollingFileAppender(FileSystem fileSystem)
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

    /**
     * Implements the usual roll over behaviour.
     * 
     * <p>
     * If <code>MaxBackupIndex</code> is positive, then files {<code>File.1</code>, ...,
     * <code>File.MaxBackupIndex -1</code>} are renamed to {<code>File.2</code>, ...,
     * <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is renamed
     * <code>File.1</code> and closed. A new <code>File</code> is created to receive further log
     * output.
     * 
     * <p>
     * If <code>MaxBackupIndex</code> is equal to zero, then the <code>File</code> is truncated
     * with no backup files created.
     *  
     */
    public// synchronization not necessary since doAppend is alreasy synched
    void rollOver()
    {
        String file;
        String target;
        
        LogLog.debug("rolling over count=" + ((CountingQuietWriter)qw).getCount());
        LogLog.debug("maxBackupIndex=" + maxBackupIndex);

        try
        {
            // If maxBackups <= 0, then there is no file renaming to be done.
            if(maxBackupIndex > 0)
            {
                // Delete the oldest file, to keep Windows happy.
                file = fileName + '.' + maxBackupIndex;
                if(fileSystem.exists(file)) fileSystem.delete(file);

                // Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3, 2}
                for (int i = maxBackupIndex - 1; i >= 1; i--)
                {
                    file = fileName + "." + i;
                    if(fileSystem.exists(file))
                    {
                        target = fileName + '.' + (i + 1);
                        LogLog.debug("Renaming file " + file + " to " + target);
                        fileSystem.rename(file, target);
                    }
                }

                // Rename fileName to fileName.1
                target = fileName + "." + 1;

                this.closeFile(); // keep windows happy.

                file = fileName;
                LogLog.debug("Renaming file " + file + " to " + target);
                fileSystem.rename(file, target);
            }
        }
        catch(Exception e)
        {
            LogLog.error("rollover operations failed", e);
        }

        try
        {
            // This will also close the file. This is OK since multiple
            // close operations are safe.
            this.setFile(fileName, false, bufferedIO, bufferSize);
        }
        catch(IOException e)
        {
            LogLog.error("setFile(" + fileName + ", false) call failed.", e);
        }
    }    
}
