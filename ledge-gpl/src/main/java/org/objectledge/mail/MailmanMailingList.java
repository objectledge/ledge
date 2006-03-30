
package org.objectledge.mail;

import java.net.MalformedURLException;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.parameters.Parameters;

/**
 * Fax manager implementation based on gnu.hylafax.* library.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: MailmanMailingList.java,v 1.1 2006-03-30 09:00:16 pablo Exp $
 */
public class MailmanMailingList implements MailingList
{
    /** logging facility */
    private Logger logger;

    /** list manager */
    private MailmanMailingListsManager manager;
    
    /** list name */
    private String listName;

    /** mailman admin password */
    private String adminPassword;
    
    /**
     * Mailman mailing list constructor.
     * 
     * @param config component configuration.
     * @param logger the logger.
     */
    public MailmanMailingList(Logger logger, MailmanMailingListsManager manager, 
        String listName, String adminPassword)
        throws MalformedURLException
    {
        this.logger = logger;
        this.manager = manager;
        this.listName = listName;
        this.adminPassword = adminPassword;
    }

    /**
     * {@inheritDoc}
     */
    public int addMember(String address, String name, String password, boolean digest, boolean ignoreCreationPolicy) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public void addMemberAddress(String oldAddress, String newAddress) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public void changeMemberAddress(String oldAddress, String newAddress) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public int deleteMember(String address, boolean ignoreDeletingPolicy) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getMembers() throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public Parameters getOptions() throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public void setOptions(Parameters options) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(String password) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }
}