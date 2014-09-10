package org.objectledge.authentication.identity;

import java.security.Principal;

/**
 * Provides a way to associate user's browser with server side Principal when HTTP sessions are not
 * available of suitable.
 * <P>
 * Identity association is maintained using textual identity tokens, that need to be stored on the
 * client side in HTML5 storage facility. Length and format of the tokens is implementation
 * specific.
 * </P>
 * <P>
 * Store implementation may keep the association across server restarts, and may impose restrictions
 * on maximum validity time of tokens.
 * </P>
 *
 * @author rafal.krzewski@caltha.pl
 */
public interface IdentityStore
{
    /**
     * Create identity token for an user.
     * <P>
     * The returned token needs to be stored on the client side in HTML5 storage facility. Creating
     * a new token does not invalidate previously generated tokens for the same user, allowing
     * access from multiple browsers / devices at the same time.
     * </P>
     * 
     * @param principal user's server side identity.
     * @param validityTime requested token validity in seconds. Special values: 0 - maximum validity
     *        time allowed by the specification, negative values - default validity time, dependent
     *        of implementation configuration.
     * @return identity token.
     */
    String save(Principal principal, int validityTime);

    /**
     * Resolve user's identity from an identity token.
     * 
     * @param identity identity token.
     * @return User's identity, or {@code null} if token is invalid.
     */
    Principal load(String identity);

    /**
     * Invalidate a specific identity token.
     * 
     * @param principal
     */
    void remove(String identity);

    /**
     * Invalidate all identity tokens for a specific user.
     * 
     * @param principal
     */
    void remove(Principal principal);
}
