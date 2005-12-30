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

package org.objectledge.web.mvc.security;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.security.RoleChecking;

/**
 * PolicySystem - simple component to checks user permission to execute the action and
 * access the view.
 *
 * <p>You need to run PolicyCheckingValve {@link 
 * org.objectledge.web.mvc.security.PolicyCheckingValve} to enforce the policies checking.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PolicySystem.java,v 1.3 2005-12-30 08:42:06 rafal Exp $
 */
public class PolicySystem
{
    /** the policies. */
    private Map policies = new HashMap();

    /** logging service */
    private Logger logger;

    /** security service */
    private RoleChecking roleChecking;

    /** secure channel requirement for requests not matchin any policy. */
    private boolean globalSSL = false;

    /** login requirement for requests not matching any policy. */
    private boolean globalLogin = false;

    /** access permission for request not matching any policy. */
    private boolean globalAccess = true;

    /** the configuration key for RoleChecking implementing service. */
    public static final String ROLE_CHEKCING_KEY = "roleChecking";

    /**
     * Component constructor.
     *  
     * @param config the configuration.
     * @param logger the logger.
     * @param roleChecking the role checking component.
     * @throws ConfigurationException if config is invalid.
     */
    public PolicySystem(Configuration config, Logger logger, RoleChecking roleChecking)
        throws ConfigurationException
    {
        this.logger = logger;
        this.roleChecking = roleChecking;
        globalSSL = config.getChild("globalSSL").getValueAsBoolean(globalSSL);
        globalLogin = config.getChild("globalLoginRequired").getValueAsBoolean(globalLogin);
        globalAccess = config.getChild("globalAccess").getValueAsBoolean(globalAccess);
        for (Configuration policyNode : config.getChildren("policy"))
        {
            addPolicy(policyNode);
        }
    }

    /**
     * Returns the secure channel requirement flag for requests not matching 
     * any specific policy.
     *
     * @return the secure channel requirement flag for requests not matching 
     *         any specific policy.
     */
    public boolean getGlobalSSL()
    {
        return globalSSL;
    }

    /**
     * Returns the login requirement flag for requests not matching any
     * specific policy.
     *
     * @return the login requirement flag for requests not matching any
     *         specific policy.
     */
    public boolean getGlobalLogin()
    {
        return globalLogin;
    }
    
    /**
     * Sets the secure channel requirement flag for requests not matching any
     * specific policy.
     *
     * @param ssl the secure channel requirement flag for requests not 
     *        matching any specific policy.
     */
    public void setGlobalSSL(boolean ssl)
    {
        globalSSL= ssl;
    }

    /**
     * Sets the login requirement flag for requests not matching any
     * specific policy.
     *
     * @param login the login requirement flag for requests not matching any
     *        specific policy.
     */
    public void setGlobalLogin(boolean login)
    {
        globalLogin = login;
    }

    /**
     * Returns the access permission flag for requests not matching any
     * specific policy.
     *
     * @return the access permission flag for requests not matching any
     *         specific policy.
     */
    public boolean getGlobalAccess()
    {
        return globalAccess;
    }
    
    /**
     * Sets the access permission flag for requests not matching any
     * specific policy.
     *
     * @param access the access permission flag for requests not matching any
     *        specific policy.
     */
    public void setGlobalAccess(boolean access)
    {
        globalAccess = access;
    }

    /**
     * Returns a named policy.
     *
     * @param name the name of the policy.
     * @return the policy.
     * @throws IllegalArgumentException when the policy is not defined.
     */
    public Policy getPolicy(String name)
        throws IllegalArgumentException
    {
        Policy policy = (Policy)policies.get(name);
        if(policy == null)
        {
            throw new IllegalArgumentException("unknown policy "+name);
        }
        return policy;
    }
    
    /**
     * Returls all defined policies.
     * 
     * @return the list of all policies.
     */
    public Policy[] getPolicies()
    {
        Policy[] result = new Policy[policies.size()];
        policies.values().toArray(result);
        return result;
    }

    /**
     * Returns the policy that will be used for determinig access privileges
     * in this request.
     *
     * <p> A screen or action can use this method to determine if it is
     * protected by a specific scurity policy. This is recommended that
     * security sensitive actions that rely on the policy service for
     * privilege checking <b>refuse</b> to work unless protected by a specific
     * (non-null) policy.</p>
     *
     * @param view the request view.
     * @param action the request action.
     * @return policy that will be used for privilege checking, or
     *         <code>null</code> if the default policy will be used.
     */
    public Policy getPolicy(String view, String action)
    {
        Iterator i = policies.values().iterator();
        while(i.hasNext())
        {
            Policy policy = (Policy)i.next();
            if(policy.matchesRequest(view, action))
            {
                return policy;
            }
        }
        return null;
    }

