package org.objectledge.modules.views.sso;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.SingleSignOnService;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;

/**
 * Retrieve one-time authentication ticket from SingleSignOn service.
 * <p>
 * This view is expected to be accessed from JavaScript embedded in a page loaded from different
 * domain. The returned string needs to be stored in HTTP cookie that will be picked up and
 * validated on a subsequent request by {@link org.objectledge.authentication.SingleSignOnValve}.
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
 * 
 * @author rkrzewski <rafal.krzewski@objectledge.org>
 */
public class Ticket
    extends AbstractBuilder
{
    private final SingleSignOnService singleSignOnService;

    public Ticket(Context context, SingleSignOnService singleSignOnService)
    {
        super(context);
        this.singleSignOnService = singleSignOnService;
    }

    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        httpContext.setContentType("text/plain");
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
        if(authContext.isUserAuthenticated() && httpContext.getRequest().isSecure())
        {
            String domain = httpContext.getRequest().getServerName();
            String ticket = singleSignOnService.logIn(authContext.getUserPrincipal(), domain);
            return SingleSignOnService.SSO_TICKET_COOKIE + "=" + ticket;
        }
        else
        {
            return SingleSignOnService.SSO_TICKET_COOKIE + "=NONE";
        }
    }

    @Override
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }
}
