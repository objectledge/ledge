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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.jcontainer.dna.Configuration;
import org.objectledge.pipeline.ProcessingException;

/**
 * MessagingConsumerHelper
 * 
 * @author lukasz
 */
public class MessagingConsumerHelper<S extends Session, C extends Connection>
{

    private Integer ackMode;

    private String clientId;

    private boolean transacted;

    private boolean topic;

    private String destinationName;

    private Destination destination;

    private S jmsSession;

    private MessageConsumer messageConsumer;

    private final C connection;

    private final MessageListener messageListener;

    private final ExceptionListener exceptionListener;

    public MessagingConsumerHelper(C connection, MessageListener messageListener,
        ExceptionListener exceptionListener)
        throws Exception
    {
        this.connection = connection;
        this.messageListener = messageListener;
        this.exceptionListener = exceptionListener;
    }

    public MessagingConsumerHelper(C connection, MessageListener messageListener,
        ExceptionListener exceptionListener, Configuration configuration)
    {
        this.connection = connection;
        this.messageListener = messageListener;
        this.exceptionListener = exceptionListener;
        configure(configuration);
    }

    public void start()
        throws ProcessingException
    {
        try
        {
            if(clientId != null && clientId.length() > 0)
            {
                connection.setClientID(clientId);
            }
            if(ackMode == null)
            {
                this.ackMode = Session.AUTO_ACKNOWLEDGE;
            }

            connection.setExceptionListener(exceptionListener);
            connection.start();
            jmsSession = (S)connection.createSession(transacted, ackMode);

            if(topic)
            {
                destination = jmsSession.createTopic(destinationName);
            }
            else
            {
                destination = jmsSession.createQueue(destinationName);

            }
            messageConsumer = jmsSession.createConsumer(destination);
            messageConsumer.setMessageListener(messageListener);

        }
        catch(JMSException e)
        {
            throw new ProcessingException("JMSException ", e);
        }
    }

    public void stop()
        throws ProcessingException
    {
        try
        {
            if(jmsSession != null)
            {
                jmsSession.close();
            }
            if(connection != null)
            {
                connection.close();
            }
        }
        catch(JMSException e)
        {
            throw new ProcessingException("JMSException ", e);
        }

    }

    protected void configure(Configuration configuration)
    {
        setAckMode(configuration.getChild("ackMode").getValueAsInteger(Session.AUTO_ACKNOWLEDGE));
        setClientId(configuration.getChild("clientId").getValue(null));
        setTopic(configuration.getChild("isTopic").getValueAsBoolean(false));
        setDestinationName(configuration.getChild("destinationName").getValue(""));
        setTransacted(configuration.getChild("isTransacted").getValueAsBoolean(false));
    }

    public Integer getAckMode()
    {
        return ackMode;
    }

    public void setAckMode(Integer ackMode)
    {
        this.ackMode = ackMode;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public boolean isTransacted()
    {
        return transacted;
    }

    public void setTransacted(boolean transacted)
    {
        this.transacted = transacted;
    }

    public boolean isTopic()
    {
        return topic;
    }

    public void setTopic(boolean topic)
    {
        this.topic = topic;
    }

    public String getDestinationName()
    {
        return destinationName;
    }

    public void setDestinationName(String destinationName)
    {
        this.destinationName = destinationName;
    }

    public Destination getDestination()
    {
        return destination;
    }

    public void setDestination(Destination destination)
    {
        this.destination = destination;
    }

    public S getJmsSession()
    {
        return jmsSession;
    }

    public MessageConsumer getMessageConsumer()
    {
        return messageConsumer;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public MessageListener getMessageListener()
    {
        return messageListener;
    }

    public ExceptionListener getExceptionListener()
    {
        return exceptionListener;
    }

}
