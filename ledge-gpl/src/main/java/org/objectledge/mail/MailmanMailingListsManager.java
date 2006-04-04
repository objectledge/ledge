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
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;

/**
 * Mailman mailing list manager implementation.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: MailmanMailingListsManager.java,v 1.8 2006-04-04 13:39:30 rafal Exp $
 */
public class MailmanMailingListsManager implements MailingListsManager
{
    /** logging facility */
    private Logger logger;
    
    /** mail system */
    private MailSystem mailSystem;

    /** mailman rcp address */
    private String address;

    /** mailman admin login */
    private String adminLogin;

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
        adminLogin = config.getChild("login").getValue("top");
        adminPassword = config.getChild("password").getValue("secret");
        monitoringAddress = config.getChild("monitoring_address").getValue("");
        monitoringSessionName = config.getChild("monitoring_session").getValue("");
        lastIdMap = new HashMap<String, Integer>();
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
        lastIdMap = new HashMap<String, Integer>();
        client = new XmlRpcClient(address);
        client.setBasicAuthentication(adminLogin, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    public String createList(String name, String domain, 
        String[] administrators, String password, 
        boolean notify, Locale locale) throws MailingListsException
    {
        Object[] params = new Object[]{
            adminPassword, name, domain, true, administrators,
            password, notify, locale.getDisplayLanguage()};
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
            if(monitoringAddress.length() > 0)
            {
                getList(name, newPassword).addMember(monitoringAddress,
                    "Mailiman - Ledge integration", "", false, true);
            }
        }
        
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
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
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
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
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
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
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */    
    public List getLocales()
        throws MailingListsException
    {
        Object[] params = new Object[]{adminPassword};
        Object result = executeMethod("Mailman.getLocales", params);
        List<String> names = new ArrayList<String>();
        if(result instanceof Collection)
        {
            Collection list = (Collection)result;
            ArrayList<String> codes = new ArrayList<String>();
            Iterator it = list.iterator();
            while(it.hasNext())
            {
                List innerList = (List)it.next();
                codes.add((String)innerList.get(0));
            }
            return codes;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }
    
    /**
     * {@inheritDoc}
     */    
    public List<Message> getNewMessages()
        throws MailingListsException
    {
        ArrayList<Message> list = new ArrayList<Message>();
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
                        Message messages[] = folder.getMessages();
                        for (Message message:messages)
                        {
                            message.setFlag(Flags.Flag.DELETED, true);
                            list.add(message);
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
                throw new MailingListsException("failed to fetch new messages", e);
            }
        }
        return list;
    }

    // package private operations
    
    /**
     * {@inheritDoc}
     */
    int addMember(String listName, String adminPassword, 
        String address, String name, String password, 
        boolean digest, boolean ignoreCreationPolicy)
            throws MailingListsException
    {
        Object[] params = new Object[]{
                        listName, adminPassword, address, name, 
                        password, digest, ignoreCreationPolicy};
        Object result = null;
        try
        {
            result = executeMethod("Mailman.addMember", params);
        }
        catch(NeedApprovalException e)
        {
            return MailingList.NEEDS_APPROVAL;
        }
        catch(NeedConfirmationException e)
        {
            return MailingList.NEEDS_TO_CONFIRM;
        }
        if(result instanceof Boolean)
        {
            if(((Boolean)result))
            {
                return MailingList.SUBSCRIBED;
            }
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
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
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    int deleteMember(String listName, String adminPassword, 
        String address, boolean ignoreDeletingPolicy) throws MailingListsException
    {
        Object[] params = new Object[]{
                        listName, adminPassword, address, ignoreDeletingPolicy};
        Object result = null;
        try
        {
            result = executeMethod("Mailman.deleteMember", params);
        }
        catch(NeedApprovalException e)
        {
            return MailingList.NEEDS_APPROVAL;
        }
        catch(NeedConfirmationException e)
        {
            return MailingList.NEEDS_TO_CONFIRM;
        }
        if(result instanceof Boolean)
        {
            if(((Boolean)result))
            {
                return MailingList.SUBSCRIBED;
            }
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result value: '"+result+
            "' or class:'"+result.getClass().getName());    
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
            return (List<String>)result;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
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
        throw new MailingListsException("Invalid result value: '"+result+"' or class:'"+result.getClass().getName());    
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
        throw new MailingListsException("Invalid result value: '"+result+"' or class:'"+result.getClass().getName());
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
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }
    
    /**
     * {@inheritDoc}
     */
    List getPendingPosts(String listName, String adminPassword) throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword};
        Object result = executeMethod("Mailman.getPendingMessages", params);
        if(result instanceof List)
        {
            return (List<String>)result;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    List getPendingSubscriptions(String listName, String adminPassword) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }

    /**
     * {@inheritDoc}
     */
    List getPendingUnsubscriptions(String listName, String adminPassword) throws MailingListsException
    {
        throw new UnsupportedOperationException("not implemented yet!");
    }
    
    Message getPendingTask(String listName, String adminPassword, Object id)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword, id};
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
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }

    List getNewPendingTasks(String listName, String adminPassword)
        throws MailingListsException
    {
        int lastId = getLastId(listName);
        Object[] params = new Object[]{listName, adminPassword, lastId};
        Object result = executeMethod("Mailman.getNewPendingTasks", params);
        if(result instanceof List)
        {
            List list = (List)result;
            setLastId(listName, (Integer)list.get(list.size()-1));
            return list;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }
    
    Integer getPendingTaskType(String listName, String adminPassword, Object id)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword, id};
        Object result = executeMethod("Mailman.getPendingTaskType", params);
        if(result instanceof Integer)
        {
            return (Integer)result;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }
    
    void postMessage(String listName, String adminPassword, String message)
        throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword, message};
        Object result = executeMethod("Mailman.postMessage", params);
        if(result instanceof Boolean && ((Boolean)result))
        {
            return;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }
    
    public void handleModeratorRequest(String listName, String adminPassword,
        Object id, int command)
            throws MailingListsException
    {
        Object[] params = new Object[]{listName, adminPassword, id, command};
        Object result = executeMethod("Mailman.handleModeratorRequest", params);
        if(result instanceof Boolean && ((Boolean)result))
        {
            return;
        }
        if(result == null)
        {
            throw new MailingListsException("Null result of rpc method invocation");
        }
        throw new MailingListsException("Invalid result class:'"+result.getClass().getName());
    }
    
    // private methods
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