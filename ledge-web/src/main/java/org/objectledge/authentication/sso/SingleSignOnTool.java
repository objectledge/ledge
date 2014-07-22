package org.objectledge.authentication.sso;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;

public class SingleSignOnTool
{
    private final Context context;

    private final SingleSignOnService singleSignOnService;

    private final Logger log;

    public SingleSignOnTool(Context context, SingleSignOnService singleSignOnService, Logger log)
    {
        this.context = context;
        this.singleSignOnService = singleSignOnService;
        this.log = log;
    }

    public boolean isActive()
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        String domain = httpContext.getRequest().getServerName();
        return singleSignOnService.ssoBaseUrl(domain) != null;
    }

    public boolean isLoggedIn()
    {
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
        return authContext.isUserAuthenticated();
    }

    public String getBaseUrl()
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        String domain = httpContext.getRequest().getServerName();
        return singleSignOnService.ssoBaseUrl(domain);
    }

    public String getToken()
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
        HttpServletRequest httpRequest = httpContext.getRequest();
        String client = httpRequest.getRemoteAddr();
        String domain = httpRequest.getServerName();
        String master = singleSignOnService.realmMaster(domain);
        Principal principal = authContext.getUserPrincipal();

        if(authContext.isUserAuthenticated())
        {
            if(master != null)
            {
                if(singleSignOnService.checkStatus(principal, domain) == SingleSignOnService.LoginStatus.LOGGED_IN)
                {
                    if(httpRequest.isSecure())
                    {
                        return singleSignOnService.generateTicket(principal, master, client);
                    }
                    else
                    {
                        log.warn("DECLINED " + client + ", " + principal.getName()
                            + " not using secure channel");
                    }
                }
                else
                {
                    log.warn("DECLINED " + client + " principal recenly logged out");
                }
            }
            else
            {
                log.warn("DECLINED " + domain + " does not belong to any realm");
            }
        }
        else
        {
            log.warn("DECLINED " + client + " session not authenticated");
        }
        return null;
    }
}
