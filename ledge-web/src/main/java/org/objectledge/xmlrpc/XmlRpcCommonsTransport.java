package org.objectledge.xmlrpc;

import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.XmlRpcHttpClientConfig;

public class XmlRpcCommonsTransport
    extends org.apache.xmlrpc.client.XmlRpcCommonsTransport
{

    public XmlRpcCommonsTransport(XmlRpcCommonsTransportFactory pFactory)
    {
        super(pFactory);
    }

    @Override
    protected PostMethod newPostMethod(XmlRpcHttpClientConfig pConfig)
    {
        HostConfiguration hostConfig = client.getHostConfiguration();
        URL requestUrl = pConfig.getServerURL();
        if(hostConfig != null)
        {
            if(requestUrl.getProtocol().equals(hostConfig.getProtocol().getScheme())
                && requestUrl.getHost().equals(hostConfig.getHost())
                && requestUrl.getPort() == hostConfig.getPort())
            {
                // use URI relative to hostConfig
                return new PostMethod(requestUrl.getFile());
            }
            else
            {
                throw new IllegalArgumentException("Server URL " + requestUrl
                    + "does not agree with HttpClient host configuration " + hostConfig);
            }
        }
        // use absolute URI
        return new PostMethod(requestUrl.toString());
    }
}
