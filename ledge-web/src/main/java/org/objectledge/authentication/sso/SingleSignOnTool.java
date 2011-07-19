package org.objectledge.authentication.sso;

import org.objectledge.web.HttpContext;

public class SingleSignOnTool
{
    private final HttpContext httpContext;

    private final SingleSignOnService singleSignOnService;

    public SingleSignOnTool(HttpContext httpContext, SingleSignOnService singleSignOnService)
    {
        this.httpContext = httpContext;
        this.singleSignOnService = singleSignOnService;
    }

    public boolean ssoAvailable()
    {
        String domain = httpContext.getRequest().getServerName();
        return singleSignOnService.ssoAvailable(domain);
    }
    
    public String loginUrl()
    {
        String domain = httpContext.getRequest().getServerName();
        String baseUrl = singleSignOnService.ssoBaseUrl(domain);
        return baseUrl != null ? baseUrl + "/view/sso.Login" : null;
    }

    public String ticketUrl()
    {
        String domain = httpContext.getRequest().getServerName();
        String baseUrl = singleSignOnService.ssoBaseUrl(domain);
        return baseUrl != null ? baseUrl + "/view/sso.Ticket" : null;
    }
}
