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

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;


/**
 *  Valve to check rights on views & actions
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class PolicyCheckingValve implements Valve
{
    /** the logger */
    private Logger logger;

    /** the web configurator */
    private WebConfigurator webConfigurator;
    
    /** policy system */
    private PolicySystem policySystem;
    
    /**
     * Compnent constructor.
     * 
     * @param logger the logger.
     * @param webConfigurator the web configurator.
     * @param policySystem the policy system component.
     */
    public PolicyCheckingValve(Logger logger, WebConfigurator webConfigurator, 
                                PolicySystem policySystem)
    {
        this.logger = logger;
        this.webConfigurator = webConfigurator;
        this.policySystem = policySystem;
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context) throws ProcessingException
    {
        // TODO Get the stage from ???
        String stage = "";
        logger.debug("Policy Hook fired at "+stage+" stage");
        if(stage.equals("preProcessing"))
        {
            Parameters parameters = RequestParameters.getRequestParameters(context);
            String action = parameters.get(webConfigurator.getActionToken(),"");    
            if(action.startsWith("authentication,"))
            {
                logger.debug("authentication context switching action "+action+
                             " skipping policy check");
                return;
            }
        }
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        Policy policy = policySystem.getPolicy(mvcContext.getView(), mvcContext.getAction());
        if(policy == null)
        {
            logger.debug("Policy Hook: no policy matched, using global settings");
            if(policySystem.getGlobalSSL() && !httpContext.getRequest().isSecure())
            {
                logger.debug("PolicyHook: secure channel required");
                throw new InsecureChannelException("Please use HTTPS");
            }
            if(policySystem.getGlobalLogin() && !mvcContext.isUserAuthenticated())
            {
                logger.debug("PolicyHook: login required");
                throw new LoginRequiredException("Please login");
            }
            if(!policySystem.getGlobalAccess())
            {
                logger.debug("PolicyHook: access denied");
                throw new AccessDeniedException("No rights to access action or view");
            }
        }
        else
        {
            if(policy.requiresSSL() && !httpContext.getRequest().isSecure())
            {
                logger.debug("PolicyHook: secure channel required");
                throw new InsecureChannelException("Please use HTTPS");
            }
            if(policy.requiresLogin() && !mvcContext.isUserAuthenticated())
            {
                logger.debug("PolicyHook: login required");
                throw new LoginRequiredException("Please login");
            }
            if(!policySystem.checkPolicy(mvcContext.getUserPrincipal(),
                mvcContext.isUserAuthenticated(), policy))
            {
                logger.debug("PolicyHook: access denied");
                throw new AccessDeniedException("No rights to access action or view");
            }
        }
        logger.debug("Policy Hook: access granted");                        

    }

}
