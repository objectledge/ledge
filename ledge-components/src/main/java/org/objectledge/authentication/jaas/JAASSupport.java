package org.objectledge.authentication.jaas;

import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

public class JAASSupport
{
    private final Configuration config;

    public JAASSupport(LoginConfigurationParameters params)
        throws NoSuchAlgorithmException
    {
        config = Configuration.getInstance("Ledge", params);
    }

    public void processValve(final Valve valve, final Context context)
        throws ProcessingException
    {

        Subject subject;
        try
        {
            LoginContext lctx = new LoginContext("Ledge", null, null, config);
            lctx.login();
            subject = lctx.getSubject();
        }
        catch(LoginException e)
        {
            throw new ProcessingException("failed to restore subject from session", e);
        }
        try
        {
            Subject.doAs(subject, new PrivilegedExceptionAction<Object>()
                {
                    public Object run()
                        throws ProcessingException
                    {
                        valve.process(context);
                        return null;
                    }
                });
        }
        catch(PrivilegedActionException e)
        {
            throw (ProcessingException)e.getCause();
        }
    }

    public void login(String login, String pass)
        throws LoginException
    {
        Subject subject = new Subject();
        Credentials cred = new Credentials(login, pass);
        subject.getPrivateCredentials().add(cred);
        LoginContext lCtx = new LoginContext("Ledge", subject, null, config);
        lCtx.login();
    }

    public void logout() throws LoginException
    {
        LoginContext lctx = new LoginContext("Ledge", null, null, config);
        lctx.logout();
    }
}
