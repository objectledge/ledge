package org.objectledge.authentication.sso;

import org.apache.xmlrpc.XmlRpcException;

public interface XmlRpcSingleSignOnService
{
    String validateTicket(String ticket, String domain, String client)
        throws XmlRpcException;

    void logIn(String principal, String domain)
        throws XmlRpcException;

    void logOut(String principal, String domain)
        throws XmlRpcException;

    String checkStatus(String principal, String domain)
        throws XmlRpcException;

    String ssoBaseUrl(String domain)
        throws XmlRpcException;
}
