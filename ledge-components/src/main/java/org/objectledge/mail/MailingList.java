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

package org.objectledge.mail;

import java.util.List;

import org.objectledge.parameters.Parameters;

/**
 * Mailing list interface.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MailingList.java,v 1.3 2006-04-03 13:38:29 pablo Exp $
 */
public interface MailingList
{
    public static final int SUBSCRIBED = 0;
    
    public static final int NEEDS_APPROVAL = 1;
    
    public static final int NEEDS_TO_CONFIRM = 2;
    
    public static final int REQUIRE_CONFIRM = 1;
    
    public static final int REQUIRE_APPROVAL = 2;
    
    public static final int REQUIRE_CONFIRM_AND_APPROVAL = 3;
    
    /**
     * Add new member to mailing list.
     * 
     * @param address email address of new member.
     * @param name full name of new member.
     * @param password member's password, auto generated if <code>null</code>
     * @param digest if <code>true</code> member will receive batched digest delivery.
     * @param ignoreCreationPolicy force creating ignoring confirmation and approval.
     * @return the state of the membership.
     * @throws MailingListsException if anything goes wrong.
     */
    public int addMember(String address, String name, String password, 
        boolean digest, boolean ignoreCreationPolicy)
        throws MailingListsException;

    /**
     * Delete list member.
     *
     * @param address email address of a member.
     * @param ignoreDeletingPolicy force deleting ignoring confirmation and approval.
     * @throws MailingListsException if anything goes wrong.
     */
    public int deleteMember(String address, boolean ignoreDeletingPolicy)
        throws MailingListsException;

    /**
     * Change list member address
     * 
     * @param oldAddress old address.
     * @param newAddress new address.
     * @throws MailingListsException if anything goes wrong.
     */
    public void changeMemberAddress(String oldAddress, String newAddress)
        throws MailingListsException;

    /**
     * Add member address
     * 
     * @param oldAddress old address.
     * @param newAddress new address.
     * @throws MailingListsException if anything goes wrong.
     */
    public void addMemberAddress(String oldAddress, String newAddress)
        throws MailingListsException;
    
    /**
     * Get all lists members.
     * 
     * @return the list of members addresses.
     * @throws MailingListsException if anything goes wrong.
     */
    public List<String> getMembers()
        throws MailingListsException;
    
    /**
     * Set ml's administrator password.
     * 
     * @param password new password.
     * @throws MailingListsException
     */
    public void setPassword(String password)
        throws MailingListsException;
    
    /**
     * Set moderation flag for mailing list.
     * 
     * @param moderated moderated if <code>true</code>
     * @throws MailingListsException
     */
    public void setPostingModerated(boolean moderated)
        throws MailingListsException;
    
    /**
     * Get current moderation flag.
     * 
     * @return whether posting on this list is moderated
     * @throws MailingListsException
     */
    public boolean isPostingModerated()
        throws MailingListsException;
    
    /**
     * Set subscription policy for list.
     * 
     * @param policy the policy.
     * @throws MailingListsException
     */
    public void setSubscriptionPolicy(int policy)
        throws MailingListsException;
    
    /**
     * Get current subscription policy.
     * 
     * @return subscription policy.
     * @throws MailingListsException
     */
    public int getSubscriptionPolicy()
        throws MailingListsException;
    
    /**
     * Get list of messages waiting for moderation.
     * 
     * @return the list of messages.
     * @throws MailingListsException
     */
    public List getPendingPosts()
        throws MailingListsException;
    
    /**
     * Get list of subscriptions. 
     * 
     * @return the list of subscriptions waiting for moderation.
     * @throws MailingListsException
     */
    public List getPendingSubscriptions()
        throws MailingListsException;

    /**
     * Get list of unsubscriptions. 
     * 
     * @return the list of unsubscriptions waiting for moderation.
     * @throws MailingListsException
     */
    public List getPendingUnubscriptions()
        throws MailingListsException;
    
    /**
     * Get pending message. 
     * 
     * @param id message identifier.
     * @return the message.
     * @throws MailingListsException
     */
    public Object getPendingMessage(int id)
        throws MailingListsException;
}
