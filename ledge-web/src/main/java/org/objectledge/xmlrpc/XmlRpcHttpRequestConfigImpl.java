package org.objectledge.xmlrpc;

public class XmlRpcHttpRequestConfigImpl
    extends org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl
    implements XmlRpcHttpRequestConfig
{
    private String remoteAddr;
    
    private String remoteHost;
    
    private int remotePort;
    
    private boolean secure;
     
    @Override
    public String getRemoteAddr()
    {
        return remoteAddr;
    }

    @Override
    public String getRemoteHost()
    {
        return remoteHost;
    }

    @Override
    public int getRemotePort()
    {
        return remotePort;
    }

    @Override
    public boolean isSecure()
    {
        return secure;
    }

    public void setRemoteAddr(String remoteAddr)
    {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(int remotePort)
    {
        this.remotePort = remotePort;
    }

    public void setSecure(boolean secure)
    {
        this.secure = secure;
    }
}

