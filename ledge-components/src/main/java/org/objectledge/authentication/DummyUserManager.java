// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.authentication;

import java.security.Principal;
import java.util.Collection;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

/**
 * The dummy user manager implementation.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class DummyUserManager
    extends AbstractUserManager
{
    /** A string that may be used as {@code dn} or {@code login} to simulate missing user account. */
    public static final String MISSSING_USER = "MISSING";

    /**
     * Creates an instance of the user manager.
     */
    public DummyUserManager()
    {
    }

    /**
     * {@inheritDoc}
     */
    public boolean userExists(String dn)
    {
        return !dn.equals(MISSSING_USER);
    }

    /**
     * {@inheritDoc}
     */
    public Principal createAccount(String login, String password)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot create new account");
    }

    /**
     * {@inheritDoc}
     */
    public void removeAccount(Principal account)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot create new account");
    }

    /**
     * {@inheritDoc}
     */
    public Principal getUserByName(String dn)
        throws AuthenticationException
    {
        if(dn.equals(MISSSING_USER))
        {
            throw new AuthenticationException("Unknown user");
        }
        return new DefaultPrincipal(dn);
    }

    /**
     * {@inheritDoc}
     */
    public Principal getUserByLogin(String login)
        throws AuthenticationException
    {
        if(login.equals(MISSSING_USER))
        {
            throw new AuthenticationException("Unknown user");
        }
        return new DefaultPrincipal(login);
    }

    /**
     * {@inheritDoc}
     */
    public Principal getAnonymousAccount()
        throws AuthenticationException
    {
        return new DefaultPrincipal("anonymous");
    }

    /**
     * {@inheritDoc}
     */
    public Principal getSuperuserAccount()
        throws AuthenticationException
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void changeUserPassword(Principal account, String password)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot change user password");
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkUserPassword(Principal account, String password)
        throws AuthenticationException
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getPersonalData(Principal account)
        throws AuthenticationException
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Principal> lookupAccounts(String attribute, String value)
        throws NamingException
    {
        throw new UnsupportedOperationException("Dummy manager cannot lookup accounts");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Principal> lookupAccounts(String query, SearchControls searchControlls)
        throws NamingException
    {
        throw new UnsupportedOperationException("Dummy manager cannot lookup accounts");
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Principal> lookupAccounts(String query)
        throws NamingException
    {
        throw new UnsupportedOperationException("Dummy manager cannot lookup accounts");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean emailExists(String email)
    {
        return false;
    }

    @Override
    public Principal createAccount(String login, String password, boolean blockPassword,
        Attributes attributes)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot create new account");
    }

    @Override
    public Principal getUserByMail(String mail)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot lookup user by mail");
    }

    @Override
    public void changeUserAttribiutes(Principal account, Attributes attribiutes)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot change user attributes");
    }

    @Override
    public void updateTrackingInformation(Principal account)
    {
        throw new UnsupportedOperationException(
            "Dummy manager cannot perform update tracking information operation");
    }

    @Override
    public String getUserAttribute(Principal account, String attribute)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot get user attribute");
    }

    @Override
    public void enableUserPassword(Principal account)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot enable user password");
    }

    @Override
    public boolean accountBlocked(String login)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Dummy manager cannot check if account is disabled");
    }

    @Override
    public BlockedReason checkAccountFlag(Principal account)
    {
        throw new UnsupportedOperationException("Failed to check account shadowFlag");
    }

    @Override
    public long getUserPasswordExpirationDays(Principal account)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Failed to get password expiration days");
    }

    @Override
    public boolean isUserPasswordExpired(Principal account)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Failed to check if password is expired");
    }

    @Override
    public void setUserShadowFlag(Principal user, String code)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Failed to set account shadowFlag");
    }

    @Override
    public boolean isUserAccountExpired(Principal account)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Failed to check if account is expired");
    }

    @Override
    public Collection<Principal> getLoginsForGivenEmail(String email)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Failed to get logins for given email");
    }

    @Override
    public boolean isEmailDuplicated(String email)
        throws AuthenticationException
    {
        throw new UnsupportedOperationException("Failed to check if account email is duplicated");
    }
}
