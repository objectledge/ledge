package org.objectledge.mail;

import java.util.Iterator;
import java.util.List;

import javax.mail.Message;

import org.jcontainer.dna.Logger;
import org.objectledge.scheduler.Job;

/**
 * A job that checks the start and expire date of the polls
 *
 */
public class MailingListProcessor
    extends Job
{
    // instance variables ////////////////////////////////////////////////////

    private MailingListsManager manager;

    private Logger logger;
    
    private MailingListsNotificationListener[] listeners;
    
    /**
     *
     */
    public MailingListProcessor(Logger logger, MailingListsManager manager, 
        MailingListsNotificationListener[] listeners)
    {
        this.logger = logger;
        this.manager = manager;
        this.listeners = listeners;
    }    

    /**
     * Performs the mainteance.
     */
    public void run(String[] args)
    {   
        try
        {
            checkPendingTasks();
            checkNewMessages();
        }
        catch(Exception e)
        {
            logger.error("failed to complete job", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void checkNewMessages() throws MailingListsException
    {
        try
        {
            List<Message> list = manager.getNewMessages();
            for(Message message: list)
            {
                String[] listPostHeader = message.getHeader(manager.getListPostHeaderName());
                if(listPostHeader != null && listPostHeader.length > 0)
                {
                    String header = listPostHeader[0]; 
                    if(header.contains("<mailto:") && header.contains(">"))
                    {
                        int startIndex = header.lastIndexOf("<mailto:");
                        int endIndex = header.lastIndexOf("@");
                        String listName = header.substring(startIndex+8, endIndex);
                        for(MailingListsNotificationListener listener: listeners)
                        {
                            listener.newMessageAdded(listName, message);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new MailingListsException("failed to fetch new messages", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void checkPendingTasks() throws MailingListsException
    {
        List<String> names = manager.getLists();
        for(String listName: names)
        {
            MailingList list = manager.getList(listName, "");
            List ids = list.getNewPendingTasks();
            Iterator it = ids.iterator();
            while(it.hasNext())
            {
                Object id = it.next();
                Object type = list.getPendingTaskType(id);
                Message message = list.getPendingMessage(id);
                if(type.equals(MailingList.PENDING_POST))
                {
                    for(MailingListsNotificationListener listener: listeners)
                    {
                        listener.newPendingMessageAdded(listName, message);
                    }
                    continue;
                }
                if(type.equals(MailingList.PENDING_SUBSCRIPTION))
                {
                    for(MailingListsNotificationListener listener: listeners)
                    {
                        listener.newPendingSubscriptionAdded(listName, message);
                    }
                    continue;
                }
                if(type.equals(MailingList.PENDING_UNSUBSCRIPTION))
                {
                    for(MailingListsNotificationListener listener: listeners)
                    {
                        listener.newPendingUnsubscriptionAdded(listName, message);
                    }
                    continue;
                }
            }
        }
    }
}
