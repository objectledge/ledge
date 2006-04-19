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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


/**
 * Mailman mailing list.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: MailmanMailingList.java,v 1.10 2006-04-19 11:04:41 rafal Exp $
 */
public class MailmanMailingList implements MailingList
{
    /** option keys */
    
    private static final String SUBSCRIBE_POLICY = "subscribe_policy";
    
    private static final String POSTING_MODERATION = "default_member_moderation";
    
    private static final String HOST_NAME = "host_name";

    /** options values */
    
    private static final int OPTION_REQUIRE_CONFIRM = 1;
    
    private static final int OPTION_REQUIRE_APPROVAL = 2;
    
    private static final int OPTION_REQUIRE_CONFIRM_AND_APPROVAL = 3;
    
    private static final int OPTION_POSTING_NOT_MODERATED = 0;
    
    private static final int OPTION_POSTING_MODERATED = 1;

    private static final int ML_ACTION_APPROVE = 1;
    private static final int ML_ACTION_REJECT = 2;
    private static final int ML_ACTION_DISCARD = 3;
    private static final int ML_ACTION_SUBSCRIBE = 4;
    private static final int ML_ACTION_UNSUBSCRIBE = 5;
    
    static final int TASK_TYPE_PENDING_POST = 1;
    static final int TASK_TYPE_PENDING_SUBSCRIPTION = 2;
    static final int TASK_TYPE_PENDING_UNSUBSCRIPTION = 3;
    
    /** list manager */
    private MailmanMailingListsManager manager;
    
    /** list name */
    private String listName;

    /** mailman admin password */
    private String adminPassword;
    
    /**
     * Mailman mailing list constructor.
     *
     * @param manager the ml manager.
     * @param listName the listName.
     * @param adminPassword the list administrator password.
     */
    public MailmanMailingList(MailmanMailingListsManager manager, 
        String listName, String adminPassword)
    {
        this.manager = manager;
        this.listName = listName;
        this.adminPassword = adminPassword;
    }

    /**
     * {@inheritDoc}
     */
    public MailingList.OperationStatus addMember(String address, String name, String password, 
        boolean digest, boolean ignoreCreationPolicy)
        throws MailingListsException
    {
        return manager.addMember(listName, adminPassword, address, name, password, digest, ignoreCreationPolicy);
    }

    /**
     * {@inheritDoc}
     */
    public void addMemberAddress(String oldAddress, String newAddress)
        throws MailingListsException
    {
        manager.changeMemberAddress(listName, adminPassword, oldAddress, newAddress, true);
    }

    /**
     * {@inheritDoc}
     */
    public void changeMemberAddress(String oldAddress, String newAddress)
        throws MailingListsException
    {
        manager.changeMemberAddress(listName, adminPassword, oldAddress, newAddress, false);
    }

