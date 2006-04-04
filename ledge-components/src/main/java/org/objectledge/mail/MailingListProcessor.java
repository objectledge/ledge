package org.objectledge.mail;

import java.util.Iterator;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.jcontainer.dna.Logger;
import org.objectledge.scheduler.Job;

/**
 * A job that checks the start and expire date of the polls
 *
 */
public class MailingListProcessor
    extends Job
{
    /** List-Id header as defined by RFC2919 */
    private static final String LIST_ID_HEADER_NAME = "List-Id";
    
    /** List-Post header as defined by RFC2369 */
    private static final String LIST_POST_HEADER_NAME = "List-Post";

    
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
            List<Message> messages = manager.getNewMessages();
            for(Message message: messages)
            {
                String listName = getListName(message);
                if(listName != null)
                {
                    for(MailingListsNotificationListener listener: listeners)
                    {
                        listener.newMessageAdded(listName, message);
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
     * Dedect which mailing list the message belongs to.
     * 
     * Unfortunately Mailman is not accepting the List-Id headers it generates as list 
     * identifiers - it creating them by joing local list name (internal identifier) with list 
     * domain using a dot, while at the same time it accepts dots in local list names. This makes 
     * it impossible to extract local list name from the header in a reliable way. On the other
     * hand the List-Post header contains always <mailto:LOCALNAME@DOMAIN> which may be parsed
     * reliably. Just make sure the list is configured to put this header in the messages.
     * 
     * @param message the message.
     * @return the list name.
     * @throws MessagingException if there is a problem parsing message headers.
     */
    private String getListName(Message message) throws MessagingException
    {
        String[] listPostHeader = message.getHeader(LIST_POST_HEADER_NAME);
        if(listPostHeader != null && listPostHeader.length > 0)
        {
            String header = listPostHeader[0]; 
            if(header.contains("<mailto:") && header.contains(">"))
            {
                int startIndex = header.lastIndexOf("<mailto:");
                int endIndex = header.lastIndexOf("@");
                return header.substring(startIndex+8, endIndex);
            }
        }
        return null;
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
