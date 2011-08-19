package org.objectledge.modules.views.sso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.sso.SingleSignOnService;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;
import org.objectledge.web.json.AbstractJsonView;

/**
 * Retrieve one-time authentication ticket from SingleSignOn service.
 * <p>
 * This view is expected to be accessed from JavaScript embedded in a page loaded from different
 * domain. The returned string needs to be stored in HTTP cookie that will be picked up and
 * validated on a subsequent request by {@link org.objectledge.authentication.sso.SingleSignOnValve}
 * .
 * </p>
 * <p>
 * In order for the ticket to be granted, user needs to authenticate using Login action first.
 * </p>
 * <p>
 * Additionally, the server from which the request for this view originated must be part of an
 * active SSO realm.
 * </p>
 * <p>
 * The generated ticket may be used only once by SingleSignOnValve. It is silently discarded after
 * first use.
 * </p>
 * <p>
 * This view may be only accessed over HTTPS, and only on the server that is designated as the
 * master of the realm. In other circumstances an attacker could authenticated with any server in
 * the SSO domain after hijacking an authenticated session to one of them.
 * </p>
 * <p>
 * The view uses content type {@code application/json} and returns JSON encoded object with single
 * field named {@code ticket}. The returned value is either ticket identifier, or {@code "NONE"} in
 * case of any errors.
 * </p>
 * <p>
 * As a security precaution, no errors are reported to client side. You need to enable logging on
 * server side to see the reasons of failed requests.
 * </p>
 * 
 * @author rkrzewski <rafal.krzewski@objectledge.org>
 */
public class Ticket
    extends AbstractJsonView
{
    private final SingleSignOnService singleSignOnService;

    private final Logger log;

    public Ticket(Context context, SingleSignOnService singleSignOnService, Logger log)
    {
        super(context, log);
        this.singleSignOnService = singleSignOnService;
        this.log = log;
    }

    @Override
    public void buildJsonStream()
        throws JsonGenerationException, IOException
    {
        HttpContext httpContext = getHttpContext();

        HttpServletRequest httpRequest = httpContext.getRequest();
        String client = httpRequest.getRemoteAddr();
        String domain = httpRequest.getServerName();
        String targetDomain = refererDomain(httpRequest);
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);

        String ticket = null;
        String status = "success";

        log.debug("request from " + client + " sessionId " + httpRequest.getSession().getId());
        if(authContext.isUserAuthenticated())
        {
            if(targetDomain != null)
            {
                Principal principal = authContext.getUserPrincipal();
                if(singleSignOnService.checkStatus(principal, targetDomain) != SingleSignOnService.LoginStatus.LOGGED_OUT)
                {
                    if(httpRequest.isSecure())
                    {
                        ticket = singleSignOnService.generateTicket(principal, domain, client);
                        if(ticket == null)
                        {
                            // domain is not a realm master, warning was logged by
                            // SingleSignOnService
                            status = "invalid_request";
                        }
                    }
                    else
                    {
                        status = "invalid_request";
                        log.warn("DECLINED " + client + ", " + principal.getName()
                            + " not using secure channel");
                    }
                }
                else
                {
                    status = "not_logged_on";
                    log.warn("DECLINED " + client + " principal recenly logged out");
                }
            }
            else
            {
                status = "invalid_request";
                log.warn("DECLINED " + client + " missing or malformed Referer header");
            }
        }
        else
        {
            status = "not_logged_on";
            log.warn("DECLINED " + client + " session not authenticated");
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

    private String refererDomain(HttpServletRequest request)
    {
        String referer = request.getHeader("Referer");
        if(referer == null)
        {
            return request.getServerName();
        }
        try
        {
            URL refererUrl = new URL(referer);
            return refererUrl.getHost();
        }
        catch(MalformedURLException e)
        {
            return null;
        }
    }
}
