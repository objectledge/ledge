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
public abstract class UserManager
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
    protected UserManager()
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
    public UserManager(NamingPolicy namingPolicy, LoginVerifier loginVerifier,
        PasswordGenerator passwordGenerator, PasswordDigester passwordDigester)
    {
        this.namingPolicy = namingPolicy;
        this.loginVerifier = loginVerifier;
        this.passwordGenerator = passwordGenerator;
        this.passwordDigester = passwordDigester;
    }

    // account creation + removal ///////////////////////////////////////////////////////////////

    /**
     * Checks if a login name is a non-occupied and non-reserved one.
     * 
     * @param login the login name to be checked.
     * @return <code>true</code> if a login name is a non-occupied and non-reserved.
     */
    public boolean checkLogin(String login)
    {
        return loginVerifier.checkLogin(login);
    }

    /**
     * Checks if a login name is acceptable.
     * 
     * @param login the login name to be checked.
     * @return <code>true</code> if a login name is correct.
     */
    public boolean validateLogin(String login)
    {
        return loginVerifier.validate(login);
    }

    /**
     * Creates a distinguished name from provided parameters in conformance to configured naming
     * policy.
     * 
     * @param parameters the parameters to generate name from.
     * @return the distinghished name.
     */
    public String createDN(Parameters parameters)
    {
        return namingPolicy.getDn(parameters);
    }

    /**
     * Check if user exists.
     * 
     * @param dn the name of the user.
     * @return <code>true</code> if user exists in system.
     */
    public abstract boolean userExists(String dn);

    /**
     * Check if email exists.
     * 
     * @param email the address to check for.
     * @return <code>true</code> if emails exists in system
     */
    public abstract boolean emailExists(String email);

    /**
     * Creates a new user account.
     * 
     * @param login login name of the user.
     * @param password initial password of the user.
     * @return the newly created account.
     * @throws AuthenticationException if the account could no be created.
     */
    public abstract Principal createAccount(String login, String password)
        throws AuthenticationException;

    /**
     * Creates a new user account with additional Attributes
     * 
     * @param login login name of the user.
     * @param password initial password of the user.
     * @param blockPassword the flag indicating if password should have addded ! mark after hashing
     *        which blocks it.
     * @param attributes the additional attributes
     * @return the newly created account.
     * @throws AuthenticationException if the account could no be created.
     */
    public abstract Principal createAccount(String login, String password, Boolean blockPassword,
        Attributes attributes)
        throws AuthenticationException;

    /**
     * Removes an user account.
     * 
     * @param account the account.
     * @throws AuthenticationException if the account could no be removed.
     */
    public abstract void removeAccount(Principal account)
        throws AuthenticationException;

    // user lookups /////////////////////////////////////////////////////////////////////////////

    /**
     * Lookup user by distinguised name.
     * 
     * @param dn the users's distinguished name.
     * @return the account's descriptor.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public abstract Principal getUserByName(String dn)
        throws AuthenticationException;

    /**
     * Lookup user by login name.
     * 
     * @param login the name used for authentication.
     * @return the account's descriptor.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public abstract Principal getUserByLogin(String login)
        throws AuthenticationException;

    /**
     * Lookup user by mail.
     * 
     * @param mail
     * @return
     * @throws AuthenticationException
     */
    public abstract Principal getUserByMail(String mail)
        throws AuthenticationException;

    /**
     * Maps user's distinguished name to login name.
     * 
     * @param dn full user name.
     * @return the login name, or <code>null</code> if not found.
     * @throws AuthenticationException if there is a problem performing the operation.
     * @throws InvalidNameException if the name does not conform to the configured naming policy.
     */
    public String getLogin(String dn)
        throws AuthenticationException, InvalidNameException
    {
        return namingPolicy.getLogin(dn);
    }

    /**
     * Returns the login name of an user.
     * 
     * @param account the account.
     * @return the login name, or <code>null</code> if not found.
     * @throws AuthenticationException if there is a problem performing the operation.
     * @throws InvalidNameException if the name does not conform to the configured naming policy.
     */
    public String getLogin(Principal account)
        throws AuthenticationException, InvalidNameException
    {
        return namingPolicy.getLogin(account.getName());
    }

    // system users /////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the anonymous account.
     * 
     * @return the anonyomous user.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public abstract Principal getAnonymousAccount()
        throws AuthenticationException;

    /**
     * Returns the superuser account.
     * 
     * @return the superuser.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public abstract Principal getSuperuserAccount()
        throws AuthenticationException;

    // passwords ////////////////////////////////////////////////////////////////////////////////

    /**
     * Changes user password.
     * 
     * @param account the account.
     * @param password the new password for the account.
     * @throws AuthenticationException if the password could not be changed.
     */
    public abstract void changeUserPassword(Principal account, String password)
        throws AuthenticationException;

    /**
     * Change user attribiutes
     * 
     * @param account to change
     * @param attributes to change
     * @throws AuthenticationException
     */

    public abstract void changeUserAttribiutes(Principal account, Attributes attribiutes)
        throws AuthenticationException;

    /**
     * Checks user supplied password.
     * 
     * @param account the account.
     * @param password the password to be checked.
     * @return <code>true</code> if the supplied password is correct.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public abstract boolean checkUserPassword(Principal account, String password)
        throws AuthenticationException;

    /**
     * Enables user's password.
     * 
     * Removes ! from the beginning of the user's password
     * 
     * @param account the account
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public abstract void enableUserPassword(Principal account)
        throws AuthenticationException;
    
    /**
     * Generates a random password.
     * 
     * @param min minimum length.
     * @param max maximum length.
     * @return a random passeword.
     */
    public String createRandomPassword(int min, int max)
    {
        return passwordGenerator.createRandomPassword(min, max);
    }

    // personal data ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the personal data of the accoun't owner.
     * 
     * @param account the account.
     * @return Parameters view of the account's owner personal data.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public abstract DirContext getPersonalData(Principal account)
        throws AuthenticationException;

    /**
     * Looks up user accounts according to personal data attributes.
     * 
     * @param attribute the personal data attribute name.
     * @param value the personal data attribute value.
     * @return the accounts that fulfill the condition.
     * @throws NamingException if the opertion could not be performed.
     */
    public abstract Collection<Principal> lookupAccounts(String attribute, String value)
        throws NamingException;

    /**
     * Looks up user accounts according to personal data attributes.
     * 
     * @param query the JNDI query in format supported by the underlying directory.
     * @return the accounts that fulfill the condition.
     * @throws NamingException if the opertion could not be performed.
     */
    public abstract Collection<Principal> lookupAccounts(String query)
        throws NamingException;

    /**
     * Looks up user accounts according to personal data attributes and search controlls.
     * 
     * @param query the JNDI query in format supported by the underlying directory.
     * @param searchControlls JNDI SearchControlls
     * @return the accounts that fulfill the condition.
     * @throws NamingException
     */
    public abstract Collection<Principal> lookupAccounts(String query, SearchControls searchControlls) 
                    throws NamingException;

    /**
     * Gets any user attribute data
     * 
     * @param account
     * @param attribute
     * @return
     * @throws AuthenticationException
     */
    public abstract String getUserAttribute(Principal account, String attribute)
        throws AuthenticationException;

    /**
     * Updates tracking information about account. Updates last logon timestamp and bumps logon
     * counter
     * 
     * @param account the account.
     * @throws AuthenticationException if the opertion could not be performed.
     * @throws NamingException if closing directory context fails
     */
    public abstract void updateTrackingInformation(Principal account)
        throws AuthenticationException, NamingException;
}
