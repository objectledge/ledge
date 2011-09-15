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
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.utils.StringUtils;

/**
 * Mailman mailing list manager implementation.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: MailmanMailingListsManager.java,v 1.32 2006-05-15 11:57:23 rafal Exp $
 */
public class MailmanMailingListsManager implements MailingListsManager
{  
    /** List-Post header as defined by RFC2369 */
    private static final String LIST_POST_HEADER_NAME = "List-Post";
    
    /** logging facility */
    private Logger logger;
    
    /** mail system */
    private MailSystem mailSystem;

    /** mailman rcp address */
    private String address;

    /** mailman admin password */
    private String adminPassword;
    
    /** rpc client */
    private XmlRpcClient client;
    
    /** last id map */
    private HashMap<String, Integer> lastIdMap;

    /** system monitoring address */
    private String monitoringAddress;
    
    /** system monitoring mail session */
    private String monitoringSessionName;
    
    /**
     * Ledge component constructor.
     * 
     * @param config component configuration.
     * @param logger the logger.
     */
    public MailmanMailingListsManager(Configuration config, Logger logger, MailSystem mailSystem)
        throws MalformedURLException
    {
        this.logger = logger;
        this.mailSystem = mailSystem;
        address = config.getChild("address").getValue("http://localhost/mailman/RPC2");
        adminPassword = config.getChild("password").getValue("secret");
        monitoringAddress = config.getChild("monitoring_address").getValue("");
        monitoringSessionName = config.getChild("monitoring_session").getValue("");
        lastIdMap = new HashMap<String, Integer>();
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL(address));
        client = new XmlRpcClient();
        client.setConfig(clientConfig);
    }
    
    /**
     * Standalone constructor.
     * 
     * @param logger the logger.
     * @param address mailman rcp address.
     * @param login mailman admin login.
     * @param password mailman admin password.
     */
    public MailmanMailingListsManager(Logger logger, String address, String password)
        throws MalformedURLException
    {
        this.logger = logger;
        this.address = address;
        this.adminPassword = password;
        lastIdMap = new HashMap<String, Integer>();
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL(address));
        client = new XmlRpcClient();
        client.setConfig(clientConfig);
    }
    
    public MailingListsManager.Status getStatus()
    {
        try
        {
            getAvailableLocales();
            checkMessageStore();
            return MailingListsManager.Status.OPERATIONAL;
        }
        catch(MailingListsException e)
        {
            logger.error("The manager is not operational", e);
            return MailingListsManager.Status.UNOPERATIONAL;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String createList(String name, String domain, 
        String[] administrators, String password, 
        boolean notify, Locale locale, boolean moderated) throws MailingListsException
    {
        String newPassword = executeMethod("Mailman.createList", adminPassword, name,
            domain, "", true, administrators, password, notify, vector(locale.toString()));
        MailingList list = getList(name, newPassword);
        if(monitoringAddress.length() > 0)
        {
            list.addMember(monitoringAddress, "Mailiman - Ledge integration", "", false, true,
                false, false);
        }
        list.setPostingModerated(moderated);
        return newPassword;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteList(String name, boolean deleteArchives) throws MailingListsException
    {
        Boolean result = executeMethod("Mailman.deleteList", adminPassword, name, deleteArchives);
        if(!result)
        {
            throw new MailingListsException("operation failed");
        }
    }

    /**
     * {@inheritDoc}
     */
    public MailingList getList(String name, String password) throws MailingListsException
    {
        return new MailmanMailingList(this, name, password);
    }

    /**
     * {@inheritDoc}
     */
    public MailingList getList(String name) throws MailingListsException
    {
        return new MailmanMailingList(this, name, adminPassword);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<String> getLists() throws MailingListsException
    {
        return getLists("");
    }
    
    private List<String> getLists(String filter)
        throws MailingListsException
    {
        List<List<String>> infos = executeMethod("Mailman.listAllLists", adminPassword, filter);
        List<String> names = new ArrayList<String>(infos.size());
        for(List<String> info : infos)
        {
            names.add(info.get(0));
        }
        return names;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPublicLists() throws MailingListsException
    {
        return getPublicLists("");
    }
    
    /**
     * {@inheritDoc}
     */
    public List<String> getPublicLists(String filter) throws MailingListsException
    {
        List<List<String>> result = executeMethod("Mailman.listAdvertisedLists", filter);
        List<String> names = new ArrayList<String>();
        for(List<String> el : result)
        {
            names.add(el.get(0));
        }
        return names;
    }

    /**
     * {@inheritDoc}
     */    
    public List<Locale> getAvailableLocales()
        throws MailingListsException
    {
        Collection<List<String>> list = executeMethod("Mailman.getAvailableLocales", adminPassword);
        ArrayList<Locale> codes = new ArrayList<Locale>();
        for(List<String> innerList : list)
        {
            codes.add(StringUtils.getLocale(innerList.get(0)));
        }
        return codes;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getAvailableDomains()
        throws MailingListsException
    {
        Collection<String> list = executeMethod("Mailman.getAvailableDomains", adminPassword);        
        ArrayList<String> domains = new ArrayList<String>();
        for(String item : list)
        {
            domains.add(item);
        }
        return domains;
    }
    
    private void checkMessageStore()
        throws MailingListsException
    {
        if(monitoringAddress.length() > 0)
        {
            Session session = mailSystem.getSession(monitoringSessionName);
            try
            {
                Store store = session.getStore();
                store.connect();
                try
                {
                    Folder folder = store.getFolder("INBOX");
                    folder.open(Folder.READ_WRITE);
                    try
                    {
                        folder.getMessages();
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
                throw new MailingListsException("failed to check new messages", e);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */    
    public Store getMessageStore() throws MailingListsException
    {
        try
        {
            Session session = mailSystem.getSession(monitoringSessionName);
            return session.getStore();
        }
        catch(Exception e)
        {
            throw new MailingListsException("failed to access message store", e);
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
    public MailingList getList(Message message) throws MailingListsException
    {
        try
        {
            String listName = getListName(message);
            if(listName != null)
            {
                return getList(listName);
            }
        }
        catch(MessagingException e)
        {
            logger.error("failed to parse message", e);
        }
        return null;
    }
    
    // package private operations
    
    /**
     * {@inheritDoc}
     */
    MailingList.OperationStatus addMember(String listName, String adminPassword, 
        String address, String name, String password, 
        boolean digest, boolean ignoreCreationPolicy, boolean acknowledge, boolean notifyAdmins)
            throws MailingListsException
    {
        try
        {
            Boolean result = executeMethod("Mailman.addMember", listName, adminPassword, address,
                name, password, digest, ignoreCreationPolicy, acknowledge, notifyAdmins);
            if(result)
            {
                return MailingList.OperationStatus.COMPLETED;
            }
            else
            {
                throw new MailingListsException("operation failed");
            }
        }
        catch(NeedApprovalException e)
        {
            return MailingList.OperationStatus.NEEDS_APPROVAL;
        }
        catch(NeedConfirmationException e)
        {
            return MailingList.OperationStatus.NEEDS_CONFIRMATION;
        }
    }

    /**
     * {@inheritDoc}
     */
    void changeMemberAddress(String listName, String adminPassword, 
        String oldAddress, String newAddress, boolean keepOld) throws MailingListsException
    {
        Boolean result = executeMethod("Mailman.changeMemberAddress", listName, adminPassword, address, oldAddress, 
            newAddress, keepOld);
        if(!result)
        {
            throw new MailingListsException("operation failed");
        }
    }

    /**
     * {@inheritDoc}
     */
    MailingList.OperationStatus deleteMember(String listName, String adminPassword, String address,
        boolean ignoreDeletingPolicy, boolean acknowledge, boolean notifyAdmins)
        throws MailingListsException
    {
        if(address.equals(monitoringAddress))
        {
            throw new MailingListsException("monitoring account cannot be unsubscribed from a list");
        }
        try
        {
            Boolean result = executeMethod("Mailman.deleteMember", listName, adminPassword,
                address, ignoreDeletingPolicy, acknowledge, notifyAdmins);
            if(result)
            {
                return MailingList.OperationStatus.COMPLETED;
            }
            else
            {
                throw new MailingListsException("operation failed");
            }
        }
        catch(NeedApprovalException e)
        {
            return MailingList.OperationStatus.NEEDS_APPROVAL;
        }
        catch(NeedConfirmationException e)
        {
            return MailingList.OperationStatus.NEEDS_CONFIRMATION;
        }
    }

    /**
     * {@inheritDoc}
     */
    List<String> getMembers(String listName, String adminPassword)
        throws MailingListsException
    {
        List<String> members = executeMethod("Mailman.getMembers", listName, adminPassword);
        members.remove(monitoringAddress);
        return members;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("cast")
    <T> T getOption(String listName, String adminPassword, String key)
        throws MailingListsException
    {
        Map<String, T> result = executeMethod("Mailman.getOptions", listName, adminPassword,
            new String[] { key });
        return (T)result.get(key);
    }

    /**
     * {@inheritDoc}
     */
    <T> void setOption(String listName, String adminPassword, 
        String key, T value) throws MailingListsException
    {
        Hashtable<String, T> map = new Hashtable<String, T>();
        map.put(key, value);
        Boolean result = executeMethod("Mailman.setOptions", listName, adminPassword, map);
        if(!result)
        {
            throw new MailingListsException("failed to set options");
        }
    }
    
    void addOptionValue(String listName, String adminPassword, String key, String value)
        throws MailingListsException
    {
        List<String> values = getOption(listName, adminPassword, key);
        values.add(value);
        setOption(listName, adminPassword, key, values);
    }

    void removeOptionValue(String listName, String adminPassword, String key, Object value)
        throws MailingListsException
    {
        List<String> values = getOption(listName, adminPassword, key);
        values.remove(value);
        setOption(listName, adminPassword, key, values);
    }
    
    /**
     * {@inheritDoc}
     */
    String setPassword(String listName, String adminPassword, 
        String password) throws MailingListsException
    {
        String result = executeMethod("Mailman.resetListPassword", listName, adminPassword,
            password);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    List<String> getPendingPosts(String listName, String adminPassword) throws MailingListsException
    {
        List<Integer> result = executeMethod("Mailman.getPendingMessages", listName, adminPassword);
        return toStringList(result);
    }

    /**
     * {@inheritDoc}
     */
    List<String> getPendingSubscriptions(String listName, String adminPassword)
        throws MailingListsException
    {
        List<Integer> result = executeMethod("Mailman.getPendingSubscriptions", listName,
            adminPassword);
        return toStringList(result);
    }

    /**
     * {@inheritDoc}
     */
    List<String> getPendingUnsubscriptions(String listName, String adminPassword)
        throws MailingListsException
    {
        List<Integer> result = executeMethod("Mailman.getPendingUnsubscriptions", listName,
            adminPassword);
        return toStringList(result);
    }    

    List<String> getNewPendingTasks(String listName, String adminPassword)
        throws MailingListsException
    {
        int lastId = getLastId(listName);
        List<Integer> list = executeMethod("Mailman.getNewPendingTasks", listName, adminPassword,
            lastId);

        if(!list.isEmpty())
        {
            setLastId(listName, list.get(list.size() - 1));
        }
        return toStringList(list);
    }

    Message getPendingTask(String listName, String adminPassword, String id)
        throws MailingListsException
    {
        String msg = executeMethod("Mailman.getPendingTask", listName, adminPassword, Integer
            .parseInt(id));

        Session session = mailSystem.getSession();
        try
        {
            InputStream is = new ByteArrayInputStream(msg.getBytes("UTF-8"));
            MimeMessage message = new MimeMessage(session, is);
            return message;
        }
        catch(Exception e)
        {
            throw new MailingListsException("Failed to deserialize message", e);
        }
    }
        
    Integer getPendingTaskType(String listName, String adminPassword, String id)
        throws MailingListsException
    {
        Integer result = executeMethod("Mailman.getPendingTaskType", listName, adminPassword,
            Integer.parseInt(id));
        return result;
    }
    
    void handleModeratorRequest(String listName, String adminPassword,
        String id, int command, String comment)
            throws MailingListsException
    {
        Boolean result;
        if(comment != null)
        {
            result = executeMethod("Mailman.handleModeratorRequest", listName, adminPassword, Integer.parseInt(id), command, comment);
        }
        else
        {
            result = executeMethod("Mailman.handleModeratorRequest", listName, adminPassword, Integer.parseInt(id), command);
        }
        if(!result)
        {
            throw new MailingListsException("failed to set options");
        }
    }
    
    void postMessage(String listName, String adminPassword, String message)
        throws MailingListsException
    {
        Boolean result = executeMethod("Mailman.postMessage", listName, adminPassword, message);
        if(!result)
        {
            throw new MailingListsException("failed to set options");
        }
    }

    String getInterfaceBaseURL(String domain)
        throws MailingListsException
    {
        String result = executeMethod("Mailman.getInterfaceBaseURL", adminPassword, domain);
        return result;
    }
    
    // -- private methods -----------------------------------------------------------------------
      
    /**
     * RPC method executor.
     * It resolve XmlRpcException codes into Java Exceptions.
     * 
     * @param method name of the method to call.
     * @param parameters arguments of the method.
     * @return result of method invocation.
     */
    @SuppressWarnings("unchecked")
    private <T> T executeMethod(String method, Object ... parameters)
        throws MailingListsException
    {
        T result = null;
        try
        {
            result = (T)client.execute(method, getParameters(parameters));
        }
        catch(ClassCastException e)
        {
            throw new MailingListsException("unexpected response type", e);
        }
        catch(Exception e)
        {
            throw new MailingListsException("failed to invoke rpc method", e);
        }
        if(result instanceof XmlRpcException)
        {
            throw unwrapException((XmlRpcException)result);
        }
        if(result == null)
        {
            throw new MailingListsException("null result of rpc invocation");
        }
        return result;
    }
    
    private static final Map<Integer,Class<? extends MailingListsException>> exceptions = 
        new HashMap<Integer,Class<? extends MailingListsException>>();
    
    static
    {
        exceptions.put(-32501, NeedConfirmationException.class);
        exceptions.put(-32502, NeedApprovalException.class);
        exceptions.put(-32503, MailingListsAuthenticationException.class);
        exceptions.put(-32504, InvalidListNameException.class);
        exceptions.put(-32505, ListAlreadyExistsException.class);
        exceptions.put(-32506, LostAdministrativeRequestException.class);
    }
    
    private MailingListsException unwrapException(XmlRpcException xmlRpcEx)
    {
        Class<? extends MailingListsException> exClass = exceptions.containsKey(xmlRpcEx.code) ? 
            exceptions.get(xmlRpcEx.code) : MailingListsException.class;
        try
        {
            Constructor<? extends MailingListsException> exCtor = exClass
                .getConstructor(new Class[] { String.class });
            return exCtor.newInstance(xmlRpcEx.getMessage());
        }
        catch(Exception e)
        {
            return new MailingListsException("failed to reflecively report exception of class "
                + exClass.getName(), e); 
        }
    }
    
    /**
     * Convert list of objects into vector.
     * 
     * @param params list of objects.
     * @return vector of objects.
     */
    private <T> Vector<T> getParameters(T[] params)
    {
        Vector<T> vector = new Vector<T>();
        for(T ob: params)
        {
            vector.add(ob);
        }
        return vector;
    }
    
    private <T> Vector<T> vector(T ... params)
    {
        Vector<T> vector = new Vector<T>();
        for(T obj: params)
        {
            vector.add(obj);
        }
        return vector;        
    }
    
    private List<String> toStringList(List<Integer> in)
    {
        List<String> out = new ArrayList<String>(in.size());
        for(Integer i : in)
        {
            out.add(i.toString());
        }
        return out;
    }    
    
    private synchronized int getLastId(String listName)
    {
        Integer lastId = lastIdMap.get(listName);
        if(lastId != null)
        {
            return lastId;
        }
        lastIdMap.put(listName, 0);
        return 0;
    }
    
    private synchronized void setLastId(String listName, int value)
    {
        lastIdMap.put(listName, value);
    }
}