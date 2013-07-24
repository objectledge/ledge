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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jmock.Mock;
import org.objectledge.LedgeWebTestCase;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.LocaleLoaderValve;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.security.RoleChecking;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;

public class PolicyCheckingValveTest extends LedgeWebTestCase
{
    private Context context;
    private Mock requestMock = mock(HttpServletRequest.class);
    private HttpServletRequest request = (HttpServletRequest)requestMock.proxy();
    private Mock responseMock = mock(HttpServletResponse.class);
    private HttpServletResponse response = (HttpServletResponse)responseMock.proxy();
    
    private PolicyCheckingValve policyValve;

    public void setUp()
        throws Exception
    {
        FileSystem fs = getFileSystem("src/test/resources/config");
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(LocaleLoaderValve.class));
        Configuration config = getConfig(fs, PolicySystem.class, PolicySystem.class);
        RoleChecking roleChecking = new RoleChecking()
            {
                public Set<String> getRoles(Principal user)
                    throws UserUnknownException
                {
                    if(user.getName() == "root")
                    {
                        return set("admin", "moderator");
                    }
                    if(user.getName() == "user")
                    {
                        return set("user");
                    }
                    if(user.getName() == "anon")
                    {
                        return set();
                    }
                    throw new UserUnknownException("unknown user: " + user.getName());
                }

                public boolean hasRole(Principal user, String role)
                    throws UserUnknownException
                {
                    return getRoles(user).contains(role);
                }
            };

        context = new Context();
        context.clearAttributes();
        requestMock.stubs().method("getContentType").will(returnValue("text/plain"));
        MVCContext mvcContext = new MVCContext();
        HttpContext httpContext = new HttpContext(request, response);
        context.setAttribute(RequestParameters.class, new MockRequestParameters());
        context.setAttribute(MVCContext.class, mvcContext);
        context.setAttribute(HttpContext.class, httpContext);

        PolicySystem policySystem = new PolicySystem(config, logger, roleChecking);
        config = getConfig(fs, WebConfigurator.class, WebConfigurator.class);
        WebConfigurator webConfigurator = new WebConfigurator(config);
        policyValve = new PolicyCheckingValve(logger, webConfigurator, policySystem);
    }

    private Set<String> set(String... strings)
    {
        return strings != null ? new HashSet<>(Arrays.asList(strings)) : Collections
            .<String> emptySet();
    }

    public void testValve()
        throws Exception
    {
        policyValve.process(context);        
    }
    
    /**
     * CGLIB fails for this type, beacuse of non-static method called from the constructor
     * so we need to resort to this.
     */
    private class MockRequestParameters
        extends RequestParameters
    {
        
    }
}