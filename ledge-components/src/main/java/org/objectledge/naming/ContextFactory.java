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
import org.picocontainer.PicoContainer;

/**
 * Naming context factory component.
 *
 * @author <a href="mail:rkrzewsk@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mail:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ContextFactory.java,v 1.5 2004-02-17 15:48:44 fil Exp $
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
     * @param container the container to resolve component properties from.
	 * @param config the configuration.
	 * @param logger the logger.
     * @throws ConfigurationException if the component's configuration is invalid.
	 */  
  	public ContextFactory(PicoContainer container, Configuration config, Logger logger)
        throws ConfigurationException
  	{
  		this.logger = logger;
        Configuration[] contexts = config.getChildren("context");
        for (int i = 0; i < contexts.length; i++)
        {
            Hashtable env = new Hashtable();
            String name = contexts[i].getAttribute("name");
            // initial factory is required.
            String initialFactory = contexts[i].getAttribute("initial_factory");
            env.put("java.naming.factory.initial", initialFactory);
            Configuration[] properties = contexts[i].getChildren("property");
            for (int j = 0; j < properties.length; j++)
            {
                String propertyName = properties[j].getAttribute("name");
                String propertyValue = properties[j].getAttribute("value", null);
                if (propertyValue == null)
                {
                    propertyValue = properties[j].getValue();
                }
                env.put(propertyName, propertyValue);
            }
            Configuration[] componentProperties = contexts[i].getChildren("component-property");
            for (int j = 0; j < componentProperties.length; j++)
            {
                String propertyName = componentProperties[j].getAttribute("name");
                Object key = componentProperties[j].getAttribute("key", null);
                if(key == null)
                {
                    String cn = componentProperties[j].getAttribute("class-key", null);
                    if(cn != null)
                    {
                        try
                        {
                            key = Class.forName(cn);
                        }
                        catch(ClassNotFoundException e)
                        {
                            throw new ConfigurationException("non-existent class "+cn, 
                                componentProperties[j].getPath(), 
                                componentProperties[j].getLocation());
                        }
                    }
                }
                Object component = null;
                if(key != null)
                {
                    component = container.getComponentInstance(key);
                }
                else
                {
                    String cn = componentProperties[j].getAttribute("class");
                    if(cn != null)
                    {
                        try
                        {
                            key = Class.forName(cn);
                        }
                        catch(ClassNotFoundException e)
                        {
                            throw new ConfigurationException("non-existent class "+cn, 
                                componentProperties[j].getPath(), 
                                componentProperties[j].getLocation());
                        }
                        component = container.getComponentInstanceOfType((Class)key);
                    }
                }
                if(component == null)
                {
                    throw new ConfigurationException("missing component "+key, 
                        componentProperties[j].getPath(), 
                        componentProperties[j].getLocation());
                }
                env.put(propertyName, component);
            }
            initial.put(name, env);
            Configuration[] aliases = contexts[i].getChildren("alias");
            for (int j = 0; j < aliases.length; j++)
            {
                String aliasName = aliases[j].getAttribute("name");
                String prevContext = (String)alias.get(aliasName);
                if(prevContext != null)
                {
                    throw new ComponentInitializationError("alias used" +
                        " in more than one context: '"+prevContext+"','"+name+"...");
                }
                alias.put(aliasName, name);
            }
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
