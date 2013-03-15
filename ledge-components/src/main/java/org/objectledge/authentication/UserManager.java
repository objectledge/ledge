package org.objectledge.authentication;

import java.security.Principal;
import java.util.Collection;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import org.objectledge.parameters.Parameters;

public interface UserManager
{

    /**
     * Checks if a login name is a non-occupied and non-reserved one.
     * 
     * @param login the login name to be checked.
     * @return <code>true</code> if a login name is a non-occupied and non-reserved.
     */
    public boolean checkLogin(String login);

    /**
     * Checks if a login name is acceptable.
     * 
     * @param login the login name to be checked.
     * @return <code>true</code> if a login name is correct.
     */
    public boolean validateLogin(String login);

    /**
     * Creates a distinguished name from provided parameters in conformance to configured naming
     * policy.
     * 
     * @param parameters the parameters to generate name from.
     * @return the distinghished name.
     */
    public String createDN(Parameters parameters);

    /**
     * Check if user exists.
     * 
     * @param dn the name of the user.
     * @return <code>true</code> if user exists in system.
     */
    public boolean userExists(String dn);

    /**
     * Check if email exists.
     * 
     * @param email the address to check for.
     * @return <code>true</code> if emails exists in system
     */
    public boolean emailExists(String email);

    /**
     * Creates a new user account.
     * 
     * @param login login name of the user.
     * @param password initial password of the user.
     * @return the newly created account.
     * @throws AuthenticationException if the account could no be created.
     */
    public Principal createAccount(String login, String password)
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
    public Principal createAccount(String login, String password, boolean blockPassword,
        Attributes attributes)
        throws AuthenticationException;

    /**
     * Check if account is blocked
     * 
     * @param login
     * @return
     * @throws AuthenticationException
     */
    public boolean accountBlocked(String login)
        throws AuthenticationException;

    /**
     * Removes an user account.
     * 
     * @param account the account.
     * @throws AuthenticationException if the account could no be removed.
     */
    public void removeAccount(Principal account)
        throws AuthenticationException;

    /**
     * Lookup user by distinguised name.
     * 
     * @param dn the users's distinguished name.
     * @return the account's descriptor.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public Principal getUserByName(String dn)
        throws AuthenticationException;

    /**
     * Lookup user by login name.
     * 
     * @param login the name used for authentication.
     * @return the account's descriptor.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public Principal getUserByLogin(String login)
        throws AuthenticationException;

    /**
     * Lookup user by mail.
     * 
     * @param mail
     * @return
     * @throws AuthenticationException
     */
    public Principal getUserByMail(String mail)
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
        throws AuthenticationException, InvalidNameException;

    /**
     * Returns the login name of an user.
     * 
     * @param account the account.
     * @return the login name, or <code>null</code> if not found.
     * @throws AuthenticationException if there is a problem performing the operation.
     * @throws InvalidNameException if the name does not conform to the configured naming policy.
     */
    public String getLogin(Principal account)
        throws AuthenticationException, InvalidNameException;

    /**
     * Returns the anonymous account.
     * 
     * @return the anonyomous user.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public Principal getAnonymousAccount()
        throws AuthenticationException;

    /**
     * Returns the superuser account.
     * 
     * @return the superuser.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public Principal getSuperuserAccount()
        throws AuthenticationException;

    /**
     * Changes user password.
     * 
     * @param account the account.
     * @param password the new password for the account.
     * @throws AuthenticationException if the password could not be changed.
     */
    public void changeUserPassword(Principal account, String password)
        throws AuthenticationException;

    /**
     * Change user attribiutes
     * 
     * @param account to change
     * @param attributes to change
     * @throws AuthenticationException
     */
    public void changeUserAttribiutes(Principal account, Attributes attribiutes)
        throws AuthenticationException;

    /**
     * Checks user supplied password.
     * 
     * @param account the account.
     * @param password the password to be checked.
     * @return <code>true</code> if the supplied password is correct.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public boolean checkUserPassword(Principal account, String password)
        throws AuthenticationException;

    /**
     * Enables user's password.
     * 
     * Removes ! from the beginning of the user's password
     * 
     * @param account the account
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public void enableUserPassword(Principal account)
        throws AuthenticationException;

    /**
     * Generates a random password.
     * 
     * @param min minimum length.
     * @param max maximum length.
     * @return a random passeword.
     */
    public String createRandomPassword(int min, int max);

    /**
     * Returns the personal data of the accoun't owner.
     * 
     * @param account the account.
     * @return Parameters view of the account's owner personal data.
     * @throws AuthenticationException if there is a problem performing the operation.
     */
    public DirContext getPersonalData(Principal account)
        throws AuthenticationException;

    /**
     * Looks up user accounts according to personal data attributes.
     * 
     * @param attribute the personal data attribute name.
     * @param value the personal data attribute value.
     * @return the accounts that fulfill the condition.
     * @throws NamingException if the opertion could not be performed.
     */
    public Collection<Principal> lookupAccounts(String attribute, String value)
        throws NamingException;

    /**
     * Looks up user accounts according to personal data attributes.
     * 
     * @param query the JNDI query in format supported by the underlying directory.
     * @return the accounts that fulfill the condition.
     * @throws NamingException if the opertion could not be performed.
     */
    public Collection<Principal> lookupAccounts(String query)
        throws NamingException;

    /**
     * Looks up user accounts according to personal data attributes and search controlls.
     * 
     * @param query the JNDI query in format supported by the underlying directory.
     * @param searchControlls JNDI SearchControlls
     * @return the accounts that fulfill the condition.
     * @throws NamingException
     */
    public Collection<Principal> lookupAccounts(String query,
        SearchControls searchControlls)
        throws NamingException;

    /**
     * Gets any user attribute data
     * 
     * @param account
     * @param attribute
     * @return
     * @throws AuthenticationException
     */
    public String getUserAttribute(Principal account, String attribute)
        throws AuthenticationException;

    /**
     * Updates tracking information about account. Updates last logon timestamp and bumps logon
     * counter
     * 
     * @param account the account.
     * @throws AuthenticationException if the opertion could not be performed.
     * @throws NamingException if closing directory context fails
     */
    public void updateTrackingInformation(Principal account)
        throws AuthenticationException, NamingException;
  
    /**
     * Check user password expiration time, if expiration time is equals or smaller than ShadowWarning attribute then method returns
     * day count to password expiry. If expiration time is bigger than ShadowWarning Value then method returns value smaller than 0
     * 
     * @param account
     * @return
     * @throws AuthenticationException
     */
    public long getUserPasswordExpirationDays(Principal account) throws AuthenticationException;
    
    /**
     * Check if user password is expired;
     * 
     * @param account
     * @return
     * @throws AuthenticationException
     */
    public boolean isUserPasswordExpired(Principal account) throws AuthenticationException;
    
    /**
     * Check if user account expired;
     * 
     * @param account
     * @return
     */
    public boolean isUserAccountExpired(Principal account) throws AuthenticationException;
    
    /**
     * Check account shadow flag and return enum with reason
     * 
     * @param account
     * @return
     * @throws AuthenticationException
     */
    public BlockedReason checkAccountFlag(Principal account) throws AuthenticationException;

    /**
     * Block user account for some reason
     * 
     * @param p
     * @param code
     */
    public void setUserShadowFlag(Principal user, String code)
        throws AuthenticationException ;
}
