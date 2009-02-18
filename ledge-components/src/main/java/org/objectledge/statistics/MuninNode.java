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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;
import org.picocontainer.Startable;

/**
 * An implementation of munin-node
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: MuninNode.java,v 1.2 2008-01-20 15:17:31 rafal Exp $
 */
public class MuninNode
    implements Startable
{
    public static final int DEFAULT_PORT = 9000;
    
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    
    /**
     * Creates new MuninNode instance.
     * 
     * @param host host address to listen on, null for any.
     * @param port the port on which the node will listen.
     * @param bufferSize size of input and output buffers.
     * @param statistics the statistics component.
     * @param threadPool the ThreadPool component.
     * @param log the log.
     * @throws IOException if the server socket could not be created.
     */
    public MuninNode(String host, int port, int bufferSize, Statistics statistics,
        ThreadPool threadPool, Logger log)
        throws IOException
    {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(true);
        SocketAddress address = host == null ? new InetSocketAddress(port) : new InetSocketAddress(
            host, port);
        channel.socket().bind(address);
        threadPool.runDaemon(new AcceptTask(channel, bufferSize, statistics, threadPool, log));
    }

    public MuninNode(Configuration config, Statistics statistics, ThreadPool threadPool, Logger log)
        throws IOException
    {
        this(config.getChild("listen").getChild("host").getValue(null), config.getChild("listen")
            .getChild("port").getValueAsInteger(DEFAULT_PORT), config.getChild("buffer-size")
            .getValueAsInteger(DEFAULT_BUFFER_SIZE), statistics, threadPool, log);
    }

    public void start()
    {

    }

    public void stop()
    {

    }

    /**
     * A daemon task that accepts incoming connections and passes them to processors.
     */
    private class AcceptTask
        extends Task
    {
        private final Statistics statistics;

        private final ServerSocketChannel channel;

        private final Logger log;

        private final int bufferSize;

        private final ThreadPool threadPool;

        public AcceptTask(ServerSocketChannel channel, int bufferSize, Statistics statistics, ThreadPool thereadPool,
            Logger log)
        {
            this.channel = channel;
            this.statistics = statistics;
            this.bufferSize = bufferSize;
            this.threadPool = thereadPool;
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
                    ProcessorTask processor = new ProcessorTask(clientChannel, bufferSize,
                        statistics, log);
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
            log.info("accept task terminating");
            thread.interrupt();
            try
            {
                channel.close();
            }
            catch(IOException e)
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
        private final Statistics statistics;

        private final SocketChannel channel;

        private final Logger log;

        private final Charset charset;

        private final ByteBuffer inputBytes;

        private final CharBuffer outputChars;

        public ProcessorTask(SocketChannel channel, int bufferSize, Statistics statistics,
            Logger log)
        {
            this.statistics = statistics;
            this.channel = channel;
            this.log = log;
            inputBytes = ByteBuffer.allocate(bufferSize);
            outputChars = CharBuffer.allocate(bufferSize);
            this.charset = Charset.forName("ISO-8859-1");
        }

        public void process(Context context)
        {
            log.info("connection from " + channel.socket().getInetAddress());
            try
            {
                String host = InetAddress.getLocalHost().getCanonicalHostName();
                write("# cykltron munin node at " + host + "\n");
                String command;
                while(!(command = read()).equals("quit"))
                {
                    log.info("got command \""+ command+"\"");
                    if(command.equals("")) // bug in munin?
                    {
                        break;
                    }
                    write(dispatch(command));
                }
                log.info("disconnecting");
            }
            catch(IOException e)
            {
                log.error("IO exception", e);
            }
            finally
            {
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

        private String read()
            throws IOException
        {
            channel.read(inputBytes);
            inputBytes.flip();
            CharBuffer inputChars = charset.decode(inputBytes);
            String string = inputChars.toString().trim();
            inputBytes.clear();
            return string;
        }

        private void write(String string)
            throws IOException
        {
            outputChars.put(string);
            outputChars.flip();
            ByteBuffer outputBytes = charset.encode(outputChars);
            channel.write(outputBytes);
            outputChars.clear();
        }

        private String dispatch(String command)
        {
            if(command.equals("version"))
            {
                return version();
            }
            if(command.equals("nodes"))
            {
                return nodes();
            }
            if(command.startsWith("list"))
            {
                return list();
            }
            if(command.startsWith("config"))
            {
                return config(command.substring(6).trim());
            }
            if(command.startsWith("fetch"))
            {
                return fetch(command.substring(5).trim());
            }
            if(command.equals("quit"))
            {
                return "good bye\n";
            }
            if(command.equals(""))
            {
                return "\n";
            }
            return "# Unknown command. Try list, nodes, config, fetch, version or quit\n";
        }

        private String version()
        {
            return "cyklotron munin node version 1.0\n";
        }

        private String nodes()
        {
            return "cyklotron\n.\n";
        }

        private String list()
        {
            StringBuilder buff = new StringBuilder();
            for (MuninGraph graph : statistics.getGraphs())
            {
                buff.append(graph.getId()).append(' ');
            }
            buff.append('\n');
            return buff.toString();
        }

        private String config(String item)
        {
            StringBuilder buff = new StringBuilder();
            for (MuninGraph graph : statistics.getGraphs())
            {
                if(graph.getId().equals(item))
                {
                    buff.append(graph.getConfig().trim()).append("\n");
                    break;
                }
            }
            buff.append(".\n");
            return buff.toString();
        }

        private String fetch(String item)
        {
            StringBuilder buff = new StringBuilder();
            for (MuninGraph graph : statistics.getGraphs())
            {
                if(graph.getId().equals(item))
                {
                    for (String variable : graph.getVariables())
                    {
                        buff.append(variable).append(".value ").append(graph.getValue(variable))
                            .append("\n");
                    }
                    break;
                }
            }
            buff.append(".\n");
            return buff.toString();
        }
    }
}