    /**
     * Creates a policy dynamically.
     *
     * @param name the name of the policy.
     * @param requiresSSL <code>true</code> if the policy requires that
     *        the request is made using secure channel.
     * @param requiresLogin <code>true</code> if the policy requires user to 
     *        be logged in.
     * @param roles the names of Roles involved in the policy.
     * @param views the patterns describing involved views.
     * @param actions the patterns describing involved actions.
     * @see Policy
     */
    public void addPolicy(String name, boolean requiresSSL, boolean requiresLogin, 
        String[] roles, String[] views, String[] actions)
    {
        Policy policy = new Policy(name, requiresSSL, requiresLogin, roles, views, actions);
        policies.put(name, policy);
    }
    
    /**
     * Creates a policy dynamically.
     *
     * @param config policy configuration.
     * @throws ConfigurationException if the configuration is malformed.
     * @see Policy
     */
    public void addPolicy(Configuration config)
        throws ConfigurationException
    {
        Policy policy = new Policy(config);
        policies.put(policy.getName(), policy);
    }
    
    /**
     * Removes a policy dynamically.
     *
     * @param name the name of the policy.
     * @throws IllegalArgumentException when the policy is not defined.
     */
    public void removePolicy(String name)
        throws IllegalArgumentException
    {
        Policy policy = (Policy)policies.remove(name);
        if(policy == null)
        {
            throw new IllegalArgumentException("unknown policy "+name);
        }
    }

    // policy checking ///////////////////////////////////////////////////////

    /**
     * Checks if the user fulfills a named policy.
     *
     * @param principal the principal.
     * @param authenticated is current principal authenticated.
     * @param policy the policy.
     * @return <code>true</code>if user accepted by policy.
     * @throws IllegalArgumentException when the policy is not defined.
     */
    public boolean checkPolicy(Principal principal, boolean authenticated, Policy policy)
        throws IllegalArgumentException
    {
        if(policy.requiresLogin())
        {
            if(authenticated)
            {
                return isUserInRole(principal, policy.getRoles());
            }
            else
            {
                return false;
            }
        }
        return true;
    }    

    // request checking //////////////////////////////////////////////////////

    /**
     * Checks if the selected view and action requires the request to be made
     * through a secure channel. 
     *
     * @param view the request view.
     * @param action the request action.
     * @return <code>true</code> if the policy requires that the request is 
     *         made using secure channel.
     */
    public boolean requiresSSL(String view, String action)
    {
        Iterator i = policies.values().iterator();
        while(i.hasNext())
        {
            Policy policy = (Policy)i.next();
            if(policy.matchesRequest(view,action))
            {
                return policy.requiresSSL();
            }
        }
        return globalSSL;
    }

    /**
     * Checks if the selected view and action requires the user to be
     * authenticated. 
     *
     * @param view the request view.
     * @param action the request action.
     * @return <code>true</code> if the policy requires the user to be
     *         authenticated. 
     */
    public boolean requiresLogin(String view, String action)
    {
        Iterator i = policies.values().iterator();
        while(i.hasNext())
        {
            Policy policy = (Policy)i.next();
            if(policy.matchesRequest(view,action))
            {
                return policy.requiresLogin();
            }
        }
        return globalLogin;
    }

    /**
     * Checks if the current user fulfills the security policy of the selected
     * view and action.
     * @param view the request view.
     * @param action the request action.
     * @param principal the request principal.
     * @param authenticated <code>true</code>if request principal is authenticated.
     * @return <code>true</code> if access granted
     */
    public boolean checkAccess(String view, String action, Principal principal, 
                                boolean authenticated)
    {
        Iterator i = policies.values().iterator();
        while(i.hasNext())
        {
            Policy policy = (Policy)i.next();
            if(policy.matchesRequest(view,action))
            {
                return checkPolicy(principal,authenticated, policy);
            }
        }
        return globalAccess;
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * Checks if the user is in one of the given roles.
     * 
     * @param user the user.
     * @param roles the list of roles.
     * @return <code>true</code> if user is in one of the roles.
     */
    private boolean isUserInRole(Principal user, String[] roles)
    {
        try
        {
            String[] userRoles = roleChecking.getRoles(user);
            if(userRoles != null)
            {
                for (int i = 0; i < roles.length; i++)
                {
                    for (int j = 0; j < userRoles.length; j++)
                    {
                        if (roles[i].equals(userRoles[j]))
                        {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        catch(UserUnknownException e)
        {
            logger.error("checking roles of unknown user "+user.getName(), e);
            return false;
        }
    }
}
