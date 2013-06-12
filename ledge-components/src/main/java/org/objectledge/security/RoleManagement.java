package org.objectledge.security;

import java.security.Principal;
import java.util.Set;

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

    /**
     * Retrieves occupants of a specific role.
     * 
     * @param role
     * @return a set of users
     */
    Set<Principal> getRoleOccupants(String role);
}
