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

/**
 * Simple container class to store the description of an access policy.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Policy.java,v 1.3 2005-02-08 19:11:29 rafal Exp $
 */
public class Policy
{
    /** The involved roles. */
    private String[] roles;
    
    /** secure channel requirement. */
    private boolean requiresSSL;
    
    /** login requirement. */
    private boolean requiresLogin;
    
    /** unparsed view patterns. */
    private String[] views;
    
    /** parsed view patterns. */
    private String[][] viewPatterns;
    
    /** unparsed action patterns. */
    private String[] actions;
    
    /** parsed action patterns. */
    private String[][] actionPatterns;

    /**
     * Constructs a policy.
     *
     * @param requiresSSL <code>true</code> if the policy requires that
     *        the request is made using secure channel.
     * @param requiresLogin <code>true</code> if the policy requires user to 
     *        be logged in.
     * @param roles the names of Roles involved in the policy.
     * @param views the patterns describing involved views.
     * @param actions the patterns describing involved actions.
     */
    Policy(boolean requiresSSL, boolean requiresLogin, String[] roles, 
           String[] views, String[] actions)
    {
        this.requiresSSL = requiresSSL;
        this.requiresLogin = requiresLogin;
        this.roles = roles;
        this.views = views;
        this.viewPatterns = new String[views.length][];
        for(int i=0; i<views.length; i++)
        {
            this.viewPatterns[i] = getPattern(views[i]);
        }
        this.actions = actions;
        this.actionPatterns = new String[actions.length][];
        for(int i=0; i<actions.length; i++)
        {
            this.actionPatterns[i] = getPattern(actions[i]);
        }
    }
    
    // public interface //////////////////////////////////////////////////////

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
     * Returns a list of patterns describing views that are affected by the
     * policy.
     *
     * <p>The pattern is composed of name of the view. with packages prefix 
     * separated by , characters. An icomplete view name followed by * 
     * character may be used to match * multiple views.</p>
     *
     * @return a list of patterns describing views that are affected by the
     *         policy. 
     */
    public String[] getViewPatterns()
    {
        return views;
    }
    
    /**
     * Returns a list of patterns describing actions that are affected by the
     * policy.
     *
     * <p>The pattern is the action class name composed of packages 
     * separated by , characters. An icomplete action name followed 
     * by * character may be used to match * multiple actions.</p>
     *
     * @return a list of patterns describing actions that are affected by the
     *         policy. 
     */
    public String[] getActionPatterns()
    {
        return actions;
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
        for(int i=0; i<viewPatterns.length; i++)
        {
            if(match(viewPatterns[i], view))
            {
                return true;
            }
        }
        for(int i=0; i<actionPatterns.length; i++)
        {
            if(match(actionPatterns[i], action))
            {
                return true;
            }
        }
        return false;
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * Parses pattern string from configuration.
     * 
     * @param pat the pattern string to be parsed
     * @return the pattern array
     */
    private String[] getPattern(String pat)
    {
        String[] pattern = new String[2];
        int pos = pat.indexOf('*');
        if(pos < 0)
        {
            pattern[0] = pat;
            pattern[1] = null;
        }
        else
        {
            pattern[0] = null;
            pattern[1] = pat.substring(0,pos);
        }
        return pattern;
    }
    
    /**
     * The pattern matching method.
     *
     * @param pattern the pattern.
     * @param value the value.
     * @return <code>true</code> if parameters match the pattern 
     */
    private boolean match(Object pattern, String value)
    {
        String[] pat = (String[])pattern;
        if(pat[0] != null && pat[0].equals(value))
        {
            return true;
        }
        if(pat[1] != null && value != null && value.startsWith(pat[1]))
        {
            return true;
        }
        return false;
    }
}
