package org.objectledge.authentication;

import java.security.Principal;

/**
 * An implementation of {@link Principal} interface
 * 
 * @author <a href="mailto:rafal@apache.org">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@apache.org">Pawel Potempski</a>
 * @version $Id: DefaultPrincipal.java,v 1.1 2004-01-21 13:24:50 pablo Exp $
 */
public class DefaultPrincipal
    implements Principal
{
    /** The name of the principal */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the principal
     */
    public DefaultPrincipal(String name)
    {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Principal)
        {
            return name.equals(((Principal)obj).getName());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        if(name != null)
        {
            return name.hashCode();
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        if(name != null)
        {
            return super.toString()+"("+name.toString()+")";
        }
        else
        {
            return super.toString()+"(null)";
        }
    }
}
