// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//
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
 * @version $Id: HibernateSessionContext.java,v 1.3 2005-07-29 14:39:05 rafal Exp $
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
