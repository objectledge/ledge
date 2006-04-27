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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.utils.StringUtils;

/**
 * Mailman mailing list manager implementation.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: MailmanMailingListsManager.java,v 1.28 2006-04-27 10:42:08 rafal Exp $
 */
public class MailmanMailingListsManager implements MailingListsManager
{
    /** List-Id header as defined by RFC2919 */
    private static final String LIST_ID_HEADER_NAME = "List-Id";
    
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
        client = new XmlRpcClient(address);
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
        client = new XmlRpcClient(address);
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
        Object[] params = new Object[]{
            adminPassword, name, domain, true, administrators,
            password, notify, locale.toString()};
        Object result = null;
        try
        {
            result = executeMethod("Mailman.createList", params);
        }
        // TODO fix it see "create.py" file ln. 82
        catch(InvalidListNameException e)
        {
            throw new ListAlreadyExistsException("",e); 
        }
        catch(ListAlreadyExistsException e)
        {
            throw new InvalidListNameException("",e);
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        if(result instanceof String)
        {
            String newPassword = (String)result;
            MailingList list = getList(name, newPassword); 
            if(monitoringAddress.length() > 0)
            {
                list.addMember(monitoringAddress, "Mailiman - Ledge integration", "", false, true,
                    false, false);
            }
            if(moderated)
            {
                addOptionValue(name, newPassword, "hold_these_nonmembers", list.getPostingAddress());
            }
            else
            {
                addOptionValue(name, newPassword, "accept_these_nonmembers", list.getPostingAddress());                
            }
            return newPassword;
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    public void deleteList(String name, boolean deleteArchives) throws MailingListsException
    {
        Object[] params = new Object[]{
                        adminPassword, name, deleteArchives};
        Object result = executeMethod("Mailman.deleteList", params);
        if(result instanceof Boolean)
        {
            if(!((Boolean)result).booleanValue())
            {
                throw new MailingListsException("failed to delete list - result false");
            }
            return;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
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
    
    private List<String> getLists(String filter) throws MailingListsException
    {
        Object[] params = new Object[]{
                        adminPassword, filter};
        Object result = executeMethod("Mailman.listAllLists", params);
        if(result instanceof List)
        {
            return (List<String>)result;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
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
        Object[] params = new Object[]{filter};
        Object result = executeMethod("Mailman.listAdvertisedLists", params);
        List<String> names = new ArrayList<String>();
        if(result instanceof List)
        {
            for(List<String> el: ((List<List<String>>)result))
            {
                names.add(el.get(0));
            }
            return names;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */    
    public List<Locale> getAvailableLocales()
        throws MailingListsException
    {
        Object[] params = new Object[] { adminPassword };
        Object result = executeMethod("Mailman.getAvailableLocales", params);
        List<String> names = new ArrayList<String>();
        if(result instanceof Collection)
        {
            Collection list = (Collection)result;
            ArrayList<Locale> codes = new ArrayList<Locale>();
            Iterator it = list.iterator();
            while(it.hasNext())
            {
                List innerList = (List)it.next();
                codes.add(StringUtils.getLocale((String)innerList.get(0)));
            }
            return codes;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getAvailableDomains()
        throws MailingListsException
    {
        Object[] params = new Object[] { adminPassword };
        Object result = executeMethod("Mailman.getAvailableDomains", params);        
        List<String> names = new ArrayList<String>();
        if(result instanceof Collection)
        {
            Collection list = (Collection)result;
            ArrayList<String> domains = new ArrayList<String>();
            Iterator it = list.iterator();
            while(it.hasNext())
            {
                domains.add((String)it.next());
            }
            return domains;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
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
        Object[] params = new Object[] { listName, adminPassword, address, name, password, digest,
                        ignoreCreationPolicy, acknowledge, notifyAdmins };
        Object result = null;
        try
        {
            result = executeMethod("Mailman.addMember", params);
        }
        catch(NeedApprovalException e)
        {
            return MailingList.OperationStatus.NEEDS_APPROVAL;
        }
        catch(NeedConfirmationException e)
        {
            return MailingList.OperationStatus.NEEDS_CONFIRMATION;
        }
        if(result instanceof Boolean)
        {
            if(((Boolean)result))
            {
                return MailingList.OperationStatus.COMPLETED;
            }
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    void changeMemberAddress(String listName, String adminPassword, 
        String oldAddress, String newAddress, boolean keepOld) throws MailingListsException
    {
        Object[] params = new Object[]{
                        listName, adminPassword, address, oldAddress, 
                        newAddress, keepOld};
        Object result = executeMethod("Mailman.changeMemberAddress", params);
        if(result instanceof Boolean)
        {
            if(((Boolean)result))
            {
               return;
            }
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
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
        Object[] params = new Object[] { listName, adminPassword, address, ignoreDeletingPolicy,
                        acknowledge, notifyAdmins };
        Object result = null;
        try
        {
            result = executeMethod("Mailman.deleteMember", params);
        }
        catch(NeedApprovalException e)
        {
            return MailingList.OperationStatus.NEEDS_APPROVAL;
        }
        catch(NeedConfirmationException e)
        {
            return MailingList.OperationStatus.NEEDS_CONFIRMATION;
        }
        if(result instanceof Boolean)
        {
            if(((Boolean)result))
            {
                return MailingList.OperationStatus.COMPLETED;
            }
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result value: '"+result+
            "' or class: "+result.getClass().getName());    
    }

    /**
     * {@inheritDoc}
     */
    List<String> getMembers(String listName, String adminPassword)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword};
        Object result = executeMethod("Mailman.getMembers", params);
        if(result instanceof List)
        {
            List<String> members = (List<String>)result;
            members.remove(monitoringAddress);
            return members;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    Object getOption(String listName, String adminPassword, String key)
        throws MailingListsException
    {
        Object[] params = new Object[]{
                        listName, adminPassword, new String[]{key}};
        Object result = executeMethod("Mailman.getOptions", params);
        if(result instanceof Map)
        {
            return ((Map)result).get(key);
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result value: '"+result+"' or class: "+result.getClass().getName());    
    }

    /**
     * {@inheritDoc}
     */
    void setOption(String listName, String adminPassword, 
        String key, Object value) throws MailingListsException
    {
        Hashtable<String, Object> map = new Hashtable<String, Object>();
        map.put(key, value);
        Object[] params = new Object[]{
                        listName, adminPassword, map};
        Object result = executeMethod("Mailman.setOptions", params);
        if(result instanceof Boolean)
        {
            if(((Boolean)result))
            {
                return;
            }
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result value: '"+result+"' or class: "+result.getClass().getName());
    }
    
    void addOptionValue(String listName, String adminPassword, String key, Object value)
        throws MailingListsException
    {
        List values = (List)getOption(listName, adminPassword, key);
        values.add(value);
        setOption(listName, adminPassword, key, values);
    }

    void removeOptionValue(String listName, String adminPassword, String key, Object value)
        throws MailingListsException
    {
        List values = (List)getOption(listName, adminPassword, key);
        values.remove(value);
        setOption(listName, adminPassword, key, values);
    }
    
    /**
     * {@inheritDoc}
     */
    String setPassword(String listName, String adminPassword, 
        String password) throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword, password};
        Object result = executeMethod("Mailman.resetListPassword", params);
        if(result instanceof String)
        {
            return ((String)result);
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }
    
    /**
     * {@inheritDoc}
     */
    List<String> getPendingPosts(String listName, String adminPassword) throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword};
        Object result = executeMethod("Mailman.getPendingMessages", params);
        if(result instanceof List)
        {
            return toStringList((List<Integer>)result);
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    List<String> getPendingSubscriptions(String listName, String adminPassword)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword};
        Object result = executeMethod("Mailman.getPendingSubscriptions", params);
        if(result instanceof List)
        {
            return toStringList((List<Integer>)result);
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    List<String> getPendingUnsubscriptions(String listName, String adminPassword)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword};
        Object result = executeMethod("Mailman.getPendingUnsubscriptions", params);
        if(result instanceof List)
        {
            return toStringList((List<Integer>)result);
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }    

    List<String> getNewPendingTasks(String listName, String adminPassword)
        throws MailingListsException
    {
        int lastId = getLastId(listName);
        Object[] params = new Object[]{listName, adminPassword, lastId};
        Object result = executeMethod("Mailman.getNewPendingTasks", params);
        if(result instanceof List)
        {
            List list = (List)result;
            if(!list.isEmpty())
            {
                setLastId(listName, (Integer)list.get(list.size() - 1));
            }
            return toStringList(list);
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }

    Message getPendingTask(String listName, String adminPassword, String id)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword, Integer.parseInt(id)};
        Object result = executeMethod("Mailman.getPendingTask", params);
        if(result instanceof String)
        {
            String msg = (String)result;
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
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }
        
    Integer getPendingTaskType(String listName, String adminPassword, String id)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword, Integer.parseInt(id)};
        Object result = executeMethod("Mailman.getPendingTaskType", params);
        if(result instanceof Integer)
        {
            return (Integer)result;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }
    
    void handleModeratorRequest(String listName, String adminPassword,
        String id, int command, String comment)
            throws MailingListsException
    {
        Object[] params;
        if(comment != null)
        {
            params = new Object[]{listName, adminPassword, Integer.parseInt(id), command};
        }
        else
        {
            params = new Object[]{listName, adminPassword, Integer.parseInt(id), command, comment};            
        }
        Object result = executeMethod("Mailman.handleModeratorRequest", params);
        if(result instanceof Boolean && ((Boolean)result))
        {
            return;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
    }
    
    void postMessage(String listName, String adminPassword, String message)
        throws MailingListsException
    {
        Object[] params = new Object[] { listName, adminPassword, message };
        Object result = executeMethod("Mailman.postMessage", params);
        if(result instanceof Boolean && ((Boolean)result))
        {
            return;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: " + result.getClass().getName());
    }

    String getInterfaceBaseURL(String domain)
        throws MailingListsException
    {
        Object[] params = new Object[] { adminPassword, domain };
        Object result = executeMethod("Mailman.getInterfaceBaseURL", params);
        if(result instanceof String)
        {
            return (String)result;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class: "+result.getClass().getName());
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
    private Object executeMethod(String method, Object[] parameters)
        throws MailingListsException
    {
        Object result = null;
        try
        {
            result = client.execute(method, getParameters(parameters));
        }
        catch(Exception e)
        {
            throw new MailingListsException("failed to invoke rpc method", e);
        }
        if(result instanceof XmlRpcException)
        {
            XmlRpcException e = ((XmlRpcException)result); 
            int errorCode = e.code;
            if(errorCode == -32501)
            {
                throw new NeedConfirmationException(e.getMessage());
            }
            if(errorCode == -32502)
            {
                throw new NeedApprovalException(e.getMessage());
            }
            if(errorCode == -32503)
            {
                throw new MailingListsAuthenticationException(e.getMessage());
            }
            if(errorCode == -32504)
            {
                throw new InvalidListNameException(e.getMessage());
            }
            if(errorCode == -32505)
            {
                throw new ListAlreadyExistsException(e.getMessage());
            }
            if(errorCode == -32506)
            {
                throw new LostAdministrativeRequestException(e.getMessage());
            }
            throw new MailingListsException(e.getMessage());
        }
        return result;
    }
    
    /**
     * Convert list of objects into vector.
     * 
     * @param params list of objects.
     * @return vector of objects.
     */
    private Vector getParameters(Object[] params)
    {
        Vector vector = new Vector();
        for(Object ob: params)
        {
            vector.add(ob);
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