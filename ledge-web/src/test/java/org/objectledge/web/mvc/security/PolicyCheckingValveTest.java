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
import org.objectledge.security.RoleChecking;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;

public class PolicyCheckingValveTest extends LedgeWebTestCase
{
    Context context;
    Mock requestMock = mock(HttpServletRequest.class);
    HttpServletRequest request = (HttpServletRequest)requestMock.proxy();
    Mock responseMock = mock(HttpServletResponse.class);
    HttpServletResponse response = (HttpServletResponse)responseMock.proxy();
    
    private PolicyCheckingValve policyValve;

    public void setUp() throws Exception
    {
        FileSystem fs = getFileSystem("src/test/resources/config");
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(LocaleLoaderValve.class));
        Configuration config = getConfig(fs, PolicySystem.class, PolicySystem.class);
        RoleChecking roleChecking = new RoleChecking()
        {
            public String[] getRoles(Principal user) throws UserUnknownException
            {
                if (user.getName() == "root")
                {
                    return new String[] { "admin", "moderator" };
                }
                if (user.getName() == "user")
                {
                    return new String[] { "user" };
                }
                if (user.getName() == "anon")
                {
                    return null;
                }
                throw new UserUnknownException("unknown user: " + user.getName());
            }
        };

        context = new Context();
        context.clearAttributes();
        requestMock.stub().method("getContentType").will(returnValue("text/plain"));
        MVCContext mvcContext = new MVCContext();
        HttpContext httpContext = new HttpContext(request, response);
        context.setAttribute(MVCContext.class, mvcContext);
        context.setAttribute(HttpContext.class, httpContext);
        
        PolicySystem policySystem = new PolicySystem(config, logger, roleChecking);
        config = getConfig(fs, WebConfigurator.class, WebConfigurator.class);
        WebConfigurator webConfigurator = new WebConfigurator(config);
        policyValve = new PolicyCheckingValve(logger, webConfigurator, policySystem);
    }

    public void testValve()
        throws Exception
    {
        policyValve.process(context);        
    }
}