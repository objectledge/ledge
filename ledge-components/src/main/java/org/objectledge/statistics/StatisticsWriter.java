// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.statistics;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.Startable;

/**
 * An add-on component for Statistics component that periodically logs statistics values to a file.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: StatisticsWriter.java,v 1.2 2008-01-20 15:17:31 rafal Exp $
 */
public class StatisticsWriter 
    implements Startable
{
    private final Statistics statistics;
    
    /**
     * Creates new StatisticsWriter instance.
     * 
     * @param statistics the Statistics component.
     * @param fileSystem the FileSystem component.
     * @param threadPool the ThreadPool component.
     * @param path the path of the statistics log file, FileSystem relative.
     * @param interval interval between statistics dumps in seconds.
     * @param keep should the log file be appended to after restart.
     */
    public StatisticsWriter(Statistics statistics, FileSystem fileSystem, ThreadPool threadPool,
        String path, int interval, boolean keep)
    {
        this.statistics = statistics;
        PrintWriter pw;
        try
        {
            fileSystem.mkdirs(FileSystem.directoryPath(path));
            OutputStream os = fileSystem.getOutputStream(path, keep);
            if(os == null)
            {
                throw new IOException("can't write to "+path);
            }
            pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(os), "UTF-8"));
            printHeader(pw);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("failed to open statistics log file", e);
        }
        threadPool.runDaemon(new StatisticsWriterTask(pw, interval));
    }

    /**
     * Creates new StatisticsWriter instance.
     * 
     * @param statistics the Statistics component.
     * @param fileSystem the FileSystem component.
     * @param threadPool the ThreadPool component.
     * @param config the Configuration.
     * @throws ConfigurationException if the configuration.
     */
    public StatisticsWriter(Statistics statistics, FileSystem fileSystem, ThreadPool threadPool,
        Configuration config) throws ConfigurationException
    {
        this(statistics, fileSystem, threadPool, config.getChild("path").getValue(),
            config.getChild("interval").getValueAsInteger(), false);
    }
    
    private void printHeader(PrintWriter pw)
    {
        pw.print("# time");
        MuninGraph[] graphs = statistics.getGraphs();
        if(graphs.length > 0)
        {
            pw.print(", ");
        }
        for(int i = 0; i < graphs.length; i++)
        {
            String[] variables = graphs[i].getVariables(); 
            for(int j = 0; j < variables.length; j++)
            {
                pw.print(graphs[i].getId());
                pw.print(".");
                pw.print(variables[j]);
                if(j < variables.length - 1 || i < graphs.length - 1)
                {
                    pw.print(", ");
                }
            }
        }
        pw.println();
        pw.flush();
    }
    
    private void printStats(PrintWriter pw)
    {
        pw.print(System.currentTimeMillis());
        MuninGraph[] graphs = statistics.getGraphs();
        if(graphs.length > 0)
        {
            pw.print(", ");
        }
        for(int i = 0; i < graphs.length; i++)
        {
            Map<String, Number> values = graphs[i].getValues();
            String[] variables = graphs[i].getVariables(); 
            for(int j = 0; j < variables.length; j++)
            {
                Number value = values.get(variables[j]);
                if(value == null)
                {
                    pw.print("U");
                }
                else
                {
                    pw.print(value);
                }
                if(j < variables.length - 1 || i < graphs.length - 1)
                {
                    pw.print(", ");
                }
            }
        }
        pw.println();
        pw.flush();
    }

    /**
     * A daemon task that handles periodical writes. 
     */
    private class StatisticsWriterTask
        extends Task
    {
        private final PrintWriter pw;
        
        private final int interval;

        /**
         * Creates new StatisticsWriterTask instance.
         * 
         * @param pw print writer the data sink.
         * @param interval interval between writes in seconds.
         */
        public StatisticsWriterTask(PrintWriter pw, int interval)
        {
            this.pw = pw;
            this.interval = interval;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getName()
        {
            return "Statistics writer";
        }

        /**
         * {@inheritDoc}
         */
        public void process(Context context)
            throws ProcessingException
        {
            while(!Thread.interrupted())
            {
                printStats(pw);
                try
                {
                    Thread.sleep(1000*interval);
                }
                catch(InterruptedException e)
                {
                    return;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
    }
}
