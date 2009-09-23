package org.objectledge.authentication.jaas;

import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;

public class HttpSessionLoginModule
    implements LoginModule
{
    private Subject subject;

    private Principal verifiedPrincipal;

    private Principal sessionPrincipal;

    private HttpContext httpContext;
    
    private Map<String, Object> sharedState;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Subject subject, CallbackHandler callbackHandler,
        Map<String, ? > sharedState, Map<String, ? > options)
    {
        this.subject = subject;
        this.sharedState = (Map<String, Object>)sharedState;
        this.httpContext = (HttpContext)options.get(HttpContext.class.getName());
    }

    @Override
    public boolean login()
        throws LoginException
    {
        verifiedPrincipal = (Principal)sharedState
            .get(UserManagerLoginModule.VERIFIED_PRINCIPAL_KEY);
        sessionPrincipal = (Principal)httpContext
            .getSessionAttribute(WebConstants.PRINCIPAL_SESSION_KEY);
        return verifiedPrincipal != null || sessionPrincipal != null;
    }

    @Override
    public boolean commit()
        throws LoginException
    {
        if(verifiedPrincipal != null)
        {
            httpContext.setSessionAttribute(WebConstants.PRINCIPAL_SESSION_KEY, verifiedPrincipal);
            return true;
        }
        else if(sessionPrincipal != null)
        {
            UserManagerLoginModule.replacePrincipal(subject, sessionPrincipal);
            return true;
        }
        return false;
    }

    @Override
    public boolean abort()
        throws LoginException
    {
        return true;
    }

    @Override
    public boolean logout()
        throws LoginException
    {
        httpContext.setSessionAttribute(WebConstants.PRINCIPAL_SESSION_KEY, null);
        return true;
    }
}
