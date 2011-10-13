package org.objectledge.xmlrpc;

/**
 * Extends {@link org.apache.xmlrpc.common.XmlRpcHttpRequestConfig} with additional properties
 * useful for authentication checking.
 */
public interface XmlRpcHttpRequestConfig
    extends org.apache.xmlrpc.common.XmlRpcHttpRequestConfig
{
    /**
     * Returns the Internet Protocol (IP) address of the client or last proxy that sent the request.
     */
    public String getRemoteAddr();

    /**
     * Returns the fully qualified name of the client or the last proxy that sent the request.
     */
    public String getRemoteHost();

    /**
     * Returns the Internet Protocol (IP) source port of the client or last proxy that sent the request.
     */
    public int getRemotePort();

    /**
     * Returns a boolean indicating whether this request was made using a secure channel, such as HTTPS.
     */
    public boolean isSecure();
}
