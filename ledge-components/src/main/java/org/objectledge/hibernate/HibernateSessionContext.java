package org.objectledge.hibernate;

import org.hibernate.Session;
import org.objectledge.context.Context;

/**
 * The hibernate session context is used to access the hiberate session for current request.
 *
 * <p>
 * Example code:
 * </p>
 * <pre>
 * import org.hibernate.Session;
 * import org.hibernate.Transaction;
 * ...
 * import org.objectledge.hibernate.HibernateSessionContext;
 * import org.objectledge.hibernate.HibernateSessionFactory;
 * 
 * ...
 * 
 * Session x = HibernateSessionContext.getHibernateSessionContext(context).getSession();
 * Transaction tx = x.beginTransaction();
 * try
 * {
 *     Event e = new Event();
 *     e.setTitle("costam");
 *     x.save(e);
 *     tx.commit();
 *     templatingContext.put("events", x.createQuery("from Event").list());
 * }
 * catch (Exception e)
 * {
 *     tx.rollback();
 * }
 * </pre>
 *
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: HibernateSessionContext.java,v 1.2 2005-07-25 12:59:09 rafal Exp $
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
