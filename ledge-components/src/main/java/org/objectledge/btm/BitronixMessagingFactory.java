package org.objectledge.btm;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

public class BitronixMessagingFactory
    implements org.objectledge.messaging.MessagingFactory
{
    private final ConnectionFactory connectionFactory;

    public BitronixMessagingFactory(String uniqueName, BitronixTransactionManager b)
    {
        connectionFactory = b.getConnectionFactory(uniqueName);
    }

    @Override
    public <C extends Connection> C createConnection(String name, Class<C> connectionClass)
        throws JMSException
    {
        return (C)connectionFactory.createConnection();
    }
}