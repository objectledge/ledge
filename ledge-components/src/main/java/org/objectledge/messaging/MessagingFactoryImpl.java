// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
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

package org.objectledge.messaging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.WeakHashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.pipeline.ProcessingException;
import org.picocontainer.Startable;

public class MessagingFactoryImpl
    implements MessagingFactory, Startable
{
    private HashMap<String, Object> connectionFactoryPool;

    private WeakHashMap<Connection, Object> connections = new WeakHashMap<>();

    private final Logger logger;

    public MessagingFactoryImpl(Configuration config, Logger logger)
        throws ProcessingException
    {
        this.logger = logger;
        try
        {
            connectionFactoryPool = new HashMap<String, Object>();
            Configuration[] connectionDefs = config.getChildren("connection");
            Configuration[] xaconnectionDefs = config.getChildren("xaconnection");

            for(Configuration connectionDef : connectionDefs)
            {
                String name = connectionDef.getAttribute("name", "");
                String url = connectionDef.getChild("url").getValue(
                    ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
                String user = connectionDef.getChild("user").getValue(
                    ActiveMQConnectionFactory.DEFAULT_USER);
                String password = connectionDef.getChild("password").getValue(
                    ActiveMQConnectionFactory.DEFAULT_PASSWORD);

                connectionFactoryPool.put(name, new ActiveMQConnectionFactory(user, password, url));
            }

            for(Configuration xaconnectionDef : xaconnectionDefs)
            {
                String name = xaconnectionDef.getAttribute("name");
                String url = xaconnectionDef.getChild("url").getValue(
                    ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
                String user = xaconnectionDef.getChild("user").getValue(
                    ActiveMQConnectionFactory.DEFAULT_USER);
                String password = xaconnectionDef.getChild("password").getValue(
                    ActiveMQConnectionFactory.DEFAULT_PASSWORD);

                connectionFactoryPool.put(name, (Object)new ActiveMQXAConnectionFactory(user,
                    password, url));
            }
        }
        catch(ConfigurationException e)
        {
            throw new ProcessingException("Configuration Exception ", e);
        }
    }

    public <C extends Connection> C createConnection(String name, Class<C> connectionClass)
        throws JMSException
    {
        Object connectionFactory = connectionFactoryPool.get(name);
        C connection = null;
        if(XAConnection.class.isAssignableFrom(connectionClass)
            && (connectionFactory instanceof XAConnectionFactory))
        {
            connection = (C)((XAConnectionFactory)connectionFactory).createXAConnection();
        }
        else if(Connection.class.isAssignableFrom(connectionClass))
        {
            connection = (C)((ConnectionFactory)connectionFactory).createConnection();
        }
        synchronized(connections)
        {
            connections.put(connection, null);
        }
        return connection;
    }

    @Override
    public void start()
    {
        // nothing to do
    }

    @Override
    public void stop()
    {
        synchronized(connections)
        {
            Iterator<Connection> i = connections.keySet().iterator();
            while(i.hasNext())
            {
                Connection conn = i.next();
                if(conn != null)
                {
                    try
                    {
                        conn.close();
                    }
                    catch(JMSException e)
                    {
                        logger.error("error closing connection", e);
                    }
                }
                i.remove();
            }
        }
        logger.info("all connections closed");
    }
}
