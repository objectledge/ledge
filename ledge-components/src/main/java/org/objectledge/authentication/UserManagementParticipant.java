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

package org.objectledge.authentication;

import java.security.Principal;

/**
 * Implemented by components that need to be aware of addition/removal of user accounts.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UserManagementParticipant.java,v 1.1 2005-05-18 06:53:07 pablo Exp $
 */
public interface UserManagementParticipant
{
	/**
	 * Expresses components's support for user account removal.
	 * 
	 * <p>If any of the components registered with user management component
	 * as implementations of this interface return <code>false</code> from
	 * this method, attempts to remove user account will fail with <code>
	 * UnsupportedOperationException</code>.
	 * 
	 * @return <code>true</code> if the component supports account removal.
	 */
	public boolean supportsRemoval();

	/**
	 * Called when user account is created.
	 * 
	 * <p>The method is called after an account has been successfully
	 * created in the user manager.</p>
	 * 
	 * @param user the new user.
	 */
	public void createAccount(Principal user)
		throws UserAlreadyExistsException;
	
	/**
	 * Called when user account is about to be removed.
	 * 
	 * <p>The method is called before an account is removed from the
	 * user manager.</p>
	 * 
	 * @param user the user being removed.
	 */
	public void removeAccount(Principal user)
		throws UserUnknownException, UserInUseException;
}
