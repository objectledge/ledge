package org.objectledge.hibernate;

import org.hibernate.Session;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

/**
 * Pipeline processing valve that initialize hibernate session.
 *
 * @author <a href="mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: HibernateSessionValve.java,v 1.1 2005-07-22 17:19:39 pablo Exp $
 */
public class HibernateSessionValve 
    implements Valve
{
	/** the authentication component */
	private HibernateSessionFactory hibernateSessionFactory;
	
	/**
	 * Constructor.
	 * 
     * @param userManager the user manager component.
	 */
	public HibernateSessionValve(HibernateSessionFactory hibernateSessionFactory)
	{
		this.hibernateSessionFactory = hibernateSessionFactory;
	}
	
    /**
     * Run the pipeline valve - create session.
     * 
     * @param context the thread's processing context.
     * @throws ProcessingException if authentication failed.
     */
    public void process(Context context)
        throws ProcessingException
    {
        Session session = hibernateSessionFactory.openHibernateSession();
        HibernateSessionContext hibernateSessionContext = new HibernateSessionContext(session);
        context.setAttribute(HibernateSessionContext.class, hibernateSessionContext);
    }
}
