package org.objectledge.authentication.sso;

import java.security.Principal;

public interface SingleSignOnService
{
    public static final String SSO_TICKET_COOKIE = "org.objectledge.web.sso.ticket";
    
    String generateTicket(Principal principal, String domain, String client);
    
    Principal validateTicket(String ticket, String domain, String client);
    
    void logIn(Principal principal, String domain);
    
    void logOut(Principal principal, String domain);
    
    LogInStatus checkStatus(Principal principal, String domain);
    
    boolean ssoAvailable(String domain);
    
    String ssoBaseUrl(String domain);
    
    public enum LogInStatus
    {
        LOGGED_IN,
        LOGGED_OUT,
        UNKNOWN
    }
}
