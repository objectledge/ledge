package org.objectledge.naming;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;

/**
 * Naming context factory component.
 *
 * @author <a href="mail:rkrzewsk@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mail:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ContextFactory.java,v 1.1 2004-01-20 11:01:23 pablo Exp $
 */
public class ContextFactory
{
	/** the logger */
	private Logger logger;
	
    /** Initial environments. */
    private Map initial = new HashMap();

    /** Context aliases. */
    private Map alias = new HashMap();

    /** Cached contexts. */
    private Map context = new HashMap();

	/**
	 * Component constructor.
	 * 
	 * @param config the configuration.
	 * @param logger the logger.
	 */  
  	public ContextFactory(Configuration config, Logger logger)
  	{
  		this.logger = logger;
  		try
  		{
  			Configuration[] contexts = config.getChildren("context");
			for (int i = 0; i < contexts.length; i++)
			{
				Hashtable env = new Hashtable();
				String name = contexts[i].getAttribute("name");
				// initial factory is required.
				String initialFactory = contexts[i].getAttribute("initial_factory");
				env.put("java.naming.initial.factory", initialFactory);
				Configuration[] properties = contexts[i].getChildren("property");
				for (int j = 0; i < properties.length; j++)
				{
					String propertyName = properties[i].getAttribute("name");
					String propertyValue = properties[i].getAttribute("value", null);
					if (propertyValue == null)
					{
						propertyValue = properties[i].getValue();
					}
					env.put(propertyName, propertyValue);
				}
				initial.put(name, env);
				Configuration[] aliases = contexts[i].getChildren("alias");
				for (int j = 0; i < aliases.length; j++)
				{
					String aliasName = aliases[i].getAttribute("name");
					String prevContext = (String)alias.get(aliasName);
					if(prevContext != null)
					{
						throw new ComponentInitializationError("alias used" +							" in more than one context: '"+prevContext+"','"+name+"...");
					}
					alias.put(aliasName, name);
				}
			}
  		}
  		catch(ConfigurationException e)
  		{
  			throw new ComponentInitializationError("Invalid configuration",e);
  		}
  	}

    /**
     * Returns the specified naming context.
     *
     * @param name the context identifier used in the configuration file.
     * @return thread-safe view of the context.
     * @throws NamingException if identifier cannot be resolved.
     */
    public Context getContext(String name)
        throws NamingException
    {
        if(alias.containsKey(name))
        {
            name = (String)alias.get(name);
        }
        Context ctx = (Context)context.get(name);
        if(ctx == null)
        {
            Hashtable props = (Hashtable)initial.get(name);
            if(props == null || props.isEmpty())
            {
                throw new NamingException("context "+name+" was not found");
            }
            ctx = new InitialContext(props);
            context.put(name, ctx);
        }
        return (Context)ctx.lookup("");
    }
    
    /**
     * Returns the specified directory context;
     *
     * @param name the directory context identifier used in the configuration file
     * @return thread-safe view of the context.
     * * @throws NamingException if identifier cannot be resolved.
     */
    public DirContext getDirContext(String name)
        throws NamingException
    {
        if(alias.containsKey(name))
        {
            name = (String)alias.get(name);
        }
        DirContext ctx = (DirContext)context.get(name);
        if(ctx == null)
        {
            Hashtable props = (Hashtable)initial.get(name);
            if(props == null || props.isEmpty())
            {
                throw new NamingException("context "+name+" was not found");
            }
            ctx = new InitialDirContext(props);
            context.put(name, ctx);
        }
        return (DirContext)ctx.lookup("");
    }

    /**
     * Requests that the cached context object be discarded and created anew. 
     *
     * <p>This method is helpful for recovering from teporary failures of
     * external naming/directory services.</p>
     *
     * @param name the name of the context.
     */
    public void reconnect(String name)
    {
        if(alias.containsKey(name))
        {
            name = (String)alias.get(name);
        }
        context.remove(name);
    }
}
