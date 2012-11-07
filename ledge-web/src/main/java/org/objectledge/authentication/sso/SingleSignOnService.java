package org.objectledge.authentication.sso;

import java.security.Principal;

public interface SingleSignOnService
{
    public static final String SSO_TICKET_COOKIE = "org.objectledge.web.sso.ticket";

    String generateTicket(Principal principal, String domain, String client);

    Principal validateTicket(String ticket, String domain, String client);

    void logIn(Principal principal, String domain);

    void logOut(Principal principal, String domain);

    LoginStatus checkStatus(Principal principal, String domain);

    String ssoBaseUrl(String domain);

    boolean validateApiRequest(String userName, String secret, String remoteAddr, boolean secure);

    public enum LoginStatus
    {
        // @formatter:off
        LOGGED_IN, 
        LOGGED_OUT, 
        UNKNOWN, 
        ERROR
        // @formatter:on
    }
}
