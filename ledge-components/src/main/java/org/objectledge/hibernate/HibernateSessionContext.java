package org.objectledge.hibernate;

import org.hibernate.Session;
import org.objectledge.context.Context;

/**
 * The hibernate session context.
 *
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: HibernateSessionContext.java,v 1.1 2005-07-22 17:19:39 pablo Exp $
 */
public class HibernateSessionContext
{
	/**
	 *  Useful method to retrieve hibernateSession context from context.
	 *
	 * @param context the context.
	 * @return the authentication context.
	 */
	public static HibernateSessionContext getHibernateSessionContext(Context context)
	{
		return (HibernateSessionContext)context.getAttribute(HibernateSessionContext.class);
	}

	/** the session. */
	private Session session;

	/**
	 * Construct new hibernate session context.
     * 
     * @param session the current hiberante session.
     */
	public HibernateSessionContext(Session session)
	{
        this.session = session;
	}
	
    /**
     * Returns the hibernate session.
     *
     * @return the the session.
     */
    public Session getSession()
    {
    	return session;
    }    
}
