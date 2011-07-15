package org.objectledge.authentication;

import java.security.Principal;

import javax.servlet.http.Cookie;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;

/**
 * Authenticates the user on the basis of SSO ticket.
 * <p>
 * The ticket is read from cookie {@link SingleSignOnService#SSO_TICKET_COOKIE}. If the ticket is
 * valid for the domain from where the ticket has originated, the user will be authenticated.
 * </p>
 * <p>
 * This valve is expected to run AFTER {@link AuthenticationValve}.
 * </p>
 * 
 * @author rkrzewski <rafal.krzewski@objectledge.org>
 */
public class SingleSignOnValve
    implements Valve
{
    private SingleSignOnService singleSignOnService;

    public SingleSignOnValve(SingleSignOnService singleSignOnService)
    {
        this.singleSignOnService = singleSignOnService;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
        if(!authContext.isUserAuthenticated())
        {
            HttpContext httpContext = context.getAttribute(HttpContext.class);
            Cookie[] cookies = httpContext.getRequest().getCookies();
            for(Cookie cookie : cookies)
            {
                if(cookie.getName().equals(SingleSignOnService.SSO_TICKET_COOKIE))
                {
                    String ticket = cookie.getValue();
                    String domain = httpContext.getRequest().getServerName();
                    // check if the authentication cookie is valid
                    Principal principal = singleSignOnService.logIn(ticket, domain);
                    if(principal != null)
                    {
                        AuthenticationContext authenticationContext = new AuthenticationContext();
                        authenticationContext.setUserPrincipal(principal, true);
                        context.setAttribute(AuthenticationContext.class, authenticationContext);

                        httpContext.getRequest().getSession()
                            .setAttribute(WebConstants.PRINCIPAL_SESSION_KEY, principal);

                        // delete ticket cookie
                        Cookie newCookie = new Cookie(SingleSignOnService.SSO_TICKET_COOKIE, "");
                        newCookie.setMaxAge(0);
                        httpContext.getResponse().addCookie(newCookie);

                        // let JS side know user is authenticated
                        newCookie = new Cookie(SingleSignOnService.SSO_AUTH_COOKIE, "true");
                        newCookie.setMaxAge(-1);
                        httpContext.getResponse().addCookie(newCookie);
                    }
                    break;
                }
            }
        }
    }
}
