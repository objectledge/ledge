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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.objectledge.database.persistence.Persistence;
import org.objectledge.database.persistence.PersistenceException;

/**
 * Database implementation of java.naming.Context interface.
 *  
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class DatabaseContext implements Context
{
    /** Environment entry for persistence component. */
    public static final String PERSISTENCE = "org.objectledge.database.persistence.Persistence";

    /** the persistence component */
    protected Persistence persistence;
    
    /** The enviroment */
    protected Hashtable env;

    /** The default parser */
    protected static NameParser parser = new DefaultNameParser();
    
    /** The context persistent delegate */
    protected PersistentContext context;
    
    /**
     * The context constructor used by initial context factory;
     * 
     * @param env the environment 
     */
    public DatabaseContext(Hashtable env)
    {
        this.env = env;
        persistence = (Persistence)env.get(PERSISTENCE);
        if(persistence == null)
        {
            throw new RuntimeException("failed to retrieve the persistence " +                                        "component from environment");
        }
        String dn = (String)env.get(Context.PROVIDER_URL);
        List list = null;
        try
        {
            list = persistence.load("dn = '"+dn+"'", PersistentContext.FACTORY);
        }
        catch(PersistenceException e)
        {
            throw new RuntimeException("failed to load '"+dn+"' context from database");
        }
        if(list.size() == 0)
        {
            throw new RuntimeException("failed to lookup the context in database");
        }
        if(list.size() > 1)
        {
            throw new RuntimeException("ambiguous context '"+dn+"' in database");
        }
        context = (PersistentContext)list.get(0);
    }

    /**
     * The context constructor;
     * 
     * @param env the environment.
     * @param context the persistent context delegate.
     * @param persistence the persistence.
     * @throws NamingException if operation failed.
     */
    protected DatabaseContext(Hashtable env, PersistentContext context, Persistence persistence)
        throws NamingException
    {
        
        this.env = env;
        this.persistence = persistence;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public Object lookup(Name name) throws NamingException
    {
        if (name.isEmpty())
        {
            return new DatabaseContext(env, context, persistence);
        }
        String dn = getDN(name);
        List list = lookupContext(dn);
        if(list.size() == 0)
        {
            throw new NamingException("faled to retrieve context '"+dn+"' form database");
        }
        if(list.size() > 1)
        {
            throw new NamingException("ambigious context '"+dn+"' in database");
        }
        return new DatabaseContext(env, (PersistentContext)list.get(0), persistence);
    }

    /**
     * {@inheritDoc}
     */
    public Object lookup(String name) throws NamingException
    {
        return lookup(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public void bind(Name name, Object obj) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void bind(String name, Object obj) throws NamingException
    {
        bind(new CompositeName(name),obj);
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(Name name, Object obj) throws NamingException
    {
        throw new UnsupportedOperationException();        
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(String name, Object obj) throws NamingException
    {
        rebind(new CompositeName(name),obj);
    }

    /**
     * {@inheritDoc}
     */
    public void unbind(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();        
    }

    /**
     * {@inheritDoc}
     */
    public void unbind(String name) throws NamingException
    {
        unbind(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public void rename(Name oldName, Name newName) throws NamingException
    {
        if (oldName.isEmpty())
        {
            throw new InvalidNameException("Invalid source name");
        }
        if (newName.isEmpty())
        {
            throw new InvalidNameException("Invalid target name");
        }
        String dn = getDN(oldName);
        List list = lookupContext(dn);
        if(list.size() == 0)
        {
            throw new NamingException("faled to retrieve context '"+dn+"' form database");
        }
        if(list.size() > 1)
        {
            throw new NamingException("ambigious context '"+dn+"' in database");
        }
        PersistentContext delegate = (PersistentContext)list.get(0);
        String newDn = getDN(newName);
        delegate.setDN(newDn);
        try
        {
            persistence.save(delegate);
        }
        catch(PersistenceException e)
        {
            throw new DatabaseNamingException("failed to rename the context name",e);        
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rename(String oldName, String newName) throws NamingException
    {
        rename(new CompositeName(oldName), new CompositeName(newName));
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration list(Name name) throws NamingException
    {
        DatabaseContext ctx = (DatabaseContext)lookup(name);
        long parentId = ctx.getDelegate().getContextId();
        try
        {
            List list = persistence.load("parent = "+parentId, PersistentContext.FACTORY);
            List target = new ArrayList();
            for(int i = 0; i < list.size(); i++)
            {
                PersistentContext delegate = (PersistentContext)list.get(0);
                target.add(new NameClassPair(delegate.getDN(), this.getClass().getName()));
            }
            return new DefaultEnumeration(target);            
        }
        catch(PersistenceException e)
        {
            throw new DatabaseNamingException("failed to retrieve child contexts from database",e);
        }        
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration list(String name) throws NamingException
    {
        return list(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration listBindings(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration listBindings(String name) throws NamingException
    {
        return listBindings(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public void destroySubcontext(Name name) throws NamingException
    {
        if (name.isEmpty())
        {
            throw new InvalidNameException("Cannot perform self destroy");
        }
        DatabaseContext ctx = (DatabaseContext)lookup(name);
        long parentId = ctx.getDelegate().getContextId();
        try
        {
            List list = persistence.load("parent = "+parentId, PersistentContext.FACTORY);
            if(list.size()>0)
            {
                throw new NamingException("failed to destroy not empty subcontext");
            }
            persistence.delete(ctx.getDelegate());
        }
        catch(PersistenceException e)
        {
            throw new DatabaseNamingException("failed to delete '"+ctx.getDelegate().getDN()+
                                               "' context from database",e);
        }        
    }

    /**
     * {@inheritDoc}
     */
    public void destroySubcontext(String name) throws NamingException
    {
        destroySubcontext(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public Context createSubcontext(Name name) throws NamingException
    {
        PersistentContext delegate = createContextDelegate(name);
        return new DatabaseContext(env, delegate, persistence);
    }

    /**
     * {@inheritDoc}
     */
    public Context createSubcontext(String name) throws NamingException
    {
        return createSubcontext(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public Object lookupLink(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();        
    }

    /**
     * {@inheritDoc}
     */
    public Object lookupLink(String name) throws NamingException
    {
        return lookupLink(new CompositeName(name));
    }

    /**
     * {@inheritDoc}
     */
    public NameParser getNameParser(Name name) throws NamingException
    {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    public NameParser getNameParser(String name) throws NamingException
    {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    public Name composeName(Name name, Name prefix) throws NamingException
    {
        Name compoundName = name;
        if(compoundName instanceof CompositeName)
        {
            compoundName = parser.parse(compoundName.toString());
        }
        Name compoundPrefix = prefix;
        if(compoundPrefix instanceof CompositeName)
        {
            compoundPrefix = parser.parse(compoundPrefix.toString());
        }
        compoundPrefix.addAll(compoundName);
        return new CompositeName(compoundPrefix.toString());
    }

    /**
     * {@inheritDoc}
     */
    public String composeName(String name, String prefix) throws NamingException
    {
        Name compoundName = parser.parse(name);
        Name compoundPrefix = parser.parse(prefix);
        compoundPrefix.addAll(compoundName);
        return compoundPrefix.toString();        
    }

    /**
     * {@inheritDoc}
     */
    public Object addToEnvironment(String propName, Object propVal) throws NamingException
    {
        if (env == null) 
        {
            env = new Hashtable();
        } 
        return env.put(propName, propVal);
    }

    /**
     * {@inheritDoc}
     */
    public Object removeFromEnvironment(String propName) throws NamingException
    {
        if(env == null)
        {
            return null;
        }
        return env.remove(propName);        
    }

    /**
     * {@inheritDoc}
     */
    public Hashtable getEnvironment() throws NamingException
    {
        return new Hashtable(env);
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws NamingException
    {
    }

    /**
     * {@inheritDoc}
     */
    public String getNameInNamespace() throws NamingException
    {
        return context.getDN();
    }


    // private helper methods

    /**
     * Lookup the context with given dn.
     * 
     * @param dn the dn of the context.
     * @return the list of contexts with given dn.
     * @throws NamingException if operation fails.
     */
    protected List lookupContext(String dn)
        throws NamingException
    {
        try
        {
            return persistence.load("dn = '"+dn+"'", PersistentContext.FACTORY);
        }
        catch(PersistenceException e)
        {
            throw new DatabaseNamingException("failed to retrieve context from database",e);
        }        
    }

    /**
     * Get the dn from relative name.
     * 
     * @param relativeName the relative name of the context.
     * @return the dn of the context.
     * @throws NamingException if operation fails.
     */    
    protected String getDN(Name relativeName)
        throws NamingException
    {
        Name base = parser.parse(new CompositeName(this.getNameInNamespace()).get(0));
        return base.add(relativeName.get(0)).toString();
    }

    /**
     * Create the context delegate.
     * 
     * @param name the name of the context.
     * @return the persistent delegate.
     * @throws NamingException if operation fails.
     */    
    protected PersistentContext createContextDelegate(Name name)
        throws NamingException
    {
        String dn = getDN(name);
        List list = lookupContext(dn);
        if(list.size() > 0)
        {
            throw new NameAlreadyBoundException("context '"+dn+"' already exists");
        }
        PersistentContext subContext = new PersistentContext(dn, context.getContextId());
        try
        {
            persistence.save(subContext);
        }
        catch(PersistenceException e)
        {
            throw new DatabaseNamingException("failed to add the subcontext with name = '"+
                                                    dn+"'",e);        
        }
        return subContext;
    }
    
    /**
     * Access to delegate object.
     * 
     * @return the delegate context.
     */
    PersistentContext getDelegate()
    {
        return context;
    }
}
