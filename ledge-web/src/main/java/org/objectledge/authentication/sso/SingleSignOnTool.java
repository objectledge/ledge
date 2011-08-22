package org.objectledge.authentication.sso;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;

public class SingleSignOnTool
{
    private final Context context;
    
    private final SingleSignOnService singleSignOnService;

    public SingleSignOnTool(Context context, SingleSignOnService singleSignOnService)
    {
        this.context = context;
        this.singleSignOnService = singleSignOnService;
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
}
