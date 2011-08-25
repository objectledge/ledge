package org.objectledge.authentication.sso;

import java.security.Principal;

import javax.servlet.http.Cookie;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.AuthenticationValve;
import org.objectledge.authentication.UserManager;
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
 * The valve will automatically log out the currently logged in user if the most recent login/logout
 * action in any domain belonging to the same SSO realm performed by this user was a logout.
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
    private final SingleSignOnService singleSignOnService;

    private final UserManager userManager;

    private final Logger log;

    public SingleSignOnValve(SingleSignOnService singleSignOnService, UserManager userManager,
        Logger log)
    {
        this.singleSignOnService = singleSignOnService;
        this.userManager = userManager;
        this.log = log;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        try
        {
            AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
            HttpContext httpContext = context.getAttribute(HttpContext.class);
            if(!authContext.isUserAuthenticated())
            {
                Cookie[] cookies = httpContext.getRequest().getCookies();
                if(cookies != null)
                {                    
                    for(Cookie cookie : cookies)
                    {
                        if(cookie.getName().equals(SingleSignOnService.SSO_TICKET_COOKIE))
                        {
                            log.info("STARTING SSO login attempt");
                            String ticket = cookie.getValue();
                            String domain = httpContext.getRequest().getServerName();
                            String client = httpContext.getRequest().getRemoteAddr();
                            // check if the authentication cookie is valid
                            Principal principal = singleSignOnService.validateTicket(ticket, domain,
                                client);
                            if(principal != null)
                            {
                                AuthenticationContext authenticationContext = context
                                    .getAttribute(AuthenticationContext.class);
                                authenticationContext.setUserPrincipal(principal, true);
                                
                                httpContext.setSessionAttribute(WebConstants.PRINCIPAL_SESSION_KEY, principal);
                                singleSignOnService.logIn(principal, domain);
                                
                                // delete ticket cookie
                                Cookie newCookie = new Cookie(SingleSignOnService.SSO_TICKET_COOKIE, "");
                                newCookie.setMaxAge(0);
                                httpContext.getResponse().addCookie(newCookie);
                                
                                log.info("LOGGED IN user " + principal.getName() + " to " + domain);
                            }
                            else
                            {
                                log.info("DECLINED SSO login attempt");
                            }
                            break;
                        }
                    }
                }
            }
            else
            {
                // check if the user has not logged out through other domain in the same realm
                Principal principal = authContext.getUserPrincipal();
                String domain = httpContext.getRequest().getServerName();
                SingleSignOnService.LoginStatus status = singleSignOnService.checkStatus(principal,
                    domain);
                if(status == SingleSignOnService.LoginStatus.LOGGED_OUT)
                {
                    httpContext.clearSessionAttributes();
                    Principal anonymous = userManager.getAnonymousAccount();
                    AuthenticationContext authenticationContext = AuthenticationContext
                        .getAuthenticationContext(context);
                    authenticationContext.setUserPrincipal(anonymous, false);
                    log.info("LOGGED OUT user " + principal.getName() + " from " + domain);
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }
}
