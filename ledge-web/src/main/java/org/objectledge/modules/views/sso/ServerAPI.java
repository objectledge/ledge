package org.objectledge.modules.views.sso;

import java.security.Principal;

import javax.servlet.ServletException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping.AuthenticationHandler;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.authentication.sso.SingleSignOnService;
import org.objectledge.authentication.sso.XmlRpcSingleSignOnService;
import org.objectledge.context.Context;
import org.objectledge.xmlrpc.XmlRpcHttpRequestConfig;
import org.objectledge.xmlrpc.POJOHandlerMapping;
import org.objectledge.xmlrpc.XmlRpcView;

public class ServerAPI
    extends XmlRpcView
    implements XmlRpcSingleSignOnService
{
    private final SingleSignOnService singleSignOnService;

    private final UserManager userManager;

    private final Logger log;

    public ServerAPI(SingleSignOnService singleSignOnService, UserManager userManager,
        Context context, Logger log)
        throws ServletException
    {
        super(context, log);
        this.singleSignOnService = singleSignOnService;
        this.userManager = userManager;
        this.log = log;
    }

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
        throws XmlRpcException
    {
        POJOHandlerMapping handlerMapping = new POJOHandlerMapping(this,
            XmlRpcSingleSignOnService.class);
        handlerMapping.setAuthenticationHandler(new AuthenticationHandler()
            {
                @Override
                public boolean isAuthorized(XmlRpcRequest request)
                    throws XmlRpcException
                {
                    XmlRpcHttpRequestConfig requestConfig = (XmlRpcHttpRequestConfig)request
                        .getConfig();
                    String secret = requestConfig.getBasicPassword();
                    String remoteAddr = requestConfig.getRemoteAddr();
                    boolean secure = requestConfig.isSecure();
                    return singleSignOnService.validateApiRequest(secret, remoteAddr, secure);
                }
            });
        return handlerMapping;
    }

    @Override
    public String validateTicket(String ticket, String domain, String client)
        throws XmlRpcException
    {
        return singleSignOnService.validateTicket(ticket, domain, client).getName();
    }

    @Override
    public String logIn(String principalName, String domain)
        throws XmlRpcException
    {
        try
        {
            Principal principal = userManager.getUserByName(principalName);
            singleSignOnService.logIn(principal, domain);           
        }
        catch(AuthenticationException e)
        {
            log.warn("unknown user " + principalName);
        }
        return ""; 
    }

    @Override
    public String logOut(String principalName, String domain)
        throws XmlRpcException
    {
        try
        {
            Principal principal = userManager.getUserByName(principalName);
            singleSignOnService.logOut(principal, domain);            
        }
        catch(AuthenticationException e)
        {
            log.warn("unknown user " + principalName);           
        }
        return "";
    }

    @Override
    public String checkStatus(String principalName, String domain)
        throws XmlRpcException
    {
        try
        {
            Principal principal = userManager.getUserByName(principalName);
            return singleSignOnService.checkStatus(principal, domain).name();
        }
        catch(AuthenticationException e)
        {
            log.warn("unknown user " + principalName);
            return null;
        }
    }

    @Override
    public String ssoBaseUrl(String domain)
        throws XmlRpcException
    {
        return singleSignOnService.ssoBaseUrl(domain);
    }
}
