// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
package org.objectledge.authentication;

import java.security.Principal;
import java.util.Collection;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import org.objectledge.parameters.Parameters;

/**
 * A base implementation of the UserManager interface.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UserManager.java,v 1.5 2006-04-24 09:50:50 rafal Exp $
 */
public abstract class AbstractUserManager implements UserManager
{
    // instance variables ///////////////////////////////////////////////////////////////////////

    /** the naming policy to be used. */
    protected NamingPolicy namingPolicy;

    /** the login verifier to be used. */
    protected LoginVerifier loginVerifier;

    /** the password digester to be used. */
    protected PasswordDigester passwordDigester;

    /** the password generator to be used. */
    protected PasswordGenerator passwordGenerator;

    // initialization ///////////////////////////////////////////////////////////////////////////

    /**
     * No-arg ctor for mock object testing.
     */
    protected AbstractUserManager()
    {
    }

    /**
     * Creates an instance of the user manager.
     * 
     * @param namingPolicy the namig policy to be used.
     * @param loginVerifier the login verifier.
     * @param passwordGenerator the password generator.
     * @param passwordDigester the password digester.
     */
    public AbstractUserManager(NamingPolicy namingPolicy, LoginVerifier loginVerifier,
        PasswordGenerator passwordGenerator, PasswordDigester passwordDigester)
    {
        this.namingPolicy = namingPolicy;
        this.loginVerifier = loginVerifier;
        this.passwordGenerator = passwordGenerator;
        this.passwordDigester = passwordDigester;
    }

    // account creation + removal ///////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#checkLogin(java.lang.String)
     */
    @Override
    public boolean checkLogin(String login)
    {
        return loginVerifier.checkLogin(login);
    }

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#validateLogin(java.lang.String)
     */
    @Override
    public boolean validateLogin(String login)
    {
        return loginVerifier.validate(login);
    }

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#createDN(org.objectledge.parameters.Parameters)
     */
    @Override
    public String createDN(Parameters parameters)
    {
        return namingPolicy.getDn(parameters);
    }

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#userExists(java.lang.String)
     */
    @Override
    public abstract boolean userExists(String dn);

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#emailExists(java.lang.String)
     */
    @Override
    public abstract boolean emailExists(String email);

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#createAccount(java.lang.String, java.lang.String)
     */
    @Override
    public abstract Principal createAccount(String login, String password)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#createAccount(java.lang.String, java.lang.String, boolean, javax.naming.directory.Attributes)
     */
    @Override
    public abstract Principal createAccount(String login, String password, boolean blockPassword,
        Attributes attributes)
        throws AuthenticationException;
    
    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#accountBlocked(java.lang.String)
     */
    @Override
    public abstract boolean accountBlocked(String login) throws AuthenticationException;
    
    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#removeAccount(java.security.Principal)
     */
    @Override
    public abstract void removeAccount(Principal account)
        throws AuthenticationException;

    // user lookups /////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getUserByName(java.lang.String)
     */
    @Override
    public abstract Principal getUserByName(String dn)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getUserByLogin(java.lang.String)
     */
    @Override
    public abstract Principal getUserByLogin(String login)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getUserByMail(java.lang.String)
     */
    @Override
    public abstract Principal getUserByMail(String mail)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getLogin(java.lang.String)
     */
    @Override
    public String getLogin(String dn)
        throws AuthenticationException, InvalidNameException
    {
        return namingPolicy.getLogin(dn);
    }

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getLogin(java.security.Principal)
     */
    @Override
    public String getLogin(Principal account)
        throws AuthenticationException, InvalidNameException
    {
        return namingPolicy.getLogin(account.getName());
    }

    // system users /////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getAnonymousAccount()
     */
    @Override
    public abstract Principal getAnonymousAccount()
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getSuperuserAccount()
     */
    @Override
    public abstract Principal getSuperuserAccount()
        throws AuthenticationException;

    // passwords ////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#changeUserPassword(java.security.Principal, java.lang.String)
     */
    @Override
    public abstract void changeUserPassword(Principal account, String password)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#changeUserAttribiutes(java.security.Principal, javax.naming.directory.Attributes)
     */
    @Override
    public abstract void changeUserAttribiutes(Principal account, Attributes attribiutes)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#checkUserPassword(java.security.Principal, java.lang.String)
     */
    @Override
    public abstract boolean checkUserPassword(Principal account, String password)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#enableUserPassword(java.security.Principal)
     */
    @Override
    public abstract void enableUserPassword(Principal account)
        throws AuthenticationException;
    
    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#createRandomPassword(int, int)
     */
    @Override
    public String createRandomPassword(int min, int max)
    {
        return passwordGenerator.createRandomPassword(min, max);
    }

    // personal data ////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getPersonalData(java.security.Principal)
     */
    @Override
    public abstract DirContext getPersonalData(Principal account)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#lookupAccounts(java.lang.String, java.lang.String)
     */
    @Override
    public abstract Collection<Principal> lookupAccounts(String attribute, String value)
        throws NamingException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#lookupAccounts(java.lang.String)
     */
    @Override
    public abstract Collection<Principal> lookupAccounts(String query)
        throws NamingException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#lookupAccounts(java.lang.String, javax.naming.directory.SearchControls)
     */
    @Override
    public abstract Collection<Principal> lookupAccounts(String query, SearchControls searchControlls) 
                    throws NamingException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#getUserAttribute(java.security.Principal, java.lang.String)
     */
    @Override
    public abstract String getUserAttribute(Principal account, String attribute)
        throws AuthenticationException;

    /* (non-Javadoc)
     * @see org.objectledge.authentication.UserManager#updateTrackingInformation(java.security.Principal)
     */
    @Override
    public abstract void updateTrackingInformation(Principal account)
        throws AuthenticationException, NamingException;
}
