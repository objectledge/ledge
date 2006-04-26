package org.objectledge.mail;

import java.util.Iterator;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.jcontainer.dna.Logger;
import org.objectledge.scheduler.Job;

/**
 * A job that checks the start and expire date of the polls
 */
public class MailingListsProcessor
    extends Job
{
    // instance variables ////////////////////////////////////////////////////

    private MailingListsManager manager;

    private Logger logger;
    
    private MailingListsNotificationListener[] listeners;
    
    /**
     *
     */
    public MailingListsProcessor(Logger logger, MailingListsManager manager, 
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
        if(manager.getStatus() == MailingListsManager.Status.OPERATIONAL)
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
        else
        {
            logger.warn("MailingListManager not operational - no work done");
        }
    }
    
    private void checkNewMessages() throws MailingListsException
    {
        try
        {
            Store store = manager.getMessageStore();
            store.connect();
            try
            {
                Folder folder = store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                try
                {
                    Message messages[] = folder.getMessages();
                    for (Message message:messages)
                    {
                        MailingList list = manager.getList(message);
                        if(list != null)
                        {
                            for(MailingListsNotificationListener listener: listeners)
                            {
                                listener.newMessageAdded(list.getName(), message);
                            }
                        }
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
                finally
                {
                    folder.close(true);
                }
            }
            finally
            {
                store.close();
            }
        }
        catch(Exception e)
        {
            throw new MailingListsException("failed to process new messages", e);
        }
    }    

    private void checkPendingTasks() throws MailingListsException
    {
        List<String> names = manager.getLists();
        for(String listName: names)
        {
            MailingList list = manager.getList(listName);
            for(String id : list.getNewPendingTasks())
            {
                Message message = list.getPendingMessage(id);
                switch(list.getPendingTaskType(id))
                {
                case PENDING_POST:
                    for(MailingListsNotificationListener listener: listeners)
                    {
                        listener.newPendingMessageAdded(listName, message);
                    }
                    break;
                case PENDING_SUBSCRIPTION:
                    for(MailingListsNotificationListener listener: listeners)
                    {
                        listener.newPendingSubscriptionAdded(listName, message);
                    }
                    break;
                case PENDING_UNSUBSCRIPTION:
                    for(MailingListsNotificationListener listener: listeners)
                    {
                        listener.newPendingUnsubscriptionAdded(listName, message);
                    }
                    break;                    
                }
            }
        }
    }    
}
