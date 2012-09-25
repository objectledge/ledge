package org.objectledge.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;

public interface MessagingFactory
{
    <C extends Connection> C createConnection(String name, Class<C> connectionClass)
        throws JMSException;
}
