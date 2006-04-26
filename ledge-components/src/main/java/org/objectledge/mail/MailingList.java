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

import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.mail.Message;

/**
 * Mailing list interface.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MailingList.java,v 1.13 2006-04-26 13:11:13 rafal Exp $
 */
public interface MailingList
{
    public enum OperationStatus
    {
        COMPLETED,
        NEEDS_APPROVAL,
        NEEDS_CONFIRMATION;
    }    
    
    public enum SubscriptionPolicy
    {
        REQUIRE_CONFIRMATION,
        REQUIRE_APPROVAL,
        REQUIRE_CONFIRMATION_AND_APPROVAL
    }    
    
    public enum TaskType
    {
        PENDING_POST,
        PENDING_SUBSCRIPTION,
        PENDING_UNSUBSCRIPTION
    }

    /**
     * Returns the list name.
     * 
     * @return the list name.
     */
    public String getName();
    
    /**
     * Add new member to mailing list.
     * 
     * @param address email address of new member.
     * @param name full name of new member.
     * @param password member's password, auto generated if <code>null</code>
     * @param digest if <code>true</code> member will receive batched digest delivery.
     * @param ignoreCreationPolicy force creating ignoring confirmation and approval.
     * @param acknowledge send acknowledge message to the subscribed user.
     * @param notifyAdmins send notification message to the list administrators.
     * @return the state of the membership.
     * @throws MailingListsException if anything goes wrong.
     */
    public OperationStatus addMember(String address, String name, String password, 
        boolean digest, boolean ignoreCreationPolicy, boolean acknowledge,
        boolean notifyAdmins)
        throws MailingListsException;

    /**
     * Delete list member.
     *
     * @param address email address of a member.
     * @param ignoreDeletingPolicy force deleting ignoring confirmation and approval.
     * @param acknowledge send acknowledge message to the subscribed user.
     * @param notifyAdmins send notification message to the list administrators.
     * @throws MailingListsException if anything goes wrong.
     */
    public OperationStatus deleteMember(String address, boolean ignoreDeletingPolicy,
        boolean acknowledge, boolean notifyAdmins)
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
    public void setSubscriptionPolicy(SubscriptionPolicy policy)
        throws MailingListsException;
    
    /**
     * Get current subscription policy.
     * 
     * @return subscription policy.
     * @throws MailingListsException
     */
    public SubscriptionPolicy getSubscriptionPolicy()
        throws MailingListsException;
    
    /**
     * Get list of messages waiting for moderation.
     * 
     * @return the list of message ids.
     * @throws MailingListsException
     */
    public List<String> getPendingPosts()
        throws MailingListsException;
    
    /**
     * Get list of subscriptions. 
     * 
     * @return the list of ids of subscriptions waiting for moderation.
     * @throws MailingListsException
     */
    public List<String> getPendingSubscriptions()
        throws MailingListsException;

    /**
     * Get list of unsubscriptions. 
     * 
     * @return the list of ids of unsubscriptions waiting for moderation.
     * @throws MailingListsException
     */
    public List<String> getPendingUnubscriptions()
        throws MailingListsException;
    
    /**
     * Get pending message. 
     * 
     * @param id message identifier.
     * @return the message.
     * @throws MailingListsException
     */
    public Message getPendingMessage(String id)
        throws MailingListsException;
    
    /**
     * Get pending tasks.
     * 
     * @return the list of tasks identifiers
     * @throws MailingListsException
     */
    public List<String> getNewPendingTasks() throws MailingListsException;
    
    /**
     * Get pending message. 
     * 
     * @param id message identifier.
     * @return the message.
     * @throws MailingListsException
     */
    public TaskType getPendingTaskType(String id)
        throws MailingListsException;
    
    /**
     * Accept message.
     * 
     * @param id message identifier.
     * @throws MailingListsException 
     */
    public void acceptMessage(String id) throws MailingListsException;
    
    /**
     * Reject message.
     * 
     * @param id message identifier.
     * @throws MailingListsException 
     */
    public void rejectMessage(String id) throws MailingListsException;
    
    /**
     * Discard message.
     * 
     * @param id message identifier.
     * @throws MailingListsException 
     */
    public void discardMessage(String id) throws MailingListsException;
    
    /**
     * Reject subscription request.
     * 
     * @param id message identifier.
     * @throws MailingListsException 
     */
    public void rejectSubscription(String id) throws MailingListsException;
    
    /**
     * Accept subscription request.
     * 
     * @param id message identifier.
     * @throws MailingListsException 
     */
    public void acceptSubscription(String id) throws MailingListsException;
    
    /**
     * Reject unsubscription request.
     * 
     * @param id message identifier.
     * @throws MailingListsException 
     */
    public void rejectUnsubscription(String id) throws MailingListsException;
    
    /**
     * Accept unsubscription request.
     * 
     * @param id message identifier.
     * @throws MailingListsException 
     */
    public void acceptUnsubscription(String id) throws MailingListsException;
    
    /**
     * Post an e-mail message to the list.
     * 
     * @param message the message.
     * @throws MailingListsException
     */
    public void postMessage(Message message) 
        throws MailingListsException;
    
    /**
     * Returns the e-mail address used for subscribing to the list.
     * 
     * @return the e-mail address used for subscribing to the list.
     * @throws MailingListsException
     */
    public String getSubscriptionAddress()
        throws MailingListsException;    
    
    /**
     * Returns the e-mail address used for posting to the list.
     * 
     * @return the e-mail address used for posting to the list.
     * @throws MailingListsException
     */
    public String getPostingAddress()
        throws MailingListsException;
    
    /**
     * Returns the location of the list member's self-service WWW interface.
     * 
     * @return the location of the list member's self-service WWW interface.
     * @throws MailingListsException
     */
    public URL getMemberInterfaceLocation()
        throws MailingListsException;
    
    /**
     * Returns the location of the list administrators WWW interface.
     * 
     * @return the location of the list administrators WWW interface.
     * @throws MailingListsException
     */
    public URL getAdministratorInterfaceLocation()
        throws MailingListsException;
    
    /**
     * Returns the preferred language for the list.
     * 
     * @return the preferred language for the list.
     * @throws MailingListsException
     */
    public Locale getPreferredLanguage()
        throws MailingListsException;
}
