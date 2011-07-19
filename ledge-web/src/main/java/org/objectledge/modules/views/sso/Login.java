package org.objectledge.modules.views.sso;

import java.security.Principal;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.SingleSignOnService;
import org.objectledge.authentication.UserManager;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;

public class Login
    extends AbstractBuilder
{
    /** login parameter name. */
    public static final String LOGIN_PARAM = "login";

    /** password parameter name. */
    public static final String PASSWORD_PARAM = "password";

    private final SingleSignOnService singleSignOnService;

    private final UserManager userManager;

    private final Logger log;

    public Login(UserManager userManager, SingleSignOnService singleSignOnService, Context context,
        Logger log)
    {
        super(context);
        this.userManager = userManager;
        this.singleSignOnService = singleSignOnService;
        this.log = log;
    }

    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        Parameters parameters = context.getAttribute(RequestParameters.class);
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        String client = httpContext.getRequest().getRemoteAddr();
        String domain = httpContext.getRequest().getServerName();
        String login = parameters.get(LOGIN_PARAM, null);
        String password = parameters.get(PASSWORD_PARAM, null);

        String status = "success";
        String ticket = null;

        if(!httpContext.getRequest().isSecure())
        {
            Principal principal = null;
            try
            {
                principal = userManager.getUserByLogin(login);
                if(!userManager.checkUserPassword(principal, password))
                {
                    log.warn("DECLINED " + client + " login " + login + " invalid password");
                    status = "invalid_credentials";
                }
                ticket = singleSignOnService.generateTicket(principal, domain, client);
                if(ticket != null)
                {
                    log.debug("ACCEPTED " + client + " login ");
                    // we don't call SingleSingOnService.logIn() here, since the login has been performed
                    // to the realm master which does not belong the realm. Login will be recored when
                    // the one time ticket will be validated by SingleSingOnValve in the actual domain
                    // the user is trying to access.
                }
                else
                {
                    // domain is not a realm master
                    status = "invalid_request";
                }
            }
            catch(UserUnknownException e)
            {
                log.warn("DECLINED " + client + " unknown user login " + login);
                status = "invalid_credentials";
            }
            catch(AuthenticationException e)
            {
                log.error("DECLINED " + client + " login " + login + " AuthenticationException ", e);
                status = "internal_error";
            }
        }
        else
        {
            log.warn("DECLINED " + client + " login " + login + " not using secure channel");
            status = "invalid_request";
        }

        httpContext.setContentType("application/json");
        return formatReply(status, ticket);
    }

    private String formatReply(String status, String ticket)
    {
        return "{ status : \"" + status + "\",\n ticket : \"" + ticket != null ? ticket : "NONE"
            + "\" }";
    }

    @Override
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }
}
