package org.objectledge.authentication;

import java.security.Principal;

public interface SingleSignOnService
{
    public static final String SSO_TICKET_COOKIE = "org.objectledge.web.sso.ticket";
    
    public static final String SSO_AUTH_COOKIE = "org.objectledge.web.sso.auth";
    
    Principal logIn(String ticket, String domain);
    
    String logIn(Principal principal, String domain);
    
    void logOut(Principal principal, String domain);
}
