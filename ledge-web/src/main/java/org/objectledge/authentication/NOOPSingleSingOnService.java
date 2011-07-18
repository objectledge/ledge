package org.objectledge.authentication;

import java.security.Principal;

public class NOOPSingleSingOnService
    implements SingleSignOnService
{
    @Override
    public Principal validateTicket(String ticket, String domain, String client)
    {
        return null;
    }

    @Override
    public String generateTicket(Principal principal, String domain, String client)
    {
        return null;
    }

    @Override
    public void logIn(Principal principal, String domain)
    {
        // does nothing
    }

    @Override
    public void logOut(Principal principal, String domain)
    {
        // does nothing
    }

    @Override
    public LogInStatus checkStatus(Principal principal, String domain)
    {
        return LogInStatus.UNKNOWN;
    }
}
