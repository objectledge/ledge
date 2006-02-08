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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcontainer.dna.Configuration;

/**
 * Verifies a login name against a set of reserved ones and pattern.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LoginVerifier.java,v 1.4 2006-02-08 18:19:37 zwierzem Exp $
 */
public class LoginVerifier
{
    /** The regexp login pattern. */
    public static final String LOGIN_PATTERN = "[-a-zA-Z0-9]+";

    /** the reserved logins set */
    private Set<String> reserved;
    
    /** the login pattern */
    private Pattern loginPattern;

    /**
     * Component constructor.
     * 
     * @param config the configuration.
     */    
    public LoginVerifier(Configuration config)
    {
        reserved = new HashSet<String>();
        String list = config.getChild("reserved").getValue("");
        StringTokenizer st = new StringTokenizer(list);
        while(st.hasMoreTokens())
        {
            String login = st.nextToken();
            reserved.add(login);
        }
        String pattern = config.getChild("loginPattern")
           .getValue(LOGIN_PATTERN);
        loginPattern = Pattern.compile(pattern);
    }
    
    /**
     * Checks if a login name is a non-reserved one.
     * 
     * @param login the login name to be checked.
     * @return <code>true</code> if a login name is a non-occupied and non-reserved.
     */
    public boolean checkLogin(String login)
    {
        return !reserved.contains(login);       
    }
    
    /**
     * Verify login.
     * 
     * @param login the login.
     * @return <code>true</code> if login is valid.
     */
    public boolean validate(String login)
    {
        Matcher m = loginPattern.matcher(login);
        return m.matches();
    }
}
