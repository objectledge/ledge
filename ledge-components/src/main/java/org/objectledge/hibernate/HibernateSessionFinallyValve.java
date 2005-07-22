package org.objectledge.hibernate;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

/**
 * Pipeline processing valve that closes hibernate session.
 * 
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: HibernateSessionFinallyValve.java,v 1.1 2005-07-22 17:19:39 pablo Exp $
 */
public class HibernateSessionFinallyValve 
    implements Valve
{
	
    /**
     * Run the pipeline valve - close session.
     * 
     * @param context the thread's processing context.
     * @throws ProcessingException if authentication failed.
     */
    public void process(Context context)
        throws ProcessingException
    {
        HibernateSessionContext.getHibernateSessionContext(context).getSession().close();
    }
}
