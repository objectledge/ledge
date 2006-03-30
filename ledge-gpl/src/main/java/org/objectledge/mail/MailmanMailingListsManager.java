
package org.objectledge.mail;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;

/**
 * Fax manager implementation based on gnu.hylafax.* library.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: MailmanMailingListsManager.java,v 1.1 2006-03-30 09:00:16 pablo Exp $
 */
public class MailmanMailingListsManager implements MailingListsManager
{
    /** logging facility */
    private Logger logger;

    /** mailman rcp address */
    private String address;

    /** mailman admin login */
    private String adminLogin;

    /** mailman admin password */
    private String adminPassword;
    
    /** rpc client */
    private XmlRpcClient client;

    /**
     * Ledge component constructor.
     * 
     * @param config component configuration.
     * @param logger the logger.
     */
    public MailmanMailingListsManager(Configuration config, Logger logger)
        throws MalformedURLException
    {
        this.logger = logger;
        address = config.getChild("address").getValue("http://localhost/mailman/RPC2");
        adminLogin = config.getChild("login").getValue("top");
        adminPassword = config.getChild("password").getValue("secret");
        client = new XmlRpcClient(address);
        client.setBasicAuthentication(adminLogin, adminPassword);
    }
    
    /**
     * Standalone constructor.
     * 
     * @param logger the logger.
     * @param address mailman rcp address.
     * @param login mailman admin login.
     * @param password mailman admin password.
     */
    public MailmanMailingListsManager(Logger logger, String address, String login, String password)
        throws MalformedURLException
    {
        this.logger = logger;
        this.address = address;
        this.adminLogin = login;
        this.adminPassword = password;
        client = new XmlRpcClient(address);
        client.setBasicAuthentication(adminLogin, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    public MailingList createList(String name, String domain, boolean moderated, 
        String[] administrators, String password, 
        boolean notify, Locale locale) throws MailingListsException
    {
        Object[] params = new Object[]{
            adminPassword, name, domain, moderated, administrators,
            password, notify, locale.getDisplayLanguage()};
        Vector vector = new Vector();
        for(Object ob: params)
        {
            vector.add(ob);
        }
        Object result = null;
        try
        {
            result = client.execute("Mailman.createList", vector);
        }
        catch(Exception e)
        {
            throw new MailingListsException("failed to create list", e);
        }
        // there should go exception recognition 
        if(result instanceof XmlRpcException)
        {
            throw new MailingListsException("failed to create list", (XmlRpcException)result);
        }
        
        System.out.println("KLASA WYNIKU: "+result.getClass().getName());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteList(MailingList list) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public MailingList getList(String name, String password) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public List<MailingList> getLists() throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    public List<MailingList> getPublicLists() throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }
}