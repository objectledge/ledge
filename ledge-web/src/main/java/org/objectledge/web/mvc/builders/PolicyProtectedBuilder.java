// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.mvc.builders;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.security.Policy;
import org.objectledge.web.mvc.security.PolicySystem;
import org.objectledge.web.mvc.security.SecurityChecking;

/**
 * A builder that must be protected by a specific policy. 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PolicyProtectedBuilder.java,v 1.2 2005-07-26 12:15:36 rafal Exp $
 */
public abstract class PolicyProtectedBuilder
    extends AbstractBuilder
    implements SecurityChecking
{
    private final PolicySystem policySystem;

    /**
     * Creates new PolicyProtectedBuider instance.
     * 
     * @param context the request context.
     * @param policySystemArg the PolicySystem component.
     */
    public PolicyProtectedBuilder(Context context, PolicySystem policySystemArg)
    {
        super(context);
        this.policySystem = policySystemArg;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        Policy policy = getPolicy(context);
        AuthenticationContext authenticationContext =
            AuthenticationContext.getAuthenticationContext(context);
        return !policy.requiresLogin() || authenticationContext.isUserAuthenticated();
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        Policy policy = getPolicy(context);
        AuthenticationContext authenticationContext =
            AuthenticationContext.getAuthenticationContext(context);
        return policySystem.checkPolicy(authenticationContext.getUserPrincipal(),
            authenticationContext.isUserAuthenticated(), policy);
    }
    
    /**
     * Retruns a policy matching the current request.
     * 
     * @param context the request context.
     * @return the matching policy.
     * @throws ProcessingException if no policy matches the request.
     */
    private Policy getPolicy(Context context)
        throws ProcessingException
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        Policy policy = policySystem.getPolicy(mvcContext.getView(), mvcContext.getAction());
        if(policy == null)
        {
            throw new ProcessingException(mvcContext.getView() + 
                " is not matched by any access policy");
        }
        return policy;
    }
}
