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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mock;
import org.objectledge.context.Context;
import org.objectledge.utils.LedgeTestCase;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class SecurityHelperTest extends LedgeTestCase
{

    Mock requestMock = mock(HttpServletRequest.class);
    HttpServletRequest request = (HttpServletRequest)requestMock.proxy();
    Mock responseMock = mock(HttpServletResponse.class);
    HttpServletResponse response = (HttpServletResponse)responseMock.proxy();

    public void testSecurityHelper() throws Exception
    {
        requestMock.stub().method("getContentType").will(returnValue("text/plain"));
        Context context = new Context();
        context.clearAttributes();
        MVCContext mvcContext = new MVCContext();
        HttpContext httpContext = new HttpContext(request, response);
        Object obj = new SecureObject(true, true, true);
        try
        {
            SecurityHelper.checkSecurity(obj, context);
            fail("should throw the exception");
        }
        catch (IllegalStateException e)
        {
            //ok!        
        }
        context.setAttribute(HttpContext.class, httpContext);
        try
        {
            requestMock.expect(once()).method("isSecure").will(returnValue(true));
            SecurityHelper.checkSecurity(obj, context);
            fail("should throw the exception");
        }
        catch (IllegalStateException e)
        {
            //ok!        
        }
        context.setAttribute(MVCContext.class, mvcContext);
        requestMock.expect(once()).method("isSecure").will(returnValue(true));
        try
        {
            SecurityHelper.checkSecurity(obj, context);
            fail("should throw the exception");
        }
        catch (LoginRequiredException e)
        {
            //ok!
        }
        
        mvcContext.setUserPrincipal(null, true);
        requestMock.expect(once()).method("isSecure").will(returnValue(true));
        SecurityHelper.checkSecurity(obj, context);
        
        requestMock.expect(once()).method("isSecure").will(returnValue(false));
        try
        {
            SecurityHelper.checkSecurity(obj, context);
            fail("should throw the exception");
        }
        catch (InsecureChannelException e)
        {
            //ok!
        }
        obj = new SecureObject(false, false, false);
        try
        {
            SecurityHelper.checkSecurity(obj, context);
            fail("should throw the exception");
        }
        catch (AccessDeniedException e)
        {
            //ok!
        }
                
    }

    public class SecureObject implements SecurityChecking
    {
        private boolean loginReq;
        private boolean sslReq;
        private boolean access;

        public SecureObject(boolean loginReq, boolean sslReq, boolean access)
        {
            this.loginReq = loginReq;
            this.sslReq = sslReq;
            this.access = access;
        }

        public boolean requiresAuthenticatedUser(Context context)
        {
            return loginReq;
        }

        public boolean requiresSecureChannel(Context context)
        {
            return sslReq;
        }

        public boolean checkAccessRights(Context context)
        {
            return access;
        }
    }
}
