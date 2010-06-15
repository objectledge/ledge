package org.objectledge.authentication.jaas;

import java.security.Provider;
import java.security.Security;

public class LoginServiceProvider
    extends Provider
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LoginServiceProvider()
    {
        super("ObjectLedge Security provider", 1.0d, "");
        put("Configuration.Ledge", "org.objectledge.authentication.jaas.LoginConfiguration");
        Security.addProvider(this);
    }
}
