package org.objectledge.authentication.jaas;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.authentication.UserManager;

public class UserManagerLoginModule
    implements LoginModule
{
    public static final String VERIFIED_PRINCIPAL_KEY = "VERIFIED_PRINCIPAL";

    private Subject subject;

    private Principal verifiedPrincipal;

    private Map<String, Object> sharedState;

    private UserManager userManager;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
        Map<String, ? > sharedState, Map<String, ? > options)
    {
        this.subject = subject;
        userManager = (UserManager)options.get(UserManager.class.getName());
        if(userManager == null)
        {
            throw new RuntimeException("Unable to access UserManager");
        }
        this.sharedState = (Map<String, Object>)sharedState;
    }

    @Override
    public boolean login()
        throws LoginException
    {
        List<Credentials> credentials = new ArrayList<Credentials>(subject
            .getPrivateCredentials(Credentials.class));
        switch(credentials.size())
        {
        case 1:
            try
            {
                verifiedPrincipal = userManager.getUserByLogin(credentials.get(0).getLogin());
                if(verifiedPrincipal == null)
                {
                    throw new AccountNotFoundException();
                }
                if(userManager.checkUserPassword(verifiedPrincipal, credentials.get(0)
                    .getPassword()))
                {
                    sharedState.put(VERIFIED_PRINCIPAL_KEY, verifiedPrincipal);
                    return true;
                }
                else
                {
                    throw new FailedLoginException();
                }
            }
            catch(AuthenticationException e)
            {
                LoginException ee = new LoginException();
                ee.initCause(e);
                throw ee;
            }
        case 0:
            return false;
        default:
            throw new CredentialException("too many credentials provided");

        }
    }

    @Override
    public boolean commit()
        throws LoginException
    {
        replacePrincipal(subject, verifiedPrincipal);
        return true;
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
        replacePrincipal(subject, null);
        return false;
    }

    public static Principal getPrincipal(Subject subject)
        throws LoginException
    {
        ArrayList<Principal> principals = new ArrayList<Principal>(subject
            .getPrincipals(DefaultPrincipal.class));
        switch(principals.size())
        {
        case 0:
            return null;
        case 1:
            return principals.get(0);
        default:
            throw new LoginException("too many principals found");
        }
    }

    public static Principal replacePrincipal(Subject subject, Principal principal)
        throws LoginException
    {
        Principal old = getPrincipal(subject);
        if(old != null)
        {
            subject.getPrincipals().remove(old);
        }
        if(principal != null)
        {
            subject.getPrincipals().add(principal);
        }
        return old;
    }
}
