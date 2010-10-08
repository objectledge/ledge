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

package org.objectledge.naming.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.database.persistence.PersistenceException;

/**
 * Database based implementation of java.naming.directory.DirContext interface.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class DatabaseDirContext extends DatabaseContext implements DirContext
{
    /**
     * The directory context constructor.
     * 
     * @param env the environment. 
     */
    public DatabaseDirContext(Hashtable<?, ?> env)
    {
        super(env);
    }

    /**
     * The context constructor.
     * 
     * @param env the environment.
     * @param context the persistent context delegate.
     * @param persistence the persistence.
     * @throws NamingException if operation failed.
     */
    protected DatabaseDirContext(Hashtable<?, ?> env, PersistentContext context, Persistence persistence)
        throws NamingException
    {
        super(env, context, persistence);
    }

    /**
     * {@inheritDoc}
     */
    public Object lookup(Name name) throws NamingException
    {
        if (name.isEmpty())
        {
            return new DatabaseDirContext(env, context, persistence);
        }
        String dn = getDN(name);
        List<PersistentContext> list = lookupContext(dn);
        if(list.size() == 0)
        {
            throw new NamingException("faled to retrieve context '"+dn+"' from database");
        }
        if(list.size() > 1)
        {
            throw new NamingException("ambigious context '"+dn+"' in database");
        }
        return new DatabaseDirContext(env, list.get(0), persistence);        
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(Name name) throws NamingException
    {
        return getAttributes(name, null);
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(String name) throws NamingException
    {
        return getAttributes(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(Name name, String[] attrIds) throws NamingException
    {
        DatabaseDirContext ctx = (DatabaseDirContext)lookup(name);
        long contextId = ctx.getDelegate().getContextId();
        Attributes allAttributes = getAllAttributes(contextId);
        return filterAttributes(allAttributes, attrIds);
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(String name, String[] attrIds) throws NamingException
    {
        return getAttributes(new CompositeName(name), attrIds);
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(Name name, int modOp, Attributes attrs) throws NamingException
    {
        if (attrs == null || attrs.size() == 0)
        {
            return;
        }
        NamingEnumeration<?> enumerator = attrs.getAll();
        ModificationItem[] items = new ModificationItem[attrs.size()];
        int i = 0;
        while(enumerator.hasMoreElements())
        {
            items[i++] = new ModificationItem(modOp, (Attribute)enumerator.next());
        }
        modifyAttributes(name, items);        
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(String name, int modOp, Attributes attrs)
        throws NamingException
    {
        modifyAttributes(new CompositeName(name), modOp, attrs);
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException
    {
        List<ModificationItem> failures = new ArrayList<ModificationItem>();
        DatabaseDirContext ctx = (DatabaseDirContext)lookup(name);
        boolean transactionControler;
        try
        {
            transactionControler = persistence.getDatabase().beginTransaction();
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Failed to begin transaction",e);
        }
        for (int i=0; i<mods.length; i++)
        {      
            Attribute attribute = mods[i].getAttribute();
            NamingEnumeration<?> values = attribute.getAll();
            switch (mods[i].getModificationOp())
            {
                case ADD_ATTRIBUTE:
                    try
                    {
                        while (values.hasMore())
                        {
                            String value = (String)values.next();
                            PersistentAttribute pAttribute = new PersistentAttribute(
                                ctx.getDelegate().getContextId(),
                                attribute.getID(), value);
                            persistence.save(pAttribute);
                        }    
                    }   
                    catch(PersistenceException e)
                    {
                        failures.add(mods[i]);
                    }
                    break;
                case REPLACE_ATTRIBUTE:
                    try
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append("context_id = ");
                        sb.append(ctx.getDelegate().getContextId());
                        sb.append(" and name = '");
                        sb.append(attribute.getID());
                        sb.append("'");
                        persistence.delete(sb.toString(), PersistentAttribute.FACTORY);
                        while (values.hasMore())
                        {
                            String value = (String)values.next();
                            PersistentAttribute pAttribute = new PersistentAttribute(
                                ctx.getDelegate().getContextId(),
                                attribute.getID(), value);
                            persistence.save(pAttribute);
                        }    
                    }   
                    catch(PersistenceException e)
                    {
                        failures.add(mods[i]);
                    }
                    break;
                case REMOVE_ATTRIBUTE:
                    try
                    {
                        if(values.hasMore())
                        {
                            while(values.hasMore())
                            {
                                String value = (String)values.next();
                                StringBuilder sb = new StringBuilder();
                                sb.append("context_id = ");
                                sb.append(ctx.getDelegate().getContextId());
                                sb.append(" and name = '");
                                sb.append(attribute.getID());
                                sb.append("' and value = '");
                                sb.append(value);
                                sb.append("'");                            
                                persistence.delete(sb.toString(), PersistentAttribute.FACTORY);
                            }
                        }
                        else
                        {
                            StringBuilder sb = new StringBuilder();
                            sb.append("context_id = ");
                            sb.append(ctx.getDelegate().getContextId());
                            sb.append(" and name = '");
                            sb.append(attribute.getID());
                            sb.append("'");                            
                            persistence.delete(sb.toString(), PersistentAttribute.FACTORY);
                        }
                    }    
                    catch(PersistenceException e)
                    {
                        failures.add(mods[i]);
                    }
                
                    break;
                default:
                    break;
            }
        }
        if( failures.size() > 0)
        {
            try
            {
                persistence.getDatabase().rollbackTransaction(transactionControler);
            }
            catch(SQLException e)
            {
                throw new RuntimeException("Failed to begin transaction",e);
            }
        }
        else
        {
            try
            {
                persistence.getDatabase().commitTransaction(transactionControler);
            }
            catch(SQLException e)
            {
                throw new RuntimeException("Failed to begin transaction",e);
            }                    
        }
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(String name, ModificationItem[] mods) 
        throws NamingException
    {
        modifyAttributes(new CompositeName(name), mods);
    }

    /**
     * {@inheritDoc}
     */
    public void bind(Name name, Object obj, Attributes attrs) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void bind(String name, Object obj, Attributes attrs) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(Name name, Object obj, Attributes attrs) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(String name, Object obj, Attributes attrs) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void destroySubcontext(Name name) throws NamingException
    {
        DatabaseContext ctx = (DatabaseContext)lookup(name);
        long contextId = ctx.getDelegate().getContextId();
        deleteAllAttributes(contextId);
        super.destroySubcontext(name);        
    }

    /**
     * {@inheritDoc}
     */
    public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException
    {
        PersistentContext delegate = createContextDelegate(name);
        DatabaseDirContext subContext = new DatabaseDirContext(env, delegate, persistence);  
        if (attrs != null && attrs.size() > 0)
        {
             subContext.modifyAttributes("", DirContext.ADD_ATTRIBUTE, attrs);
        }
        return subContext;
    }

    /**
     * {@inheritDoc}
     */
    public DirContext createSubcontext(String name, Attributes attrs) throws NamingException
    {
        return createSubcontext(new CompositeName(name),attrs);
    }
    
    /**
     * {@inheritDoc}
     */
    public Context createSubcontext(String name) throws NamingException
    {
        return createSubcontext(name,null);
    }
        
    /**
     * {@inheritDoc}
     */
    public Context createSubcontext(Name name) throws NamingException
    {
        return createSubcontext(name, null);
    }
        

    /**
     * {@inheritDoc}
     */
    public DirContext getSchema(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getSchema(String name) throws NamingException
    {
        return getSchema(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getSchemaClassDefinition(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getSchemaClassDefinition(String name) throws NamingException
    {
        return getSchemaClassDefinition(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, 
                                     String[] attributesToReturn)
        throws NamingException
    {
        List<SearchResult> searchResult = new ArrayList<SearchResult>();
        DatabaseDirContext ctx = (DatabaseDirContext)lookup(name);
        long contextId = ctx.getDelegate().getContextId();
        Map<String, Attributes> attributeMap = getChildrenAttributes(contextId);
        Iterator<String> it = attributeMap.keySet().iterator();
        while(it.hasNext())
        {
            String dn = it.next();        
            Attributes attributes = attributeMap.get(dn);
            NamingEnumeration<?> enumeration = matchingAttributes.getAll();
            if(attributes == null && enumeration.hasMore())
            {
                break;
            }
            boolean found = true;
            outer: while(enumeration.hasMore())
            {
                Attribute searchAttr = (Attribute)enumeration.next();
                Attribute attr = attributes.get(searchAttr.getID());
                if(attr == null)
                {
                    found = false;
                    break;
                }
                NamingEnumeration<?> values = searchAttr.getAll();
                while (values.hasMore())
                {
                    String value = (String)values.next();
                    if(attr.contains(value))
                    {
                        break outer;
                    }
                }
                found = false;
            }
            if(found)
            {
                SearchResult result = new SearchResult(dn.substring(0, getNameInNamespace()
                    .length() + 1), null, filterAttributes(attributes, attributesToReturn));
                result.setNameInNamespace(dn);
                searchResult.add(result);
            }
        }
        return new DefaultEnumeration<SearchResult>(searchResult);
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, 
                                     String[] attributesToReturn)
        throws NamingException
    {
        return search(new CompositeName(name), matchingAttributes, null);
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes)
        throws NamingException
    {
        return search(name, matchingAttributes, null);
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes)
        throws NamingException
    {
        return search(new CompositeName(name), matchingAttributes);
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons)
        throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons)
        throws NamingException
    {
        return search(new CompositeName(name), filter, cons);
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(Name name, String filterExpr, 
                                     Object[] filterArgs, SearchControls cons) 
        throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration<SearchResult> search(String name, String filterExpr,
                                     Object[] filterArgs, SearchControls cons)
        throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Get all attributes that belongs to specified context.
     * 
     * @param contextId the context id.
     * @return the attributes.
     * @throws NamingException thrown if operation fails.
     */
    private Attributes getAllAttributes(long contextId)
        throws NamingException
    {
        Attributes attrs = new BasicAttributes();
        try
        {
            List<PersistentAttribute> list = persistence.load("context_id = "+contextId, PersistentAttribute.FACTORY);
            for(int i = 0; i < list.size(); i++)
            {
                PersistentAttribute attribute = list.get(i);
                Attribute attr = attrs.get(attribute.getName());
                if (attr != null)
                {
                    attr.add(attribute.getValue());
                }
                else
                {
                    attr = new BasicAttribute(attribute.getName());
                    attr.add(attribute.getValue());
                    attrs.put(attr);
                }                
            }
            return attrs;
        }
        catch(PersistenceException e)
        {
            throw new DatabaseNamingException("failed to retrieve context from database",e);
        }
    }

    /**
     * Delete all attributes that belongs to specified context.
     * 
     * @param contextId the context id.
     * @throws NamingException thrown if operation fails.
     */
    private void deleteAllAttributes(long contextId)
        throws NamingException
    {
        try
        {
            List<PersistentAttribute> list = persistence.load("context_id = "+contextId, PersistentAttribute.FACTORY);
            for(int i = 0; i < list.size(); i++)
            {
                PersistentAttribute attribute = list.get(i);
                persistence.delete(attribute);
            }
        }
        catch(PersistenceException e)
        {
            throw new DatabaseNamingException("failed to retrieve context from database",e);
        }
    }

    /**
     * Filter attributes with attribute list.
     * 
     * @param attributes the attributes to be filter.
     * @param attrIds the filter.
     * @return the filtered attributes.
     */
    private Attributes filterAttributes(Attributes attributes, String[] attrIds)
    {
        if(attrIds == null)
        {
            return attributes;
        }
        Attributes target = new BasicAttributes();
        for(int i = 0; i < attrIds.length; i++)
        {
            Attribute attr = attributes.get(attrIds[i]);
            if(attr != null)
            {
                target.put(attr);   
            }
        }
        return target;
    }
    
    /**
     * Get all attributes from child contexts of specified context. 
     * 
     * @param parentId the parent context id.
     * @return the map with attributes grouped by contexts.
     * @throws NamingException thrown if operation fails.
     */
    private Map<String, Attributes> getChildrenAttributes(long parentId)
        throws NamingException
    {
        Map<String, Attributes> map = new HashMap<String, Attributes>();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try
        {
            conn = persistence.getDatabase().getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(
                "SELECT dn, name, value FROM ledge_naming_context, ledge_naming_attribute " +
                "WHERE ledge_naming_context.context_id = ledge_naming_attribute.context_id" +                " and ledge_naming_context.parent=" + parentId);            
            while(rs.next())
            {
                String dn = rs.getString("dn");
                String name = rs.getString("name");
                String value = rs.getString("value");
                Attributes attributes = map.get(dn);
                if(attributes == null)
                {
                    attributes = new BasicAttributes();
                    map.put(dn,attributes);
                }
                Attribute attribute = attributes.get(name);
                if (attribute != null)
                {
                    attribute.add(value);
                }
                else
                {
                    attribute = new BasicAttribute(name);
                    attribute.add(value);
                    attributes.put(attribute);
                }
            }
        }
        catch (Exception e)
        {
            throw new DatabaseNamingException("Failed to execute query", e);
        }
        finally
        {
            DatabaseUtils.close(rs);
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
        return map;        
    }
}
