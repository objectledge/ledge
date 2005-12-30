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

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 * Simple container class to store the description of an access policy.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Policy.java,v 1.7 2005-12-30 12:43:53 rafal Exp $
 */
public class Policy
{
    private static final String[] STRINGS = {};

    /** The name of the policy. */
    private String name;
    
    /** The involved roles. */
    private String[] roles;
    
    /** secure channel requirement. */
    private boolean requiresSSL;
    
    /** login requirement. */
    private boolean requiresLogin;
    
    /** the matchers. */
    private List<Matcher> matchers;
    
    /**
     * Constructs a policy.
     *
     * @param name the name of the policy.
     * @param requiresSSL <code>true</code> if the policy requires that
     *        the request is made using secure channel.
     * @param requiresLogin <code>true</code> if the policy requires user to 
     *        be logged in.
     * @param roles the names of Roles involved in the policy.
     * @param views the patterns describing involved views.
     * @param actions the patterns describing involved actions.
     */
    Policy(String name, boolean requiresSSL, boolean requiresLogin, String[] roles, 
           String[] views, String[] actions)
    {
        this.name = name;
        this.requiresSSL = requiresSSL;
        this.requiresLogin = requiresLogin;
        this.roles = roles;
        this.matchers = getWildcardMatchers(views, actions);
    }
    
    /**
     * Creates a new Policy instance.
     *
     * @param config policy configuration object.
     * @throws ConfigurationException if the configuration is malformed.
     */
    Policy(Configuration config) throws ConfigurationException        
    {
        name = config.getAttribute("name");
        requiresSSL = config.getAttributeAsBoolean("ssl",false);
        requiresLogin = config.getAttributeAsBoolean("auth",false);
        Configuration[] roleNodes = config.getChildren("role");
        ArrayList<String> rolesList = new ArrayList<String>();
        for(int j = 0; j < roleNodes.length; j++)
        {
            rolesList.add(roleNodes[j].getValue());
        }
        roles = rolesList.toArray(STRINGS);
        matchers = getMatchers(config.getChildren(), false);
    }
    
    // public interface //////////////////////////////////////////////////////

    /**
     * Returns the name of the policy.
     * 
     * @return name of the policy.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Returns the names of Roles involved in the policy.
     *
     * <p>The user must be in one or more of the returned roles to fullfill
     * the policy.</p>
     *
     * @return the names of Roles involved in the policy.
     */
    public String[] getRoles()
    {
        return roles;
    }
    
    /**
     * Returns <code>true</code> if the request must be made using a secure
     * channel to fulfill the policy. 
     * 
     * @return <code>true</code> if the request must be made using a secure
     * channel to fulfill the policy.
     */
    public boolean requiresSSL()
    {
        return requiresSSL;
    }

    /**
     * Returns <code>true</code> if the user must be authenticated to fulfill
     * the policy.
     *
     * @return <code>true</code> if the user must be authenticated to fulfill
     * the policy.
     */
    public boolean requiresLogin()
    {
        return requiresLogin;
    }

    /**
     * Checks if the view or action selected in the request match this policy.
     *
     * @param view the request view.
     * @param action the request action.
     * @return <code>true</code> if the reqest matches the policy.
     */
    public boolean matchesRequest(String view, String action)
    {
        for(Matcher m : matchers)
        {
            String s = (m.getType() == Matcher.Type.VIEW) ? view : action;
            if(m.matches(s))
            {
                return true;
            }
        }
        return false;
    }

    // implementation ////////////////////////////////////////////////////////

    private List<Matcher> getWildcardMatchers(String[] views, String[] actions)
    {
        List<Matcher> out = new ArrayList<Matcher>(views.length + actions.length);
        for(String pattern : views)
        {
            out.add(new WildcardMatcher(pattern, Matcher.Type.VIEW));
        }
        for(String pattern : actions)
        {
            out.add(new WildcardMatcher(pattern, Matcher.Type.ACTION));
        }
        return out;
    }    
    
    private List<Matcher> getMatchers(Configuration[] nodes, boolean inverted)
        throws ConfigurationException
    {
        List<Matcher> out = new ArrayList<Matcher>();
        for(Configuration node : nodes)
        {
            String type = node.getName();
            if("except".equals(type))
            {
                out.addAll(getMatchers(node.getChildren(), true));
            }
            if("view".equals(type))
            {
                out.add(invert(new WildcardMatcher(node.getValue(), Matcher.Type.VIEW), inverted));
            }
            if("action".equals(type))
            {
                out.add(invert(new WildcardMatcher(node.getValue(), Matcher.Type.ACTION), inverted));
            }
            // ignore others
        }
        return out;
    }
    
    private Matcher invert(Matcher matcher, boolean invert)
    {
        if(invert)
        {
            return new InverseMatcher(matcher);
        }
        else
        {
            return matcher;
        }
    }
    
    /**
     * Matches a string against a pattern. 
     */
    private abstract static class Matcher
    {
        private final Type type;

        public Matcher(Type type)
        {
            this.type = type;            
        }
        
        public abstract boolean matches(String value);
        
        public Type getType()
        {
            return type;
        }
        
        public enum Type
        {
            VIEW,
            ACTION
        }
    }

    /**
     * Wildcard matcher that supports exact match and something* match. 
     */
    private static class WildcardMatcher
        extends Matcher
    {
        private final String exact;
        private final String head;
        
        public WildcardMatcher(String pattern, Type type)
        {
            super(type);
            int pos = pattern.indexOf('*');
            if(pos < 0)
            {
                exact = pattern;
                head = null;
            }
            else
            {
                exact = null;
                head = pattern.substring(0,pos);
            }            
        }
        
        public boolean matches(String value)
        {
            if(exact != null && exact.equals(value))
            {
                return true;
            }
            if(head != null && value != null && value.startsWith(head))
            {
                return true;
            }
            return false;            
        }
    }
    
    /**
     * Provides inverse match for another matcher. 
     */
    private static class InverseMatcher
        extends Matcher
    {
        private final Matcher matcher;

        public InverseMatcher(Matcher matcher)
        {
            super(matcher.getType());
            this.matcher = matcher;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean matches(String value)
        {
            return !matcher.matches(value);
        }        
    }    
}
