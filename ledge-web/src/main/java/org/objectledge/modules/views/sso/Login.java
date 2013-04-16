package org.objectledge.modules.views.sso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.BlockedReason;
import org.objectledge.authentication.UserManager;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.authentication.sso.SingleSignOnService;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;
import org.objectledge.web.json.AbstractJsonView;

import com.fasterxml.jackson.core.JsonGenerationException;

public class Login
    extends AbstractJsonView
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
        super(context, log);
        this.userManager = userManager;
        this.singleSignOnService = singleSignOnService;
        this.log = log;
    }

    @Override
    public void buildJsonStream()
        throws JsonGenerationException, IOException
    {
        Parameters parameters = getRequestParameters();
        HttpContext httpContext = getHttpContext();
        HttpServletRequest httpRequest = httpContext.getRequest();

        String client = httpRequest.getRemoteAddr();
        String domain = httpRequest.getServerName();
        String login = parameters.get(LOGIN_PARAM, null);
        String password = parameters.get(PASSWORD_PARAM, null);

        String status = "success";
        String ticket = null;
        HttpSession session = httpRequest.getSession(false);
        String sessionId = session != null ? session.getId() : "N/A";
        log.debug("request from " + client + " sessionId " + sessionId);
        if(httpRequest.isSecure())
        {
            if(login != null && password != null)
            {
                Principal principal = null;
                try
                {
                    principal = userManager.getUserByLogin(login);
                    if(userManager.checkUserPassword(principal, password))
                    {
                        
                        boolean passwordExpired =  userManager.isUserPasswordExpired(principal) ;
                        boolean accountExpired = userManager.isUserAccountExpired(principal);
                        if(passwordExpired && accountExpired)
                        {
                            log.info("Account password, and Account expiration - OK");
                        }
                        BlockedReason reason = userManager.checkAccountFlag(principal);
                        if(!reason.equals(BlockedReason.OK))
                        {
                           log.warn("DECLINED " + client + "login " + login + "reason " + reason.getReason());
                           status = reason.getShortReason();
                        }
                        else{
                            ticket = singleSignOnService.generateTicket(principal, domain, client);
                            if(ticket != null)
                            {
                                log.debug("ACCEPTED " + client + " login ");
                                // we don't call SingleSingOnService.logIn() here, since the login has
                                // been performed to the realm master which does not belong the realm.
                                // Login will be recored when the one time ticket will be validated by
                                // SingleSingOnValve in the actual domain the user is trying to access.

                                // make the session between client and realm master an authenticated
                                // one, so that subsequent ticket requests from the same clients can
                                // succeed
                                httpContext.setSessionAttribute(WebConstants.PRINCIPAL_SESSION_KEY,
                                    principal);
                            }
                            else
                            {
                                // domain is not a realm master, warning was logged by
                                // SingleSignOnService
                                status = "invalid_request";
                            }
                        }                     
                    }
                    else
                    {
                        log.warn("DECLINED " + client + " login " + login + " invalid password");
                        status = "invalid_credentials";
                    }
                }
                catch(UserUnknownException e)
                {
                    log.warn("DECLINED " + client + " unknown user login " + login);
                    status = "invalid_credentials";
                }
                catch(AuthenticationException e)
                {
                    log.error("DECLINED " + client + " login " + login
                        + " AuthenticationException ", e);
                    status = "internal_error";
                }
            }
            else
            {
                log.warn("DECLINED " + client + " missing parameters");
                status = "invalid_request";
            }
        }
        else
        {
            log.warn("DECLINED " + client + " login " + login + " not using secure channel");
            status = "invalid_request";
        }
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("status", status);
        jsonGenerator.writeStringField("ticket", ticket);
        jsonGenerator.writeEndObject();
    }

    @Override
    protected String getCallbackParameterName()
    {
        return "callback";
    }

    protected String refererDomain(HttpServletRequest request)
    {
        String referer = request.getHeader("Referer");
        if(referer == null)
        {
            String serverName = request.getServerName();
            log.warn("No Referer header received from " + request.getRemoteAddr() + " assuming "
                + serverName);
            return serverName;
        }
        try
        {
            URL refererUrl = new URL(referer);
            return refererUrl.getHost();
        }
        catch(MalformedURLException e)
        {
            log.error("Malformed Referer header received from " + request);
            return null;
        }
    }
}