    /**
     * {@inheritDoc}
     */
    public MailingList.OperationStatus deleteMember(String address, boolean ignoreDeletingPolicy)
        throws MailingListsException
    {
        return manager.deleteMember(listName, adminPassword, address, ignoreDeletingPolicy); 
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getMembers() throws MailingListsException
    {
        return manager.getMembers(listName, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(String password) throws MailingListsException
    {
        this.adminPassword = manager.setPassword(listName, adminPassword, password); 
    }

    /**
     * {@inheritDoc}
     */
    public List getPendingPosts() throws MailingListsException
    {
        return manager.getPendingPosts(listName, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    public List getNewPendingTasks() throws MailingListsException
    {
        return manager.getNewPendingTasks(listName, adminPassword);
    }
    
    /**
     * {@inheritDoc}
     */
    public List getPendingSubscriptions() throws MailingListsException
    {
        return manager.getPendingSubscriptions(listName, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    public List getPendingUnubscriptions() throws MailingListsException
    {
        return manager.getPendingUnsubscriptions(listName, adminPassword);
    }
    
    /**
     * {@inheritDoc}
     */
    public Message getPendingMessage(Object id) throws MailingListsException
    {
        return manager.getPendingTask(listName, adminPassword, id);
    }

    /**
     * 
     */
    public MailingList.TaskType getPendingTaskType(Object id) throws MailingListsException
    {
        Integer type = manager.getPendingTaskType(listName, adminPassword, id);
        switch(type)
        {
        case TASK_TYPE_PENDING_POST:
            return MailingList.TaskType.PENDING_POST;
        case TASK_TYPE_PENDING_SUBSCRIPTION:
            return MailingList.TaskType.PENDING_SUBSCRIPTION;
        case TASK_TYPE_PENDING_UNSUBSCRIPTION:
            return MailingList.TaskType.PENDING_UNSUBSCRIPTION;
        default:
            throw new IllegalStateException("invalid pending task type: "+type);            
        }
    }
    
    /**
     * 
     */
    public void postMessage(Message message) throws MailingListsException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            message.writeTo(baos);
            manager.postMessage(listName, adminPassword, 
                baos.toString("UTF-8"));
        }
        catch(Exception e)
        {
            throw new MailingListsException("failed to serialize message", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public MailingList.SubscriptionPolicy getSubscriptionPolicy() throws MailingListsException
    {
        Integer value = (Integer)manager.getOption(listName, adminPassword, SUBSCRIBE_POLICY);
        switch(value)
        {
            case OPTION_REQUIRE_APPROVAL:
                return MailingList.SubscriptionPolicy.REQUIRE_APPROVAL;
            case OPTION_REQUIRE_CONFIRM:
                return MailingList.SubscriptionPolicy.REQUIRE_CONFIRMATION;
            case OPTION_REQUIRE_CONFIRM_AND_APPROVAL:
                return MailingList.SubscriptionPolicy.REQUIRE_CONFIRMATION_AND_APPROVAL;
            default:
                throw new IllegalStateException("invalid subscription policy option: "+value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPostingModerated() throws MailingListsException
    {
        Integer value = (Integer)manager.getOption(listName, adminPassword, POSTING_MODERATION);
        switch(value)
        {
            case OPTION_POSTING_MODERATED:
                return true;
            case OPTION_POSTING_NOT_MODERATED:
                return false;
            default:
                throw new IllegalStateException("invalid posting moderation option: "+value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setPostingModerated(boolean moderated) throws MailingListsException
    {
        if(moderated)
        {
            manager.setOption(listName, adminPassword, POSTING_MODERATION, ""+OPTION_POSTING_MODERATED);
        }
        else
        {
            manager.setOption(listName, adminPassword, POSTING_MODERATION, ""+OPTION_POSTING_NOT_MODERATED);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSubscriptionPolicy(MailingList.SubscriptionPolicy policy) throws MailingListsException
    {
        switch(policy)
        {
            case REQUIRE_APPROVAL:
                manager.setOption(listName, adminPassword, SUBSCRIBE_POLICY, ""+OPTION_REQUIRE_APPROVAL);
                return;
            case REQUIRE_CONFIRMATION:
                manager.setOption(listName, adminPassword, SUBSCRIBE_POLICY, ""+OPTION_REQUIRE_CONFIRM);
                return;
            case REQUIRE_CONFIRMATION_AND_APPROVAL:
                manager.setOption(listName, adminPassword, SUBSCRIBE_POLICY, ""+OPTION_REQUIRE_CONFIRM_AND_APPROVAL);
                return;
            default:
                throw new IllegalStateException("unknown policy: "+policy);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void acceptMessage(Object id) throws MailingListsException
    {
        manager.handleModeratorRequest(listName, adminPassword, id, ML_ACTION_APPROVE);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rejectMessage(Object id) throws MailingListsException
    {
        manager.handleModeratorRequest(listName, adminPassword, id, ML_ACTION_REJECT);
    }
    
    /**
     * {@inheritDoc}
     */
    public void discardMessage(Object id) throws MailingListsException
    {
        manager.handleModeratorRequest(listName, adminPassword, id, ML_ACTION_DISCARD);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rejectSubscription(Object id) throws MailingListsException
    {
        manager.handleModeratorRequest(listName, adminPassword, id, ML_ACTION_REJECT);
    }
    
    /**
     * {@inheritDoc}
     */
    public void acceptSubscription(Object id) throws MailingListsException
    {
        manager.handleModeratorRequest(listName, adminPassword, id, ML_ACTION_SUBSCRIBE);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rejectUnsubscription(Object id) throws MailingListsException
    {
        manager.handleModeratorRequest(listName, adminPassword, id, ML_ACTION_REJECT);
    }
    
    /**
     * {@inheritDoc}
     */
    public void acceptUnsubscription(Object id) throws MailingListsException
    {
        manager.handleModeratorRequest(listName, adminPassword, id, ML_ACTION_UNSUBSCRIBE);
    }
    
    
    /**
     * Returns the e-mail address used for subscribing to the list.
     * 
     * @return the e-mail address used for subscribing to the list.
     * @throws MailingListsException
     */
    public String getSubscriptionAddress()
        throws MailingListsException
    {
        String listDomain = (String)manager.getOption(listName, adminPassword, HOST_NAME);
        return listName + "-subscribe@" + listDomain;
    }    
    
    /**
     * Returns the e-mail address used for posting to the list.
     * 
     * @return the e-mail address used for posting to the list.
     * @throws MailingListsException
     */
    public String getPostingAddress()
        throws MailingListsException
    {
        String listDomain = (String)manager.getOption(listName, adminPassword, HOST_NAME);
        return listName + "@" + listDomain;
    }
    
    /**
     * Returns the location of the list member's self-service WWW interface.
     * 
     * @return the location of the list member's self-service WWW interface.
     * @throws MailingListsException
     */
    public URL getMemberInterfaceLocation()
        throws MailingListsException
    {
        String listDomain = (String)manager.getOption(listName, adminPassword, HOST_NAME);
        String interfaceBaseURL = manager.getInterfaceBaseURL(listDomain);
        try
        {
            return new URL(interfaceBaseURL + "listinfo/" + listName);
        }
        catch(MalformedURLException e)
        {
            throw new MailingListsException("invalid URL specifier returned by mailman "+ interfaceBaseURL);
        }
    }
    
    /**
     * Returns the location of the list administrators WWW interface.
     * 
     * @return the location of the list administrators WWW interface.
     * @throws MailingListsException
     */
    public URL getAdministratorInterfaceLocation()
        throws MailingListsException
    {
        String listDomain = (String)manager.getOption(listName, adminPassword, HOST_NAME);
        String interfaceBaseURL = manager.getInterfaceBaseURL(listDomain);
        try
        {
            return new URL(interfaceBaseURL + "admin/" + listName);
        }
        catch(MalformedURLException e)
        {
            throw new MailingListsException("invalid URL specifier returned by mailman "+ interfaceBaseURL);
        }
    }
}