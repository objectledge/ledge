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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.Startable;

/**
 * An implementation of munin-node  
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: MuninNode.java,v 1.1 2005-05-13 06:46:21 rafal Exp $
 */
public class MuninNode implements Startable
{
    private final Logger log;
    
    private final ThreadPool threadPool;
    
    private final MuninStatsisticsFormatter formatter;
    
    /** Number of incoming connections that can await handling. */ 
    private static final int BACKLOG = 1;
    
    /**
     * Creates new MuninNode instance.
     * 
     * @param threadPool the ThreadPool component.
     * @param log the log.
     * @param statistics the statistics component.
     * @param port the port on which the node will listen.
     * @throws IOException if the server socket could not be created.
     */
    public MuninNode(ThreadPool threadPool, Logger log, Statistics statistics, int port)
        throws IOException
    {
        this.threadPool = threadPool;
        this.log = log;
        this.formatter = new MuninStatsisticsFormatter(statistics);
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(true);
        channel.socket().bind(new InetSocketAddress(port), BACKLOG);
        threadPool.runDaemon(new AcceptTask(channel, log));
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
    
    /**
     * A daemon task that accepts incoming connections and passes them to processors.
     */
    private class AcceptTask 
        extends Task
    {
        private final ServerSocketChannel channel;
        
        private final Logger log;
        
        public AcceptTask(ServerSocketChannel channel, Logger log)
        {
            this.channel = channel;
            this.log = log;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getName()
        {
            return "munin-node accept thread";
        }

        /**
         * {@inheritDoc}
         */
        public void process(Context context)
        {
            while(!Thread.interrupted())
            {
                try
                {
                    SocketChannel clientChannel = channel.accept();
                    ProcessorTask processor = new ProcessorTask(clientChannel, log);
                    threadPool.runWorker(processor);
                }
                catch(IOException e)
                {
                    log.error("problem accepting connection", e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public void terminate(Thread thread)
        {
            log.info("accep task terminating");
            thread.interrupt();
            try
            {
                channel.close();
            }
            catch (IOException e) 
            {
                log.error("problem closing server socket channel");
            }
        }
    }

    /**
     * A class that encapsulates connection handing logic.
     */
    private class ProcessorTask
        extends Task
    {
        private final SocketChannel channel;
        
        private final ByteBuffer buffer;
        
        private final Logger log;
        
        public ProcessorTask(SocketChannel channel, Logger log)
        {
            this.channel = channel;
            this.log = log;
            this.buffer = ByteBuffer.allocate(64);
        }
        
        public void process(Context context)
        {
            log.info("connection from "+channel.socket().getInetAddress());
            // TODO implement
            try
            {
                channel.close();
            }
            catch(IOException e)
            {
                log.error("problem closing connection", e);
            }
        }
    }
    
    /**
     * A class that provides the data from the Statistics component to Munin in textual form. 
     */
    private class MuninStatsisticsFormatter
    {
        private final Statistics statistics;

/* Contents of removed Munin.vt template to be used for impelemnting the following methods
#macro(EOL)
    
#end
#if($parametersTool.isDefined('config'))
#set($graph = $statistics.getGraph($parametersTool.config))
graph_tile $graph.title
#if($graph.createArgs)create_args $graph.createArgs#EOL()#end
#if($graph.graphArgs)graph_args $graph.graphArgs#EOL()#end
graph_order #foreach($dataSource in $graph.order)$dataSource.name #end#EOL()
#if($graph.vLabel)graph_vlabel $graph.vLabel#EOL()#end
#if($graph.totalLabel)graph_total $graph.totalLabel#EOL()#end
scale#if($graph.notScaled) no#else yes#end#EOL()
graph#if($graph.notDrawn) no#else yes#end#EOL()
update#if($graph.notUpdated) no#else yes#end#EOL()
#foreach($ds in $graph.order)
${ds.name}.label $ds.label
#if($ds.cdef)${ds.name}.cdef $ds.cdef#EOL()#end
#if($ds.graph.toString().equals('HIDDEN'))
${ds.name}.graph no
#else
${ds.name}.draw $ds.graph
#end
#if($ds.max)${ds.name}.max $ds.max#EOL()#end
#if($ds.min)${ds.name}.min $ds.min#EOL()#end
${ds.name}.type $ds.type
#if($ds.minWarning || $ds.maxWarning)
${ds.name}.warning $!{ds.minWarning}:$!{ds.maxWarning}
#end
#if($ds.minCritical || $ds.maxCritical)
${ds.name}.critical $!{ds.minCritical}:$!{ds.maxCritical}
#end
#end
#elseif($parametersTool.isDefined('data'))
#set($graph = $statistics.getGraph($parametersTool.data))
#foreach($ds in $graph.order)
$ds.name $statistics.getDataValue($ds) 
#end
#else
#foreach($name in $statistics.graphNames)
$name
#end
#end
*/
        
        public MuninStatsisticsFormatter(Statistics statistics)
        {
            this.statistics = statistics;
        }
        
        public String nodes()
        {
            // TODO implement
            return "";
        }
        
        public String config(String node)
        {
            // TODO implement
            return "";
        }
        
        public String fetch(String node)
        {
            // TODO implement
            return "";
        }
        
        public String version()
        {
            // TODO implement
            return "";
        }
    }
}
