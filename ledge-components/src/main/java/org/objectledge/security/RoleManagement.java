package org.objectledge.security;

import java.security.Principal;

/**
 * Optional extension of RoleChecking interface allowing management of role assignments.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public interface RoleManagement
    extends RoleChecking
{
    /**
     * Grant specified role to an user.
     * 
     * @param user
     * @param role
     */
    void grant(Principal user, String role);

    /**
     * Revoke specified role from an user.
     * 
     * @param user
     * @param role
     */
    void revoke(Principal user, String role);
}
