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
package org.objectledge.modules.actions.authentication;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.Authentication;
import org.objectledge.context.Context;

/**
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseAuthenticationAction.java,v 1.3 2004-02-20 12:47:30 zwierzem Exp $
 */
public class BaseAuthenticationAction
{
    /** login parameter name */
    public static final String LOGIN_PARAM = "login";

    /** password parameter name */
    public static final String PASSWORD_PARAM = "password";

    /** the logger */
    protected Logger logger;

    /** the authentication component */
    protected Authentication authentication;

    /**
     * Action constructor.
     * 
     * @param logger the logger.
     * @param authentication the authentication.
     */
    public BaseAuthenticationAction(Logger logger, Authentication authentication)
    {
        this.logger = logger;
        this.authentication = authentication;
    }

    /**
     * Clear session to prevent the scenario that
     * one user uses the session of another.
     * <p>TODO: Isn't it enough to call <code>session.invalidate()</code>?
     * @param session the http session.
     */
    protected void clearSession(HttpSession session)
    {
        Enumeration attrNames = session.getAttributeNames();
        ArrayList temp = new ArrayList();
        while (attrNames.hasMoreElements())
        {
            String name = (String)attrNames.nextElement();
            temp.add(name);
        }
        for (int i = 0; i < temp.size(); i++)
        {
            String name = (String)temp.get(i);
            session.removeAttribute(name);
        }
    }
}
