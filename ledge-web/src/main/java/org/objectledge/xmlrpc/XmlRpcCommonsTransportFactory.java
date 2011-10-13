package org.objectledge.xmlrpc;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcTransport;

public class XmlRpcCommonsTransportFactory extends org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory
{
    public XmlRpcCommonsTransportFactory(XmlRpcClient pClient)
    {
        super(pClient);       
    }

    @Override
    public XmlRpcTransport getTransport()
    {        
        return new XmlRpcCommonsTransport(this);
    }   
}
