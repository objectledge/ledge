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

import org.objectledge.context.Context;

/**
 * The interface of all mvc security objects.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: SecurityChecking.java,v 1.4 2004-10-11 09:49:25 zwierzem Exp $
 */
public interface SecurityChecking
{
    /**
     * Check if the component requires secure channel. 
     * 
     * @param context the context.
     * @return <code>true</code>if requires.
     */
    public boolean requiresSecureChannel(Context context)
    	throws Exception;

    /**
     * Check if the component requires authenticated user. 
     * 
     * @param context the context.
     * @return <code>true</code>if requires.
     */
    public boolean requiresAuthenticatedUser(Context context)
    	throws Exception;
    
    /**
     * Check the access rights.
     * 
     * <p>TODO: this security checking context information should be passed up the call hierarchy
     * using an exception or some kind of container object. This method instead of returning
     * <code>boolean</code> should throw an exception with meaningful message.</p>
     * 
     * @param context the context.
     * @return <code>true</code>if requires.
     */
    public boolean checkAccessRights(Context context)
    	throws Exception;
}
